package com.lacv.jmagrexs.service;

import com.lacv.jmagrexs.annotation.QueryParam;
import com.lacv.jmagrexs.dao.GenericDao;
import com.lacv.jmagrexs.dao.Parameters;
import com.lacv.jmagrexs.domain.BaseEntity;
import com.lacv.jmagrexs.mapper.EntityMapper;
import com.lacv.jmagrexs.reflection.EntityReflection;
import com.lacv.jmagrexs.reflection.ReflectionUtils;
import com.lacv.jmagrexs.util.FilterQueryJSON;
import java.lang.reflect.Field;
import java.util.ArrayList;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementacion de servicio generico para operaciones CRUD sobre una entidad
 * particular.
 *
 * @author lacastrillov@gmail.com
 *
 * @param <T> clase de la entidad sobre la cual trabaja el servicio.
 *
 */
public abstract class EntityServiceImpl2<T extends BaseEntity> implements EntityService<T> {

    protected final String TRANSACTION_MANAGER = "TRANSACTION_MANAGER_2";

    private final Class<T> entityClass;

    public EntityServiceImpl2() {
        entityClass = ReflectionUtils.getParametrizedType(this.getClass());
    }

    public abstract GenericDao getGenericDao();
    
    public abstract EntityMapper getEntityMapper();
    
    @Override
    public Class<T> getEntityClass() {
        return this.entityClass;
    }
    
    @Override
    @Transactional(value = TRANSACTION_MANAGER, readOnly = true)
    public T loadById(Object id) {
        return (T) this.getGenericDao().loadById(id);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER, propagation = Propagation.REQUIRED)
    public void create(T entity) {
        this.getGenericDao().create(entity);
    }
    
    @Override
    @Transactional(value = TRANSACTION_MANAGER, propagation = Propagation.REQUIRED)
    public void createForced(T entity) {
        this.getGenericDao().createForced(entity);
    }
    
    @Override
    @Transactional(value = TRANSACTION_MANAGER, propagation = Propagation.REQUIRED)
    public void insert(T entity){
        this.getGenericDao().insert(entity);
    }
    
    @Override
    @Transactional(value = TRANSACTION_MANAGER, propagation = Propagation.REQUIRED)
    public void massiveInsert(List<T> entities){
        this.getGenericDao().massiveInsert(entities);
    }
    
    @Override
    @Transactional(value = TRANSACTION_MANAGER, propagation = Propagation.REQUIRED)
    public void removeById(Object id) {
        this.getGenericDao().removeById(id);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER, propagation = Propagation.REQUIRED)
    public void remove(T entity) {
        this.getGenericDao().remove(entity);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER, propagation = Propagation.REQUIRED)
    public void removeAllByIds(List<Object> entityIds) {
        this.getGenericDao().removeAllByIds(entityIds);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER, propagation = Propagation.REQUIRED)
    public void update(T entity) {
        this.getGenericDao().update(entity);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER, readOnly = true)
    public void reload(T entity) {
        this.getGenericDao().reload(entity);
    }
    
    @Override
    @Transactional(value = TRANSACTION_MANAGER, readOnly = true)
    public List<T> listAll() {
        return (List<T>) this.getGenericDao().listAll();
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER, readOnly = true)
    public List<T> listAllByIds(List<Object> entityIds) {
        return (List<T>) this.getGenericDao().listAllByIds(entityIds);
    }
    
    @Override
    public BaseEntity getReference(Class entityClass, Object id) {
        return this.getGenericDao().getReference(entityClass, id);
    }
    
    @Override
    @Transactional(value = TRANSACTION_MANAGER, readOnly = true)
    public T loadByParameters(Parameters parameters) {
        return (T) getGenericDao().loadByParameters(parameters);
    }
    
    @Override
    @Transactional(value = TRANSACTION_MANAGER, readOnly = true)
    public T loadByParameter(String parameter, Object value) {
        Parameters parameters= new Parameters();
        parameters.whereEqual(parameter, value);
        return (T) getGenericDao().loadByParameters(parameters);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER, readOnly = true)
    public List<T> findByParameters(Parameters parameters) {
        return getGenericDao().findByParameters(parameters);
    }
    
    @Override
    @Transactional(value = TRANSACTION_MANAGER, readOnly = true)
    public List<T> findByNameQueryJPQL(String nameQueryJPQL, Map<String, Object> mapParameters, Integer maxResults, Integer firstResult){
        return getGenericDao().findByNameQueryJPQL(nameQueryJPQL, mapParameters, maxResults, firstResult);
    }
    
