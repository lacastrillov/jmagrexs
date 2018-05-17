/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lacv.jmagrexs.modules.security.services;


import com.lacv.jmagrexs.modules.security.daos.WebresourceRoleJpa;
import com.lacv.jmagrexs.modules.security.entities.WebresourceRole;
import com.lacv.jmagrexs.modules.security.mappers.WebresourceRoleMapper;
import com.lacv.jmagrexs.modules.security.services.WebresourceRoleService;
import com.lacv.jmagrexs.dao.GenericDao;
import com.lacv.jmagrexs.service.EntityServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author lcastrillo
 */
@Service("webresourceRoleService")
public class WebresourceRoleServiceImpl extends EntityServiceImpl<WebresourceRole> implements WebresourceRoleService {
    
    @Autowired
    public WebresourceRoleJpa webresourceRoleJpa;
    
    @Autowired
    public WebresourceRoleMapper webresourceRoleMapper;
    
    @Override
    public GenericDao getGenericDao(){
        return webresourceRoleJpa;
    }
    
}
