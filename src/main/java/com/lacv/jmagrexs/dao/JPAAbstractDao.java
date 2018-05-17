package com.lacv.jmagrexs.dao;

import java.util.List;

import javax.persistence.Query;

import com.lacv.jmagrexs.domain.BaseEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.springframework.stereotype.Repository;

/**
 * JPA based implementation of a GenericDao.
 *
 * @author lacastrillov@gmail.com
 * @param <T>
 *
 */
@Repository
public abstract class JPAAbstractDao<T extends BaseEntity> extends JdbcAbstractRepository<T> implements GenericDao<T> {
    
    protected EntityManager entityManager;
    
    /**
     *
     * @param persistenceUnit
     */
    public void setPersistenceUnit(String persistenceUnit){
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnit);
        entityManager = emf.createEntityManager();
    }

    /**
     *
     * @param entityManager
     */
    public abstract void setEntityManager(EntityManager entityManager);

    /**
     * JPA Entity Manager.
     *
     * @return the entity manager
     */
    public EntityManager getEntityManager() {
        return entityManager;
    }
    
    /**
     *
     * @param entity
     */
    @Override
    public void create(T entity) {
        this.getEntityManager().persist(entity);
    }
    
    /**
     *
     * @param entity
     */
    @Override
    public void createForce(T entity) {
        this.getEntityManager().persist(entity);
        this.getEntityManager().flush();
    }

    /**
     *
     * @param entity
     */
    @Override
    public void update(T entity) {
        this.getEntityManager().merge(entity);
    }

    /**
     *
     * @param entity
     */
    @Override
    public void reload(T entity) {
        this.getEntityManager().refresh(entity);
    }

    /**
     *
     * @param id
     * @return
     */
    @Override
    public T loadById(Object id) {
        return this.getEntityManager().find(getPersistentClass(), id);
    }

    /**
     *
     * @param entityClass
     * @param id
     * @return
     */
    @Override
    public BaseEntity getReference(Class entityClass, Object id) {
        return (BaseEntity) entityManager.getReference(entityClass, id);
    }

    /**
     *
     * @return
     */
    @Override
    public List<T> listAll() {
        String hql = "SELECT object(o) FROM " + getPersistentClass().getSimpleName() + " o";
        Query query = this.getEntityManager().createQuery(hql);
        return (List<T>) query.getResultList();
    }

    /**
     *
     * @param entityIds
     * @return
     */
    @Override
    public List<T> listAllByIds(List<Object> entityIds) {
        Parameters p= new Parameters();
        p.whereIn("id", entityIds.toArray());
        return findByParameters(p);
    }

    /**
     *
     * @param id
     */
    @Override
    public void removeById(final Object id) {
        Object entity = getEntityManager().find(getPersistentClass(), id);
        getEntityManager().remove(entity);
    }

    /**
     *
     * @param entity
     */
    @Override
    public void remove(T entity) {
        this.getEntityManager().remove(this.getEntityManager().merge(entity));
    }

    /**
     *
     * @param entityIds
     */
    @Override
    public void removeAllByIds(List<Object> entityIds) {
        Parameters p= new Parameters();
        p.whereIn("id", entityIds.toArray());
        removeByParameters(p);
    }
    
    /**
     *
     * @param parameters
     * @return
     */
    @Override
    public T loadByParameters(Parameters parameters) {
        List<T> resultList = findByParameters(parameters);
        if (!resultList.isEmpty() && resultList.size() == 1) {
            return resultList.get(0);
        }
        return null;
    }

    /**
     *
     * @param parameters
     * @return
     */
    @Override
    public List<T> findByParameters(Parameters parameters) {
        StringBuilder sql = new StringBuilder("SELECT object(o) FROM " + getPersistentClass().getSimpleName() + " AS o");
        HashMap<String, Object> mapParameters = new HashMap<>();

        sql.append(getFilterQuery(parameters, mapParameters));

        sql.append(getOrderQuery(parameters.getOrderByParameters()));
        
        Query q = this.getEntityManager().createQuery(sql.toString());
        for (Map.Entry<String, Object> entry : mapParameters.entrySet()){
            q.setParameter(entry.getKey(), entry.getValue());
        }

        if (parameters.getMaxResults() != null) {
            q.setMaxResults(parameters.getMaxResults().intValue());
        }
        if (parameters.getFirstResult() != null) {
            q.setFirstResult(parameters.getFirstResult().intValue());
        }
        
        parameters.setTotalResults(countByParameters(parameters));
        
        System.out.println("CONSULTA SQL: "+sql);

        return q.getResultList();
    }
    
    /**
     *
     * @param nameQueryJPQL
     * @param mapParameters
     * @param maxResults
     * @param firstResult
     * @return
     */
    @Override
    public List<T> findByNameQueryJPQL(String nameQueryJPQL, Map<String, Object> mapParameters, Integer maxResults, Integer firstResult) {
        String jpql = queryMap.get(nameQueryJPQL);
        
        Query q = this.getEntityManager().createQuery(jpql);
        for (Map.Entry<String, Object> entry : mapParameters.entrySet()){
            q.setParameter(entry.getKey(), entry.getValue());
        }
        if (maxResults != null) {
            q.setMaxResults(maxResults);
        }
        if (firstResult != null) {
            q.setFirstResult(firstResult);
        }
        System.out.println("JPQL :: "+jpql);

        return q.getResultList();
    }

    /**
     *
     * @param parameters
     * @return
     */
    @Override
    public Long countByParameters(Parameters parameters) {
        StringBuilder sql = new StringBuilder("SELECT count(o) FROM " + getPersistentClass().getSimpleName() + " AS o");
        HashMap<String, Object> mapParameters = new HashMap<>();

        sql.append(getFilterQuery(parameters, mapParameters));
        
        Query q = this.getEntityManager().createQuery(sql.toString());
        for (Map.Entry<String, Object> entry : mapParameters.entrySet()){
            q.setParameter(entry.getKey(), entry.getValue());
        }
        
        return (Long) q.getSingleResult();
    }

    /**
     *
     * @param parameters
     * @return
     */
    @Override
    public int updateByParameters(Parameters parameters) {
        StringBuilder sql = new StringBuilder("UPDATE " + getPersistentClass().getSimpleName() + " AS o");
        HashMap<String, Object> mapParameters = new HashMap<>();

        sql.append(getUpdateQuery(parameters, mapParameters));

        sql.append(getFilterQuery(parameters, mapParameters));
        
        Query q = this.getEntityManager().createQuery(sql.toString());
        for (Map.Entry<String, Object> entry : mapParameters.entrySet()){
            q.setParameter(entry.getKey(), entry.getValue());
        }

        return q.executeUpdate();
    }

    /**
     *
     * @param parameters
     * @return
     */
    @Override
    public int removeByParameters(Parameters parameters) {
        StringBuilder sql = new StringBuilder("DELETE FROM " + getPersistentClass().getSimpleName() + " AS o");
        HashMap<String, Object> mapParameters = new HashMap<>();

        sql.append(getFilterQuery(parameters, mapParameters));
        
        Query q = this.getEntityManager().createQuery(sql.toString());
        for (Map.Entry<String, Object> entry : mapParameters.entrySet()){
            q.setParameter(entry.getKey(), entry.getValue());
        }

        return q.executeUpdate();
    }

    private String getFilterQuery(Parameters parameters, HashMap mapParameters) {
        StringBuilder sql = new StringBuilder("");
        boolean parametersSet = false;
        int i, numParameters;

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

                    mapParameters.put(parameterRef, data[1]);
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

                mapParameters.put(parameter + "_l" + i, "%" + value + "%");
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
                    if( k == 0 ){
                        sql.append(" in (");
                    } else {
                        sql.append(" not in (");
                    }
                    for (int j = 0; j < values.length; j++) {
                        mapParameters.put(parameter + "_i" + j, values[j]);
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

                mapParameters.put(parameter + "_b0", range[0]);
                mapParameters.put(parameter + "_b1", range[1]);

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

                mapParameters.put("query_"+i, "%" + query + "%");
                
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

    private String getUpdateQuery(Parameters parameters, HashMap mapParameters) {
        StringBuilder sql = new StringBuilder("");

        int numParameters = parameters.getUpdateValueParameters().entrySet().size();
        if (numParameters > 0) {
            sql.append(" SET ");
            int i = 0;
            for (Map.Entry<String, Object> entry : parameters.getUpdateValueParameters().entrySet()) {
                String parameter = entry.getKey();
                Object value = entry.getValue();

                mapParameters.put(parameter + "_u" + i, value);
                sql.append("o.").append(parameter).append(" = :").append(parameter).append("_u").append(i);

                if (i < numParameters - 1) {
                    sql.append(", ");
                }
                i++;
            }
        }

        return sql.toString();
    }

    private String getOrderQuery(List<String[]> orderByParameters) {
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

}
