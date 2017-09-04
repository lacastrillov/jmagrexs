/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dot.gcpbasedot.service;

import com.dot.gcpbasedot.dao.JdbcDirectRepository;
import com.dot.gcpbasedot.dao.Parameters;
import com.dot.gcpbasedot.dto.GenericTableColumn;
import com.dot.gcpbasedot.util.FilterQueryDirectJSON;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author lacastrillov
 */
public abstract class JdbcDirectAbstractService implements JdbcDirectService {
    
    private JdbcDirectRepository jdbcDirectRepository;
    
    /**
     *
     * @param dataSource
     */
    public void setDataSource(DataSource dataSource){
        jdbcDirectRepository= new JdbcDirectRepository();
        jdbcDirectRepository.setDataSource(dataSource);
    }

    /**
     * 
     * @param tableName
     * @param data 
     */
    @Override
    @Transactional()
    public void create(String tableName, Map<String, Object> data) {
        jdbcDirectRepository.create(tableName, data);
    }
    
    /**
     * 
     * @param tableName
     * @param data
     * @param parameter
     * @param value
     * @return 
     */
    @Override
    @Transactional()
    public int updateByParameter(String tableName, Map<String,Object> data, String parameter, Object value){
        return jdbcDirectRepository.updateByParameter(tableName, data, parameter, value);
    }
    
    /**
     * 
     * @param tableName
     * @param parameter
     * @param value
     * @return 
     */
    @Override
    @Transactional()
    public int removeByParameter(String tableName, String parameter, Object value){
        return jdbcDirectRepository.removeByParameter(tableName, parameter, value);
    }
    
    /**
     * 
     * @param tableName
     * @param parameter
     * @param value
     * @return 
     */
    @Override
    public Map<String, Object> findUniqueByParameter(String tableName, String parameter, Object value){
        Parameters parameters= new Parameters();
        parameters.whereEqual(parameter, value);
        return jdbcDirectRepository.findUniqueByParameters(tableName, parameters);
    }

    /**
     * 
     * @param tableName
     * @param parameters
     * @return 
     */
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> findUniqueByParameters(String tableName, Parameters parameters) {
        return jdbcDirectRepository.findUniqueByParameters(tableName, parameters);
    }

    /**
     * 
     * @param tableName
     * @param parameters
     * @param c
     * @return 
     */
    @Override
    @Transactional(readOnly = true)
    public Object findUniqueByParameters(String tableName, Parameters parameters, Class c) {
        return jdbcDirectRepository.findUniqueByParameters(tableName, parameters, c);
    }
    
    /**
     * 
     * @param tableName
     * @param parameter
     * @param value
     * @return 
     */
    @Override
    public List<Map<String, Object>> findByParameter(String tableName, String parameter, Object value){
        Parameters parameters= new Parameters();
        parameters.whereEqual(parameter, value);
        return jdbcDirectRepository.findByParameters(tableName, parameters);
    }

    /**
     * 
     * @param tableName
     * @param parameters
     * @return 
     */
    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> findByParameters(String tableName, Parameters parameters) {
        return jdbcDirectRepository.findByParameters(tableName, parameters);
    }

    /**
     * 
     * @param tableName
     * @param parameters
     * @param c
     * @return 
     */
    @Override
    @Transactional(readOnly = true)
    public List<Object> findByParameters(String tableName, Parameters parameters, Class c) {
        return jdbcDirectRepository.findByParameters(tableName, parameters, c);
    }

    /**
     * 
     * @param tableName
     * @param parameters
     * @return 
     */
    @Override
    @Transactional(readOnly = true)
    public Long countByParameters(String tableName, Parameters parameters) {
        return jdbcDirectRepository.countByParameters(tableName, parameters);
    }

    /**
     * 
     * @param tableName
     * @param parameters
     * @return 
     */
    @Override
    @Transactional()
    public int updateByParameters(String tableName, Parameters parameters) {
        return jdbcDirectRepository.updateByParameters(tableName, parameters);
    }

    /**
     * 
     * @param tableName
     * @param parameters
     * @return 
     */
    @Override
    @Transactional()
    public int removeByParameters(String tableName, Parameters parameters) {
        return jdbcDirectRepository.removeByParameters(tableName, parameters);
    }
    
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
    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> findByJSONFilters(String tableName, List<GenericTableColumn> columns, String jsonFilters, Long page, Long limit, String sort, String dir){
        Parameters parameters= new Parameters();

        if (jsonFilters != null && !jsonFilters.equals("")) {
            parameters = FilterQueryDirectJSON.processFilters(jsonFilters, columns);
        }
        
        if(page!=null){
            parameters.setPage(page);
        }
        if(limit!=null){
            parameters.setMaxResults(limit);
        }

        if(sort!=null && !sort.equals("") && dir!=null && !dir.equals("")){
            parameters.orderBy(sort, dir);
        }
        
        List<Map<String, Object>> listEntities = jdbcDirectRepository.findByParameters(tableName, parameters);

        return listEntities;
    }
    
    /**
     *
     * @param tableName
     * @param columns
     * @param jsonFilters
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public Long countByJSONFilters(String tableName, List<GenericTableColumn> columns, String jsonFilters){
        Parameters parameters= new Parameters();

        if (jsonFilters != null && !jsonFilters.equals("")) {
            parameters = FilterQueryDirectJSON.processFilters(jsonFilters, columns);
        }

        return jdbcDirectRepository.countByParameters(tableName, parameters);
    }
    
    /**
     *
     * @param tableName
     * @param columns
     * @param jsonFilters
     * @return
     */
    @Override
    public Integer updateByJSONFilters(String tableName, List<GenericTableColumn> columns, String jsonFilters){
        Parameters parameters= new Parameters();

        if (jsonFilters != null && !jsonFilters.equals("")) {
            parameters = FilterQueryDirectJSON.processFilters(jsonFilters, columns);
        }

        return jdbcDirectRepository.updateByParameters(tableName, parameters);
    }

    /**
     * 
     * @param tableName
     * @param columns 
     */
    @Override
    @Transactional()
    public void createTable(String tableName, List<GenericTableColumn> columns) {
        jdbcDirectRepository.createTable(tableName, columns);
    }

    /**
     * 
     * @param tableName
     * @param newTableName 
     */
    @Override
    @Transactional()
    public void changeTableName(String tableName, String newTableName) {
        jdbcDirectRepository.changeTableName(tableName, newTableName);
    }

    /**
     * 
     * @param tableName 
     */
    @Override
    @Transactional()
    public void dropTable(String tableName) {
        jdbcDirectRepository.dropTable(tableName);
    }

    /**
     * 
     * @param tableName
     * @param columnName
     * @param column 
     */
    @Override
    @Transactional()
    public void changeTableColumn(String tableName, String columnName, GenericTableColumn column) {
        jdbcDirectRepository.changeTableColumn(tableName, columnName, column);
    }

    /**
     * 
     * @param tableName
     * @param column 
     */
    @Override
    @Transactional()
    public void addTableColumn(String tableName, GenericTableColumn column) {
        jdbcDirectRepository.addTableColumn(tableName, column);
    }

    /**
     * 
     * @param tableName
     * @param columnName 
     */
    @Override
    @Transactional()
    public void dropTableColumn(String tableName, String columnName) {
        jdbcDirectRepository.dropTableColumn(tableName, columnName);
    }
    
}
