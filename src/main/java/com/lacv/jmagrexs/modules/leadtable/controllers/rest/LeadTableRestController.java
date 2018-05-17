/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lacv.jmagrexs.modules.leadtable.controllers.rest;

import com.lacv.jmagrexs.modules.leadtable.mappers.LeadTableMapper;
import com.lacv.jmagrexs.modules.leadtable.services.LeadTableService;
import com.lacv.jmagrexs.controller.rest.RestEntityController;
import com.lacv.jmagrexs.dto.GenericTableColumn;
import com.lacv.jmagrexs.service.JdbcDirectService;
import com.lacv.jmagrexs.util.Formats;
import com.lacv.jmagrexs.modules.leadtable.entities.LeadTable;
import com.lacv.jmagrexs.modules.leadtable.entities.TableColumn;
import com.lacv.jmagrexs.modules.leadtable.services.TableColumnService;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
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
@RequestMapping(value="/rest/leadTable")
public class LeadTableRestController extends RestEntityController {
    
    @Autowired
    LeadTableService leadTableService;
    
    @Autowired
    TableColumnService tableColumnService;
    
    @Autowired
    JdbcDirectService jdbcDirectService;
    
    @Autowired
    LeadTableMapper leadTableMapper;
    
    
    @PostConstruct
    public void init(){
        super.addControlMapping("leadTable", leadTableService, leadTableMapper);
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
            String tableAlias= formatTableAlias(jsonObject.getString("tableAlias"));
            jsonObject.put("tableAlias", tableAlias);
        } catch (Exception e) {
            LOGGER.error("update " + entityRef, e);
        }
        
        byte[] result= super.create(jsonObject.toString(), request);
        
        try{
            JSONObject jsonResult= new JSONObject(new String(result, StandardCharsets.UTF_8));
            if(jsonResult.getBoolean("success")){
                TableColumn tableColumn= new TableColumn();
                tableColumn.setColumnAlias("id");
                tableColumn.setName("Id");
                tableColumn.setWidth(100);
                tableColumn.setColumnOrder(1);
                tableColumn.setNotNull(false);
                tableColumn.setDataType("int");
                tableColumn.setLeadTable(new LeadTable(jsonResult.getJSONObject("data").getInt("id")));
                
                tableColumnService.create(tableColumn);
                
                List<GenericTableColumn> columns= new ArrayList<>();
                GenericTableColumn idColumn= new GenericTableColumn();
                idColumn.setColumnAlias("id");
                idColumn.setDataTypeDB("INT");
                idColumn.setAutoIncrement(true);
                idColumn.setNotNull(true);
                idColumn.setPrimaryKey(true);
                columns.add(idColumn);

                String tableName= jsonResult.getJSONObject("data").getString("tableAlias");

                jdbcDirectService.createTable(tableName, columns);
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
        String oldTableAlias="";
        JSONObject jsonObject=null;
        try {
            String jsonData= data;
            if(jsonData==null){
                jsonData = IOUtils.toString(request.getInputStream());
            }
            jsonObject= new JSONObject(jsonData);
            if(jsonObject.has("tableAlias")){
                String tableAlias= formatTableAlias(jsonObject.getString("tableAlias"));
                jsonObject.put("tableAlias", tableAlias);
            }
            LeadTable leadTable= leadTableService.loadById(jsonObject.getInt("id"));
            oldTableAlias= leadTable.getTableAlias();
        } catch (Exception e) {
            LOGGER.error("update " + entityRef, e);
        }
        
        byte[] result= super.update(jsonObject.toString(), request);
        
        try{
            JSONObject jsonResult= new JSONObject(new String(result, StandardCharsets.UTF_8));
            if(jsonResult.getBoolean("success") && !jsonResult.getJSONObject("data").getString("tableAlias").equals(oldTableAlias)){
                String tableName= oldTableAlias;
                String newTableName= jsonResult.getJSONObject("data").getString("tableAlias");
                jdbcDirectService.changeTableName(tableName, newTableName);
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
        String result= super.delete(idEntity);
        
        try{
            JSONObject jsonResult= new JSONObject(result);
            if(jsonResult.getBoolean("success")){
                String tableName= jsonResult.getJSONObject("data").getString("tableAlias");

                jdbcDirectService.dropTable(tableName);
            }
        }catch(Exception e){
            LOGGER.error("update " + entityRef, e);
        }
        
        return result;
    }
    
    private String formatTableAlias(String originalName){
        if(originalName.startsWith("lt_")){
            return Formats.stripAccents(originalName).toLowerCase().replaceAll(" ", "_");
        }else{
            return "lt_"+Formats.stripAccents(originalName).toLowerCase().replaceAll(" ", "_");
        }
    }
    
}
