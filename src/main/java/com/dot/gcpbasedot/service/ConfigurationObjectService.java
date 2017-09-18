/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dot.gcpbasedot.service;

/**
 *
 * @author lacastrillov
 * @param <T>
 */
public interface ConfigurationObjectService<T> {
    
    /**
     * 
     * @return 
     */
    String getConfigurationObjectType();
    
    /**
     * Searchs the entity by id.
     *
     * @return the entiy or null in case it doesn't exists.
     */
    T load();
    
    /**
     * 
     * @param relatedEntity
     * @param relatedId
     * @return 
     */
    T load(String relatedEntity, Integer relatedId);
    
    /**
     * 
     * @param configurationObject 
     */
    void save(T configurationObject);
    
    /**
     * 
     * @param configurationObject 
     * @param relatedEntity 
     * @param relatedId 
     */
    void save(T configurationObject, String relatedEntity, Integer relatedId);
    
}
