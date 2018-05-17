/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.dto;

/**
 *
 * @author grupot
 */
public class SOAPServiceDto {
    
    private String processName;
    
    private String endpoint;
    
    private Class inClass;
    
    private boolean saveResponseInLog;
    
    
    public SOAPServiceDto(String processName, String endpoint, Class inClass){
        this.processName= processName;
        this.endpoint= endpoint;
        this.inClass= inClass;
        this.saveResponseInLog= true;
    }

    /**
     * @return the processName
     */
    public String getProcessName() {
        return processName;
    }

    /**
     * @param processName the processName to set
     */
    public void setProcessName(String processName) {
        this.processName = processName;
    }

    /**
     * @return the endpoint
     */
    public String getEndpoint() {
        return endpoint;
    }

    /**
     * @param endpoint the endpoint to set
     */
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * @return the inClass
     */
    public Class getInClass() {
        return inClass;
    }

    /**
     * @param inClass the inClass to set
     */
    public void setInClass(Class inClass) {
        this.inClass = inClass;
    }

    /**
     * 
     * @return saveResponseInLog
     */
    public boolean isSaveResponseInLog() {
        return saveResponseInLog;
    }

    /**
     * 
     * @param saveResponseInLog 
     */
    public void setSaveResponseInLog(boolean saveResponseInLog) {
        this.saveResponseInLog = saveResponseInLog;
    }
    
}
