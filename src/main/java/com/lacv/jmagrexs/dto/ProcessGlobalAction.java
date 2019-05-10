/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.dto;

/**
 *
 * @author lacastrillov
 */
public class ProcessGlobalAction {
    
    private String mainProcessRef;
    
    private String processName;
    
    private String processTitle;
    
    private String iconUrl;
    
    private Class dtoClass;
    
    
    public ProcessGlobalAction(){
        this.processTitle= "Proceso";
        this.iconUrl= "/img/process_icons/settings.png";
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
