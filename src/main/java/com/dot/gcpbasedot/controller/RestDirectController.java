package com.dot.gcpbasedot.controller;

import com.dot.gcpbasedot.components.TableColumnsConfig;
import com.dot.gcpbasedot.dao.Parameters;
import com.dot.gcpbasedot.reflection.EntityReflection;
import com.dot.gcpbasedot.util.Util;
import com.dot.gcpbasedot.util.XMLMarshaller;
import com.dot.gcpbasedot.dto.ResultListCallback;
import com.dot.gcpbasedot.dto.GenericTableColumn;
import com.dot.gcpbasedot.service.JdbcDirectService;
import com.dot.gcpbasedot.util.ExcelService;
import com.dot.gcpbasedot.util.FileService;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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
public abstract class RestDirectController {

    protected static final Logger LOGGER = Logger.getLogger(RestDirectController.class);

    @Autowired
    protected JdbcDirectService directService;
    
    @Autowired
    private TableColumnsConfig tableColumnsConfig;
    
    private Long maxFileSizeToUpload=1024L;
    

    @RequestMapping(value = "/{tableName}/find.htm", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public byte[] find(@PathVariable String tableName, @RequestParam(required = false) String filter, @RequestParam(required = false) Long start,
            @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir,
            @RequestParam(required = false) Long numColumns) {

        String resultData;
        try {
            List<GenericTableColumn> columns= tableColumnsConfig.getColumnsFromTableName(tableName);
            List<Map<String, Object>> listItems = directService.findByJSONFilters(tableName, columns, filter, page, limit, sort, dir);
            Long totalCount = directService.countByJSONFilters(tableName, columns, filter);
            
            resultData=Util.getResultListCallback(listItems, totalCount, "Busqueda de " + tableName + " realizada...", true);
        } catch (Exception e) {
            LOGGER.error("find " + tableName, e);
            resultData=Util.getResultListCallback(new ArrayList(), "Error buscando " + tableName + ": " + e.getMessage(), false);
        }
        
        return getStringBytes(resultData);
    }

    @RequestMapping(value = "/{tableName}/find/xml.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public HttpEntity<byte[]> findXml(@PathVariable String tableName, @RequestParam(required = false) String filter, @RequestParam(required = false) Long start,
            @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir) {

        try {
            List<GenericTableColumn> columns= tableColumnsConfig.getColumnsFromTableName(tableName);
            List<Map<String, Object>> listItems = directService.findByJSONFilters(tableName, columns, filter, page, limit, sort, dir);
            Long totalCount = directService.countByJSONFilters(tableName, columns, filter);

            ResultListCallback resultListCallBack = Util.getResultList(listItems, totalCount, "Busqueda de " + tableName + " realizada...", true);
            String xml = XMLMarshaller.convertObjectToXML(resultListCallBack);
            byte[] documentBody = xml.getBytes();

            HttpHeaders header = new HttpHeaders();
            header.setContentType(new MediaType("application", "xml"));
            header.setContentLength(documentBody.length);

            return new HttpEntity<>(documentBody, header);
        } catch (Exception e) {
            LOGGER.error("find " + tableName, e);
            return null;
        }
    }

    @RequestMapping(value = "/{tableName}/find/xls.htm", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public void findXls(@PathVariable String tableName, @RequestParam(required = false) String filter, @RequestParam(required = false) Long start,
            @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir,
            HttpServletResponse response) {
        
        response.setContentType("application/xls");
        response.setHeader("Content-Disposition", "attachment; filename=\""+ tableName + "_report.xls\"");

        try {
            List<GenericTableColumn> columns= tableColumnsConfig.getColumnsFromTableName(tableName);
            List<Map<String, Object>> listItems = directService.findByJSONFilters(tableName, columns, filter, page, limit, sort, dir);
            
            ExcelService.generateExcelReport(listItems, response.getOutputStream(), columns);
        } catch (Exception e) {
            LOGGER.error("find " + tableName, e);
        }
    }

    @RequestMapping(value = "/{tableName}/create.htm", method = RequestMethod.POST)
    @ResponseBody
    public byte[] create(@PathVariable String tableName, @RequestParam(required= false) String data, HttpServletRequest request) {
        String resultData;
        try {
            List<GenericTableColumn> columns= tableColumnsConfig.getColumnsFromTableName(tableName);
            String jsonData= data;
            if(jsonData==null){
                jsonData = IOUtils.toString(request.getInputStream());
            }
            Map<String, Object> entity = EntityReflection.readEntity(jsonData, columns);

            directService.create(tableName, entity);
            
            Parameters p= new Parameters();
            for (Map.Entry<String,Object> entry : entity.entrySet()){
                p.whereEqual(entry.getKey(), entry.getValue());
            }
            p.orderBy("id", "DESC");
            List<Map<String, Object>> recover= directService.findByParameters(tableName, p);
            if(recover.size()>0){
                entity= recover.get(0);
            }
            
            resultData= Util.getOperationCallback(entity, "Creaci&oacute;n de " + tableName + " realizada...", true);
        } catch (Exception e) {
            LOGGER.error("create " + tableName, e);
            resultData= Util.getOperationCallback(null, "Error en creaci&oacute;n de " + tableName + ": " + e.getMessage(), false);
        }
        return getStringBytes(resultData);
    }

    @RequestMapping(value = "/{tableName}/update.htm", method = {RequestMethod.PUT, RequestMethod.POST})
    @ResponseBody
    public byte[] update(@PathVariable String tableName, @RequestParam(required= false) String data, HttpServletRequest request) {
        String resultData;
        try {
            List<GenericTableColumn> columns= tableColumnsConfig.getColumnsFromTableName(tableName);
            String jsonData= data;
            if(jsonData==null){
                jsonData = IOUtils.toString(request.getInputStream());
            }
            JSONObject jsonObject= new JSONObject(jsonData);
            
            Map<String, Object> entity = directService.loadByParameter(tableName, "id", jsonObject.getInt("id"));
            if(entity!=null){
                EntityReflection.updateEntity(jsonData, entity, columns);

                directService.updateByParameter(tableName, entity, "id", jsonObject.getInt("id"));
                resultData= Util.getOperationCallback(entity, "Actualizaci&oacute;n de " + tableName + " realizada...", true);
            }else{
                return this.create(tableName, data, request);
            }
        } catch (Exception e) {
            LOGGER.error("update " + tableName, e);
            resultData= Util.getOperationCallback(null, "Error en actualizaci&oacute;n de " + tableName + ": " + e.getMessage(), false);
        }
        return getStringBytes(resultData);
    }
    
    /*@RequestMapping(value = "/{tableName}/update/byfilter.htm", method = {RequestMethod.PUT, RequestMethod.POST})
    @ResponseBody
    public byte[] updateByFilter(@PathVariable String tableName, @RequestParam(required= false) String filter, HttpServletRequest request) {
        String resultData;
        try {
            String jsonData= filter;
            if(jsonData==null){
                jsonData = IOUtils.toString(request.getInputStream());
            }
            Integer updatedRecords= directService.updateByJSONFilters(tableName, jsonData);
            resultData= Util.getOperationCallback(null, "Actualizaci&oacute;n masiva de " + updatedRecords +" " + entityRef + " realizada...", true);
        } catch (Exception e) {
            LOGGER.error("update " + entityRef, e);
            resultData= Util.getOperationCallback(null, "Error en actualizaci&oacute;n masiva de " + entityRef + ": " + e.getMessage(), false);
        }
        return getStringBytes(resultData);
    }*/

    @RequestMapping(value = "/{tableName}/load.htm", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public byte[] load(@PathVariable String tableName, @RequestParam String data) {
        JSONObject jsonObject= new JSONObject(data);

        String resultData;
        try {
            Map<String, Object> entity = directService.loadByParameter(tableName, "id", jsonObject.getInt("id"));
            
            resultData= Util.getOperationCallback(entity, "Carga de " + tableName + " realizada...", true);
        } catch (Exception e) {
            LOGGER.error("load " + tableName, e);
            resultData= Util.getOperationCallback(null, "Error en carga de " + tableName + ": " + e.getMessage(), true);
        }
        return getStringBytes(resultData);
    }

    @RequestMapping(value = "/{tableName}/delete.htm", method = {RequestMethod.DELETE, RequestMethod.GET})
    @ResponseBody
    public String delete(@PathVariable String tableName, @RequestParam String idEntity) {
        try {
            Map<String, Object> entity = directService.loadByParameter(tableName, "id", idEntity);
            
            directService.removeByParameter(tableName, "id", idEntity);
            return Util.getOperationCallback(entity, "Eliminaci&oacute;n de " + tableName + " realizada...", true);
        } catch (Exception e) {
            LOGGER.error("delete " + tableName, e);
            return Util.getOperationCallback(null, "Error en eliminaci&oacute;n de " + tableName + ": " + e.getMessage(), true);
        }
    }
    
    @RequestMapping(value = "/{tableName}/delete/byfilter.htm", method = {RequestMethod.DELETE, RequestMethod.GET})
    @ResponseBody
    public String deleteByFilter(@PathVariable String tableName, @RequestParam String filter) {
        try {
            List<GenericTableColumn> columns= tableColumnsConfig.getColumnsFromTableName(tableName);
            List<Map<String, Object>> listItems = directService.findByJSONFilters(tableName, columns, filter, null, null, null, null);
            
            for(Map<String, Object> entity: listItems){
                directService.removeByParameter(tableName, "id", entity.get("id"));
            }
            return Util.getResultListCallback(listItems, (long)listItems.size(),"Eliminaci&oacute;n de " + tableName + " realizada...", true);
        } catch (Exception e) {
            LOGGER.error("delete " + tableName, e);
            return Util.getResultListCallback(new ArrayList(), 0L,"Error en eliminaci&oacute;n de " + tableName + ": " + e.getMessage(), true);
        }
    }
    
    @RequestMapping(value = "/{tableName}/import/{format}.htm")
    @ResponseBody
    public byte[] importData(HttpServletRequest request, @PathVariable String tableName, @PathVariable String format) {
        List listDtos= new ArrayList();
        //50MB
        long maxFileSize= maxFileSizeToUpload * 1024 * 1024;

        String resultData;
        try {
            List<GenericTableColumn> columns= tableColumnsConfig.getColumnsFromTableName(tableName);
            FileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setSizeMax(maxFileSize);
            
            List items = upload.parseRequest(request);
            Iterator iterator = items.iterator();
            while (iterator.hasNext()) {
                FileItem item = (FileItem) iterator.next();
                InputStream is= item.getInputStream();
                if(!item.isFormField() && item.getFieldName().equals("data")){
                    String data= FileService.getStringFromInputStream(is);
                    List<Map<String, Object>> entities= new ArrayList<>();
                    switch(format){
                        case "xml":
                            data= XMLMarshaller.convertXMLToJSON(data);
                        case "json":
                            JSONObject object= new JSONObject(data);
                            JSONArray array= object.getJSONArray("data");
                            for (int i = 0; i < array.length(); i++) {
                                entities.add(EntityReflection.readEntity(array.getJSONObject(i).toString(), columns));
                            }
                            break;
                    }
                    for(Map<String, Object> entity: entities){
                        try{
                            directService.create(tableName, entity);
                            listDtos.add(entity);
                        }catch(Exception e){
                            LOGGER.error("importData " + tableName, e);
                        }
                    }
                }
            }
            
            resultData= Util.getResultListCallback(listDtos, (long)listDtos.size(),"Inserci&oacute;n de " + tableName + " realizada...", true);
        } catch (Exception e) {
            LOGGER.error("importData " + tableName, e);
            resultData= Util.getOperationCallback(null, "Error en inserci&oacute;n de " + tableName + ": " + e.getMessage(), false);
        }
        return getStringBytes(resultData);
    }
    
    @RequestMapping(value = "/{tableName}/upload/{idEntity}.htm")
    @ResponseBody
    public byte[] upload(HttpServletRequest request, @PathVariable String tableName, @PathVariable String idEntity) {
        String result="";
        //50MB
        long maxFileSize= maxFileSizeToUpload * 1024 * 1024;

        String resultData;
        try {
            Object id = Integer.parseInt(idEntity);
            
            ServletFileUpload upload = new ServletFileUpload();
            upload.setSizeMax(maxFileSize);
            
            FileItemIterator iterator;

            iterator = upload.getItemIterator(request);
            while (iterator.hasNext()) {
                FileItemStream fileIS = iterator.next();
                if (fileIS.getName()!=null && !fileIS.getName().equals("")){
                    result+= saveFile(tableName, fileIS, id);
                }
            }
            
            Map<String, Object> entity = directService.loadByParameter(tableName, "id", id);
            
            resultData= Util.getOperationCallback(entity, result, true);
        } catch (Exception e) {
            LOGGER.error("upload " + tableName, e);
            resultData= Util.getOperationCallback(null, "Error en actualizaci&oacute;n de " + tableName + ": " + e.getMessage(), false);
        }
        return getStringBytes(resultData);
    }
    
    @RequestMapping(value = "/{tableName}/diskupload/{idEntity}.htm")
    @ResponseBody
    public byte[] diskupload(HttpServletRequest request, @PathVariable String tableName, @PathVariable String idEntity) {
        String result="";
        //50MB
        long maxFileSize= maxFileSizeToUpload * 1024 * 1024;

        String resultData;
        try {
            Integer id = Integer.parseInt(idEntity);
            
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
                    result+= saveFilePart(tableName, fieldName, item.getName(), item.getContentType(), (int)item.getSize(), is, id);
                }
            }
            
            Map<String, Object> entity = directService.loadByParameter(tableName, "id", id);
            
            resultData= Util.getOperationCallback(entity, result, true);
        } catch (Exception e) {
            LOGGER.error("upload " + tableName, e);
            resultData= Util.getOperationCallback(null, "Error en actualizaci&oacute;n de " + tableName + ": " + e.getMessage(), false);
        }
        return getStringBytes(resultData);
    }
    
    
    
    protected byte[] getStringBytes(String data){
        try {
            return data.getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            LOGGER.error("getStringBytes", ex);
            return null;
        }
    }
    
    protected String saveFile(String tableName, FileItemStream fileIS, Object idEntity){
        // ABSTRACT CODE HERE
        return "Almacenamiento de archivo no implementado!!";
    }
    
    protected String saveFilePart(String tableName, String fieldName, String fileName, String fileType, int fileSize, InputStream is, Integer idParent){
        // ABSTRACT CODE HERE
        return "Almacenamiento de archivo no implementado!!";
    }

    /**
     * @param maxFileSizeToUpload the maxFileSizeToUpload to set in MB
     */
    protected void setMaxFileSizeToUpload(Long maxFileSizeToUpload) {
        this.maxFileSizeToUpload = maxFileSizeToUpload;
    }

}
