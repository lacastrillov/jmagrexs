package com.lacv.jmagrexs.dao;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import com.lacv.jmagrexs.dto.GenericTableColumn;

/**
 * Abstract DAO that handles commons DAO tasks.
 *
 * @author lacastrillov@gmail.com
 *
 */
public class JdbcDirectRepository {
    
    protected NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    
    protected JdbcTemplate jdbcTemplate;
    
    protected String dbEngine= "MySQL";
    
    public static final String MY_SQL="MySQL", SQL_SERVER="SQLServer", ORACLE="Oracle";
    
    private final String PAGINATE_QUERY="SELECT * FROM (SELECT ROWNUM R, A.* FROM ( %s ) A  WHERE ROWNUM <= %s )  WHERE R >= %s";
    
    
    /**
     *
     * @param dataSource
     */
    public void setDataSource(DataSource dataSource){
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcTemplate= new JdbcTemplate(dataSource);
    }

    /**
     * 
     * @return dbEngine
     */
    public String getDbEngine() {
        return dbEngine;
    }

    /**
     * 
     * @param dbEngine 
     */
    public void setDbEngine(String dbEngine) {
        this.dbEngine = dbEngine;
    }
    
    /**
     * *************************************************************************
     * ********** JDBC BY TABLE NAME *******************************************
     */
    
    /**
     *
     * @param tableName
     * @param data
     */
    public void create(String tableName, Map<String,Object> data) {
        StringBuilder sql = new StringBuilder("");
        StringBuilder columnsSql = new StringBuilder("");
        StringBuilder valuesSql = new StringBuilder("");
        MapSqlParameterSource mapParameters = new MapSqlParameterSource();
        
        if (data != null) {
            for (Map.Entry<String, Object> entry : data.entrySet()){
                String parameter = entry.getKey();
                Object value = entry.getValue();
                mapParameters.addValue(parameter, value);
                
                columnsSql.append(parameter).append(", ");
                valuesSql.append(":").append(parameter).append(", ");
            }
            
            sql.append("INSERT INTO ").append(tableName);
            sql.append(" (").append(columnsSql.substring(0, columnsSql.length()-2)).append(")");
            sql.append(" VALUES ( ").append(valuesSql.substring(0, valuesSql.length()-2)).append(" )");

            namedParameterJdbcTemplate.update(sql.toString(), mapParameters);
        }
    }
    
    /**
     *
     * @param tableName
     * @param items
     */
    public void massiveCreate(String tableName, List<Map<String,Object>> items) {
        StringBuilder sql = new StringBuilder("");
        StringBuilder columnsSql = new StringBuilder("");
        StringBuilder valuesSql = new StringBuilder("");
        MapSqlParameterSource mapParameters = new MapSqlParameterSource();
        List<String> columns= new ArrayList<>();
        
        if (items.size()>0) {
            for (Map.Entry<String, Object> entry : items.get(0).entrySet()){
                String parameter = entry.getKey();
                columns.add(parameter);
                columnsSql.append(parameter).append(", ");
            }
            for(int i=0; i<items.size(); i++){
                valuesSql.append("(");
                Map<String,Object> data= items.get(i);
                for (int j=0; j<columns.size(); j++){
                    String parameter= columns.get(j);
                    Object value = data.get(parameter);
                    mapParameters.addValue(parameter+i, value);
                    valuesSql.append(":").append(parameter).append(i);
                    if(j<columns.size()-1){
                        valuesSql.append(", ");
                    }
                }
                valuesSql.append(")");
                if(i<items.size()-1){
                    valuesSql.append(", ");
                }
            }
            
            sql.append("INSERT INTO ").append(tableName);
            sql.append(" (").append(columnsSql.substring(0, columnsSql.length()-2)).append(")");
            sql.append(" VALUES ").append(valuesSql);

            namedParameterJdbcTemplate.update(sql.toString(), mapParameters);
        }
    }
    
