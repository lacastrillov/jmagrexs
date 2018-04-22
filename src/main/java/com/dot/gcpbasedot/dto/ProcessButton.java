/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dot.gcpbasedot.dto;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author lacastrillov
 */
public class ProcessButton {
    
    private String mainProcessRef;
    
    private String processName;
    
    private String processTitle;
    
    private final Map<String, String> sourceByDestinationFields;
    
    private String iconUrl;
    
    private Class dtoClass;
    
    
    public ProcessButton(){
        this.processTitle= "Proceso";
        this.iconUrl= "/img/process_icons/settings.png";
        this.sourceByDestinationFields= new HashMap<>();
    }

    /**
     * @return the mainProcessRef
     */
    public String getMainProcessRef() {
        return mainProcessRef;
    }

    /**
     * @param mainProcessRef the mainProcessRef to set
     */
    public void setMainProcessRef(String mainProcessRef) {
        this.mainProcessRef = mainProcessRef;
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
     * @return the processTitle
     */
    public String getProcessTitle() {
        return processTitle;
    }

    /**
     * @param processTitle the processTitle to set
     */
    public void setProcessTitle(String processTitle) {
        this.processTitle = processTitle;
    }

    /**
     * @return the sourceByDestinationFields
     */
    public Map<String, String> getSourceByDestinationFields() {
        return sourceByDestinationFields;
    }
    
    /**
     * 
     * @param sourceField
     * @param destinationField 
     */
    public void addSourceByDestinationField(String sourceField, String destinationField) {
        sourceByDestinationFields.put(sourceField, destinationField);
    }

    /**
     * @return the iconUrl
     */
    public String getIconUrl() {
        return iconUrl;
    }

    /**
     * @param iconUrl the iconUrl to set
     */
    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    /**
     * @return the dtoClass
     */
    public Class getDtoClass() {
        return dtoClass;
    }

    /**
     * @param dtoClass the dtoClass to set
     */
    public void setDtoClass(Class dtoClass) {
        this.dtoClass = dtoClass;
    }
    
}
