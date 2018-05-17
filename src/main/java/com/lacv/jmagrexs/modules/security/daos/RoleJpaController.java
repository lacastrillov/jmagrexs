/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.modules.security.daos;


import com.lacv.jmagrexs.modules.security.entities.Role;
import com.lacv.jmagrexs.dao.JPAGenericDao;
import org.springframework.stereotype.Repository;

/**
 *
 * @author lacastrillov
 */
@Repository
public class RoleJpaController extends JPAGenericDao<Role> implements RoleJpa {

    
}