    /**
     * 
     * @param tableName
     * @param data
     * @param parameter
     * @param value
     * @return 
     */
    public int updateByParameter(String tableName, Map<String,Object> data, String parameter, Object value){
        MapSqlParameterSource mapParameters = new MapSqlParameterSource();
        
        StringBuilder sql = new StringBuilder("UPDATE " + tableName + " AS o");

        sql.append(getUpdateQuery(data, mapParameters));

        sql.append(" WHERE ").append(parameter).append("=:").append(parameter).append("_c");
        
        mapParameters.addValue(parameter+"_c", value);

        return namedParameterJdbcTemplate.update(sql.toString(), mapParameters);
    }
    
    /**
     *
     * @param tableName
     * @param parameter
     * @param value
     * @return
     */
    public int removeByParameter(String tableName, String parameter, Object value){
        MapSqlParameterSource mapParameters = new MapSqlParameterSource();
        
        StringBuilder sql = new StringBuilder("DELETE FROM " + tableName + " ");
        
        sql.append(" WHERE ").append(parameter).append("=:").append(parameter).append("_c");
        
        mapParameters.addValue(parameter+"_c", value);
         
        return namedParameterJdbcTemplate.update(sql.toString(), mapParameters);
    }
    
    /**
     *
     * @param tableName
     * @param parameters
     * @return
     */
    public Map<String, Object> loadByParameters(String tableName, Parameters parameters) {
        List<Map<String, Object>> resultList = findByParameters(tableName, parameters);
        if (!resultList.isEmpty() && resultList.size() == 1) {
            return resultList.get(0);
        }
        return null;
    }
    
    /**
     *
     * @param tableName
     * @param parameters
     * @param c
     * @return
     */
    public Object loadByParameters(String tableName, Parameters parameters, Class c) {
        List<Object> resultList = findByParameters(tableName, parameters, c);
        if (!resultList.isEmpty() && resultList.size() == 1) {
            return resultList.get(0);
        }
        return null;
    }
    
    /**
     *
     * @param tableName
     * @param parameters
     * @return
     */
    public List<Map<String, Object>> findByParameters(String tableName, Parameters parameters) {
        MapSqlParameterSource mapParameters = new MapSqlParameterSource();
        
        StringBuilder sql = new StringBuilder("SELECT * FROM " + tableName + " o");

        sql.append(getFilterQuery(parameters, mapParameters));

        sql.append(getOrderQuery(parameters.getOrderByParameters()));

        sql= getPaginateQuery(sql, parameters);

        parameters.setTotalResults(countByParameters(tableName, parameters));
        
        System.out.println("SQL :: "+sql.toString());

        return namedParameterJdbcTemplate.queryForList(sql.toString(), mapParameters);
    }
    
    /**
     *
     * @param tableName
     * @param parameters
     * @param c
     * @return
     */
    public List<Object> findByParameters(String tableName, Parameters parameters, Class c) {
        MapSqlParameterSource mapParameters = new MapSqlParameterSource();
        
        StringBuilder sql = new StringBuilder("SELECT * FROM " + tableName + " o");

        sql.append(getFilterQuery(parameters, mapParameters));

        sql.append(getOrderQuery(parameters.getOrderByParameters()));

        sql= getPaginateQuery(sql, parameters);

        parameters.setTotalResults(countByParameters(tableName, parameters));
        
        System.out.println("SQL :: "+sql.toString());

        return namedParameterJdbcTemplate.query(sql.toString(), mapParameters, new BeanPropertyRowMapper(c));
    }
    
    /**
     *
     * @param tableName
     * @param parameters
     * @return
     */
    public Long countByParameters(String tableName, Parameters parameters){
        MapSqlParameterSource mapParameters = new MapSqlParameterSource();
        
        StringBuilder sql = new StringBuilder("SELECT count(*) FROM " + tableName + " o");

        sql.append(getFilterQuery(parameters, mapParameters));

        System.out.println("SQL :: "+sql.toString());
        
        return namedParameterJdbcTemplate.queryForObject(sql.toString(), mapParameters, Long.class);
    }

