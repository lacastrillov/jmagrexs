package com.lacv.jmagrexs.dao;

import com.lacv.jmagrexs.domain.BaseEntity;
import com.lacv.jmagrexs.reflection.EntityReflection;
import static com.lacv.jmagrexs.reflection.EntityReflection.getEntityAnnotatedFields;
import com.lacv.jmagrexs.reflection.ReflectionUtils;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import javax.sql.DataSource;
import org.springframework.beans.BeanWrapperImpl;
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
    
    private final Table table;
    
    private final List<Field> columnFields;
    
    private final List<Field> joinColumnFields;
    
    protected boolean embeddedId;
    
    /**
     *
     */
    public JdbcAbstractRepository() {
        jdbcDirectRepository= new JdbcDirectRepository();
        persistentClass = ReflectionUtils.getParametrizedType(this.getClass());
        table= (Table) EntityReflection.getClassAnnotation(persistentClass, Table.class);
        columnFields= getEntityAnnotatedFields(persistentClass, Column.class);
        joinColumnFields= getEntityAnnotatedFields(persistentClass, JoinColumn.class);
        embeddedId= !(getEntityAnnotatedFields(persistentClass, EmbeddedId.class).isEmpty());
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
     * ********** JDBC BY entity ***********************************************
     */
    
    /**
     *
     * @param entity
     */
    public void insert(T entity) {
        BeanWrapperImpl sourceWrapper = new BeanWrapperImpl(entity);
        Map<String,Object> data= new HashMap<>();
        
        for(Field f: columnFields){
            Column an= f.getAnnotation(Column.class);
            Object value= sourceWrapper.getPropertyValue(f.getName());
            if(value!=null){
                data.put(an.name(), value);
            }
        }
        
        for(Field f: joinColumnFields){
            JoinColumn an= f.getAnnotation(JoinColumn.class);
            BaseEntity joinEntity= (BaseEntity) sourceWrapper.getPropertyValue(f.getName());
            if(joinEntity!=null){
                data.put(an.name(), joinEntity.getId());
            }
        }
        
        jdbcDirectRepository.create(table.name(), data);
    }
    
    /**
     *
     * @param entities
     */
    public void massiveInsert(List<T> entities) {
        if(entities.size()>0){
            List<Map<String,Object>> items= new ArrayList<>();
            for(T entity: entities){
                BeanWrapperImpl sourceWrapper = new BeanWrapperImpl(entity);
                Map<String,Object> data= new HashMap<>();
                for(Field f: columnFields){
                    Column an= f.getAnnotation(Column.class);
                    Object value= sourceWrapper.getPropertyValue(f.getName());
                    if(value!=null){
                        data.put(an.name(), value);
                    }
                }
                for(Field f: joinColumnFields){
                    JoinColumn an= f.getAnnotation(JoinColumn.class);
                    BaseEntity joinEntity= (BaseEntity) sourceWrapper.getPropertyValue(f.getName());
                    if(joinEntity!=null){
                        data.put(an.name(), joinEntity.getId());
                    }
                }
                items.add(data);
            }
            
            jdbcDirectRepository.massiveCreate(table.name(), items);
        }
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
     * @param query
     * @param mapParameters
     * @return 
     */
    public List<Map<String, Object>> findByQuery(String query, Map<String, Object> mapParameters) {
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
