/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lacv.jmagrexs.modules.leadtable.controllers.rest;


import com.lacv.jmagrexs.components.TableColumnsConfig;
import com.lacv.jmagrexs.modules.leadtable.mappers.TableColumnMapper;
import com.lacv.jmagrexs.modules.leadtable.services.TableColumnService;
import com.lacv.jmagrexs.controller.rest.RestEntityController;
import com.lacv.jmagrexs.dto.GenericTableColumn;
import com.lacv.jmagrexs.service.JdbcDirectService;
import com.lacv.jmagrexs.util.Formats;
import com.lacv.jmagrexs.modules.leadtable.entities.TableColumn;
import java.nio.charset.StandardCharsets;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author lcastrillo
 */
@Controller
@RequestMapping(value="/rest/tableColumn")
public class TableColumnRestController extends RestEntityController {
    
    @Autowired
    TableColumnService tableColumnService;
    
    @Autowired
    JdbcDirectService jdbcDirectService;
    
    @Autowired
    TableColumnsConfig tableColumnsConfig;
    
    @Autowired
    TableColumnMapper tableColumnMapper;
    
    
    @PostConstruct
    public void init(){
        super.addControlMapping("tableColumn", tableColumnService, tableColumnMapper);
    }
    
    @RequestMapping(value = "/create.htm", method = RequestMethod.POST)
    @ResponseBody
    @Override
    public byte[] create(@RequestParam(required= false) String data, HttpServletRequest request) {
        JSONObject jsonObject=null;
        try {
            String jsonData= data;
            if(jsonData==null){
                jsonData = IOUtils.toString(request.getInputStream());
            }
            jsonObject= new JSONObject(jsonData);
            String columnAlias= formatColumnAlias(jsonObject.getString("columnAlias"));
            jsonObject.put("columnAlias", columnAlias);
        } catch (Exception e) {
            LOGGER.error("update " + entityRef, e);
        }
        
        byte[] result= super.create(jsonObject.toString(), request);
        
        try{
            JSONObject jsonResult= new JSONObject(new String(result, StandardCharsets.UTF_8));
            if(jsonResult.getBoolean("success")){
                JSONObject jsonColumn= jsonResult.getJSONObject("data");
                TableColumn tableColumn= tableColumnService.loadById(jsonColumn.getInt("id"));
                String tableName= tableColumn.getLeadTable().getTableAlias();
                
                GenericTableColumn column= new GenericTableColumn();
                column.setColumnAlias(jsonColumn.getString("columnAlias"));
                column.setDataType(jsonColumn.getString("dataType"));
                column.setDataTypeDB(Formats.getDatabaseType(jsonColumn.getString("dataType")));
                if(jsonColumn.getString("dataType").equals("java.lang.String")){
                    column.setColumnSize(jsonColumn.getInt("columnSize"));
                }
                if(jsonColumn.has("notNull")){
                    column.setNotNull(jsonColumn.getBoolean("notNull"));
                }

                jdbcDirectService.addTableColumn(tableName, column);
                
                tableColumnsConfig.updateColumnsConfig(tableName);
            }
        }catch(Exception e){
            LOGGER.error("create " + entityRef, e);
        }
        
        return result;
    }

    @RequestMapping(value = "/update.htm", method = {RequestMethod.PUT, RequestMethod.POST})
    @ResponseBody
    @Override
    public byte[] update(@RequestParam(required= false) String data, HttpServletRequest request) {
        String oldColumnAlias="";
        String tableName="";
        JSONObject jsonObject=null;
        try {
            String jsonData= data;
            if(jsonData==null){
                jsonData = IOUtils.toString(request.getInputStream());
            }
            jsonObject= new JSONObject(jsonData);
            if(jsonObject.has("columnAlias")){
                String columnAlias= formatColumnAlias(jsonObject.getString("columnAlias"));
                jsonObject.put("columnAlias", columnAlias);
            }
            TableColumn tableColumn= tableColumnService.loadById(jsonObject.getInt("id"));
            oldColumnAlias= tableColumn.getColumnAlias();
            tableName= tableColumn.getLeadTable().getTableAlias();
        } catch (Exception e) {
            LOGGER.error("update " + entityRef, e);
        }
        
        byte[] result= super.update(jsonObject.toString(), request);
        
        try{
            JSONObject jsonResult= new JSONObject(new String(result, StandardCharsets.UTF_8));
            if(jsonResult.getBoolean("success")){
                JSONObject jsonColumn= jsonResult.getJSONObject("data");
                
                GenericTableColumn column= new GenericTableColumn();
                column.setColumnAlias(jsonColumn.getString("columnAlias"));
                column.setDataTypeDB(Formats.getDatabaseType(jsonColumn.getString("dataType")));
                if(jsonColumn.getString("dataType").equals("java.lang.String")){
                    column.setColumnSize(jsonColumn.getInt("columnSize"));
                }
                if(jsonColumn.has("notNull")){
                    column.setNotNull(jsonColumn.getBoolean("notNull"));
                }
                
                jdbcDirectService.changeTableColumn(tableName, oldColumnAlias, column);
                
                tableColumnsConfig.updateColumnsConfig(tableName);
            }
        }catch(Exception e){
            LOGGER.error("update " + entityRef, e);
        }
        
        return result;
    }
    
    @RequestMapping(value = "/delete.htm", method = {RequestMethod.DELETE, RequestMethod.GET})
    @ResponseBody
    @Override
    public String delete(@RequestParam String idEntity) {
        String tableName="";
        String columnAlias= "";
        try {
            TableColumn tableColumn= tableColumnService.loadById(new Integer(idEntity));
            columnAlias= tableColumn.getColumnAlias();
            tableName= tableColumn.getLeadTable().getTableAlias();
        } catch (Exception e) {
            LOGGER.error("update " + entityRef, e);
        }
        
        String result= super.delete(idEntity);
        
        try{
            JSONObject jsonResult= new JSONObject(result);
            if(jsonResult.getBoolean("success")){
                jdbcDirectService.dropTableColumn(tableName, columnAlias);
                
                tableColumnsConfig.updateColumnsConfig(tableName);
            }
        }catch(Exception e){
            LOGGER.error("update " + entityRef, e);
        }
        
        return result;
    }
    
    private String formatColumnAlias(String originalName){
        return Formats.stripAccents(originalName).toLowerCase().replaceAll(" ", "_");
    }
    
}
