/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lacv.jmagrexs.modules.security.services;


import com.lacv.jmagrexs.modules.security.daos.UserRoleJpa;
import com.lacv.jmagrexs.modules.security.entities.UserRole;
import com.lacv.jmagrexs.modules.security.mappers.UserRoleMapper;
import com.lacv.jmagrexs.modules.security.services.UserRoleService;
import com.lacv.jmagrexs.dao.GenericDao;
import com.lacv.jmagrexs.service.EntityServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author lcastrillo
 */
@Service("userRoleService")
public class UserRoleServiceImpl extends EntityServiceImpl<UserRole> implements UserRoleService {
    
    @Autowired
    public UserRoleJpa userRoleJpa;
    
    @Autowired
    public UserRoleMapper userRoleMapper;
    
    @Override
    public GenericDao getGenericDao(){
        return userRoleJpa;
    }
    
}
