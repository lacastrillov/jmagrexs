/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lacv.jmagrexs.modules.security.services;


import com.lacv.jmagrexs.modules.security.daos.RoleJpa;
import com.lacv.jmagrexs.modules.security.entities.Role;
import com.lacv.jmagrexs.modules.security.mappers.RoleMapper;
import com.lacv.jmagrexs.modules.security.services.RoleService;
import com.lacv.jmagrexs.dao.GenericDao;
import com.lacv.jmagrexs.service.EntityServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author lcastrillo
 */
@Service("roleService")
public class RoleServiceImpl extends EntityServiceImpl<Role> implements RoleService {
    
    @Autowired
    public RoleJpa roleJpa;
    
    @Autowired
    public RoleMapper roleMapper;
    
    @Override
    public GenericDao getGenericDao(){
        return roleJpa;
    }
    
}
