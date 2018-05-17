package com.lacv.jmagrexs.dao;

import com.lacv.jmagrexs.domain.BaseEntity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Repository;

@Repository
public class JPAGenericDao<T extends BaseEntity> extends JPAAbstractDao<T> {

    @Autowired
    public void init(DataSource dataSource){
        super.setDataSource(dataSource);
    }
    
    @Override
    @PersistenceContext(unitName ="PERSISTENCE_UNIT")
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager= entityManager;
    }

}
