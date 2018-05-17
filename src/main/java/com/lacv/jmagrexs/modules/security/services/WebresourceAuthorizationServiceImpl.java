/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lacv.jmagrexs.modules.security.services;


import com.lacv.jmagrexs.modules.security.daos.WebresourceAuthorizationJpa;
import com.lacv.jmagrexs.modules.security.entities.WebresourceAuthorization;
import com.lacv.jmagrexs.modules.security.mappers.WebresourceAuthorizationMapper;
import com.lacv.jmagrexs.modules.security.services.WebresourceAuthorizationService;
import com.lacv.jmagrexs.dao.GenericDao;
import com.lacv.jmagrexs.service.EntityServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author lcastrillo
 */
@Service("webresourceAuthorizationService")
public class WebresourceAuthorizationServiceImpl extends EntityServiceImpl<WebresourceAuthorization> implements WebresourceAuthorizationService {
    
    @Autowired
    public WebresourceAuthorizationJpa webresourceAuthorizationJpa;
    
    @Autowired
    public WebresourceAuthorizationMapper webresourceAuthorizationMapper;
    
    @Override
    public GenericDao getGenericDao(){
        return webresourceAuthorizationJpa;
    }
    
}
