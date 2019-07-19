package com.lacv.jmagrexs.controller.rest;

import com.lacv.jmagrexs.domain.BaseDto;
import com.lacv.jmagrexs.domain.BaseEntity;
import com.lacv.jmagrexs.reflection.EntityReflection;
import com.lacv.jmagrexs.util.Util;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
            @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir,
            @RequestParam(required = false) String templateName, @RequestParam(required = false) Long numColumns,
            HttpServletRequest request) {

        
        String sessionFilter= getSessionFilters(filter, null);
            
        return find(sessionFilter, query, limit, page, sort, dir, templateName, numColumns, request);
    }

    @RequestMapping(value = "/session_find/xml.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public HttpEntity<byte[]> sessionFindXml(@RequestParam(required = false) String filter, @RequestParam(required = false) String query,
            @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir, HttpServletRequest request) {

        String sessionFilter= getSessionFilters(filter, null);
        
        return findXml(sessionFilter, query, limit, page, sort, dir, request);
    }

    @RequestMapping(value = "/session_find/xlsx.htm", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public void sessionFindXls(@RequestParam(required = false) String filter, @RequestParam(required = false) String query,
            @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir,
            HttpServletRequest request, HttpServletResponse response) {
        
        String sessionFilter= getSessionFilters(filter, null);
        
        findXls(sessionFilter, query, limit, page, sort, dir, request, response);
    }
    
    @RequestMapping(value = "/session_find/csv.htm", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public void sessionFindCsv(@RequestParam(required = false) String filter, @RequestParam(required = false) String query, 
            @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir,
            HttpServletRequest request, HttpServletResponse response) {
        
        String sessionFilter= getSessionFilters(filter, null);
        
        findCsv(sessionFilter, query, limit, page, sort, dir, request, response);
    }
    
    @RequestMapping(value = "/session_find/yaml.htm", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public byte[] sessionFindYaml(@RequestParam(required = false) String filter, @RequestParam(required = false) String query,
            @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir,
            @RequestParam(required = true) Boolean yalmFormat, HttpServletRequest request) {

        String sessionFilter= getSessionFilters(filter, null);
        
        return findYaml(sessionFilter, query, limit, page, sort, dir, yalmFormat, request);
    }
    
    @RequestMapping(value = "/session_report/{reportName}.htm", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public HttpEntity<byte[]> sessionReport(@RequestParam(required = false) String filter,
            @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir,
            @RequestParam(required = false) String templateName, @RequestParam(required = false) Long numColumns,
            @PathVariable String reportName, HttpServletRequest request) {

        String sessionFilter= getSessionFilters(filter, reportName);
        
        return report(sessionFilter, limit, page, sort, dir, templateName, numColumns, reportName, request);
    }
    
    @RequestMapping(value = "/session_report/xml/{reportName}.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public HttpEntity<byte[]> sessionReportXml(@RequestParam(required = false) String filter,
            @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir,
            @PathVariable String reportName, HttpServletRequest request) {

        String sessionFilter= getSessionFilters(filter, reportName);
        
        return reportXml(sessionFilter, limit, page, sort, dir, reportName, request);
    }
    
    @RequestMapping(value = "/session_report/xls/{reportName}.htm", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public void sessionReportXls(@RequestParam(required = false) String filter,
            @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir,
            @PathVariable String reportName, HttpServletRequest request, HttpServletResponse response) {
        
        String sessionFilter= getSessionFilters(filter, reportName);
        
        reportXls(sessionFilter, limit, page, sort, dir, reportName, request, response);
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
            if(canSessionCreate(entity)){
                return super.create(data, request);
            }else{
                resultData= Util.getOperationCallback(dto, "Error, no puede crear la entidad " + entityRef, false);
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
            if(id!=null){
                BaseEntity entity = (BaseEntity) service.loadById(id);
                if(entity!=null){
                    EntityReflection.updateEntity(jsonData, entity);
                    entity.setId(id);
                    if(canSessionUpdate(entity)){
                        return super.update(data, request);
                    }else{
                        resultData= Util.getOperationCallback(dto, "Error, no puede actualizar la entidad " + entityRef + " con id "+jsonObject.get("id").toString(), false);
                    }
                }else{
                    return this.sessionCreate(data, request);
                }
            }else{
                return this.sessionCreate(jsonData, request);
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
            if(canSessionUpdateByFilters(new JSONObject(sessionFilters))){
                return super.updateByFilter(filter, jsonData, request);
            }else{
                resultData= Util.getOperationCallback(null, "Error, no puede actualizar la entidad " + entityRef + " por filtros", false);
            }
        } catch (Exception e) {
            LOGGER.error("update " + entityRef, e);
            resultData= Util.getOperationCallback(null, "Error en actualizaci&oacute;n masiva de " + entityRef + ": " + e.getMessage(), false);
        }
        return Util.getStringBytes(resultData);
    }

    @RequestMapping(value = "/session_load.htm", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public byte[] sessionLoad(@RequestParam String idEntity, HttpServletRequest request) {
        BaseDto dto = null;
        String resultData;
        try {
            Object id = EntityReflection.getParsedFieldValue(entityClass, "id", idEntity);
            BaseEntity entity = (BaseEntity) service.loadById(id);
            if(canSessionLoad(entity)){
                dto = mapper.entityToDto(entity);
                resultData= Util.getOperationCallback(dto, "Carga de " + entityRef + " realizada...", true);
            }else{
                resultData= Util.getOperationCallback(dto, "Error, no puede cargar el " + entityRef + " con id "+idEntity, false);
            }
        } catch (Exception e) {
            LOGGER.error("load " + entityRef, e);
            resultData= Util.getOperationCallback(dto, "Error en carga de " + entityRef + ": " + e.getMessage(), true);
        }
        return Util.getStringBytes(resultData);
    }

    @RequestMapping(value = "/session_delete.htm", method = {RequestMethod.DELETE, RequestMethod.GET})
    @ResponseBody
    public String sessionDelete(@RequestParam String idEntity, HttpServletRequest request) {
        BaseDto dto = null;
        try {
            Object id = EntityReflection.getParsedFieldValue(entityClass, "id", idEntity);
            BaseEntity entity = (BaseEntity) service.loadById(id);
            if(canSessionDelete(entity)){
                dto = mapper.entityToDto(entity);
                service.remove(entity);
                return Util.getOperationCallback(dto, "Eliminaci&oacute;n de " + entityRef + " realizada...", true);
            }else{
                return Util.getOperationCallback(dto, "Error, no puede eliminar el " + entityRef + " con id "+idEntity, false);
            }
        } catch (Exception e) {
            LOGGER.error("delete " + entityRef, e);
            return Util.getOperationCallback(dto, "Error en eliminaci&oacute;n de " + entityRef + ": " + e.getMessage(), true);
        }
    }
    
    @RequestMapping(value = "/session_delete/byfilter.htm", method = {RequestMethod.DELETE, RequestMethod.GET})
    @ResponseBody
    public String sessionDeleteByFilter(@RequestParam String filter, HttpServletRequest request) {
        String sessionFilter= getSessionFilters(filter, null);
        if(canSessionDeleteByFilters(new JSONObject(sessionFilter))){
            return deleteByFilter(sessionFilter, request);
        }else{
            return Util.getResultListCallback(null, 0L, "Error, no puede eliminar la entidad " + entityRef + " por filtros", false);
        }
    }
    
    @RequestMapping(value = "/session_import.htm", method = {RequestMethod.POST})
    @ResponseBody
    public byte[] sessionImportData(@RequestParam(required= false) String data, HttpServletRequest request) {
        return importData(data, request);
    }
    
    @RequestMapping(value = "/session_import/{format}.htm")
    @ResponseBody
    public byte[] sessionImportData(HttpServletRequest request, @PathVariable String format) {
        return importData(request, format);
    }
    
    @Override
    protected String validateSessionImportEntities(List<BaseEntity> entities, List listDtos){
        String resultData;
        if(canSessionImportData(entities)){
            listDtos= importEntities(entities);
            resultData= Util.getResultListCallback(listDtos, (long)listDtos.size(),"Importaci&oacute;n de "+listDtos.size()+" registros tipo " + entityRef + " finalizada...", true);
        }else{
            resultData= Util.getResultListCallback(listDtos, 0L, "Error, no se pudo importar los registros de tipo " + entityRef, false);
        }
        return resultData;
    }
    
    @RequestMapping(value = "/session_upload/{idEntity}.htm")
    @ResponseBody
    public byte[] sessionUpload(HttpServletRequest request, @PathVariable String idEntity) {
        try {
            Object id = EntityReflection.getParsedFieldValue(entityClass, "id", idEntity);
            BaseEntity entity = (BaseEntity) service.loadById(id);
            if(canSessionUpdate(entity)){
                return upload(request, idEntity);
            }
        } catch (ClassNotFoundException e) {
            LOGGER.error("upload " + entityRef, e);
        }
        return Util.getStringBytes(Util.getOperationCallback(null, "Error, no puede subir archivo en la entidad " + entityRef + "con id "+idEntity, false));
    }
    
    @RequestMapping(value = "/session_diskupload/{idEntity}.htm")
    @ResponseBody
    public byte[] sessionDiskupload(HttpServletRequest request, @PathVariable String idEntity) {
        try {
            Object id = EntityReflection.getParsedFieldValue(entityClass, "id", idEntity);
            BaseEntity entity = (BaseEntity) service.loadById(id);
            if(canSessionUpdate(entity)){
                return diskupload(request, idEntity, true);
            }
        } catch (ClassNotFoundException e) {
            LOGGER.error("upload " + entityRef, e);
        }
        return Util.getStringBytes(Util.getOperationCallback(null, "Error, no puede subir archivo en la entidad " + entityRef + "con id "+idEntity, false));
    }
    
    @RequestMapping(value = "/session_multipartupload/{idParent}.htm")
    @ResponseBody
    public String sessionMultipartupload(HttpServletRequest request, @PathVariable String idParent) {
        try {
            Object id = EntityReflection.getParsedFieldValue(entityClass, "id", idParent);
            BaseEntity entity = (BaseEntity) service.loadById(id);
            if(canSessionUpdate(entity)){
                return multipartupload(request, idParent, true);
            }
        } catch (ClassNotFoundException e) {
            LOGGER.error("upload " + entityRef, e);
        }
        return Util.getOperationCallback(null, "Error, no puede subir archivo en la entidad " + entityRef + "con id "+idParent, false);
    }
        
    protected String getSessionFilters(String filter, String reportName){
        String sessionFilter;
        JSONObject jsonFilter;
        if(filter!=null && !filter.equals("")){
            filter= formatFilter(filter);
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
    
    public abstract boolean canSessionLoad(BaseEntity entity);
    
    public abstract boolean canSessionCreate(BaseEntity entity);
    
    public abstract boolean canSessionUpdate(BaseEntity entity);
    
    public abstract boolean canSessionUpdateByFilters(JSONObject jsonFilters);
    
    public abstract boolean canSessionDelete(BaseEntity entity);
    
    public abstract boolean canSessionDeleteByFilters(JSONObject jsonFilters);
    
    public abstract boolean canSessionImportData(List<BaseEntity> entities);
    
}
