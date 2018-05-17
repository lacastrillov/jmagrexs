/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lacv.jmagrexs.modules.security.services;


import com.lacv.jmagrexs.modules.security.daos.RoleAuthorizationJpa;
import com.lacv.jmagrexs.modules.security.entities.RoleAuthorization;
import com.lacv.jmagrexs.modules.security.mappers.RoleAuthorizationMapper;
import com.lacv.jmagrexs.dao.GenericDao;
import com.lacv.jmagrexs.service.EntityServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author lcastrillo
 */
@Service("roleAuthorizationService")
public class RoleAuthorizationServiceImpl extends EntityServiceImpl<RoleAuthorization> implements RoleAuthorizationService {
    
    @Autowired
    public RoleAuthorizationJpa roleAuthorizationJpa;
    
    @Autowired
    public RoleAuthorizationMapper roleAuthorizationMapper;
    
    @Override
    public GenericDao getGenericDao(){
        return roleAuthorizationJpa;
    }
    
}
