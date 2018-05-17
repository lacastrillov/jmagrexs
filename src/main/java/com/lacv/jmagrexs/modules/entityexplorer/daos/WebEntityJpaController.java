/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.modules.entityexplorer.daos;


import com.lacv.jmagrexs.modules.entityexplorer.entities.WebEntity;
import com.lacv.jmagrexs.dao.JPAGenericDao;
import org.springframework.stereotype.Repository;

/**
 *
 * @author lacastrillov
 */
@Repository
public class WebEntityJpaController extends JPAGenericDao<WebEntity> implements WebEntityJpa {

    
}