    /**
     *
     * @param tableName
     * @param parameters
     * @return
     */
    public int updateByParameters(String tableName, Parameters parameters){
        MapSqlParameterSource mapParameters = new MapSqlParameterSource();
        
        StringBuilder sql = new StringBuilder("UPDATE " + tableName + " AS o");

        sql.append(getUpdateQuery(parameters.getUpdateValueParameters(), mapParameters));

        sql.append(getFilterQuery(parameters, mapParameters));

        return namedParameterJdbcTemplate.update(sql.toString(), mapParameters);
    }

    /**
     *
     * @param tableName
     * @param parameters
     * @return
     */
    public int removeByParameters(String tableName, Parameters parameters){
        MapSqlParameterSource mapParameters = new MapSqlParameterSource();
        
        StringBuilder sql = new StringBuilder("DELETE FROM " + tableName + " ");
        
        sql.append(getFilterQuery(parameters, mapParameters));
         
        return namedParameterJdbcTemplate.update(sql.toString(), mapParameters);
    }
    
    /**
     * 
     * @param parameters
     * @param mapParameters
     * @return 
     */
    public String getFilterQuery(Parameters parameters, MapSqlParameterSource mapParameters) {
        StringBuilder sql = new StringBuilder("");
        boolean parametersSet = false;
        int i, numParameters;

        /********************************************************************************************
         * [0] Agregando Parametros: valueMap
         ********************************************************************************************/
        numParameters = parameters.getValueMapParameters().size();
        if (numParameters > 0) {
            for (Map.Entry<String, Object> entry : parameters.getValueMapParameters().entrySet()) {
                String parameter = entry.getKey();
                Object value = entry.getValue();

                mapParameters.addValue(parameter, value);
            }
        }

        /********************************************************************************************
         * [1] Agregando Parametros: compare
         ********************************************************************************************/
        List<Map<String, Object[]>> compareParameters = new ArrayList<>();
        compareParameters.add(parameters.getEqualParameters());
        compareParameters.add(parameters.getGreaterThanParameters());
        compareParameters.add(parameters.getGreaterThanOrEqualParameters());
        compareParameters.add(parameters.getLessThanParameters());
        compareParameters.add(parameters.getLessThanOrEqualParameters());
        compareParameters.add(parameters.getDifferentThanParameters());

        for (int k = 0; k < compareParameters.size(); k++) {
            Map<String, Object[]> compareParameter = compareParameters.get(k);
            numParameters = compareParameter.entrySet().size();
            if (numParameters > 0) {
                if (parametersSet) {
                    sql.append(" AND ");
                } else {
                    sql.append(" WHERE ");
                    parametersSet = true;
                }
                i = 0;
                for (Map.Entry<String, Object[]> entry : compareParameter.entrySet()) {
                    String parameter = entry.getKey();
                    String parameterRef= parameter.replaceAll("\\.", "_") + "_c" + k + "_" + i;
                    Object[] data = entry.getValue();

                    mapParameters.addValue(parameterRef, data[1]);
                    sql.append("o.").append(parameter).append(data[0]).append(":").append(parameterRef);

                    if (i < numParameters - 1) {
                        sql.append(" AND ");
                    }
                    i++;
                }
            }
        }

        /********************************************************************************************
         * [2] Agregando Parametros: is
         ********************************************************************************************/
        numParameters = parameters.getIsParameters().size();
        if (numParameters > 0) {
            if (parametersSet) {
                sql.append(" AND ");
            } else {
                sql.append(" WHERE ");
                parametersSet = true;
            }
            i = 0;
            for (Map.Entry<String, String> entry : parameters.getIsParameters().entrySet()) {
                String parameter = entry.getKey();
                Object value = entry.getValue();

                sql.append("o.").append(parameter).append(" is ").append(value);

                if (i < numParameters - 1) {
                    sql.append(" AND ");
                }
                i++;
            }
        }

        /********************************************************************************************
         * [4] Agregando Parametros: like
         ********************************************************************************************/
        numParameters = parameters.getLikeParameters().entrySet().size();
        if (numParameters > 0) {
            if (parametersSet) {
                sql.append(" AND ");
            } else {
                sql.append(" WHERE ");
                parametersSet = true;
            }
            i = 0;
            for (Map.Entry<String, String> entry : parameters.getLikeParameters().entrySet()) {
                String parameter = entry.getKey();
                String value = entry.getValue();

                mapParameters.addValue(parameter + "_l" + i, "%" + value + "%");
                sql.append("o.").append(parameter).append(" like :").append(parameter).append("_l").append(i);

                if (i < numParameters - 1) {
                    sql.append(" AND ");
                }
                i++;
            }
        }

        /********************************************************************************************
         * [5] Agregando Parametros: contain
         ********************************************************************************************/
        List<Map<String, Object[]>> containParameters = new ArrayList<>();
        containParameters.add(parameters.getInParameters());
        containParameters.add(parameters.getNotInParameters());

        for (int k = 0; k < containParameters.size(); k++) {
            Map<String, Object[]> containParameter = containParameters.get(k);
            numParameters = containParameter.entrySet().size();
            if (numParameters > 0) {
                if (parametersSet) {
                    sql.append(" AND ");
                } else {
                    sql.append(" WHERE ");
                    parametersSet = true;
                }
                i = 0;
                for (Map.Entry<String, Object[]> entry : containParameter.entrySet()) {
                    String parameter = entry.getKey();
                    Object[] values = entry.getValue();

                    sql.append("o.").append(parameter);
                    if (k == 0) {
                        sql.append(" in (");
                    } else {
                        sql.append(" not in (");
                    }
                    for (int j = 0; j < values.length; j++) {
                        mapParameters.addValue(parameter + "_i" + j, values[j]);
                        sql.append(":").append(parameter).append("_i").append(j);
                        if (j < values.length - 1) {
                            sql.append(",");
                        }
                    }
                    sql.append(")");

                    if (i < numParameters - 1) {
                        sql.append(" AND ");
                    }
                    i++;
                }
            }
        }

        /********************************************************************************************
         * [6] Agregando Parametros: between
         ********************************************************************************************/
        numParameters = parameters.getBetweenParameters().entrySet().size();
        if (numParameters > 0) {
            if (parametersSet) {
                sql.append(" AND ");
            } else {
                sql.append(" WHERE ");
                parametersSet = true;
            }
            i = 0;
            for (Map.Entry<String, Object[]> entry : parameters.getBetweenParameters().entrySet()) {
                String parameter = entry.getKey();
                Object[] range = entry.getValue();

                mapParameters.addValue(parameter + "_b0", range[0]);
                mapParameters.addValue(parameter + "_b1", range[1]);

                sql.append("o.").append(parameter).append(" between ").append(":").append(parameter).append("_b0").append(" and ").append(":")
                        .append(parameter).append("_b1");

                if (i < numParameters - 1) {
                    sql.append(" AND ");
                }
                i++;
            }
        }
        
        /********************************************************************************************
         * [7] Agregando Parametros: query
         ********************************************************************************************/
        numParameters = parameters.getQueryParameters().entrySet().size();
        if (numParameters > 0) {
            if (parametersSet) {
                sql.append(" AND ");
            } else {
                sql.append(" WHERE ");
                parametersSet = true;
            }
            i = 0;
            for (Map.Entry<String, String[]> entry : parameters.getQueryParameters().entrySet()) {
                String query = entry.getKey();
                String[] params= entry.getValue();

                mapParameters.addValue("query_"+i, "%" + query + "%");
                
                sql.append("concat(");
                for(String parameter: params){
                    sql.append("coalesce(").append("o.").append(parameter).append(",'')").append(",' ',");
                }
                sql.append("'')").append(" like :query_").append(i);

                if (i < numParameters - 1) {
                    sql.append(" AND ");
                }
                i++;
            }
        }

        return sql.toString();
    }

