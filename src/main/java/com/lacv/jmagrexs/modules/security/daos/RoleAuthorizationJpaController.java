/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.modules.security.daos;


import com.lacv.jmagrexs.modules.security.entities.RoleAuthorization;
import com.lacv.jmagrexs.dao.JPAGenericDao;
import org.springframework.stereotype.Repository;

/**
 *
 * @author lacastrillov
 */
@Repository
public class RoleAuthorizationJpaController extends JPAGenericDao<RoleAuthorization> implements RoleAuthorizationJpa {

    
}
