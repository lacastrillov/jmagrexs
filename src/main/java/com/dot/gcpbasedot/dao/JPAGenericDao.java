package com.dot.gcpbasedot.dao;

import com.dot.gcpbasedot.domain.BaseEntity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

@Repository
public class JPAGenericDao<T extends BaseEntity> extends JPAAbstractDao<T> {

    @Override
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

}
