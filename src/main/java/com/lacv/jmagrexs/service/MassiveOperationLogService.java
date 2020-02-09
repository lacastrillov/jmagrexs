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
public interface MassiveOperationLogService<T> extends EntityService<T> {
    
    /**
     * 
     * @param entityRef
     * @param type
     * @param total
     * @param message
     * @return 
     */
    MassiveOperationInterface start(String entityRef, String type, int total, String message);
    
    /**
     * 
     * @param massiveOperation
     * @param success 
     */
    void move(MassiveOperationInterface massiveOperation, Boolean success);
    
    /**
     * 
     * @param massiveOperation 
     * @param message 
     */
    void end(MassiveOperationInterface massiveOperation, String message);
    
}
