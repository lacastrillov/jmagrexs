package com.dot.gcpbasedot.controller;

import com.dot.gcpbasedot.annotation.ImageResize;
import com.dot.gcpbasedot.domain.BaseEntity;
import com.dot.gcpbasedot.dto.ItemTemplate;
import com.dot.gcpbasedot.mapper.BasicEntityMapper;
import com.dot.gcpbasedot.reflection.EntityReflection;
import com.dot.gcpbasedot.service.EntityService;
import com.dot.gcpbasedot.util.Util;
import com.dot.gcpbasedot.util.XMLMarshaller;
import com.dot.gcpbasedot.dto.ResultListCallback;
import com.dot.gcpbasedot.util.ExcelService;
import com.dot.gcpbasedot.util.FileService;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public abstract class RestEntityController {

    protected static final Logger LOGGER = Logger.getLogger(RestEntityController.class);
    
    protected String entityRef= null;

    protected EntityService service;

    protected BasicEntityMapper mapper;
    
    protected Class entityClass;
    
    protected Class dtoClass;
    
    private final String TEMPLATES_DIR ="/ext/gridtemplates/";
    
    @Autowired
    private ServletContext selvletContext;
    
    @Autowired
    private VelocityEngine velocityEngine;
    
    @Autowired
    @Value("${static.domain.url}")
    public String LOCAL_DOMAIN;
    
    @Autowired
    @Value("${static.folder}")
    public String LOCAL_DIR;
    
    protected Long maxFileSizeToUpload=1024L;
    

    protected void addControlMapping(String entityRef, EntityService entityService, BasicEntityMapper entityMapper) {
        this.entityRef= entityRef;
        this.service=  entityService;
        this.mapper= entityMapper;
        this.entityClass= service.getEntityClass();
    }

    public void setDtoClass(Class dtoClass) {
        this.dtoClass = dtoClass;
    }
    
    @RequestMapping(value = "/find.htm", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public byte[] find(@RequestParam(required = false) String filter, @RequestParam(required = false) String query,
            @RequestParam(required = false) Long start,  @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir,
            @RequestParam(required = false) String templateName, @RequestParam(required = false) Long numColumns) {

        String resultData;
        try {
            List<? extends BaseEntity> listEntities = service.findByJSONFilters(filter, query, page, limit, sort, dir);
            List listDtos = mapper.listEntitiesToListDtos(listEntities);
            Long totalCount = service.countByJSONFilters(filter, query);
            
            if(templateName!=null){
                resultData= generateTemplateData(listDtos, totalCount, entityRef, true, templateName, numColumns);
            }else{
                resultData=Util.getResultListCallback(listDtos, totalCount, "Busqueda de " + entityRef + " realizada...", true);
            }
        } catch (Exception e) {
            LOGGER.error("find " + entityRef, e);
            resultData=Util.getResultListCallback(new ArrayList(), "Error buscando " + entityRef + ": " + e.getMessage(), false);
        }
        
        return getStringBytes(resultData);
    }

    @RequestMapping(value = "/find/xml.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public HttpEntity<byte[]> findXml(@RequestParam(required = false) String filter, @RequestParam(required = false) String query,
            @RequestParam(required = false) Long start, @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir) {

        try {
            List<? extends BaseEntity> listEntities = service.findByJSONFilters(filter, query, page, limit, sort, dir);
            List<? extends BaseEntity> listDtos = mapper.listEntitiesToListDtos(listEntities);
            Long totalCount = service.countByJSONFilters(filter, query);

            ResultListCallback resultListCallBack = Util.getResultList(listDtos, totalCount, "Busqueda de " + entityRef + " realizada...", true);
            String xml = XMLMarshaller.convertObjectToXML(resultListCallBack);
            byte[] documentBody = xml.getBytes();

            HttpHeaders header = new HttpHeaders();
            header.setContentType(new MediaType("application", "xml"));
            header.setContentLength(documentBody.length);

            return new HttpEntity<>(documentBody, header);
        } catch (Exception e) {
            LOGGER.error("find " + entityRef, e);
            return null;
        }
    }

    @RequestMapping(value = "/find/xls.htm", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public void findXls(@RequestParam(required = false) String filter, @RequestParam(required = false) String query, 
            @RequestParam(required = false) Long start, @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir,
            HttpServletResponse response) {
        
        response.setContentType("application/xls");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + "report.xls\"");

        try {
            List<Object> listEntities = service.findByJSONFilters(filter, query, page, limit, sort, dir);
            
            ExcelService.generateExcelReport(listEntities, response.getOutputStream(), entityClass);
        } catch (Exception e) {
            LOGGER.error("find " + entityRef, e);
        }
    }
    
    @RequestMapping(value = "/find/yaml.htm", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public byte[] findYaml(@RequestParam(required = false) String filter, @RequestParam(required = false) String query,
            @RequestParam(required = false) Long start, @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir,
            @RequestParam(required = true) Boolean yalmFormat) {

        String resultData;
        try {
            List<? extends BaseEntity> listEntities = service.findByJSONFilters(filter, query, page, limit, sort, dir);
            List listDtos = mapper.listEntitiesToListDtos(listEntities);
            Long totalCount = service.countByJSONFilters(filter, query);
            
            resultData= Util.jsonToYaml(Util.getResultList(listDtos, totalCount, "Busqueda de " + entityRef + " realizada...", true));
        } catch (Exception e) {
            LOGGER.error("find " + entityRef, e);
            resultData=Util.getResultListCallback(new ArrayList(), "Error buscando " + entityRef + ": " + e.getMessage(), false);
        }
        
        if(yalmFormat){
            resultData= "<textarea style='width:100%; height:100%;color:darkblue'>"+resultData+"</textarea>";
        }
        
        return getStringBytes(resultData);
    }
    
    @RequestMapping(value = "/report/{reportName}.htm", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public byte[] report(@RequestParam(required = false) String filter, @RequestParam(required = false) Long start,
            @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir,
            @RequestParam(required = false) String templateName, @RequestParam(required = false) Long numColumns,
            @RequestParam(required = true) String dtoName,
            @PathVariable String reportName) {

        String resultData;
        try {
            Class dtoReportClass= Class.forName(dtoName);
            List<Object> listDtos = service.findByJSONFilters(reportName, filter, page, limit, sort, dir, dtoReportClass);
            Long totalCount = service.countByJSONFilters(reportName, filter, dtoReportClass);
            
            if(templateName!=null){
                resultData= generateTemplateData(listDtos, totalCount, entityRef, true, templateName, numColumns);
            }else{
                resultData= Util.getResultListCallback(listDtos, totalCount, "Buequeda reporte " + reportName + " realizada...", true);
            }
        } catch (Exception e) {
            LOGGER.error("find " + entityRef + " - " + reportName, e);
            resultData= Util.getResultListCallback(new ArrayList(), "Error reporte " + reportName + ": " + e.getMessage(), false);
        }
        
        return getStringBytes(resultData);
    }
    
    @RequestMapping(value = "/report/xml/{reportName}.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public HttpEntity<byte[]> reportXml(@RequestParam(required = false) String filter, @RequestParam(required = false) Long start,
            @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir,
            @RequestParam(required = false) String callback, @RequestParam(required = true) String dtoName,
            @PathVariable String reportName) {

        try {
            Class dtoReportClass= Class.forName(dtoName);
            List<Object> listDtos = service.findByJSONFilters(reportName, filter, page, limit, sort, dir, dtoReportClass);
            Long totalCount = service.countByJSONFilters(reportName, filter, dtoReportClass);

            ResultListCallback resultListCallBack = Util.getResultList(listDtos, totalCount, "Buequeda reporte " + reportName + " realizada...", true);
            String xml = XMLMarshaller.convertObjectToXML(resultListCallBack);
            byte[] documentBody = xml.getBytes();

            HttpHeaders header = new HttpHeaders();
            header.setContentType(new MediaType("application", "xml"));
            header.setContentLength(documentBody.length);

            return new HttpEntity<>(documentBody, header);
        } catch (Exception e) {
            LOGGER.error("find " + entityRef + " - " + reportName, e);
            return null;
        }
    }
    
    @RequestMapping(value = "/report/xls/{reportName}.htm", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public void reportXls(@RequestParam(required = false) String filter, @RequestParam(required = false) Long start,
            @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir,
            @RequestParam(required = true) String dtoName, @PathVariable String reportName, HttpServletResponse response) {
        
        response.setContentType("application/xls");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + "report.xls\"");

        try {
            Class dtoReportClass= Class.forName(dtoName);
            List<Object> listDtos = service.findByJSONFilters(reportName, filter, page, limit, sort, dir, dtoReportClass);
            
            ExcelService.generateExcelReport(listDtos, response.getOutputStream(), dtoReportClass);
        } catch (Exception e) {
            LOGGER.error("find " + entityRef + " - " + reportName, e);
        }
    }

    @RequestMapping(value = "/create.htm", method = RequestMethod.POST)
    @ResponseBody
    public byte[] create(@RequestParam(required= false) String data, HttpServletRequest request) {
        BaseEntity dto = null;

        String resultData;
        try {
            String jsonData= data;
            if(jsonData==null){
                jsonData = IOUtils.toString(request.getInputStream());
            }
            BaseEntity entity = EntityReflection.readEntity(jsonData, entityClass);

            service.create(entity);
            dto = mapper.entityToDto(entity);
            resultData= Util.getOperationCallback(dto, "Creaci&oacute;n de " + entityRef + " realizada...", true);
        } catch (Exception e) {
            LOGGER.error("create " + entityRef, e);
            resultData= Util.getOperationCallback(dto, "Error en creaci&oacute;n de " + entityRef + ": " + e.getMessage(), false);
        }
        return getStringBytes(resultData);
    }

    @RequestMapping(value = "/update.htm", method = {RequestMethod.PUT, RequestMethod.POST})
    @ResponseBody
    public byte[] update(@RequestParam(required= false) String data, HttpServletRequest request) {
        BaseEntity dto = null;
        
        String resultData;
        try {
            String jsonData= data;
            if(jsonData==null){
                jsonData = IOUtils.toString(request.getInputStream());
            }
            JSONObject jsonObject= new JSONObject(jsonData);
            
            Object id = EntityReflection.getParsedFieldValue(entityClass, "id", jsonObject.get("id").toString());
            BaseEntity entity = (BaseEntity) service.loadById(id);
            if(entity!=null){
                EntityReflection.updateEntity(jsonData, entity);

                service.update(entity);
                dto = mapper.entityToDto(entity);
                resultData= Util.getOperationCallback(dto, "Actualizaci&oacute;n de " + entityRef + " realizada...", true);
            }else{
                return this.create(data, request);
            }
        } catch (Exception e) {
            LOGGER.error("update " + entityRef, e);
            resultData= Util.getOperationCallback(dto, "Error en actualizaci&oacute;n de " + entityRef + ": " + e.getMessage(), false);
        }
        return getStringBytes(resultData);
    }
    
    @RequestMapping(value = "/update/byfilter.htm", method = {RequestMethod.PUT, RequestMethod.POST})
    @ResponseBody
    public byte[] updateByFilter(@RequestParam(required= false) String filter, HttpServletRequest request) {
        String resultData;
        try {
            String jsonData= filter;
            if(jsonData==null){
                jsonData = IOUtils.toString(request.getInputStream());
            }
            Integer updatedRecords= service.updateByJSONFilters(jsonData);
            resultData= Util.getOperationCallback(null, "Actualizaci&oacute;n masiva de " + updatedRecords +" " + entityRef + " realizada...", true);
        } catch (Exception e) {
            LOGGER.error("update " + entityRef, e);
            resultData= Util.getOperationCallback(null, "Error en actualizaci&oacute;n masiva de " + entityRef + ": " + e.getMessage(), false);
        }
        return getStringBytes(resultData);
    }

    @RequestMapping(value = "/load.htm", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public byte[] load(@RequestParam String data) {
        JSONObject jsonObject= new JSONObject(data);
        BaseEntity dto = null;

        String resultData;
        try {
            Object id = EntityReflection.getParsedFieldValue(entityClass, "id", jsonObject.get("id").toString());
            BaseEntity entity = (BaseEntity) service.loadById(id);
            dto = mapper.entityToDto(entity);
            resultData= Util.getOperationCallback(dto, "Carga de " + entityRef + " realizada...", true);
        } catch (Exception e) {
            LOGGER.error("load " + entityRef, e);
            resultData= Util.getOperationCallback(dto, "Error en carga de " + entityRef + ": " + e.getMessage(), true);
        }
        return getStringBytes(resultData);
    }

    @RequestMapping(value = "/delete.htm", method = {RequestMethod.DELETE, RequestMethod.GET})
    @ResponseBody
    public String delete(@RequestParam String idEntity) {
        BaseEntity dto = null;

        try {
            Object id = EntityReflection.getParsedFieldValue(entityClass, "id", idEntity);
            BaseEntity entity = (BaseEntity) service.loadById(id);
            dto = mapper.entityToDto(entity);
            service.remove(entity);
            return Util.getOperationCallback(dto, "Eliminaci&oacute;n de " + entityRef + " realizada...", true);
        } catch (Exception e) {
            LOGGER.error("delete " + entityRef, e);
            return Util.getOperationCallback(dto, "Error en eliminaci&oacute;n de " + entityRef + ": " + e.getMessage(), true);
        }
    }
    
    @RequestMapping(value = "/delete/byfilter.htm", method = {RequestMethod.DELETE, RequestMethod.GET})
    @ResponseBody
    public String deleteByFilter(@RequestParam String filter) {
        try {
            List<? extends BaseEntity> listEntities = service.findByJSONFilters(filter, null, null, null, null, null);
            List listDtos = mapper.listEntitiesToListDtos(listEntities);
            
            for(BaseEntity entity: listEntities){
                service.remove(entity);
            }
            return Util.getResultListCallback(listDtos, (long)listDtos.size(),"Eliminaci&oacute;n de " + entityRef + " realizada...", true);
        } catch (Exception e) {
            LOGGER.error("delete " + entityRef, e);
            return Util.getResultListCallback(new ArrayList(), 0L,"Error en eliminaci&oacute;n de " + entityRef + ": " + e.getMessage(), true);
        }
    }
    
    @RequestMapping(value = "/upload/{idEntity}.htm")
    @ResponseBody
    public byte[] upload(HttpServletRequest request, @PathVariable String idEntity) {
        String result="";
        BaseEntity dto;
        //50MB
        long maxFileSize= maxFileSizeToUpload * 1024 * 1024;

        String resultData;
        try {
            Object id = EntityReflection.getParsedFieldValue(entityClass, "id", idEntity);
            
            ServletFileUpload upload = new ServletFileUpload();
            upload.setSizeMax(maxFileSize);
            
            FileItemIterator iterator;

            iterator = upload.getItemIterator(request);
            while (iterator.hasNext()) {
                FileItemStream fileIS = iterator.next();
                if (fileIS.getName()!=null && !fileIS.getName().equals("")){
                    result+= saveFile(fileIS, id);
                }
            }
            
            BaseEntity entity = (BaseEntity) service.loadById(id);
            dto = mapper.entityToDto(entity);
            
            resultData= Util.getOperationCallback(dto, result, true);
        } catch (Exception e) {
            LOGGER.error("upload " + entityRef, e);
            resultData= Util.getOperationCallback(null, "Error en actualizaci&oacute;n de " + entityRef + ": " + e.getMessage(), false);
        }
        return getStringBytes(resultData);
    }
    
    @RequestMapping(value = "/diskupload/{idEntity}.htm")
    @ResponseBody
    public byte[] diskupload(HttpServletRequest request, @PathVariable String idEntity) {
        String result="";
        BaseEntity dto;
        //50MB
        long maxFileSize= maxFileSizeToUpload * 1024 * 1024;

        String resultData;
        try {
            Object id = EntityReflection.getParsedFieldValue(entityClass, "id", idEntity);
            
            FileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setSizeMax(maxFileSize);
            
            List items = upload.parseRequest(request);
            Iterator iterator = items.iterator();
            while (iterator.hasNext()) {
                FileItem item = (FileItem) iterator.next();
                InputStream is= item.getInputStream();
                if(!item.isFormField() && !item.getName().equals("")){
                    if(dtoClass!=null){
                        is= generateResizedImages(item.getFieldName(), item.getName(), item.getContentType(), is, id);
                    }
                    result+= saveFilePart(0, item.getName(), item.getContentType(), (int)item.getSize(), is, id);
                }
            }
            
            BaseEntity entity = (BaseEntity) service.loadById(id);
            dto = mapper.entityToDto(entity);
            
            resultData= Util.getOperationCallback(dto, result, true);
        } catch (Exception e) {
            LOGGER.error("upload " + entityRef, e);
            resultData= Util.getOperationCallback(null, "Error en actualizaci&oacute;n de " + entityRef + ": " + e.getMessage(), false);
        }
        return getStringBytes(resultData);
    }
    
    protected InputStream generateResizedImages(String fieldName, String fileName, String contentType, InputStream is, Object idParent) throws IOException{
        List<Field> fields= EntityReflection.getEntityAnnotatedFields(dtoClass, ImageResize.class);
        for(Field f: fields){
            if(f.getName().equals(fieldName)){
                ImageResize an= f.getAnnotation(ImageResize.class);
                String[] values= an.value();
                BufferedImage original = ImageIO.read(is);
                for(String value: values){
                    String[] dimensions= value.split(",");
                    int width= Integer.parseInt(dimensions[0]);
                    int height= Integer.parseInt(dimensions[1]);
                    BufferedImage imageR= FileService.resizeImage(original, width, height);
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    ImageIO.write(imageR, contentType.split("/")[1], os);
                    InputStream resizedIs = new ByteArrayInputStream(os.toByteArray());
                    saveResizedImage(fileName, contentType, width, height, os.size(), resizedIs, idParent);
                }
                return FileService.bufferedImageToInputStream(original, contentType);
            }
        }
        return is;
    }
    
    @RequestMapping(value = "/multipartupload/{idParent}.htm")
    @ResponseBody
    public String multipartupload(HttpServletRequest request, @PathVariable String idParent) {
        JSONObject result= new JSONObject();
        long maxFileSize= maxFileSizeToUpload * 1024 * 1024;

        try {
            FileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setSizeMax(maxFileSize);

            int slice = 0;
            String fileName="";
            String fileType="";
            int fileSize = 0;
            InputStream filePart = null;
            
            List items = upload.parseRequest(request);
            Iterator iterator = items.iterator();
            while (iterator.hasNext()) {
                FileItem item = (FileItem) iterator.next();
                InputStream is= item.getInputStream();

                if (item.isFormField()) {
                    switch (item.getFieldName()) {
                        case "slice":
                            slice= Integer.parseInt(Streams.asString(is));
                            break;
                        case "fileName":
                            fileName= Streams.asString(is);
                            break;
                        case "fileType":
                            fileType= Streams.asString(is);
                            break;
                        case "fileSize":
                            fileSize= Integer.parseInt(Streams.asString(is));
                            break;
                    }
                }else{
                    filePart= is;
                }
            }

            LOGGER.info("IN MULTIPARTDATA: "+slice+" "+fileName+" "+fileType+" "+fileSize);
            String message= saveFilePart(slice, fileName, fileType, fileSize, filePart, idParent);
            
            result.put("slice", slice);
            result.put("fileName", fileName);
            result.put("fileType", fileType);
            result.put("message", message);
            result.put("success", true);
            
            return result.toString();
        } catch (Exception e) {
            LOGGER.error("upload " + entityRef, e);
            return "{message:\"Error en cargue de archivos\", success:false}";
        }
    }
    
    @RequestMapping(value = "/getContentFile.htm")
    @ResponseBody
    public byte[] getContentFile(@RequestParam(required = true) String fileUrl) {
        String content="";
        try {
            String pathFile= fileUrl.replace(LOCAL_DOMAIN, LOCAL_DIR);
            content= FileService.getTextFile(pathFile);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(ExtFileExplorerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return getStringBytes(content);
    }
    
    @RequestMapping(value = "/setContentFile.htm")
    @ResponseBody
    public String setContentFile(@RequestParam(required = true) String fileUrl, @RequestParam(required = true) String content) {
        try {
            String pathFile= fileUrl.replace(LOCAL_DOMAIN, LOCAL_DIR);
            FileService.setTextFile(content, pathFile);
            return "Contenido guardado";
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(ExtFileExplorerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return "Error al guardar";
    }
    
    protected byte[] getStringBytes(String data){
        try {
            return data.getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            LOGGER.error("getStringBytes", ex);
            return null;
        }
    }
    
    protected String saveFile(FileItemStream fileIS, Object idEntity){
        // ABSTRACT CODE HERE
        return "Almacenamiento de archivo no implementado!!";
    }
    
    protected String saveFilePart(int slice, String fileName, String fileType, int fileSize, InputStream is, Object idParent){
        // ABSTRACT CODE HERE
        return "Almacenamiento de archivo no implementado!!";
    }
    
    protected String saveResizedImage(String fileName, String fileType, int width, int height, int fileSize, InputStream is, Object idParent){
        // ABSTRACT CODE HERE
        return "Almacenamiento de archivo no implementado!!";
    }

    /**
     * @param maxFileSizeToUpload the maxFileSizeToUpload to set in MB
     */
    protected void setMaxFileSizeToUpload(Long maxFileSizeToUpload) {
        this.maxFileSizeToUpload = maxFileSizeToUpload;
    }

    protected String generateTemplateData(List<Object> listDtos, Long totalCount, String entityRef,
            boolean success, String templateName, Long numColumns) {
        
        List<ItemTemplate> items= new ArrayList<>();
        ItemTemplate item= new ItemTemplate();
        
        int index= 0;
        for(Object dto: listDtos){
            StringWriter sw = new StringWriter();
            Template vtemplate = velocityEngine.getTemplate(TEMPLATES_DIR + templateName);
            VelocityContext context= new VelocityContext();
            context.put("contextPath", selvletContext.getContextPath());
            context.put("item", dto);
            
            vtemplate.merge(context, sw);
            
            switch(index){
                case 0:
                    item.setColumn0(sw.toString());
                    break;
                case 1:
                    item.setColumn1(sw.toString());
                    break;
                case 2:
                    item.setColumn2(sw.toString());
                    break;
                case 3:
                    item.setColumn3(sw.toString());
                    break;
                case 4:
                    item.setColumn4(sw.toString());
                    break;
                case 5:
                    item.setColumn5(sw.toString());
                    break;
                case 6:
                    item.setColumn6(sw.toString());
                    break;
                case 7:
                    item.setColumn7(sw.toString());
                    break;
                case 8:
                    item.setColumn8(sw.toString());
                    break;
                case 9:
                    item.setColumn9(sw.toString());
                    break;
            }
            index++;
            
            if(index==numColumns){
                items.add(item);
                index=0;
                item= new ItemTemplate();
            }
        }
        if(index>0 && index<numColumns){
            items.add(item);
        }
        
        return Util.getResultListCallback(items, totalCount, "Busqueda de " + entityRef + " realizada...", success);
    }
    
}