    /**
     * 
     * @param orderByParameters
     * @return 
     */
    public String getOrderQuery(List<String[]> orderByParameters) {
        StringBuilder sql = new StringBuilder("");

        if (orderByParameters != null && orderByParameters.size() > 0) {
            sql.append(" ORDER BY ");

            for (int i = 0; i < orderByParameters.size(); i++) {
                String[] orderBy = orderByParameters.get(i);
                sql.append("o.").append(orderBy[0]).append(" ").append(orderBy[1]);
                if (i < orderByParameters.size() - 1) {
                    sql.append(", ");
                }
            }
        }

        return sql.toString();
    }
    
    /**
     * 
     * @param sql
     * @param parameters 
     * @return sql
     */
    private StringBuilder getPaginateQuery(StringBuilder sql, Parameters parameters){
        if (parameters.getFirstResult() != null && parameters.getLastResult() != null) {
            switch(dbEngine){
                case MY_SQL:
                    sql.append(" LIMIT ").append(parameters.getFirstResult()).append(", ").append(parameters.getMaxResults());
                    break;
                case ORACLE:
                    sql = new StringBuilder(String.format(PAGINATE_QUERY, sql.toString(), parameters.getLastResult(), parameters.getFirstResult()));
                    break;
                case SQL_SERVER:
                    sql.append(" LIMIT ").append(parameters.getFirstResult()).append(", ").append(parameters.getMaxResults());
                    break;
            }
        }
        return sql;
    }
    
