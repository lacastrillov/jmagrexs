/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.components;

import com.lacv.jmagrexs.dao.Parameters;
import com.lacv.jmagrexs.dto.GenericTableColumn;
import com.lacv.jmagrexs.service.JdbcDirectService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author grupot
 */
//@Component
public class TableColumnsConfig {
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    JdbcDirectService jdbcDirectService;
    
    private Map<String, List<GenericTableColumn>> mapTableColumns;
    
    private String columsConfigNameQuery;
    
    private String columsConfigQuerySource;
    
    
    public void setColumsConfigNameQuery(String columsConfigNameQuery) {
        this.columsConfigNameQuery= columsConfigNameQuery;
        mapTableColumns= new HashMap<>();
        String[] ccNQ= this.columsConfigNameQuery.split("\\.");
        Map queryMap = (Map<String, String>)applicationContext.getBean(ccNQ[0]);
        columsConfigQuerySource= queryMap.get(ccNQ[1]).toString();
    }
    
    public void updateColumnsConfig(String tableName){
        Parameters p= new Parameters();
        p.whereEqual("tableAlias", tableName);
        p.orderBy("columnOrder", "ASC");
        List<Object> result= jdbcDirectService.findByParameters("("+columsConfigQuerySource+")", p, GenericTableColumn.class);
        List<GenericTableColumn> columns= new ArrayList<>();
        for(Object item: result){
            columns.add((GenericTableColumn)item);
        }
        mapTableColumns.put(tableName, columns);
    }
    
    public List<GenericTableColumn> getColumnsFromTableName(String tableName){
        if(mapTableColumns.get(tableName)==null){
            updateColumnsConfig(tableName);
        }
        return mapTableColumns.get(tableName);
    }
    
}
