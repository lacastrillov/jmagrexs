/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.service;

import com.lacv.jmagrexs.interfaces.MassiveOperationInterface;

/**
 *
 * @author e11001a
 * @param <T>
 */
public interface DbOperationLogService<T> extends EntityService<T> {
    
    /**
     * 
     * @param entityRef
     * @param type
     * @param entity
     * @param message
     * @param success
     * @param massiveOperation 
     */
    void save(String entityRef, String type, Object entity, String message, Boolean success, MassiveOperationInterface massiveOperation);
    
}