    /**
     * 
     * @param parameters
     * @param mapParameters
     * @return 
     */
    private String getUpdateQuery(Map<String, Object> updateValueParameters, MapSqlParameterSource mapParameters) {
        StringBuilder sql = new StringBuilder("");

        int numParameters = updateValueParameters.entrySet().size();
        if (numParameters > 0) {
            sql.append(" SET ");
            int i = 0;
            for (Map.Entry<String, Object> entry : updateValueParameters.entrySet()) {
                String parameter = entry.getKey();
                Object value = entry.getValue();

                mapParameters.addValue(parameter + "_u" + i, value);
                sql.append("o.").append(parameter).append(" = :").append(parameter).append("_u").append(i);

                if (i < numParameters - 1) {
                    sql.append(", ");
                }
                i++;
            }
        }

        return sql.toString();
    }

    /**
     * 
     * @param tableName
     * @param columns 
     */
    public void createTable(String tableName, List<GenericTableColumn> columns) {
        StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        sql.append(tableName).append(" (");
        if (columns != null) {
            for (GenericTableColumn column : columns) {
                sql.append(column.getColumnAlias()).append(" ");
                sql.append(column.getDataTypeDB()).append(" ");
                if (column.getColumnSize() != null) {
                    sql.append("(").append(column.getColumnSize()).append(") ");
                }
                if(column.isNotNull()){
                    sql.append("NOT NULL ");
                }else{
                    sql.append("NULL ");
                }
                if(column.isAutoIncrement()){
                    sql.append("AUTO_INCREMENT ");
                }else if(column.getDefaultValue()!=null){
                    sql.append("DEFAULT ").append("'").append(column.getDefaultValue()).append("'");
                }else if(!column.isNotNull()){
                    sql.append("DEFAULT NULL");
                }
                sql.append(", ");
                if(column.isPrimaryKey()){
                    sql.append("PRIMARY KEY (").append(column.getColumnAlias()).append("), ");
                }else if(column.isUnique()){
                    sql.append("UNIQUE INDEX ").append(column.getColumnAlias()).append("_UNIQUE (").append(column.getColumnAlias()).append(" ASC), ");
                }
            }
            sql= new StringBuilder(sql.toString().substring(0, sql.toString().length()-2));
        }
        sql.append(")");
        
        System.out.println(sql.toString());
        jdbcTemplate.update(sql.toString());
    }
    
