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
public class ProcessForm {
    
    private String mainProcessRef;
    
    private String processName;
    
    private String processTitle;
    
    
    
    public ProcessForm(){
        this.processTitle= "Proceso";
    }
    
    public ProcessForm(String mainProcessRef, String processName, String processTitle){
        this.mainProcessRef= mainProcessRef;
        this.processName= processName;
        this.processTitle= processTitle;
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
    
}
