/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dot.gcpbasedot.service;

import com.dot.gcpbasedot.dao.GenericDao;
import com.dot.gcpbasedot.dao.Parameters;
import com.dot.gcpbasedot.interfaces.JsonObjectInterface;
import com.dot.gcpbasedot.reflection.EntityReflection;
import com.dot.gcpbasedot.reflection.ReflectionUtils;
import com.dot.gcpbasedot.util.JSONService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author lacastrillov
 * @param <T>
 */
public abstract class ConfigurationObjectServiceImpl1<T> implements ConfigurationObjectService<T> {
    
    private final Class<T> coClass;
    
    protected final String TRANSACTION_MANAGER = "TRANSACTION_MANAGER_1";
    

    public ConfigurationObjectServiceImpl1() {
        coClass = ReflectionUtils.getParametrizedType(this.getClass());
    }
    
    @Override
    public Class<T> getConfigurationObjectClass() {
        return this.coClass;
    }
    
    public abstract GenericDao getJsonObjectDao();

    @Override
    @Transactional(value = TRANSACTION_MANAGER, readOnly = true)
    public T load() {
        Parameters p= new Parameters();
        p.whereEqual("type", getConfigurationObjectClass().getSimpleName());
        p.whereIsNull("relatedEntity");
        
        return loadConfigurationObject(p);
    }
    
    @Override
    @Transactional(value = TRANSACTION_MANAGER, readOnly = true)
    public String loadJson() {
        Parameters p= new Parameters();
        p.whereEqual("type", getConfigurationObjectClass().getSimpleName());
        p.whereIsNull("relatedEntity");
        
        return loadConfigurationObjectJson(p);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER, readOnly = true)
    public T load(String relatedEntity, Integer relatedId) {
        Parameters p= new Parameters();
        p.whereEqual("type", getConfigurationObjectClass().getSimpleName());
        p.whereEqual("relatedEntity", relatedEntity);
        p.whereEqual("relatedId", relatedId);
        
        return loadConfigurationObject(p);
    }
    
    @Override
    @Transactional(value = TRANSACTION_MANAGER, readOnly = true)
    public String loadJson(String relatedEntity, Integer relatedId) {
        Parameters p= new Parameters();
        p.whereEqual("type", getConfigurationObjectClass().getSimpleName());
        p.whereEqual("relatedEntity", relatedEntity);
        p.whereEqual("relatedId", relatedId);
        
        return loadConfigurationObjectJson(p);
    }
    
    private T loadConfigurationObject(Parameters p) {
        Object configurationObject= EntityReflection.jsonToObject(loadConfigurationObjectJson(p), coClass);
        
        return (T) configurationObject;
    }
    
    private String loadConfigurationObjectJson(Parameters p) {
        JsonObjectInterface jsonObject= (JsonObjectInterface) getJsonObjectDao().loadByParameters(p);
        String data="{}";
        if(jsonObject!=null){
            data= jsonObject.getData();
        }
        
        return data;
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER, propagation = Propagation.REQUIRED)
    public void save(T configurationObject) {
        Parameters p= new Parameters();
        p.whereEqual("type", getConfigurationObjectClass().getSimpleName());
        p.whereIsNull("relatedEntity");
        
        saveConfigurationObject(configurationObject, p);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER, propagation = Propagation.REQUIRED)
    public void save(T configurationObject, String relatedEntity, Integer relatedId) {
        Parameters p= new Parameters();
        p.whereEqual("type", getConfigurationObjectClass().getSimpleName());
        p.whereEqual("relatedEntity", relatedEntity);
        p.whereEqual("relatedId", relatedId);
        
        saveConfigurationObject(configurationObject, p);
    }
    
    private void saveConfigurationObject(T configurationObject, Parameters p){
        JsonObjectInterface jsonObject= (JsonObjectInterface) getJsonObjectDao().loadByParameters(p);
        if(jsonObject!=null){
            jsonObject.setData(JSONService.objectToJson(configurationObject));
            getJsonObjectDao().update(jsonObject);
        }else{
            jsonObject= (JsonObjectInterface) EntityReflection.getObjectForClass(getJsonObjectDao().getPersistentClass());
            jsonObject.setType(getConfigurationObjectClass().getSimpleName());
            jsonObject.setData(JSONService.objectToJson(configurationObject));
            
            getJsonObjectDao().create(jsonObject);
        }
    }
    
}
