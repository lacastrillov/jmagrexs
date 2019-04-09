package com.lacv.jmagrexs.controller.rest;

import com.lacv.jmagrexs.components.TableColumnsConfig;
import com.lacv.jmagrexs.dao.Parameters;
import com.lacv.jmagrexs.reflection.EntityReflection;
import com.lacv.jmagrexs.util.Util;
import com.lacv.jmagrexs.util.XMLMarshaller;
import com.lacv.jmagrexs.dto.ResultListCallback;
import com.lacv.jmagrexs.dto.GenericTableColumn;
import com.lacv.jmagrexs.service.JdbcDirectService;
import com.lacv.jmagrexs.util.CSVService;
import com.lacv.jmagrexs.util.ExcelService;
import com.lacv.jmagrexs.util.FileService;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
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
    
    private final String LEAD_TABLE_ERROR_MESSAGE = "No existe la tabla Lead ";
    

    @RequestMapping(value = "/{tableName}/find.htm", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public HttpEntity<byte[]> find(@PathVariable String tableName, @RequestParam(required = false) String filter,
            @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir,
            @RequestParam(required = false) Long numColumns) {

        String resultData;
        try {
            List<GenericTableColumn> columns= tableColumnsConfig.getColumnsFromTableName(tableName);
            if(tableColumnsConfig.existLeadTable(tableName)){
                List<Map<String, Object>> listItems = directService.findByJSONFilters(tableName, columns, filter, page, limit, sort, dir);
                Long totalCount = directService.countByJSONFilters(tableName, columns, filter);

                resultData=Util.getResultListCallback(listItems, totalCount, "Busqueda de " + tableName + " realizada...", true);
                resultData= cleanTimeInDateFieldList(resultData, columns);
            }else{
                resultData= Util.getResultListCallback(new ArrayList(), LEAD_TABLE_ERROR_MESSAGE + tableName, false);
            }
        } catch (Exception e) {
            LOGGER.error("find " + tableName, e);
            resultData=Util.getResultListCallback(new ArrayList(), "Error buscando " + tableName + ": " + e.getMessage(), false);
        }

        return Util.getHttpEntityBytes(resultData, "json");
    }

    @RequestMapping(value = "/{tableName}/find/xml.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public HttpEntity<byte[]> findXml(@PathVariable String tableName, @RequestParam(required = false) String filter,
            @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir) {

        try {
            List<GenericTableColumn> columns= tableColumnsConfig.getColumnsFromTableName(tableName);
            if(tableColumnsConfig.existLeadTable(tableName)){
                List<Map<String, Object>> listItems = directService.findByJSONFilters(tableName, columns, filter, page, limit, sort, dir);
                Long totalCount = directService.countByJSONFilters(tableName, columns, filter);

                String resultData = Util.getResultListCallback(listItems, totalCount, "Busqueda de " + tableName + " realizada...", true);
                resultData= cleanTimeInDateFieldList(resultData, columns);
                String xml = XMLMarshaller.convertJSONToXML(resultData, ResultListCallback.class.getSimpleName());

                return Util.getHttpEntityBytes(xml, "xml");
            }else{
                return null;
            }
        } catch (Exception e) {
            LOGGER.error("find " + tableName, e);
            return null;
        }
    }

    @RequestMapping(value = "/{tableName}/find/xls.htm", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public void findXls(@PathVariable String tableName, @RequestParam(required = false) String filter,
            @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir,
            HttpServletResponse response) {
        
        try {
            List<GenericTableColumn> columns= tableColumnsConfig.getColumnsFromTableName(tableName);
            if(tableColumnsConfig.existLeadTable(tableName)){
                List<Map<String, Object>> listItems = directService.findByJSONFilters(tableName, columns, filter, null, null, sort, dir);

                response.setContentType("application/xls");
                response.setHeader("Content-Disposition", "attachment; filename=\""+ tableName + "_report.xls\"");
                ExcelService.generateExcelReport(listItems, response.getOutputStream(), columns);
            }
        } catch (Exception e) {
            LOGGER.error("find " + tableName, e);
        }
    }
    
    @RequestMapping(value = "/{tableName}/find/csv.htm", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public void findCsv(@PathVariable String tableName, @RequestParam(required = false) String filter,
            @RequestParam(required = false) Long limit, @RequestParam(required = false) Long page,
            @RequestParam(required = false) String sort, @RequestParam(required = false) String dir,
            HttpServletResponse response) {
        
        try {
            List<GenericTableColumn> columns= tableColumnsConfig.getColumnsFromTableName(tableName);
            if(tableColumnsConfig.existLeadTable(tableName)){
                List<Map<String, Object>> listItems = directService.findByJSONFilters(tableName, columns, filter, null, null, sort, dir);

                response.setContentType("text/csv; charset=utf-8");
                response.setHeader("Content-Disposition", "attachment; filename=\""+ tableName + "_report.csv\"");
                response.getWriter().print(CSVService.generateCSVReport(listItems, columns));
            }
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
            if(tableColumnsConfig.existLeadTable(tableName)){
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
                resultData= cleanTimeInDateFieldEntity(resultData, columns);
            }else{
                resultData= Util.getOperationCallback(null, LEAD_TABLE_ERROR_MESSAGE + tableName, false);
            }
        } catch (Exception e) {
            LOGGER.error("create " + tableName, e);
            resultData= Util.getOperationCallback(null, "Error en creaci&oacute;n de " + tableName + ": " + e.getMessage(), false);
        }
        return Util.getStringBytes(resultData);
    }

    @RequestMapping(value = "/{tableName}/update.htm", method = {RequestMethod.PUT, RequestMethod.POST})
    @ResponseBody
    public byte[] update(@PathVariable String tableName, @RequestParam(required= false) String data, HttpServletRequest request) {
        String resultData;
        try {
            List<GenericTableColumn> columns= tableColumnsConfig.getColumnsFromTableName(tableName);
            if(tableColumnsConfig.existLeadTable(tableName)){
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
                    resultData= cleanTimeInDateFieldEntity(resultData, columns);
                }else{
                    return this.create(tableName, data, request);
                }
            }else{
                resultData= Util.getOperationCallback(null, LEAD_TABLE_ERROR_MESSAGE + tableName, false);
            }
        } catch (Exception e) {
            LOGGER.error("update " + tableName, e);
            resultData= Util.getOperationCallback(null, "Error en actualizaci&oacute;n de " + tableName + ": " + e.getMessage(), false);
        }
        return Util.getStringBytes(resultData);
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
    public byte[] load(@PathVariable String tableName, @RequestParam String idEntity) {
        String resultData;
        try {
            List<GenericTableColumn> columns= tableColumnsConfig.getColumnsFromTableName(tableName);
            if(tableColumnsConfig.existLeadTable(tableName)){
                Map<String, Object> entity = directService.loadByParameter(tableName, "id", idEntity);

                resultData= Util.getOperationCallback(entity, "Carga de " + tableName + " realizada...", true);
                resultData= cleanTimeInDateFieldEntity(resultData, columns);
            }else{
                resultData= Util.getOperationCallback(null, LEAD_TABLE_ERROR_MESSAGE + tableName, true);
            }
        } catch (Exception e) {
            LOGGER.error("load " + tableName, e);
            resultData= Util.getOperationCallback(null, "Error en carga de " + tableName + ": " + e.getMessage(), true);
        }
        return Util.getStringBytes(resultData);
    }

    @RequestMapping(value = "/{tableName}/delete.htm", method = {RequestMethod.DELETE, RequestMethod.GET})
    @ResponseBody
    public String delete(@PathVariable String tableName, @RequestParam String idEntity) {
        try {
            if(tableColumnsConfig.existLeadTable(tableName)){
                Map<String, Object> entity = directService.loadByParameter(tableName, "id", idEntity);

                directService.removeByParameter(tableName, "id", idEntity);
                return Util.getOperationCallback(entity, "Eliminaci&oacute;n de " + tableName + " realizada...", true);
            }else{
                return Util.getOperationCallback(null, LEAD_TABLE_ERROR_MESSAGE + tableName, true);
            }
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
            if(tableColumnsConfig.existLeadTable(tableName)){
                List<Map<String, Object>> listItems = directService.findByJSONFilters(tableName, columns, filter, null, null, null, null);

                for(Map<String, Object> entity: listItems){
                    directService.removeByParameter(tableName, "id", entity.get("id"));
                }
                return Util.getResultListCallback(listItems, (long)listItems.size(),"Eliminaci&oacute;n de " + tableName + " realizada...", true);
            }else{
                return Util.getResultListCallback(new ArrayList(), 0L, LEAD_TABLE_ERROR_MESSAGE + tableName, true);
            }
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
        int totalRecords=0;
        try {
            List<GenericTableColumn> columns= tableColumnsConfig.getColumnsFromTableName(tableName);
            if(tableColumnsConfig.existLeadTable(tableName)){
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
                        List<Map<String, Object>> entities= new ArrayList<>();
                        JSONArray array;
                        switch(format){
                            case "csv":
                                data= FileService.getLinesFromInputStream(is);
                                csvData= CSVService.csvRecordsToJSON(data, columns);
                                array= new JSONArray(csvData);
                                for (int i = 0; i < array.length(); i++) {
                                    entities.add(EntityReflection.readEntity(array.getJSONObject(i).toString(), columns));
                                }
                                break;
                            case "xls":
                                xlsData= ExcelService.xlsTableToJSON(is, columns);
                                array= new JSONArray(xlsData);
                                for (int i = 0; i < array.length(); i++) {
                                    entities.add(EntityReflection.readEntity(array.getJSONObject(i).toString(), columns));
                                }
                                break;
                            case "xml":
                                data= FileService.getStringFromInputStream(is);
                                jsonData= XMLMarshaller.convertXMLToJSON(data);
                            case "json":
                                JSONObject object= new JSONObject(jsonData);
                                array= object.getJSONArray("data");
                                for (int i = 0; i < array.length(); i++) {
                                    entities.add(EntityReflection.readEntity(array.getJSONObject(i).toString(), columns));
                                }
                                break;
                        }

                        //Buscar entidades existentes
                        List ids= new ArrayList<>();
                        for(Map<String, Object> newEntity: entities){
                            ids.add(newEntity.get("id"));
                        }

                        Parameters p= new Parameters();
                        p.whereIn("id", ids.toArray());
                        List<Map<String, Object>> existingEntities = directService.findByParameters(tableName, p);
                        Map<Object, Map<String, Object>> mapExistingEntities= new HashMap();
                        for(Map<String, Object> entity: existingEntities){
                            mapExistingEntities.put(entity.get("id"), entity);
                        }

                        //Insertar o actualizar la entidad
                        for(Map<String, Object> entity: entities){
                            try{
                                if(!mapExistingEntities.containsKey(entity.get("id"))){
                                    directService.create(tableName, entity);
                                }else{
                                    Map<String, Object> existingEntity= mapExistingEntities.get(entity.get("id"));
                                    EntityReflection.updateMap(entity, existingEntity);
                                    directService.updateByParameter(tableName, existingEntity, "id", entity.get("id"));
                                }
                                listDtos.add(entity);
                                totalRecords++;
                            }catch(Exception e){
                                LOGGER.error("importData " + tableName, e);
                            }
                        }
                    }
                }

                resultData= Util.getResultListCallback(listDtos, (long)listDtos.size(),"Importaci&oacute;n de "+totalRecords+" registros tipo " + tableName + " finalizada...", true);
            }else{
                resultData= Util.getOperationCallback(null, LEAD_TABLE_ERROR_MESSAGE + tableName, false);
            }
        } catch (Exception e) {
            LOGGER.error("importData " + tableName, e);
            resultData= Util.getOperationCallback(null, "Error en importaci&oacute;n de registros tipo " + tableName + ": " + e.getMessage(), false);
        }
        return Util.getStringBytes(resultData);
    }
    
    @RequestMapping(value = "/{tableName}/upload/{idEntity}.htm")
    @ResponseBody
    public byte[] upload(HttpServletRequest request, @PathVariable String tableName, @PathVariable String idEntity) {
        String result="";
        //50MB
        long maxFileSize= maxFileSizeToUpload * 1024 * 1024;

        String resultData;
        try {
            if(tableColumnsConfig.existLeadTable(tableName)){
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
            }else{
                resultData= Util.getOperationCallback(null, LEAD_TABLE_ERROR_MESSAGE + tableName, false);
            }
        } catch (Exception e) {
            LOGGER.error("upload " + tableName, e);
            resultData= Util.getOperationCallback(null, "Error en actualizaci&oacute;n de " + tableName + ": " + e.getMessage(), false);
        }
        return Util.getStringBytes(resultData);
    }
    
    @RequestMapping(value = "/{tableName}/diskupload/{idEntity}.htm")
    @ResponseBody
    public byte[] diskupload(HttpServletRequest request, @PathVariable String tableName, @PathVariable String idEntity) {
        String result="";
        //50MB
        long maxFileSize= maxFileSizeToUpload * 1024 * 1024;

        String resultData;
        try {
            if(tableColumnsConfig.existLeadTable(tableName)){
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
                        result+= saveFilePart(tableName, fieldName, item.getName(), item.getContentType(), (int)item.getSize(), is, id)+"<br>";
                    }
                }

                Map<String, Object> entity = directService.loadByParameter(tableName, "id", id);

                resultData= Util.getOperationCallback(entity, result, true);
            }else{
                resultData= Util.getOperationCallback(null, LEAD_TABLE_ERROR_MESSAGE + tableName, false);
            }
        } catch (Exception e) {
            LOGGER.error("upload " + tableName, e);
            resultData= Util.getOperationCallback(null, "Error en actualizaci&oacute;n de " + tableName + ": " + e.getMessage(), false);
        }
        return Util.getStringBytes(resultData);
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
    
    private String cleanTimeInDateFieldList(String data, List<GenericTableColumn> columns){
        List<String>dateFields= new ArrayList<>();
        for(GenericTableColumn column: columns){
            if(column.getDataType().equals("java.util.Date") && column.getFieldType()==null){
                dateFields.add(column.getColumnAlias());
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
    
    private String cleanTimeInDateFieldEntity(String data, List<GenericTableColumn> columns){
        List<String>dateFields= new ArrayList<>();
        for(GenericTableColumn column: columns){
            if(column.getDataType().equals("java.util.Date") && column.getFieldType()==null){
                dateFields.add(column.getColumnAlias());
            }
        }
        JSONObject result= new JSONObject(data);
        JSONObject entityJson= result.getJSONObject("data");
        if(dateFields.size()>0){
            for(int j=0; j<dateFields.size(); j++){
                String fieldName= dateFields.get(j);
                if(!entityJson.isNull(fieldName)){
                    String date= entityJson.getString(fieldName);
                    result.getJSONObject("data").put(fieldName, date.split(" ")[0]);
                }
            }
            return result.toString();
        }
        return data;
    }

}
