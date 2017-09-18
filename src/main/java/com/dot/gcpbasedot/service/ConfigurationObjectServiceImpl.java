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
import com.dot.gcpbasedot.util.Util;

/**
 *
 * @author lacastrillov
 * @param <T>
 */
public abstract class ConfigurationObjectServiceImpl<T> implements ConfigurationObjectService<T> {
    
    private final Class<T> coClass;

    public ConfigurationObjectServiceImpl() {
        coClass = ReflectionUtils.getParametrizedType(this.getClass());
    }
    
    @Override
    public String getConfigurationObjectType() {
        return this.coClass.getSimpleName();
    }
    
    public abstract GenericDao getJsonObjectDao();

    @Override
    public T load() {
        Parameters p= new Parameters();
        p.whereEqual("type", getConfigurationObjectType());
        p.whereIsNull("relatedEntity");
        
        return loadConfigurationObject(p);
    }

    @Override
    public T load(String relatedEntity, Integer relatedId) {
        Parameters p= new Parameters();
        p.whereEqual("type", getConfigurationObjectType());
        p.whereEqual("relatedEntity", relatedEntity);
        p.whereEqual("relatedId", relatedId);
        
        return loadConfigurationObject(p);
    }
    
    private T loadConfigurationObject(Parameters p){
        JsonObjectInterface jsonObject= (JsonObjectInterface) getJsonObjectDao().findByParameters(p);
        String data="{}";
        if(jsonObject!=null){
            data= jsonObject.getData();
        }
        Object configurationObject= EntityReflection.jsonToObject(data, coClass);
        
        return (T) configurationObject;
    }

    @Override
    public void save(T configurationObject) {
        Parameters p= new Parameters();
        p.whereEqual("type", getConfigurationObjectType());
        p.whereIsNull("relatedEntity");
        
        saveConfigurationObject(configurationObject, p);
    }

    @Override
    public void save(T configurationObject, String relatedEntity, Integer relatedId) {
        Parameters p= new Parameters();
        p.whereEqual("type", getConfigurationObjectType());
        p.whereEqual("relatedEntity", relatedEntity);
        p.whereEqual("relatedId", relatedId);
        
        saveConfigurationObject(configurationObject, p);
    }
    
    private void saveConfigurationObject(T configurationObject, Parameters p){
        JsonObjectInterface jsonObject= (JsonObjectInterface) getJsonObjectDao().findByParameters(p);
        if(jsonObject!=null){
            jsonObject.setData(Util.objectToJson(configurationObject));
            getJsonObjectDao().update(jsonObject);
        }else{
            jsonObject= (JsonObjectInterface) EntityReflection.getObjectForClass(getJsonObjectDao().getPersistentClass());
            jsonObject.setType(getConfigurationObjectType());
            jsonObject.setData(Util.objectToJson(configurationObject));
            
            getJsonObjectDao().create(jsonObject);
        }
    }
    
}
