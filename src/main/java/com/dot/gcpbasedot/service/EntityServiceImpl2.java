package com.dot.gcpbasedot.service;

import com.dot.gcpbasedot.dao.GenericDao;
import com.dot.gcpbasedot.dao.Parameters;
import com.dot.gcpbasedot.domain.BaseEntity;
import com.dot.gcpbasedot.reflection.ReflectionUtils;
import com.dot.gcpbasedot.util.FilterQueryJSON;

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
    public void createForce(T entity) {
        this.getGenericDao().createForce(entity);
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
    @Transactional(value = TRANSACTION_MANAGER, readOnly = true)
    public List<T> findByJSONFilters(String jsonfilters, Long page, Long limit, String sort, String dir) {
        Parameters parameters= new Parameters();

        if (jsonfilters != null && !jsonfilters.equals("")) {
            parameters = FilterQueryJSON.processFilters(jsonfilters, entityClass);
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
        
        List<T> listEntities = findByParameters(parameters);

        return listEntities;
    }
    
    @Override
    @Transactional(value = TRANSACTION_MANAGER, readOnly = true)
    public Long countByJSONFilters(String jsonfilters) {
        Parameters parameters= new Parameters();

        if (jsonfilters != null && !jsonfilters.equals("")) {
            parameters = FilterQueryJSON.processFilters(jsonfilters, entityClass);
        }

        return this.getGenericDao().countByParameters(parameters);
    }
    
    @Override
    @Transactional(value = TRANSACTION_MANAGER)
    public Integer updateByJSONFilters(String jsonfilters) {
        Parameters parameters= new Parameters();

        if (jsonfilters != null && !jsonfilters.equals("")) {
            parameters = FilterQueryJSON.processFilters(jsonfilters, entityClass);
        }

        return this.getGenericDao().updateByParameters(parameters);
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
     * @param nameQuerySource
     * @param filters
     * @param page
     * @param limit
     * @param sort
     * @param dir
     * @param c
     * @return
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER, readOnly = true)
    public List<Object> findByJSONFilters(String nameQuerySource, String filters, Long page, Long limit, String sort, String dir, Class c) {
        Parameters parameters= new Parameters();

        if (filters != null && !filters.equals("")) {
            parameters = FilterQueryJSON.processFilters(filters, c);
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
        
        List<Object> listEntities = findByParameters(nameQuerySource, parameters, c);

        return listEntities;
    }
    
    /**
     *
     * @param nameQuerySource
     * @param jsonfilters
     * @param c
     * @return
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER, readOnly = true)
    public Long countByJSONFilters(String nameQuerySource, String jsonfilters, Class c) {
        Parameters parameters= new Parameters();

        if (jsonfilters != null && !jsonfilters.equals("")) {
            parameters = FilterQueryJSON.processFilters(jsonfilters, c);
        }

        return this.getGenericDao().countByParameters(nameQuerySource, parameters);
    }
    
    /**
     *
     * @param nameQuery
     * @param nameParameters
     * @param valueParameters
     * @return
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER, readOnly = true)
    public Map<String, Object> loadByNameQuery(String nameQuery, String[] nameParameters, Object[] valueParameters){
        return this.getGenericDao().loadByNameQuery(nameQuery, nameParameters, valueParameters);
    }
    
    /**
     *
     * @param nameQuery
     * @param nameParameters
     * @param valueParameters
     * @param c
     * @return
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER, readOnly = true)
    public Object loadByNameQuery(String nameQuery, String[] nameParameters, Object[] valueParameters, Class c){
        return this.getGenericDao().loadByNameQuery(nameQuery, nameParameters, valueParameters, c);
    }
    
    /**
     *
     * @param nameQuery
     * @param nameParameters
     * @param valueParameters
     * @return
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER, readOnly = true)
    public List<Map<String, Object>> findByNameQuery(String nameQuery, String[] nameParameters, Object[] valueParameters){
        return this.getGenericDao().findByNameQuery(nameQuery, nameParameters, valueParameters);
    }
    
    /**
     *
     * @param nameQuery
     * @param nameParameters
     * @param valueParameters
     * @param c
     * @return
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER, readOnly = true)
    public List<Object> findByNameQuery(String nameQuery, String[] nameParameters, Object[] valueParameters, Class c){
        return this.getGenericDao().findByNameQuery(nameQuery, nameParameters, valueParameters, c);
    }

}
