package com.dot.gcpbasedot.controller;

import com.dot.gcpbasedot.annotation.ImageResize;
import com.dot.gcpbasedot.components.FieldConfigurationByAnnotations;
import com.dot.gcpbasedot.domain.BaseEntity;
import com.dot.gcpbasedot.dto.ItemTemplate;
import com.dot.gcpbasedot.reflection.EntityReflection;
import com.dot.gcpbasedot.service.EntityService;
import com.dot.gcpbasedot.util.Util;
import com.dot.gcpbasedot.util.XMLMarshaller;
import com.dot.gcpbasedot.dto.ResultListCallback;
import com.dot.gcpbasedot.enums.FieldType;
import com.dot.gcpbasedot.interfaces.WebEntityInterface;
import com.dot.gcpbasedot.mapper.EntityMapper;
import com.dot.gcpbasedot.util.CSVService;
import com.dot.gcpbasedot.util.ExcelService;
import com.dot.gcpbasedot.util.FileService;
import com.dot.gcpbasedot.util.JSONService;
import java.awt.image.BufferedImage;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;

@Controller
public abstract class RestEntityController {

    protected static final Logger LOGGER = Logger.getLogger(RestEntityController.class);
    
    protected String entityRef= null;

    protected EntityService service;

    protected EntityMapper mapper;
    
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
    
    @Autowired
    public FieldConfigurationByAnnotations fcba;
    

    protected void addControlMapping(String entityRef, EntityService entityService, EntityMapper entityMapper) {
        this.entityRef= entityRef;
        this.service=  entityService;
        this.mapper= entityMapper;
        this.entityClass= service.getEntityClass();
        this.dtoClass= mapper.getDtoClass();
    }
    
