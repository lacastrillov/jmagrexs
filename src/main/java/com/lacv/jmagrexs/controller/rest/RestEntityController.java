package com.lacv.jmagrexs.controller.rest;

import com.lacv.jmagrexs.annotation.ImageResize;
import com.lacv.jmagrexs.components.ExplorerConstants;
import com.lacv.jmagrexs.components.FieldConfigurationByAnnotations;
import com.lacv.jmagrexs.components.ServerDomain;
import com.lacv.jmagrexs.dao.Parameters;
import com.lacv.jmagrexs.domain.BaseDto;
import com.lacv.jmagrexs.domain.BaseEntity;
import com.lacv.jmagrexs.dto.ItemTemplate;
import com.lacv.jmagrexs.reflection.EntityReflection;
import com.lacv.jmagrexs.service.EntityService;
import com.lacv.jmagrexs.util.Util;
import com.lacv.jmagrexs.util.XMLMarshaller;
import com.lacv.jmagrexs.dto.ResultListCallback;
import com.lacv.jmagrexs.interfaces.MassiveOperationInterface;
import com.lacv.jmagrexs.interfaces.WebEntityInterface;
import com.lacv.jmagrexs.mapper.EntityMapper;
import com.lacv.jmagrexs.service.DbOperationLogService;
import com.lacv.jmagrexs.service.MassiveOperationLogService;
import com.lacv.jmagrexs.util.CSVService;
import com.lacv.jmagrexs.util.ExcelService;
import com.lacv.jmagrexs.util.FileService;
import com.lacv.jmagrexs.util.JSONService;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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
    
    protected Map<String, Class> enabledReports;
    
    private final String TEMPLATES_DIR ="/ext/gridtemplates/";
    
    @Autowired
    public ServerDomain serverDomain;
    
    @Autowired
    private VelocityEngine velocityEngine;
    
    @Autowired
    protected ExplorerConstants explorerConstants;
    
    protected Long maxFileSizeToUpload=1024L;
    
    @Autowired
    public FieldConfigurationByAnnotations fcba;
    
    @Autowired(required = false)
    private DbOperationLogService dbOperationService;
    
    @Autowired(required = false)
    private MassiveOperationLogService massiveOperationService;
    
    private Boolean dbLog= false, massLog=false;
    

    protected void addControlMapping(String entityRef, EntityService entityService, EntityMapper entityMapper) {
        this.entityRef= entityRef;
        this.service=  entityService;
        this.mapper= entityMapper;
        this.entityClass= service.getEntityClass();
        this.dtoClass= mapper.getDtoClass();
        this.enabledReports= new HashMap<>();
    }
    
    @RequestMapping(value = "/find.htm", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public HttpEntity<byte[]> find(@RequestParam(required = false) String filter, @RequestParam(required = false) String query,
            @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir,
            @RequestParam(required = false) String templateName, @RequestParam(required = false) Long numColumns, HttpServletRequest request) {

        String resultData;
        try {
            filter= (!isSessionRequest(request))?getFilters(filter, null):filter;
            Parameters p= service.buildParameters(filter, query, page, limit, sort, dir);
            List<BaseEntity> listEntities = service.findByParameters(p);
            List listDtos = mapper.listEntitiesToListDtos(listEntities);
            Long totalCount = p.getTotalResults();
            
            if(templateName!=null){
                resultData= generateTemplateData(listDtos, totalCount, entityRef, true, templateName, numColumns);
            }else{
                resultData=Util.getResultListCallback(listDtos, totalCount, "Busqueda de " + entityRef + " realizada...", true);
            }
        } catch (Exception e) {
            LOGGER.error("find " + entityRef, e);
            resultData=Util.getResultListCallback(new ArrayList(), "Error buscando " + entityRef + ": " + e.getMessage(), false);
        }

        return Util.getHttpEntityBytes(resultData, "json");
    }

    @RequestMapping(value = "/find/xml.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public HttpEntity<byte[]> findXml(@RequestParam(required = false) String filter, @RequestParam(required = false) String query,
            @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir, HttpServletRequest request) {

        try {
            filter= (!isSessionRequest(request))?getFilters(filter, null):filter;
            Parameters p= service.buildParameters(filter, query, page, limit, sort, dir);
            List<BaseEntity> listEntities = service.findByParameters(p);
            List<BaseEntity> listDtos = mapper.listEntitiesToListDtos(listEntities);
            Long totalCount = p.getTotalResults();

            String resultData = Util.getResultListCallback(listDtos, totalCount, "Busqueda de " + entityRef + " realizada...", true);
            String xml = XMLMarshaller.convertJSONToXML(resultData, ResultListCallback.class.getSimpleName());
            
            return Util.getHttpEntityBytes(xml, "xml");
        } catch (Exception e) {
            LOGGER.error("find " + entityRef, e);
            return null;
        }
    }

    @RequestMapping(value = "/find/xlsx.htm", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public void findXlsx(@RequestParam(required = false) String filter, @RequestParam(required = false) String query, 
            @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir,
            HttpServletRequest request, HttpServletResponse response) {
        
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + entityRef + "_report.xlsx\"");

        try {
            filter= (!isSessionRequest(request))?getFilters(filter, null):filter;
            Parameters p= service.buildParameters(filter, query, page, limit, sort, dir);
            List<Object> listEntities = service.findByParameters(p);
            
            ExcelService.generateExcelReport(listEntities, response.getOutputStream(), dtoClass);
        } catch (Exception e) {
            LOGGER.error("find " + entityRef, e);
        }
    }
    
    @RequestMapping(value = "/find/csv.htm", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public void findCsv(@RequestParam(required = false) String filter, @RequestParam(required = false) String query, 
            @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir,
            HttpServletRequest request, HttpServletResponse response) {
        
        response.setContentType("text/csv; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + entityRef + "_report.csv\"");

        try {
            filter= (!isSessionRequest(request))?getFilters(filter, null):filter;
            Parameters p= service.buildParameters(filter, query, page, limit, sort, dir);
            List<Object> listEntities = service.findByParameters(p);
            
            response.getWriter().print(CSVService.generateCSVReport(listEntities, dtoClass));
        } catch (Exception e) {
            LOGGER.error("find " + entityRef, e);
        }
    }
    
    @RequestMapping(value = "/find/yaml.htm", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public byte[] findYaml(@RequestParam(required = false) String filter, @RequestParam(required = false) String query,
            @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir,
            @RequestParam(required = false) Boolean yamlFormat, HttpServletRequest request) {

        String resultData;
        try {
            filter= (!isSessionRequest(request))?getFilters(filter, null):filter;
            Parameters p= service.buildParameters(filter, query, page, limit, sort, dir);
            List<BaseEntity> listEntities = service.findByParameters(p);
            List listDtos = mapper.listEntitiesToListDtos(listEntities);
            Long totalCount = p.getTotalResults();
            
            resultData = Util.getResultListCallback(listDtos, totalCount, "Busqueda de " + entityRef + " realizada...", true);
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
    public HttpEntity<byte[]> report(@RequestParam(required = false) String filter,
            @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir,
            @RequestParam(required = false) String templateName, @RequestParam(required = false) Long numColumns,
            @PathVariable String reportName, HttpServletRequest request) {

        String resultData;
        try {
            if(this.enabledReports.containsKey(reportName)){
                Class dtoReportClass= this.enabledReports.get(reportName);
                filter= (!isSessionRequest(request))?getFilters(filter, reportName):filter;
                Parameters p= service.buildParameters(filter, null, page, limit, sort, dir, dtoReportClass);
                List<Object> listDtos = service.findByParameters(reportName, p, dtoReportClass);
                Long totalCount = p.getTotalResults();

                if(templateName!=null){
                    resultData= generateTemplateData(listDtos, totalCount, entityRef, true, templateName, numColumns);
                }else{
                    resultData= Util.getResultListCallback(listDtos, totalCount, "Buequeda reporte " + reportName + " realizada...", true);
                }
            }else{
                resultData= Util.getResultListCallback(new ArrayList(), "El reporte " + reportName + " no se encuentra habilitado", false);
            }
        } catch (Exception e) {
            LOGGER.error("find " + entityRef + " - " + reportName, e);
            resultData= Util.getResultListCallback(new ArrayList(), "Error reporte " + reportName + ": " + e.getMessage(), false);
        }
        
        return Util.getHttpEntityBytes(resultData, "json");
    }
    
    @RequestMapping(value = "/report/xml/{reportName}.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public HttpEntity<byte[]> reportXml(@RequestParam(required = false) String filter,
            @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir,
            @PathVariable String reportName, HttpServletRequest request) {

        try {
            if(this.enabledReports.containsKey(reportName)){
                Class dtoReportClass= this.enabledReports.get(reportName);
                filter= (!isSessionRequest(request))?getFilters(filter, reportName):filter;
                Parameters p= service.buildParameters(filter, null, page, limit, sort, dir, dtoReportClass);
                List<Object> listDtos = service.findByParameters(reportName, p, dtoReportClass);
                Long totalCount = p.getTotalResults();

                String resultData = Util.getResultListCallback(listDtos, totalCount, "Buequeda reporte " + reportName + " realizada...", true);
                String xml = XMLMarshaller.convertJSONToXML(resultData, ResultListCallback.class.getSimpleName());

                return Util.getHttpEntityBytes(xml, "xml");
            }else{
                return null;
            }
        } catch (Exception e) {
            LOGGER.error("find " + entityRef + " - " + reportName, e);
            return null;
        }
    }
    
    @RequestMapping(value = "/report/xlsx/{reportName}.htm", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public void reportXlsx(@RequestParam(required = false) String filter,
            @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir,
            @PathVariable String reportName, HttpServletRequest request, HttpServletResponse response) {
        
        try {
            if(this.enabledReports.containsKey(reportName)){
                Class dtoReportClass= this.enabledReports.get(reportName);
                filter= (!isSessionRequest(request))?getFilters(filter, reportName):filter;
                Parameters p= service.buildParameters(filter, null, null, null, sort, dir, dtoReportClass);
                List<Object> listDtos = service.findByParameters(reportName, p, dtoReportClass);
                
                response.setContentType("application/vnd.ms-excel");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + reportName + "_report.xlsx\"");
                ExcelService.generateExcelReport(listDtos, response.getOutputStream(), dtoReportClass);
            }
        } catch (Exception e) {
            LOGGER.error("find " + entityRef + " - " + reportName, e);
        }
    }
    
    @RequestMapping(value = "/report/csv/{reportName}.htm", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public void reportCsv(@RequestParam(required = false) String filter,
            @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir,
            @PathVariable String reportName, HttpServletRequest request, HttpServletResponse response) {
        
        try {
            if(this.enabledReports.containsKey(reportName)){
                Class dtoReportClass= this.enabledReports.get(reportName);
                filter= (!isSessionRequest(request))?getFilters(filter, reportName):filter;
                Parameters p= service.buildParameters(filter, null, null, null, sort, dir, dtoReportClass);
                List<Object> listDtos = service.findByParameters(reportName, p, dtoReportClass);

                response.setContentType("text/csv; charset=utf-8");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + reportName + "_report.csv\"");
                response.getWriter().print(CSVService.generateCSVReport(listDtos, dtoReportClass));
            }
        } catch (Exception e) {
            LOGGER.error("find " + entityRef + " - " + reportName, e);
        }
    }

    @RequestMapping(value = "/create.htm", method = RequestMethod.POST)
    @ResponseBody
    public byte[] create(@RequestParam(required= false) String data, HttpServletRequest request) {
        String resultData, message;
        try {
            String jsonData= data;
            if(jsonData==null){
                jsonData = IOUtils.toString(request.getInputStream());
            }
            BaseEntity entity = EntityReflection.readEntity(jsonData, entityClass);
            if(isSessionRequest(request) || canCreate(entity)){
                service.create(entity);
                entity = (BaseEntity) service.loadById(entity.getId());
                BaseDto dto = mapper.entityToDto(entity);
                updateRelatedWebEntity(entity, request);
                message= "Creaci&oacute;n de " + entityRef + " realizada...";
                resultData= Util.getOperationCallback(dto, message, true);
                if(dbLog) dbOperationService.save(entityRef, "create", dto, message, true, null);
            }else{
                BaseDto dto = mapper.entityToDto(entity);
                message= "Error, no puede crear la entidad " + entityRef;
                resultData= Util.getOperationCallback(dto, message, false);
                if(dbLog) dbOperationService.save(entityRef, "create", dto, message, false, null);
            }
        } catch (Exception e) {
            LOGGER.error("create " + entityRef, e);
            resultData= Util.getOperationCallback(null, "Error en creaci&oacute;n de " + entityRef + ": " + e.getMessage(), false);
        }
        return Util.getStringBytes(resultData);
    }

    @RequestMapping(value = "/update.htm", method = {RequestMethod.PUT, RequestMethod.POST})
    @ResponseBody
    public byte[] update(@RequestParam(required= false) String data, HttpServletRequest request) {
        String resultData, message;
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
                    if(isSessionRequest(request) || canUpdate(entity)){
                        service.update(entity);
                        entity = (BaseEntity) service.loadById(id);
                        BaseDto dto = mapper.entityToDto(entity);
                        updateRelatedWebEntity(entity, request);
                        message= "Actualizaci&oacute;n de " + entityRef + " realizada...";
                        resultData= Util.getOperationCallback(dto, message, true);
                        if(dbLog) dbOperationService.save(entityRef, "update", dto, message, true, null);
                    }else{
                        message= "Error, no puede actualizar la entidad " + entityRef + " con id "+jsonObject.get("id").toString();
                        resultData= Util.getOperationCallback(null, message, false);
                        if(dbLog) dbOperationService.save(entityRef, "update", jsonData, message, false, null);
                    }
                }else{
                    return this.create(jsonData, request);
                }
            }else{
                return this.create(jsonData, request);
            }
        } catch (Exception e) {
            LOGGER.error("update " + entityRef, e);
            resultData= Util.getOperationCallback(null, "Error en actualizaci&oacute;n de " + entityRef + ": " + e.getMessage(), false);
        }
        return Util.getStringBytes(resultData);
    }
    
    @RequestMapping(value = "/update/byfilter.htm", method = {RequestMethod.PUT, RequestMethod.POST})
    @ResponseBody
    public byte[] updateByFilter(@RequestParam String filter, @RequestParam(required= false) String data, HttpServletRequest request) {
        String resultData, message;
        try {
            String jsonData= filter;
            String updateData= data;
            if(updateData==null){
                updateData= IOUtils.toString(request.getInputStream());
            }
            if(JSONService.isJSONObject(updateData)){
                JSONObject jsonFilter= new JSONObject(filter);
                JSONObject jsonUpdate= new JSONObject(updateData);
                jsonUpdate.remove("id");
                jsonFilter.put("uv", jsonUpdate);
                jsonData= jsonFilter.toString();
            }
            jsonData= (!isSessionRequest(request))?getFilters(jsonData, null):formatFilter(jsonData);
            if(isSessionRequest(request) || canUpdateByFilters(new JSONObject(jsonData))){
                Parameters p= service.buildParameters(jsonData, null, null, null, null, null);
                Integer updatedRecords= service.updateByParameters(p);
                message= "Actualizaci&oacute;n masiva de " + updatedRecords +" " + entityRef + " realizada...";
                resultData= Util.getOperationCallback(null, message, true);
                if(dbLog) dbOperationService.save(entityRef, "update_byfilter", jsonData, message, true, null);
            }else{
                message= "Error, no puede actualizar la entidad " + entityRef + " por filtros";
                resultData= Util.getOperationCallback(null, message, false);
                if(dbLog) dbOperationService.save(entityRef, "update_byfilter", jsonData, message, false, null);
            }
        } catch (Exception e) {
            LOGGER.error("update " + entityRef, e);
            resultData= Util.getOperationCallback(null, "Error en actualizaci&oacute;n masiva de " + entityRef + ": " + e.getMessage(), false);
        }
        return Util.getStringBytes(resultData);
    }

    @RequestMapping(value = "/load.htm", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public byte[] load(@RequestParam String idEntity, HttpServletRequest request) {
        BaseDto dto = null;
        String resultData;
        try {
            Object id = EntityReflection.getParsedFieldValue(entityClass, "id", idEntity);
            BaseEntity entity = (BaseEntity) service.loadById(id);
            if(isSessionRequest(request) || canLoad(entity)){
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

    @RequestMapping(value = "/delete.htm", method = {RequestMethod.DELETE, RequestMethod.GET})
    @ResponseBody
    public String delete(@RequestParam String idEntity, HttpServletRequest request) {
        String resultData, message;
        try {
            Object id = EntityReflection.getParsedFieldValue(entityClass, "id", idEntity);
            BaseEntity entity = (BaseEntity) service.loadById(id);
            BaseDto dto = mapper.entityToDto(entity);
            if(isSessionRequest(request) || canDelete(entity)){
                service.remove(entity);
                message= "Eliminaci&oacute;n de " + entityRef + " realizada...";
                resultData= Util.getOperationCallback(dto, message, true);
                if(dbLog) dbOperationService.save(entityRef, "delete", dto, message, true, null);
            }else{
                message= "Error, no puede eliminar el " + entityRef + " con id "+idEntity;
                resultData= Util.getOperationCallback(null, message, false);
                if(dbLog) dbOperationService.save(entityRef, "delete", dto, message, false, null);
            }
        } catch (Exception e) {
            LOGGER.error("delete " + entityRef, e);
            resultData= Util.getOperationCallback(null, "Error en eliminaci&oacute;n de " + entityRef + ": " + e.getMessage(), true);
        }
        return resultData;
    }
    
    @RequestMapping(value = "/delete/byfilter.htm", method = {RequestMethod.DELETE, RequestMethod.GET})
    @ResponseBody
    public String deleteByFilter(@RequestParam String filter, HttpServletRequest request) {
        String resultData, message;
        MassiveOperationInterface massiveOperation= null;
        try {
            filter= (!isSessionRequest(request))?getFilters(filter, null):formatFilter(filter);
            if(isSessionRequest(request) || canDeleteByFilters(new JSONObject(filter))){
                Parameters p= service.buildParameters(filter, null, null, null, null, null);
                List<BaseEntity> listEntities = service.findByParameters(p);
                List listDtos =  new ArrayList();
                message= "Proceso de eliminaci&oacute;n de "+ listDtos.size() +" "+ entityRef + " en curso...";
                if(massLog){
                    massiveOperation= massiveOperationService.start(entityRef, "DELETE", listEntities.size(), message);
                }
                for(BaseEntity entity: listEntities){
                    if(massLog && massiveOperation.getStatus().equals("Cancelado")) break;
                    BaseDto dto= mapper.entityToDto(entity);
                    try{
                        service.remove(entity);
                        message= "Eliminaci&oacute;n de " + entityRef + " realizada...";
                        if(dbLog) dbOperationService.save(entityRef, "delete", dto, message, true, massiveOperation);
                        listDtos.add(dto);
                    }catch(Exception e){
                        message= "Error, no puede eliminar el " + entityRef + " con id "+entity.getId();
                        if(dbLog) dbOperationService.save(entityRef, "delete", dto, message, false, massiveOperation);
                    }
                }
                message= "Eliminaci&oacute;n de "+ listDtos.size() +" "+ entityRef + " realizada...";
                resultData= Util.getResultListCallback(listDtos, (long)listDtos.size(), message, true);
                if(massLog) massiveOperationService.end(massiveOperation, message);
            }else{
                message= "Error, no puede eliminar la entidad " + entityRef + " por filtros";
                resultData= Util.getResultListCallback(null, 0L, message, false);
            }
        } catch (Exception e) {
            LOGGER.error("delete " + entityRef, e);
            resultData= Util.getResultListCallback(new ArrayList(), 0L,"Error en eliminaci&oacute;n de " + entityRef + ": " + e.getMessage(), true);
        }
        return resultData;
    }
    
    @RequestMapping(value = "/delete/byids.htm", method = {RequestMethod.DELETE, RequestMethod.GET})
    @ResponseBody
    public String deleteByIds(@RequestParam String ids, HttpServletRequest request) {
        String resultData, message;
        MassiveOperationInterface massiveOperation= null;
        try {
            int total= ids.split(",").length;
            List listDtos= new ArrayList();
            message= "Proceso de eliminaci&oacute;n de "+ total +" "+ entityRef + " en curso...";
            if(massLog){
                massiveOperation= massiveOperationService.start(entityRef, "DELETE", total, message);
            }
            for(String idEntity: ids.split(",")){
                if(massLog && massiveOperation.getStatus().equals("Cancelado")) break;
                Object id = EntityReflection.getParsedFieldValue(entityClass, "id", idEntity);
                BaseEntity entity = (BaseEntity) service.loadById(id);
                BaseDto dto= mapper.entityToDto(entity);
                try{
                    if(isSessionRequest(request) || canDelete(entity)){
                        service.remove(entity);
                        message= "Eliminaci&oacute;n de " + entityRef + " realizada...";
                        if(dbLog) dbOperationService.save(entityRef, "delete", dto, message, true, massiveOperation);
                        listDtos.add(dto);
                    }else{
                        message= "Error, no puede eliminar el " + entityRef + " con id "+idEntity;
                        if(dbLog) dbOperationService.save(entityRef, "delete", dto, message, false, massiveOperation);
                    }
                }catch(Exception e){
                    message= "Error, no puede eliminar el " + entityRef + " con id "+idEntity;
                    if(dbLog) dbOperationService.save(entityRef, "delete", dto, message, false, massiveOperation);
                }
            }
            message= "Eliminaci&oacute;n de "+ listDtos.size() +" "+ entityRef + " realizada...";
            resultData= Util.getResultListCallback(listDtos, (long)listDtos.size(), message, true);
            if(massLog) massiveOperationService.end(massiveOperation, message);
        } catch (Exception e) {
            LOGGER.error("delete " + entityRef, e);
            resultData= Util.getResultListCallback(new ArrayList(), 0L,"Error en eliminaci&oacute;n de " + entityRef + ": " + e.getMessage(), true);
        }
        return resultData;
    }
    
    @RequestMapping(value = "/import.htm", method = RequestMethod.POST)
    @ResponseBody
    public byte[] importData(@RequestParam(required= false) String data, HttpServletRequest request) {
        List listDtos= null;
        String resultData;
        try {
            String jsonData= data;
            if(jsonData==null){
                jsonData = IOUtils.toString(request.getInputStream());
            }
            
            List<BaseEntity> entities= new ArrayList<>();
            JSONArray array;
            
            JSONObject object= new JSONObject(jsonData);
            array= object.getJSONArray("data");
            for (int i = 0; i < array.length(); i++) {
                entities.add((BaseEntity) EntityReflection.jsonToObject(array.getJSONObject(i).toString(), entityClass));
            }
            if(!isSessionRequest(request)){
                resultData= validateImportEntities(entities, listDtos);
            }else{
                resultData= validateSessionImportEntities(entities, listDtos);
            }
        } catch (Exception e) {
            LOGGER.error("import " + entityRef, e);
            resultData= Util.getOperationCallback(null, "Error en Importaci&oacute;n de registros tipo " + entityRef + ": " + e.getMessage(), false);
        }
        return Util.getStringBytes(resultData);
    }
    
    @RequestMapping(value = "/import/{format}.htm")
    @ResponseBody
    public byte[] importData(HttpServletRequest request, @PathVariable String format) {
        List listDtos= new ArrayList();
        //1GB
        long maxFileSize= maxFileSizeToUpload * 1024 * 1024;
        String resultData="{}";
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
                    String data, jsonData=null;
                    List<BaseEntity> entities= new ArrayList<>();
                    JSONArray array;
                    switch(format){
                        case "csv":
                            data= FileService.getLinesFromInputStream(is);
                            jsonData= CSVService.csvRecordsToJSON(data, dtoClass);
                            array= new JSONArray(jsonData);
                            for (int i = 0; i < array.length(); i++) {
                                entities.add((BaseEntity) EntityReflection.readEntity(array.getJSONObject(i).toString(), entityClass));
                            }
                            break;
                        case "xlsx":
                            jsonData= ExcelService.xlsxTableToJSON(is, dtoClass);
                            array= new JSONArray(jsonData);
                            for (int i = 0; i < array.length(); i++) {
                                entities.add((BaseEntity) EntityReflection.readEntity(array.getJSONObject(i).toString(), entityClass));
                            }
                            break;
                        default:
                            if(format.equals("xml")){
                                data= FileService.getStringFromInputStream(is);
                                jsonData= XMLMarshaller.convertXMLToJSON(data);
                            }else if(format.equals("json")){
                                jsonData= FileService.getStringFromInputStream(is);
                            }
                            JSONObject object= new JSONObject(jsonData);
                            array= object.getJSONArray("data");
                            for (int i = 0; i < array.length(); i++) {
                                entities.add((BaseEntity) EntityReflection.jsonToObject(array.getJSONObject(i).toString(), entityClass));
                            }
                            break;
                    }
                    if(!isSessionRequest(request)){
                        resultData= validateImportEntities(entities, listDtos);
                    }else{
                        resultData= validateSessionImportEntities(entities, listDtos);
                    }
                }
            }
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
        BaseDto dto;
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
    public byte[] diskupload(HttpServletRequest request, @PathVariable String idEntity, @RequestParam(required= false) Boolean sessionUpload) {
        String result="";
        BaseDto dto;
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
                        is= generateResizedImages(fieldName, item.getName(), item.getContentType(), is, id, sessionUpload);
                    }
                    result+= saveFilePart(0, fieldName, item.getName(), item.getContentType(), (int)item.getSize(), is, id, sessionUpload)+"<br>";
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
    
    protected InputStream generateResizedImages(String fieldName, String fileName, String contentType, InputStream is, Object idParent, Boolean sessionUpload) throws IOException{
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
                    saveResizedImage(fieldName, fileName, contentType, width, height, os.size(), resizedIs, idParent, sessionUpload);
                }
                return FileService.bufferedImageToInputStream(original, contentType);
            }
        }
        return is;
    }
    
    @RequestMapping(value = "/multipartupload/{idParent}.htm")
    @ResponseBody
    public String multipartupload(HttpServletRequest request, @PathVariable String idParent, @RequestParam(required= false) Boolean sessionUpload) {
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
            if(fileType.equals("")){
                fileType= FilenameUtils.getExtension(fileName);
            }
            LOGGER.info("IN MULTIPARTDATA: "+slice+" "+fileName+" "+fileType+" "+fileSize);
            String message= saveFilePart(slice, fieldName, fileName, fileType, fileSize, filePart, idParent, sessionUpload);
            
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
    
    protected String validateImportEntities(List<BaseEntity> entities, List listDtos){
        String resultData;
        if(canImportData(entities)){
            listDtos= importEntities(entities);
            resultData= Util.getResultListCallback(listDtos, (long)listDtos.size(),"Importaci&oacute;n de "+listDtos.size()+" registros tipo " + entityRef + " finalizada...", true);
        }else{
            resultData= Util.getResultListCallback(listDtos, 0L, "Error, no se pudo importar los registros de tipo " + entityRef, false);
        }
        return resultData;
    }
    
    protected String validateSessionImportEntities(List<BaseEntity> entities, List listDtos){
        return validateImportEntities(entities, listDtos);
    }
    
    protected List importEntities(List<BaseEntity> entities){
        List listDtos= new ArrayList();
        MassiveOperationInterface massiveOperation= null;
        
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
        int total= entities.size();
        String message= "Almacenamiento de "+ total +" "+ entityRef + " en curso...";
        if(massLog){
            massiveOperation= massiveOperationService.start(entityRef, "SAVE", total, message);
        }
        for(BaseEntity entity: entities){
            if(massLog && massiveOperation.getStatus().equals("Cancelado")) break;
            BaseDto dto = mapper.entityToDto(entity);
            try{
                if(!mapExistingEntities.containsKey(entity.getId())){
                    service.createNatively(entity);
                    message= "Creaci&oacute;n de " + entityRef + " realizada...";
                    if(dbLog) dbOperationService.save(entityRef, "create", dto, message, true, massiveOperation);
                }else{
                    BaseEntity existingEntity= mapExistingEntities.get(entity.getId());
                    EntityReflection.updateEntity(entity, existingEntity);
                    service.update(existingEntity);
                    message= "Actualizaci&oacute;n de " + entityRef + " realizada...";
                    if(dbLog) dbOperationService.save(entityRef, "update", dto, message, true, massiveOperation);
                }
                listDtos.add(dto);
            }catch(Exception e){
                LOGGER.error("importData " + entityRef, e);
                message= "Error, no puede realizar el almacenamiento de " + entityRef;
                if(dbLog) dbOperationService.save(entityRef, "create", dto, message, false, massiveOperation);
            }
        }
        message= "Almacenamiento de "+ listDtos.size() +" "+ entityRef + " realizada...";
        if(massLog) massiveOperationService.end(massiveOperation, message);
        
        return listDtos;
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
    
    protected String saveFilePart(int slice, String fieldName, String fileName, String fileType, int fileSize, InputStream is, Object idParent, Boolean sessionUpload){
        // ABSTRACT CODE HERE
        return "Almacenamiento de archivo no implementado!!";
    }
    
    protected String saveResizedImage(String fieldName, String fileName, String fileType, int width, int height, int fileSize, InputStream is, Object idParent, Boolean sessionUpload){
        // ABSTRACT CODE HERE
        return "Almacenamiento de archivo no implementado!!";
    }

    /**
     * @param maxFileSizeToUpload the maxFileSizeToUpload to set in MB
     */
    protected void setMaxFileSizeToUpload(Long maxFileSizeToUpload) {
        this.maxFileSizeToUpload = maxFileSizeToUpload;
    }
    
    /**
     * 
     * @param reportName 
     * @param dtoReportClass 
     */
    protected void enableReport(String reportName, Class dtoReportClass){
        this.enabledReports.put(reportName, dtoReportClass);
    }
    
    /**
     * 
     * @param enableOperationLog 
     */
    protected void setEnableOperationLog(Boolean enableOperationLog){
        dbLog= (dbOperationService!=null && enableOperationLog);
        massLog= (massiveOperationService!=null && enableOperationLog);
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
            context.put("serverDomain", serverDomain);
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
    
    protected String getFilters(String filter, String reportName){
        String newFilter;
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
            jsonFilter= addReportFilter(reportName, jsonFilter);
        }else{
            jsonFilter= addSearchFilter(jsonFilter);
        }
        newFilter= jsonFilter.toString();
        
        return newFilter;
    }
    
    protected String formatFilter(String filter){
        filter= filter.replaceAll("\\(", "{").replaceAll("\\)", "}");
        filter= filter.replaceAll("<", "[").replaceAll(">", "]");
        return filter;
    }
    
    public boolean isSessionRequest(HttpServletRequest request){
        String requestURI= request.getRequestURI();
        String method= requestURI.substring(requestURI.lastIndexOf("/")+1);
        return method.startsWith("session_");
    }
    
    public JSONObject addSearchFilter(JSONObject jsonFilters){
        return jsonFilters;
    }
    
    public JSONObject addReportFilter(String reportName, JSONObject jsonFilters){
        return jsonFilters;
    }
    
    public boolean canLoad(BaseEntity entity){
        return true;
    }
    
    public boolean canCreate(BaseEntity entity){
        return true;
    }
    
    public boolean canUpdate(BaseEntity entity){
        return true;
    }
    
    public boolean canUpdateByFilters(JSONObject jsonFilters){
        return true;
    }
    
    public boolean canDelete(BaseEntity entity){
        return true;
    }
    
    public boolean canDeleteByFilters(JSONObject jsonFilters){
        return true;
    }
    
    public boolean canImportData(List<BaseEntity> entities){
        return true;
    }
    
}
