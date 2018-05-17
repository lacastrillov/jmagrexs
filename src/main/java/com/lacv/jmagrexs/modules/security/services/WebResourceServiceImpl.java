/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lacv.jmagrexs.modules.security.services;


import com.lacv.jmagrexs.modules.security.daos.WebResourceJpa;
import com.lacv.jmagrexs.modules.security.entities.WebResource;
import com.lacv.jmagrexs.modules.security.mappers.WebResourceMapper;
import com.lacv.jmagrexs.modules.security.services.WebResourceService;
import com.lacv.jmagrexs.dao.GenericDao;
import com.lacv.jmagrexs.service.EntityServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author lcastrillo
 */
@Service("webResourceService")
public class WebResourceServiceImpl extends EntityServiceImpl<WebResource> implements WebResourceService {
    
    @Autowired
    public WebResourceJpa webResourceJpa;
    
    @Autowired
    public WebResourceMapper webResourceMapper;
    
    @Override
    public GenericDao getGenericDao(){
        return webResourceJpa;
    }
    
}
