/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dot.gcpbasedot.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author grupot
 */
public abstract class ObjectExplorerServiceImpl implements ObjectExplorerService {
    
    protected static final Logger LOGGER = Logger.getLogger(ObjectExplorerServiceImpl.class);
    
    protected Map<String, String> entityTitles= new HashMap<>();
    
    protected Map<Class, EntityService> services= new HashMap<>();
    
    
    protected void addControlMapping(String entityRef, String entityTitle, String icon, EntityService entityService) {
        entityTitles.put(entityRef, entityTitle);
        services.put(entityService.getClass(), entityService);
    }
    
    @Override
    public List<Object> getAllObjectsByPath(Class type, String path){
        EntityService entityService= services.get(type);
        return entityService.listAll();
    }
    
}
