package com.dot.gcpbasedot.dao;

import com.dot.gcpbasedot.domain.BaseEntity;
import com.dot.gcpbasedot.reflection.ReflectionUtils;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * Abstract DAO that handles commons DAO tasks.
 *
 * @author lacastrillov@gmail.com
 * @param <T>
 *
 */
public abstract class JdbcAbstractRepository<T extends BaseEntity> {
    
    protected JdbcDirectRepository jdbcDirectRepository;
    
    protected NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    
    protected JdbcTemplate jdbcTemplate;
    
    @Autowired
    private ApplicationContext applicationContext;

    protected Map<String, String> queryMap = null;
    
    private final Class<T> persistentClass;
    
    /**
     *
     */
    public JdbcAbstractRepository() {
        jdbcDirectRepository= new JdbcDirectRepository();
        persistentClass = ReflectionUtils.getParametrizedType(this.getClass());
    }
    
    /**
     *
     * @return 
     */
    public Class<T> getPersistentClass() {
        return persistentClass;
    }
    
    /**
     *
     * @param dataSource
     */
    public void setDataSource(DataSource dataSource){
        jdbcDirectRepository.setDataSource(dataSource);
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        jdbcTemplate= new JdbcTemplate(dataSource);
        if(applicationContext.containsBeanDefinition("queries"+persistentClass.getSimpleName())){
            queryMap = (Map<String, String>)applicationContext.getBean("queries"+persistentClass.getSimpleName());
        }
    }

    public String getDbEngine() {
        return jdbcDirectRepository.getDbEngine();
    }

    public void setDbEngine(String dbEngine) {
        jdbcDirectRepository.setDbEngine(dbEngine);
    }
    
    /**
     * *************************************************************************
     * ********** JDBC BY nameQuerySource *******************************************
     */
    
    /**
     * 
     * @param nameQuerySource
     * @param parameters
     * @param c
     * @return 
     */
    public Object loadByParameters(String nameQuerySource, Parameters parameters, Class c) {
        String querySource = queryMap.get(nameQuerySource);
        
        return jdbcDirectRepository.findByParameters("("+querySource+")", parameters, c);
    }
    
    /**
     *
     * @param nameQuerySource
     * @param parameters
     * @param c
     * @return
     */
    public List<Object> findByParameters(String nameQuerySource, Parameters parameters, Class c) {
        String querySource = queryMap.get(nameQuerySource);
        
        return jdbcDirectRepository.findByParameters("("+querySource+")", parameters, c);
    }

    /**
     * 
     * @param nameQuerySource
     * @param parameters
     * @return 
     */
    public Long countByParameters(String nameQuerySource, Parameters parameters) {
        String querySource = queryMap.get(nameQuerySource);
        
        return jdbcDirectRepository.countByParameters("("+querySource+")", parameters);
    }
    
    /**
     * *************************************************************************
     * ********** JDBC BY nameQuery *******************************************
     */

    /**
     * 
     * @param nameQuery
     * @param mapParameters
     * @return 
     */
    public Map<String, Object> loadByNameQuery(String nameQuery, Map<String, Object> mapParameters) {
        String query = String.format(queryMap.get(nameQuery));

        List<Map<String, Object>> rows = namedParameterJdbcTemplate.queryForList(query, getMapSqlParameterSource(mapParameters));
        if (rows.size() > 0) {
            return rows.get(0);
        }

        return null;
    }

    /**
     * 
     * @param nameQuery
     * @param mapParameters
     * @param c
     * @return 
     */
    public Object loadByNameQuery(String nameQuery, Map<String, Object> mapParameters, Class c) {
        String query = String.format(queryMap.get(nameQuery));

        List<Object> rows = namedParameterJdbcTemplate.query(query, getMapSqlParameterSource(mapParameters),
                new BeanPropertyRowMapper(c));
        if (rows.size() > 0) {
            return rows.get(0);
        }

        return null;
    }

    /**
     * 
     * @param nameQuery
     * @param mapParameters
     * @return 
     */
    public List<Map<String, Object>> findByNameQuery(String nameQuery, Map<String, Object> mapParameters) {
        String query = String.format(queryMap.get(nameQuery));

        return namedParameterJdbcTemplate.queryForList(query, getMapSqlParameterSource(mapParameters));
    }

    /**
     * 
     * @param nameQuery
     * @param mapParameters
     * @param c
     * @return 
     */
    public List<Object> findByNameQuery(String nameQuery, Map<String, Object> mapParameters, Class c) {
        String query = String.format(queryMap.get(nameQuery));

        return namedParameterJdbcTemplate.query(query, getMapSqlParameterSource(mapParameters), new BeanPropertyRowMapper(c));
    }

    /**
     * 
     * @param mapParameters
     * @return 
     */
    private MapSqlParameterSource getMapSqlParameterSource(Map<String, Object> mapParameters) {
        MapSqlParameterSource mapSqlParameters = new MapSqlParameterSource();

        if (mapParameters != null) {
            for (Map.Entry<String, Object> entry : mapParameters.entrySet()){
                mapSqlParameters.addValue(entry.getKey(), entry.getValue());
            }
        }

        return mapSqlParameters;
    }

}
