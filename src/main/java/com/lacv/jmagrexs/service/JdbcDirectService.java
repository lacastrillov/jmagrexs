/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.service;

import com.lacv.jmagrexs.dao.Parameters;
import com.lacv.jmagrexs.dto.GenericTableColumn;
import java.util.List;
import java.util.Map;

/**
 *
 * @author lacastrillov
 */
public interface JdbcDirectService {
    
    /**
     * 
     * @param tableName
     * @param data 
     */
    void create(String tableName, Map<String,Object> data);
    
    /**
     * 
     * @param tableName
     * @param data
     * @param parameter
     * @param value
     * @return 
     */
    int updateByParameter(String tableName, Map<String,Object> data, String parameter, Object value);
    
    /**
     * 
     * @param tableName
     * @param parameter
     * @param value
     * @return 
     */
    int removeByParameter(String tableName, String parameter, Object value);
    
    /**
     * 
     * @param tableName
     * @param parameter
     * @param value
     * @return 
     */
    Map<String, Object> loadByParameter(String tableName, String parameter, Object value);
    
    /**
     * 
     * @param tableName
     * @param parameters
     * @return 
     */
    Map<String, Object> loadByParameters(String tableName, Parameters parameters);
    
    /**
     * 
     * @param tableName
     * @param parameters
     * @param c
     * @return 
     */
    Object loadByParameters(String tableName, Parameters parameters, Class c);
    
    
    /**
     * 
     * @param tableName
     * @param parameter
     * @param value
     * @return 
     */
    List<Map<String, Object>> findByParameter(String tableName, String parameter, Object value);
    
    /**
     * 
     * @param tableName
     * @param parameters
     * @return 
     */
    List<Map<String, Object>> findByParameters(String tableName, Parameters parameters);
    
    /**
     * 
     * @param tableName
     * @param parameters
     * @param c
     * @return 
     */
    List<Object> findByParameters(String tableName, Parameters parameters, Class c);
    
    /**
     * 
     * @param tableName
     * @param parameters
     * @return 
     */
    Long countByParameters(String tableName, Parameters parameters);
    
    /**
     * 
     * @param tableName
     * @param parameters
     * @return 
     */
    int updateByParameters(String tableName, Parameters parameters);
    
    /**
     * 
     * @param tableName
     * @param parameters
     * @return 
     */
    int removeByParameters(String tableName, Parameters parameters);
    
    /**
     *
     * @param tableName
     * @param columns
     * @param jsonFilters
     * @param page
     * @param limit
     * @param sort
     * @param dir
     * @return
     */
    List<Map<String, Object>> findByJSONFilters(String tableName, List<GenericTableColumn> columns, String jsonFilters, Long page, Long limit, String sort, String dir);
    
    /**
     *
     * @param tableName
     * @param columns
     * @param jsonFilters
     * @return
     */
    Long countByJSONFilters(String tableName, List<GenericTableColumn> columns, String jsonFilters);
    
    /**
     *
     * @param tableName
     * @param columns
     * @param jsonFilters
     * @return
     */
    Integer updateByJSONFilters(String tableName, List<GenericTableColumn> columns, String jsonFilters);
    
    /**
     * 
     * @param tableName
     * @param columns 
     */
    void createTable(String tableName, List<GenericTableColumn> columns);
    
    /**
     * 
     * @param tableName
     * @param newTableName 
     */
    void changeTableName(String tableName, String newTableName);
    
    /**
     * 
     * @param tableName 
     */
    void dropTable(String tableName);
    
    /**
     * 
     * @param tableName
     * @param columnName
     * @param column 
     */
    void changeTableColumn(String tableName, String columnName, GenericTableColumn column);
    
    /**
     * 
     * @param tableName
     * @param column 
     */
    void addTableColumn(String tableName, GenericTableColumn column);
    
    /**
     * 
     * @param tableName
     * @param columnName 
     */
    void dropTableColumn(String tableName, String columnName);
    
}
