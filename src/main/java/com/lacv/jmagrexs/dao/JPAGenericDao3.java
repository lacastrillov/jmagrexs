package com.lacv.jmagrexs.dao;

import com.lacv.jmagrexs.domain.BaseEntity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Repository;

@Repository
public class JPAGenericDao3<T extends BaseEntity> extends JPAAbstractDao<T> {

    @Autowired
    public void init(DataSource dataSource3){
        super.setDataSource(dataSource3);
    }
    
    @Override
    @PersistenceContext(unitName ="PERSISTENCE_UNIT_3")
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager= entityManager;
    }

}