    /**
     * 
     * @param tableName
     * @param newTableName 
     */
    public void changeTableName(String tableName, String newTableName){
        StringBuilder sql = new StringBuilder("ALTER TABLE ");
        sql.append(tableName).append(" RENAME TO ").append(newTableName);
        
        System.out.println(sql.toString());
        jdbcTemplate.update(sql.toString());
    }
    
    /**
     * 
     * @param tableName 
     */
    public void dropTable(String tableName){
        StringBuilder sql = new StringBuilder("DROP TABLE ");
        sql.append(tableName);
        
        changeTableName(tableName, "deleted_"+tableName);
        //jdbcTemplate.update(sql.toString());
    }
    
    /**
     * 
     * @param tableName
     * @param columnName
     * @param column 
     */
    public void changeTableColumn(String tableName, String columnName, GenericTableColumn column){
        StringBuilder sql = new StringBuilder("ALTER TABLE ");
        sql.append(tableName).append(" CHANGE COLUMN ").append(columnName).append(" ");
        sql.append(column.getColumnAlias()).append(" ");
        sql.append(column.getDataTypeDB()).append(" ");
        if (column.getColumnSize() != null) {
            sql.append("(").append(column.getColumnSize()).append(") ");
        }
        if(column.isNotNull()){
            sql.append("NOT NULL ");
        }else{
            sql.append("NULL ");
        }
        if(column.isAutoIncrement()){
            sql.append("AUTO_INCREMENT ");
        }else if(column.getDefaultValue()!=null){
            sql.append("DEFAULT ").append("'").append(column.getDefaultValue()).append("'");
        }else if(!column.isNotNull()){
            sql.append("DEFAULT NULL");
        }
        if(column.isPrimaryKey()){
            sql.append(", ").append("ADD PRIMARY KEY (").append(column.getColumnAlias()).append(")");
        }else if(column.isUnique()){
            sql.append(", ").append("ADD UNIQUE INDEX ").append(column.getColumnAlias()).append("_UNIQUE (").append(column.getColumnAlias()).append(" ASC)");
        }
        
        System.out.println(sql.toString());
        jdbcTemplate.update(sql.toString());
    }
    
    /**
     * 
     * @param tableName
     * @param column 
     */
    public void addTableColumn(String tableName, GenericTableColumn column){
        StringBuilder sql = new StringBuilder("ALTER TABLE ");
        sql.append(tableName).append(" ADD COLUMN ");
        sql.append(column.getColumnAlias()).append(" ");
        sql.append(column.getDataTypeDB()).append(" ");
        if (column.getColumnSize() != null) {
            sql.append("(").append(column.getColumnSize()).append(") ");
        }
        if(column.isNotNull()){
            sql.append("NOT NULL ");
        }else{
            sql.append("NULL ");
        }
        if(column.isAutoIncrement()){
            sql.append("AUTO_INCREMENT ");
        }else if(column.getDefaultValue()!=null){
            sql.append("DEFAULT ").append("'").append(column.getDefaultValue()).append("'");
        }else if(!column.isNotNull()){
            sql.append("DEFAULT NULL");
        }
        if(column.isPrimaryKey()){
            sql.append(", ").append("ADD PRIMARY KEY (").append(column.getColumnAlias()).append(")");
        }else if(column.isUnique()){
            sql.append(", ").append("ADD UNIQUE INDEX ").append(column.getColumnAlias()).append("_UNIQUE (").append(column.getColumnAlias()).append(" ASC)");
        }
        
        System.out.println(sql.toString());
        jdbcTemplate.update(sql.toString());
    }
    
    /**
     * 
     * @param tableName
     * @param columnName 
     */
    public void dropTableColumn(String tableName, String columnName){
        StringBuilder sql = new StringBuilder("ALTER TABLE ");
        sql.append(tableName).append(" DROP COLUMN ");
        sql.append(columnName);
        
        System.out.println(sql.toString());
        jdbcTemplate.update(sql.toString());
    }
    
}
