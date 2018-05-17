/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lacv.jmagrexs.modules.security.services;


import com.lacv.jmagrexs.modules.security.daos.AuthorizationJpa;
import com.lacv.jmagrexs.modules.security.entities.Authorization;
import com.lacv.jmagrexs.modules.security.mappers.AuthorizationMapper;
import com.lacv.jmagrexs.dao.GenericDao;
import com.lacv.jmagrexs.service.EntityServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author lcastrillo
 */
@Service("authorizationService")
public class AuthorizationServiceImpl extends EntityServiceImpl<Authorization> implements AuthorizationService {
    
    @Autowired
    public AuthorizationJpa authorizationJpa;
    
    @Autowired
    public AuthorizationMapper authorizationMapper;
    
    @Override
    public GenericDao getGenericDao(){
        return authorizationJpa;
    }
    
}
