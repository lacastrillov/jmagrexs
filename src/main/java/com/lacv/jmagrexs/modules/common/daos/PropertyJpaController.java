/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.modules.common.daos;


import com.lacv.jmagrexs.modules.common.entities.Property;
import com.lacv.jmagrexs.dao.JPAGenericDao;
import org.springframework.stereotype.Repository;

/**
 *
 * @author lacastrillov
 */
@Repository
public class PropertyJpaController extends JPAGenericDao<Property> implements PropertyJpa {

    
}