    @RequestMapping(value = "/find.htm", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public HttpEntity<byte[]> find(@RequestParam(required = false) String filter, @RequestParam(required = false) String query,
            @RequestParam(required = false) Long start,  @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir,
            @RequestParam(required = false) String templateName, @RequestParam(required = false) Long numColumns) {

        String resultData;
        try {
            List<BaseEntity> listEntities = service.findByJSONFilters(filter, query, page, limit, sort, dir);
            List listDtos = mapper.listEntitiesToListDtos(listEntities);
            Long totalCount = service.countByJSONFilters(filter, query);
            
            if(templateName!=null){
                resultData= generateTemplateData(listDtos, totalCount, entityRef, true, templateName, numColumns);
            }else{
                resultData=Util.getResultListCallback(listDtos, totalCount, "Busqueda de " + entityRef + " realizada...", true);
                resultData= cleanTimeInDateField(resultData, dtoClass);
            }
        } catch (Exception e) {
            LOGGER.error("find " + entityRef, e);
            resultData=Util.getResultListCallback(new ArrayList(), "Error buscando " + entityRef + ": " + e.getMessage(), false);
        }

        return Util.getHttpEntityBytes(resultData, "json");
    }

    @RequestMapping(value = "/find/xml.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public HttpEntity<byte[]> findXml(@RequestParam(required = false) String filter, @RequestParam(required = false) String query,
            @RequestParam(required = false) Long start, @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir) {

        try {
            List<BaseEntity> listEntities = service.findByJSONFilters(filter, query, page, limit, sort, dir);
            List<BaseEntity> listDtos = mapper.listEntitiesToListDtos(listEntities);
            Long totalCount = service.countByJSONFilters(filter, query);

            String resultData = Util.getResultListCallback(listDtos, totalCount, "Busqueda de " + entityRef + " realizada...", true);
            resultData= cleanTimeInDateField(resultData, dtoClass);
            String xml = XMLMarshaller.convertJSONToXML(resultData, ResultListCallback.class.getSimpleName());
            
            return Util.getHttpEntityBytes(xml, "xml");
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
        response.setHeader("Content-Disposition", "attachment; filename=\"" + entityRef + "_report.xls\"");

        try {
            List<Object> listEntities = service.findByJSONFilters(filter, query, page, limit, sort, dir);
            
            ExcelService.generateExcelReport(listEntities, response.getOutputStream(), dtoClass);
        } catch (Exception e) {
            LOGGER.error("find " + entityRef, e);
        }
    }
    
    @RequestMapping(value = "/find/csv.htm", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public void findCsv(@RequestParam(required = false) String filter, @RequestParam(required = false) String query, 
            @RequestParam(required = false) Long start, @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir,
            HttpServletResponse response) {
        
        response.setContentType("text/csv; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + entityRef + "_report.csv\"");

        try {
            List<Object> listEntities = service.findByJSONFilters(filter, query, page, limit, sort, dir);
            
            response.getWriter().print(CSVService.generateCSVReport(listEntities, dtoClass));
        } catch (Exception e) {
            LOGGER.error("find " + entityRef, e);
        }
    }
    
    @RequestMapping(value = "/find/yaml.htm", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public byte[] findYaml(@RequestParam(required = false) String filter, @RequestParam(required = false) String query,
            @RequestParam(required = false) Long start, @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir,
            @RequestParam(required = false) Boolean yamlFormat) {

        String resultData;
        try {
            List<BaseEntity> listEntities = service.findByJSONFilters(filter, query, page, limit, sort, dir);
            List listDtos = mapper.listEntitiesToListDtos(listEntities);
            Long totalCount = service.countByJSONFilters(filter, query);
            
            resultData = Util.getResultListCallback(listDtos, totalCount, "Busqueda de " + entityRef + " realizada...", true);
            resultData= cleanTimeInDateField(resultData, dtoClass);
            resultData= JSONService.jsonToYaml(resultData);
        } catch (Exception e) {
            LOGGER.error("find " + entityRef, e);
            resultData=Util.getResultListCallback(new ArrayList(), "Error buscando " + entityRef + ": " + e.getMessage(), false);
            resultData= JSONService.jsonToYaml(resultData);
        }
        
        if(yamlFormat!=null && yamlFormat){
            resultData= "<textarea style='width:100%; height:100%;color:darkblue'>"+resultData+"</textarea>";
        }
        
        return Util.getStringBytes(resultData);
    }
    
    @RequestMapping(value = "/report/{reportName}.htm", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public HttpEntity<byte[]> report(@RequestParam(required = false) String filter, @RequestParam(required = false) Long start,
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
                resultData= cleanTimeInDateField(resultData, dtoReportClass);
            }
        } catch (Exception e) {
            LOGGER.error("find " + entityRef + " - " + reportName, e);
            resultData= Util.getResultListCallback(new ArrayList(), "Error reporte " + reportName + ": " + e.getMessage(), false);
        }
        
        return Util.getHttpEntityBytes(resultData, "json");
    }
    
    @RequestMapping(value = "/report/xml/{reportName}.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public HttpEntity<byte[]> reportXml(@RequestParam(required = false) String filter, @RequestParam(required = false) Long start,
            @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir,
            @RequestParam(required = true) String dtoName,
            @PathVariable String reportName) {

        try {
            Class dtoReportClass= Class.forName(dtoName);
            List<Object> listDtos = service.findByJSONFilters(reportName, filter, page, limit, sort, dir, dtoReportClass);
            Long totalCount = service.countByJSONFilters(reportName, filter, dtoReportClass);

            String resultData = Util.getResultListCallback(listDtos, totalCount, "Buequeda reporte " + reportName + " realizada...", true);
            resultData= cleanTimeInDateField(resultData, dtoClass);
            String xml = XMLMarshaller.convertJSONToXML(resultData, ResultListCallback.class.getSimpleName());
            
            return Util.getHttpEntityBytes(xml, "xml");
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
        response.setHeader("Content-Disposition", "attachment; filename=\"" + reportName + "_report.xls\"");

        try {
            Class dtoReportClass= Class.forName(dtoName);
            List<Object> listDtos = service.findByJSONFilters(reportName, filter, null, null, sort, dir, dtoReportClass);
            
            ExcelService.generateExcelReport(listDtos, response.getOutputStream(), dtoReportClass);
        } catch (Exception e) {
            LOGGER.error("find " + entityRef + " - " + reportName, e);
        }
    }
    
    @RequestMapping(value = "/report/csv/{reportName}.htm", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public void reportCsv(@RequestParam(required = false) String filter, @RequestParam(required = false) Long start,
            @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir,
            @RequestParam(required = true) String dtoName, @PathVariable String reportName, HttpServletResponse response) {
        
        response.setContentType("text/csv; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + reportName + "_report.csv\"");

        try {
            Class dtoReportClass= Class.forName(dtoName);
            List<Object> listDtos = service.findByJSONFilters(reportName, filter, null, null, sort, dir, dtoReportClass);
            
            response.getWriter().print(CSVService.generateCSVReport(listDtos, dtoReportClass));
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
            updateRelatedWebEntity(entity, request);
            resultData= Util.getOperationCallback(dto, "Creaci&oacute;n de " + entityRef + " realizada...", true);
        } catch (Exception e) {
            LOGGER.error("create " + entityRef, e);
            resultData= Util.getOperationCallback(dto, "Error en creaci&oacute;n de " + entityRef + ": " + e.getMessage(), false);
        }
        return Util.getStringBytes(resultData);
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
            if(id!=null){
                BaseEntity entity = (BaseEntity) service.loadById(id);
                if(entity!=null){
                    EntityReflection.updateEntity(jsonData, entity);

                    service.update(entity);
                    dto = mapper.entityToDto(entity);
                    updateRelatedWebEntity(entity, request);
                    resultData= Util.getOperationCallback(dto, "Actualizaci&oacute;n de " + entityRef + " realizada...", true);
                }else{
                    return this.create(data, request);
                }
            }else{
                return this.create(data, request);
            }
        } catch (Exception e) {
            LOGGER.error("update " + entityRef, e);
            resultData= Util.getOperationCallback(dto, "Error en actualizaci&oacute;n de " + entityRef + ": " + e.getMessage(), false);
        }
        return Util.getStringBytes(resultData);
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
        return Util.getStringBytes(resultData);
    }

    @RequestMapping(value = "/load.htm", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public byte[] load(@RequestParam String idEntity) {
        BaseEntity dto = null;

        String resultData;
        try {
            Object id = EntityReflection.getParsedFieldValue(entityClass, "id", idEntity);
            BaseEntity entity = (BaseEntity) service.loadById(id);
            dto = mapper.entityToDto(entity);
            resultData= Util.getOperationCallback(dto, "Carga de " + entityRef + " realizada...", true);
        } catch (Exception e) {
            LOGGER.error("load " + entityRef, e);
            resultData= Util.getOperationCallback(dto, "Error en carga de " + entityRef + ": " + e.getMessage(), true);
        }
        return Util.getStringBytes(resultData);
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
            List<BaseEntity> listEntities = service.findByJSONFilters(filter, null, null, null, null, null);
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
    
    @RequestMapping(value = "/import/{format}.htm")
    @ResponseBody
    public byte[] importData(HttpServletRequest request, @PathVariable String format) {
        List listDtos= new ArrayList();
        //50MB
        long maxFileSize= maxFileSizeToUpload * 1024 * 1024;

        String resultData;
        int totalRecords=0;
        try {
            
            FileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setSizeMax(maxFileSize);
            
            List items = upload.parseRequest(request);
            Iterator iterator = items.iterator();
            while (iterator.hasNext()) {
                FileItem item = (FileItem) iterator.next();
                InputStream is= item.getInputStream();
                if(!item.isFormField() && item.getFieldName().equals("data")){
                    String data, csvData, xlsData, jsonData=null;
                    List<BaseEntity> entities= new ArrayList<>();
                    JSONArray array;
                    switch(format){
                        case "csv":
                            data= FileService.getLinesFromInputStream(is);
                            csvData= CSVService.csvRecordsToJSON(data, dtoClass);
                            array= new JSONArray(csvData);
                            for (int i = 0; i < array.length(); i++) {
                                entities.add((BaseEntity) EntityReflection.readEntity(array.getJSONObject(i).toString(), entityClass));
                            }
                            break;
                        case "xls":
                            xlsData= ExcelService.xlsTableToJSON(is, dtoClass);
                            array= new JSONArray(xlsData);
                            for (int i = 0; i < array.length(); i++) {
                                entities.add((BaseEntity) EntityReflection.readEntity(array.getJSONObject(i).toString(), entityClass));
                            }
                            break;
                        case "xml":
                            data= FileService.getStringFromInputStream(is);
                            jsonData= XMLMarshaller.convertXMLToJSON(data);
                        case "json":
                            JSONObject object= new JSONObject(jsonData);
                            array= object.getJSONArray("data");
                            for (int i = 0; i < array.length(); i++) {
                                entities.add((BaseEntity) EntityReflection.jsonToObject(array.getJSONObject(i).toString(), entityClass));
                            }
                            break;
                    }
                    
                    //Buscar entidades existentes
                    List ids= new ArrayList<>();
                    for(BaseEntity newEntity: entities){
                        ids.add(newEntity.getId());
                    }
                    List<BaseEntity> existingEntities = service.listAllByIds(ids);
                    Map<Object, BaseEntity> mapExistingEntities= new HashMap();
                    for(BaseEntity entity: existingEntities){
                        mapExistingEntities.put(entity.getId(), entity);
                    }
                    
                    //Insertar o actualizar la entidad
                    for(BaseEntity entity: entities){
                        try{
                            if(!mapExistingEntities.containsKey(entity.getId())){
                                service.insert(entity);
                            }else{
                                BaseEntity existingEntity= mapExistingEntities.get(entity.getId());
                                EntityReflection.updateEntity(entity, existingEntity);
                                service.update(existingEntity);
                            }
                            listDtos.add(entity);
                            totalRecords++;
                        }catch(Exception e){
                            LOGGER.error("importData " + entityRef, e);
                        }
                    }
                }
            }
            
            resultData= Util.getResultListCallback(listDtos, (long)listDtos.size(),"Importaci&oacute;n de "+totalRecords+" registros tipo " + entityRef + " finalizada...", true);
        } catch (Exception e) {
            LOGGER.error("importData " + entityRef, e);
            resultData= Util.getOperationCallback(null, "Error en importaci&oacute;n de registros tipo " + entityRef + ": " + e.getMessage(), false);
        }
        return Util.getStringBytes(resultData);
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
        return Util.getStringBytes(resultData);
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
                    String fieldName= item.getFieldName().replaceAll("_File", "");
                    if(dtoClass!=null){
                        is= generateResizedImages(fieldName, item.getName(), item.getContentType(), is, id);
                    }
                    result+= saveFilePart(0, fieldName, item.getName(), item.getContentType(), (int)item.getSize(), is, id)+"<br>";
                }
            }
            
            BaseEntity entity = (BaseEntity) service.loadById(id);
            dto = mapper.entityToDto(entity);
            
            resultData= Util.getOperationCallback(dto, result, true);
        } catch (Exception e) {
            LOGGER.error("upload " + entityRef, e);
            resultData= Util.getOperationCallback(null, "Error en actualizaci&oacute;n de " + entityRef + ": " + e.getMessage(), false);
        }
        return Util.getStringBytes(resultData);
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
                    saveResizedImage(fieldName, fileName, contentType, width, height, os.size(), resizedIs, idParent);
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
            String fieldName="";
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
                    fieldName= item.getFieldName();
                    filePart= is;
                }
            }

            LOGGER.info("IN MULTIPARTDATA: "+slice+" "+fileName+" "+fileType+" "+fileSize);
            String message= saveFilePart(slice, fieldName, fileName, fileType, fileSize, filePart, idParent);
            
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
            content= (pathFile.startsWith(LOCAL_DIR))?FileService.getTextFile(pathFile):"";
        } catch (IOException ex) {
            LOGGER.error("getContentFile ",ex);
        }
        return Util.getStringBytes(content);
    }
    
