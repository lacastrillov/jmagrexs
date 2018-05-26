package com.lacv.jmagrexs.controller.rest;

import com.lacv.jmagrexs.controller.view.ExtFileExplorerController;
import com.lacv.jmagrexs.domain.BaseDto;
import com.lacv.jmagrexs.domain.BaseEntity;
import com.lacv.jmagrexs.reflection.EntityReflection;
import com.lacv.jmagrexs.util.Util;
import com.lacv.jmagrexs.util.FileService;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public abstract class RestSessionController extends RestEntityController {
    

    @RequestMapping(value = "/session_find.htm", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public HttpEntity<byte[]> sessionFind(@RequestParam(required = false) String filter, @RequestParam(required = false) String query,
            @RequestParam(required = false) Long start, @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir,
            @RequestParam(required = false) String templateName, @RequestParam(required = false) Long numColumns) {

        
        String sessionFilter= getSessionFilters(filter, null);
            
        return super.find(sessionFilter, query, start, limit, page, sort, dir, templateName, numColumns);
    }

    @RequestMapping(value = "/session_find/xml.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public HttpEntity<byte[]> sessionFindXml(@RequestParam(required = false) String filter, @RequestParam(required = false) String query,
            @RequestParam(required = false) Long start, @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir) {

        String sessionFilter= getSessionFilters(filter, null);
        
        return super.findXml(sessionFilter, query, start, limit, page, sort, dir);
    }

    @RequestMapping(value = "/session_find/xls.htm", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public void sessionFindXls(@RequestParam(required = false) String filter, @RequestParam(required = false) String query,
            @RequestParam(required = false) Long start, @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir,
            HttpServletResponse response) {
        
        String sessionFilter= getSessionFilters(filter, null);
        
        super.findXls(sessionFilter, query, start, limit, page, sort, dir, response);
    }
    
    @RequestMapping(value = "/session_find/csv.htm", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public void sessionFindCsv(@RequestParam(required = false) String filter, @RequestParam(required = false) String query, 
            @RequestParam(required = false) Long start, @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir,
            HttpServletResponse response) {
        
        String sessionFilter= getSessionFilters(filter, null);
        
        super.findCsv(sessionFilter, query, start, limit, page, sort, dir, response);
    }
    
    @RequestMapping(value = "/session_find/yaml.htm", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public byte[] sessionFindYaml(@RequestParam(required = false) String filter, @RequestParam(required = false) String query,
            @RequestParam(required = false) Long start, @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir,
            @RequestParam(required = true) Boolean yalmFormat) {

        String sessionFilter= getSessionFilters(filter, null);
        
        return super.findYaml(sessionFilter, query, start, limit, page, sort, dir, yalmFormat);
    }
    
    @RequestMapping(value = "/session_report/{reportName}.htm", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public HttpEntity<byte[]> sessionReport(@RequestParam(required = false) String filter, @RequestParam(required = false) Long start,
            @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir,
            @RequestParam(required = false) String templateName, @RequestParam(required = false) Long numColumns,
            @RequestParam(required = true) String dtoName,
            @PathVariable String reportName) {

        String sessionFilter= getSessionFilters(filter, reportName);
        
        return super.report(sessionFilter, start, limit, page, sort, dir, templateName, numColumns, dtoName, reportName);
    }
    
    @RequestMapping(value = "/session_report/xml/{reportName}.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public HttpEntity<byte[]> sessionReportXml(@RequestParam(required = false) String filter,
            @RequestParam(required = false) Long start, @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir,
            @RequestParam(required = true) String dtoName,
            @PathVariable String reportName) {

        String sessionFilter= getSessionFilters(filter, reportName);
        
        return super.reportXml(sessionFilter, start, limit, page, sort, dir, dtoName, reportName);
    }
    
    @RequestMapping(value = "/session_report/xls/{reportName}.htm", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public void sessionReportXls(@RequestParam(required = false) String filter,
            @RequestParam(required = false) Long start, @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir,
            @RequestParam(required = true) String dtoName, @PathVariable String reportName, HttpServletResponse response) {
        
        String sessionFilter= getSessionFilters(filter, reportName);
        
        super.reportXls(sessionFilter, start, limit, page, sort, dir, dtoName, reportName, response);
    }

    @RequestMapping(value = "/session_create.htm", method = RequestMethod.POST)
    @ResponseBody
    public byte[] sessionCreate(@RequestParam(required= false) String data, HttpServletRequest request) {
        BaseDto dto = null;

        String resultData;
        try {
            String jsonData= data;
            if(jsonData==null){
                jsonData = IOUtils.toString(request.getInputStream());
            }
            BaseEntity entity = EntityReflection.readEntity(jsonData, entityClass);
            if(canCreate(entity)){
                service.create(entity);
                dto = mapper.entityToDto(entity);
                resultData= Util.getOperationCallback(dto, "Creaci&oacute;n de " + entityRef + " realizada...", true);
            }else{
                resultData= "{\"success\":false,\"message\":\"Error, no puede crear la entidad " + entityRef + "\"}";
            }
        } catch (Exception e) {
            LOGGER.error("create " + entityRef, e);
            resultData= Util.getOperationCallback(dto, "Error en creaci&oacute;n de " + entityRef + ": " + e.getMessage(), false);
        }
        return Util.getStringBytes(resultData);
    }

    @RequestMapping(value = "/session_update.htm", method = {RequestMethod.PUT, RequestMethod.POST})
    @ResponseBody
    public byte[] sessionUpdate(@RequestParam(required= false) String data, HttpServletRequest request) {
        BaseDto dto = null;
        
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
                entity.setId(id);
                if(canUpdate(entity)){
                    service.update(entity);
                    dto = mapper.entityToDto(entity);
                    resultData= Util.getOperationCallback(dto, "Actualizaci&oacute;n de " + entityRef + " realizada...", true);
                }else{
                    resultData= "{\"success\":false,\"message\":\"Error, no puede actualizar la entidad " + entityRef + " con id "+jsonObject.get("id").toString() + "\"}";
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
    
    @RequestMapping(value = "/session_update/byfilter.htm", method = {RequestMethod.PUT, RequestMethod.POST})
    @ResponseBody
    public byte[] sessionUpdateByFilter(@RequestParam(required= false) String filter, HttpServletRequest request) {
        String resultData;
        try {
            String jsonData= filter;
            if(jsonData==null){
                jsonData = IOUtils.toString(request.getInputStream());
            }
            String sessionFilters= getSessionFilters(jsonData, null);
            if(canUpdateByFilters(new JSONObject(sessionFilters))){
                Integer updatedRecords= service.updateByJSONFilters(sessionFilters);
                resultData= Util.getOperationCallback(null, "Actualizaci&oacute;n masiva de " + updatedRecords +" " + entityRef + " realizada...", true);
            }else{
                resultData= "{\"success\":false,\"message\":\"Error, no puede actualizar la entidad " + entityRef + " por filtros\"}";
            }
        } catch (Exception e) {
            LOGGER.error("update " + entityRef, e);
            resultData= Util.getOperationCallback(null, "Error en actualizaci&oacute;n masiva de " + entityRef + ": " + e.getMessage(), false);
        }
        return Util.getStringBytes(resultData);
    }

    @RequestMapping(value = "/session_load.htm", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public byte[] sessionLoad(@RequestParam String idEntity) {
        BaseDto dto = null;

        String resultData;
        try {
            Object id = EntityReflection.getParsedFieldValue(entityClass, "id", idEntity);
            BaseEntity entity = (BaseEntity) service.loadById(id);
            if(canLoad(entity)){
                dto = mapper.entityToDto(entity);
                resultData= Util.getOperationCallback(dto, "Carga de " + entityRef + " realizada...", true);
            }else{
                resultData= "{\"success\":false,\"message\":\"Error, no puede cargar el " + entityRef + " con id "+idEntity+"\"}";
            }
        } catch (Exception e) {
            LOGGER.error("load " + entityRef, e);
            resultData= Util.getOperationCallback(dto, "Error en carga de " + entityRef + ": " + e.getMessage(), true);
        }
        return Util.getStringBytes(resultData);
    }

    @RequestMapping(value = "/session_delete.htm", method = {RequestMethod.DELETE, RequestMethod.GET})
    @ResponseBody
    public String sessionDelete(@RequestParam String idEntity) {
        BaseDto dto = null;

        try {
            Object id = EntityReflection.getParsedFieldValue(entityClass, "id", idEntity);
            BaseEntity entity = (BaseEntity) service.loadById(id);
            if(canDelete(entity)){
                dto = mapper.entityToDto(entity);
                service.remove(entity);
                return Util.getOperationCallback(dto, "Eliminaci&oacute;n de " + entityRef + " realizada...", true);
            }else{
                return "{\"success\":false,\"message\":\"Error, no puede eliminar el " + entityRef + " con id "+idEntity+"\"}";
            }
        } catch (Exception e) {
            LOGGER.error("delete " + entityRef, e);
            return Util.getOperationCallback(dto, "Error en eliminaci&oacute;n de " + entityRef + ": " + e.getMessage(), true);
        }
    }
    
    @RequestMapping(value = "/session_delete/byfilter.htm", method = {RequestMethod.DELETE, RequestMethod.GET})
    @ResponseBody
    public String sessionDeleteByFilter(@RequestParam String filter) {
        String sessionFilter= getSessionFilters(filter, null);
        
        if(canDeleteByFilters(new JSONObject(sessionFilter))){
            return super.deleteByFilter(sessionFilter);
        }else{
            return "{\"success\":false,\"message\":\"Error, no puede eliminar la entidad " + entityRef + " por filtros\"}";
        }
    }
    
    @RequestMapping(value = "/session_import.htm", method = {RequestMethod.POST})
    @ResponseBody
    public byte[] sessionImportData(@RequestParam(required= false) String data, HttpServletRequest request) {
        if(canImportData()){
            return super.importData(data, request);
        }else{
            return Util.getStringBytes("{\"success\":false,\"message\":\"Error, no puede importar datos tipo " + entityRef + "\"}");
        }
    }
    
    @RequestMapping(value = "/session_import/{format}.htm")
    @ResponseBody
    public byte[] sessionImportData(HttpServletRequest request, @PathVariable String format) {
        if(canImportData()){
            return super.importData(request, format);
        }else{
            return Util.getStringBytes("{\"success\":false,\"message\":\"Error, no puede importar datos tipo " + entityRef + "\"}");
        }
    }
    
    @RequestMapping(value = "/session_upload/{idEntity}.htm")
    @ResponseBody
    public byte[] sessionUpload(HttpServletRequest request, @PathVariable String idEntity) {
        try {
            Object id = EntityReflection.getParsedFieldValue(entityClass, "id", idEntity);
            BaseEntity entity = (BaseEntity) service.loadById(id);
            if(canUpdate(entity)){
                return super.upload(request, idEntity);
            }
        } catch (ClassNotFoundException e) {
            LOGGER.error("upload " + entityRef, e);
        }
        return Util.getStringBytes("{\"success\":false,\"message\":\"Error, no puede subir archivo en la entidad " + entityRef + "con id "+idEntity+ "\"}");
    }
    
    @RequestMapping(value = "/session_diskupload/{idEntity}.htm")
    @ResponseBody
    public byte[] sessionDiskupload(HttpServletRequest request, @PathVariable String idEntity) {
        try {
            Object id = EntityReflection.getParsedFieldValue(entityClass, "id", idEntity);
            BaseEntity entity = (BaseEntity) service.loadById(id);
            if(canUpdate(entity)){
                return super.diskupload(request, idEntity);
            }
        } catch (ClassNotFoundException e) {
            LOGGER.error("upload " + entityRef, e);
        }
        return Util.getStringBytes("{\"success\":false,\"message\":\"Error, no puede subir archivo en la entidad " + entityRef + "con id "+idEntity+ "\"}");
    }
    
    @RequestMapping(value = "/session_multipartupload/{idParent}.htm")
    @ResponseBody
    public String sessionMultipartupload(HttpServletRequest request, @PathVariable String idParent) {
        try {
            Object id = EntityReflection.getParsedFieldValue(entityClass, "id", idParent);
            BaseEntity entity = (BaseEntity) service.loadById(id);
            if(canUpdate(entity)){
                return super.multipartupload(request, idParent);
            }
        } catch (ClassNotFoundException e) {
            LOGGER.error("upload " + entityRef, e);
        }
        return "{\"success\":false,\"message\":\"Error, no puede subir archivo en la entidad " + entityRef + "con id "+idParent+ "\"}";
    }
    
    @RequestMapping(value = "/session_readFile.htm", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public byte[] sessionReadFile(@RequestParam(required = true) String fileUrl) {
        String content="";
        try {
            String userPath= "/ucp"+getUserCode();
            String pathFile= fileUrl.replace(LOCAL_DOMAIN + userPath, LOCAL_DIR + userPath);
            content= FileService.getTextFile(pathFile);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(ExtFileExplorerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Util.getStringBytes(content);
    }
    
    @RequestMapping(value = "/session_writeFile.htm", method = RequestMethod.POST)
    @ResponseBody
    public String sessionWriteFile(@RequestParam(required = true) String fileUrl, @RequestParam(required = true) String content) {
        try {
            String userPath= "/ucp"+getUserCode();
            String pathFile= fileUrl.replace(LOCAL_DOMAIN + userPath, LOCAL_DIR + userPath);
            FileService.setTextFile(content, pathFile);
            return "Contenido guardado";
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(ExtFileExplorerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return "Error al guardar";
    }
    
    private String getSessionFilters(String filter, String reportName){
        String sessionFilter;
        JSONObject jsonFilter;
        if(filter!=null && !filter.equals("")){
            jsonFilter= new JSONObject(filter);
        }else{
            jsonFilter= new JSONObject();
        }
        String[] filterTypes= {"eq","lk","in","btw"};
        for(String filterType: filterTypes){
            if(!jsonFilter.has(filterType)){
                jsonFilter.put(filterType, new JSONObject());
            }
        }
        if(reportName!=null){
            jsonFilter= addSessionReportFilter(reportName, jsonFilter);
        }else{
            jsonFilter= addSessionSearchFilter(jsonFilter);
        }
        sessionFilter= jsonFilter.toString();
        
        return sessionFilter;
    }
    
    public abstract JSONObject addSessionSearchFilter(JSONObject jsonFilters);
    
    public abstract JSONObject addSessionReportFilter(String reportName, JSONObject jsonFilters);
    
    public abstract boolean canLoad(BaseEntity entity);
    
    public abstract boolean canCreate(BaseEntity entity);
    
    public abstract boolean canUpdate(BaseEntity entity);
    
    public abstract boolean canUpdateByFilters(JSONObject jsonFilters);
    
    public abstract boolean canDelete(BaseEntity entity);
    
    public abstract boolean canDeleteByFilters(JSONObject jsonFilters);
    
    public boolean canImportData(){ return false; }
    
    public String getUserCode(){
        return "";
    }
    
    protected String saveSessionFile(FileItemStream fileIS, Object idEntity){
        // ABSTRACT CODE HERE
        return "Almacenamiento de archivo no implementado!!";
    }
    
    protected String saveSessionFilePart(int slice, String fileName, String fileType, int fileSize, InputStream is, Object idParent){
        // ABSTRACT CODE HERE
        return "Almacenamiento de archivo no implementado!!";
    }
    
}
