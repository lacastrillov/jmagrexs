package com.dot.gcpbasedot.dao;

import java.util.List;

import com.dot.gcpbasedot.domain.BaseEntity;
import java.util.Map;

/**
 * Dao that performs severals operations with simultaneous entity classes.
 *
 * @author lacastrillov@gmail.com
 * @param <T>
 *
 */
public interface GenericDao<T extends BaseEntity> {

    
    /**
     * *************************************************************************
     * ********** JPA **********************************************************
     */
    
    /**
     * Searchs the entity by id.
     *
     * @param id entity id.
     * @return the entiy or null in case it doesn't exists.
     */
    T findById(Object id);

    /**
     * Persists a new entity.
     *
     * @param entity the new entity.
     */
    void create(T entity);
    
    /**
     * Persists a new entity.
     *
     * @param entity the new entity.
     */
    void createForce(T entity);

    /**
     * Guarda los cambios en la entidad.
     *
     * @param entity instancia de la entidad.
     */
    void update(T entity);

    /**
     * Discards any changes on the entity.
     *
     * @param entity
     */
    void reload(T entity);

    /**
     * Lists all existing entity.
     *
     * @return at least an empty list.
     */
    List<T> listAll();

    /**
     * Searches all entities whose id's appears in the specified list.
     *
     * @param entityIds the list of entities id.
     *
     * @return a list.
     */
    List<T> listAllByIds(List<Object> entityIds);
    
    /**
     * Removes the entity.
     *
     * @param id entity id.
     */
    void removeById(Object id);

    /**
     * Removes the entity.
     *
     * @param entity instance.
     */
    void remove(T entity);

    /**
     * 
     * @param entityIds 
     */
    void removeAllByIds(List<Object> entityIds);

    /**
     *
     * @param entityClass
     * @param id
     * @return
     */
    BaseEntity getReference(Class entityClass, Object id);
    
    /**
     *
     * @param parameters
     * @return
     * @throws java.lang.Exception
     */
    T findUniqueByParameters(Parameters parameters) throws Exception;

    /**
     *
     * @param parameters
     * @return
     */
    List<T> findByParameters(Parameters parameters);

    /**
     *
     * @param parameters
     * @return
     */
    Long countByParameters(Parameters parameters);

    /**
     *
     * @param parameters
     * @return
     */
    int updateByParameters(Parameters parameters);

    /**
     *
     * @param parameters
     * @return
     */
    int removeByParameters(Parameters parameters);
    
    
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
    Object findUniqueByParameters(String nameQuerySource, Parameters parameters, Class c);
    
    /**
     *
     * @param nameQuerySource
     * @param parameters
     * @param c
     * @return
     */
    List<Object> findByParameters(String nameQuerySource, Parameters parameters, Class c);
    
    /**
     *
     * @param nameQuerySource
     * @param parameters
     * @return
     */
    Long countByParameters(String nameQuerySource, Parameters parameters);
    
    /**
     *
     * @param nameQuery
     * @param nameParameters
     * @param valueParameters
     * @return
     */
    Map<String, Object> findUniqueByNameQuery(String nameQuery, String[] nameParameters, Object[] valueParameters);
    
    /**
     *
     * @param nameQuery
     * @param nameParameters
     * @param valueParameters
     * @param c
     * @return
     */
    Object findUniqueByNameQuery(String nameQuery, String[] nameParameters, Object[] valueParameters, Class c);
    
    /**
     *
     * @param nameQuery
     * @param nameParameters
     * @param valueParameters
     * @return
     */
    List<Map<String, Object>> findByNameQuery(String nameQuery, String[] nameParameters, Object[] valueParameters);
    
    /**
     *
     * @param nameQuery
     * @param nameParameters
     * @param valueParameters
     * @param c
     * @return
     */
    List<Object> findByNameQuery(String nameQuery, String[] nameParameters, Object[] valueParameters, Class c);

}