    @RequestMapping(value = "/setContentFile.htm")
    @ResponseBody
    public String setContentFile(@RequestParam(required = true) String fileUrl, @RequestParam(required = true) String content) {
        try {
            String pathFile= fileUrl.replace(LOCAL_DOMAIN, LOCAL_DIR);
            if(pathFile.startsWith(LOCAL_DIR)){
                FileService.setTextFile(content, pathFile);
                return "Contenido guardado";
            }else{
                return "El contenido no pudo ser guardado";
            }
        } catch (IOException ex) {
            LOGGER.error("setContentFile ",ex);
        }
        
        return "Error al guardar";
    }
    
    private void updateRelatedWebEntity(BaseEntity entity, HttpServletRequest request){
        try{
            if(request.getParameter("webEntityId")!=null){
                Long webEntityId= Long.parseLong(request.getParameter("webEntityId"));
                ApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
                if(ctx.getBean("webEntityService")!=null){
                    EntityService entityService = (EntityService) ctx.getBean("webEntityService");
                    WebEntityInterface webEntity= (WebEntityInterface) entityService.loadById(webEntityId);
                    webEntity.setEntityId(""+entity.getId());
                    webEntity.setModificationDate(new Date());
                    entityService.update(webEntity);
                }
            }
        }catch(Exception e){
            LOGGER.error("updateRelatedWebEntity " + entityRef, e);
        }
    }
    
