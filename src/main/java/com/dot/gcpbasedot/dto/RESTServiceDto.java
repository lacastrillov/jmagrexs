/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dot.gcpbasedot.dto;

import org.springframework.http.HttpMethod;

/**
 *
 * @author grupot
 */
public class RESTServiceDto {
    
    private String processName;
    
    private String endpoint;

    private HttpMethod method;
    
    private Class inClass;

    private Class outClass;
    
    private String modeSendingData;
    
    private String inputDataFormat;
    
    private String responseDataFormat;
    
    private boolean saveResponseInLog;
    
    // modeSendingData
    public static final String IN_PARAMETERS="IN_PARAMETERS", IN_BODY="IN_BODY";
    
    // inputDataFormat, responseDataFormat
    public static final String JSON="JSON", XML="XML", HTML="HTML", PLAIN="PLAIN";
    
    
    public RESTServiceDto(String processName, String endpoint, HttpMethod method, Class inClass){
        this.processName= processName;
        this.endpoint= endpoint;
        this.method= method;
        this.inClass= inClass;
        this.outClass= String.class;
        this.modeSendingData= IN_PARAMETERS;
        this.inputDataFormat= JSON;
        this.responseDataFormat= JSON;
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
     * @return the method
     */
    public HttpMethod getMethod() {
        return method;
    }

    /**
     * @param method the method to set
     */
    public void setMethod(HttpMethod method) {
        this.method = method;
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
     * @return the outClass
     */
    public Class getOutClass() {
        return outClass;
    }

    /**
     * @param outClass the outClass to set
     */
    public void setOutClass(Class outClass) {
        this.outClass = outClass;
    }

    /**
     * @return the modeSendingData
     */
    public String getModeSendingData() {
        return modeSendingData;
    }

    /**
     * @param modeSendingData the modeSendingData to set
     */
    public void setModeSendingData(String modeSendingData) {
        this.modeSendingData = modeSendingData;
    }

    /**
     * 
     * @return inputDataFormat
     */
    public String getInputDataFormat() {
        return inputDataFormat;
    }

    /**
     * 
     * @param inputDataFormat 
     */
    public void setInputDataFormat(String inputDataFormat) {
        this.inputDataFormat = inputDataFormat;
    }

    /**
     * 
     * @return responseDataFormat
     */
    public String getResponseDataFormat() {
        return responseDataFormat;
    }

    /**
     * 
     * @param responseDataFormat 
     */
    public void setResponseDataFormat(String responseDataFormat) {
        this.responseDataFormat = responseDataFormat;
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
