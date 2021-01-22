package com.lacv.jmagrexs.dao;

import java.util.List;

import com.lacv.jmagrexs.domain.BaseEntity;
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
     * 
     * @return 
     */
    Class<T> getPersistentClass();
    
    /**
     * Searchs the entity by id.
     *
     * @param id entity id.
     * @return the entiy or null in case it doesn't exists.
     */
    T loadById(Object id);

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
    void createForced(T entity);
    
    /**
     * 
     * @param entity 
     */
    void createNatively(T entity);
    
    /**
     * 
     * @param entity 
     */
    void insert(T entity);
    
    /**
     * 
     * @param entities 
     */
    void massiveInsert(List<T> entities);
    
    /**
     * Guarda los cambios en la entidad.
     *
     * @param entity instancia de la entidad.
     */
    void update(T entity);
    
    /**
     * 
     * @param entity 
     */
    void updateNatively(T entity);

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
     */
    T loadByParameters(Parameters parameters);

    /**
     *
     * @param parameters
     * @return
     */
    List<T> findByParameters(Parameters parameters);
    
    /**
     * 
     * @param nameQueryJPQL
     * @param mapParameters
     * @param maxResults
     * @param firstResult
     * @return 
     */
    List<T> findByNameQueryJPQL(String nameQueryJPQL, Map<String, Object> mapParameters, Integer maxResults, Integer firstResult);

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
    Object loadByParameters(String nameQuerySource, Parameters parameters, Class c);
    
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
     * @param mapParameters
     * @return
     */
    Map<String, Object> loadByNameQuery(String nameQuery, Map<String, Object> mapParameters);
    
    /**
     *
     * @param nameQuery
     * @param mapParameters
     * @param c
     * @return
     */
    Object loadByNameQuery(String nameQuery, Map<String, Object> mapParameters, Class c);
    
    /**
     *
     * @param nameQuery
     * @param mapParameters
     * @return
     */
    List<Map<String, Object>> findByNameQuery(String nameQuery, Map<String, Object> mapParameters);
    
    /**
     *
     * @param query
     * @param mapParameters
     * @return
     */
    List<Map<String, Object>> findByQuery(String query, Map<String, Object> mapParameters);
    
    /**
     *
     * @param nameQuery
     * @param mapParameters
     * @param c
     * @return
     */
    List<Object> findByNameQuery(String nameQuery, Map<String, Object> mapParameters, Class c);

}