    protected String saveFile(FileItemStream fileIS, Object idEntity){
        // ABSTRACT CODE HERE
        return "Almacenamiento de archivo no implementado!!";
    }
    
    protected String saveFilePart(int slice, String fieldName, String fileName, String fileType, int fileSize, InputStream is, Object idParent){
        // ABSTRACT CODE HERE
        return "Almacenamiento de archivo no implementado!!";
    }
    
    protected String saveResizedImage(String fieldName, String fileName, String fileType, int width, int height, int fileSize, InputStream is, Object idParent){
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
    
    private String cleanTimeInDateField(String data, Class dtoClass){
        PropertyDescriptor[] propertyDescriptors = EntityReflection.getPropertyDescriptors(dtoClass);
        Set<String> datetimeFields= new HashSet<>();
        HashMap<String,String[]> typeFormFields= fcba.getTypeFormFields(dtoClass);
        for (Map.Entry<String, String[]> entry : typeFormFields.entrySet()) {
            String typeForm= entry.getValue()[0];
            if(typeForm.equals(FieldType.DATETIME.name())){
                datetimeFields.add(entry.getKey());
            }
        }
        List<String> dateFields= new ArrayList<>();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String fieldName= propertyDescriptor.getName();
            String type = propertyDescriptor.getPropertyType().getName();
            if(type.equals("java.util.Date") && !datetimeFields.contains(fieldName)){
                dateFields.add(fieldName);
            }
        }
        if(dateFields.size()>0){
            JSONObject result= new JSONObject(data);
            for(int i=0; i<result.getJSONArray("data").length(); i++){
                JSONObject entityJson= result.getJSONArray("data").getJSONObject(i);
                for(int j=0; j<dateFields.size(); j++){
                    String fieldName= dateFields.get(j);
                    if(!entityJson.isNull(fieldName)){
                        String date= entityJson.getString(fieldName);
                        result.getJSONArray("data").getJSONObject(i).put(fieldName, date.split(" ")[0]);
                    }
                }
            }
            return result.toString();
        }
        return data;
    }
    
}