    @Override
    @Transactional(value = TRANSACTION_MANAGER, readOnly = true)
    public List<T> findByParameter(String parameter, Object value) {
        Parameters parameters= new Parameters();
        parameters.whereEqual(parameter, value);
        return getGenericDao().findByParameters(parameters);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER, readOnly = true)
    public Long countByParameters(Parameters parameters) {
        return getGenericDao().countByParameters(parameters);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER, propagation = Propagation.REQUIRED)
    public int updateByParameters(Parameters parameters) {
        return getGenericDao().updateByParameters(parameters);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER, propagation = Propagation.REQUIRED)
    public int removeByParameters(Parameters parameters) {
        return getGenericDao().removeByParameters(parameters);
    }
    
    @Override
    public Parameters buildParameters(String jsonfilters, String query, Long page, Long limit, String sort, String dir){
        return buildParameters(jsonfilters, query, page, limit, sort, dir, entityClass);
    }
    
    @Override
    public Parameters buildParameters(String jsonfilters, String query, Long page, Long limit, String sort, String dir, Class c){
        Parameters parameters= new Parameters();

        if (jsonfilters != null && !jsonfilters.equals("")) {
            parameters = FilterQueryJSON.processFilters(jsonfilters, c);
        }
        if(query!=null && !query.equals("")){
            parameters.whereQuery(getQueryParams(), query);
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
        
        return parameters;
    }
    
    private String[] getQueryParams(){
        List<String> queryParams= new ArrayList<>();
        queryParams.add("id");
        List<Field> queryFields= EntityReflection.getEntityAnnotatedFields(entityClass, QueryParam.class);
        for(Field f: queryFields){
            queryParams.add(f.getName());
        }
        return queryParams.toArray(new String[queryParams.size()]);
    }
    
    /**
     * *************************************************************************
     * ********** JDBC TEMPLATE ************************************************
     */
    
    /**
     * 
     * @param nameQuerySource
     * @param parameters
     * @param c
     * @return 
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER, readOnly = true)
    public Object loadByParameters(String nameQuerySource, Parameters parameters, Class c){
        return this.getGenericDao().loadByParameters(nameQuerySource, parameters, c);
    }
    
    /**
     *
     * @param nameQuerySource
     * @param parameters
     * @param c
     * @return
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER, readOnly = true)
    public List<Object> findByParameters(String nameQuerySource, Parameters parameters, Class c){
        return this.getGenericDao().findByParameters(nameQuerySource, parameters, c);
    }
    
    /**
     *
     * @param nameQuerySource
     * @param parameters
     * @return
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER, readOnly = true)
    public Long countByParameters(String nameQuerySource, Parameters parameters){
        return this.getGenericDao().countByParameters(nameQuerySource, parameters);
    }
    
    /**
     *
     * @param nameQuery
     * @param mapParameters
     * @return
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER, readOnly = true)
    public Map<String, Object> loadByNameQuery(String nameQuery, Map<String, Object> mapParameters){
        return this.getGenericDao().loadByNameQuery(nameQuery, mapParameters);
    }
    
    /**
     *
     * @param nameQuery
     * @param mapParameters
     * @param c
     * @return
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER, readOnly = true)
    public Object loadByNameQuery(String nameQuery, Map<String, Object> mapParameters, Class c){
        return this.getGenericDao().loadByNameQuery(nameQuery, mapParameters, c);
    }
    
    /**
     *
     * @param nameQuery
     * @param mapParameters
     * @return
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER, readOnly = true)
    public List<Map<String, Object>> findByNameQuery(String nameQuery, Map<String, Object> mapParameters){
        return this.getGenericDao().findByNameQuery(nameQuery, mapParameters);
    }
    
    /**
     *
     * @param query
     * @param mapParameters
     * @return
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER, readOnly = true)
    public List<Map<String, Object>> findByQuery(String query, Map<String, Object> mapParameters){
        return this.getGenericDao().findByQuery(query, mapParameters);
    }
    
    /**
     *
     * @param nameQuery
     * @param mapParameters
     * @param c
     * @return
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER, readOnly = true)
    public List<Object> findByNameQuery(String nameQuery, Map<String, Object> mapParameters, Class c){
        return this.getGenericDao().findByNameQuery(nameQuery, mapParameters, c);
    }

}
