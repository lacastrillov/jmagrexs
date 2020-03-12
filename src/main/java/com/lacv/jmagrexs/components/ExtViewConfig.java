/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.components;

import com.lacv.jmagrexs.util.FileService;

/**
 *
 * @author lcastrillo
 */
public class ExtViewConfig {
    
    private String appName;
    
    private String jmagrexsBuildCode;
    
    private String extJsLib;
    
    private String extJsVersion;
    
    private String favicon;
    
    private String dateFormat;
    
    private String dateFormatJava;
    
    private String datetimeFormat;
    
    private String datetimeFormatJava;
    
    private String timeFormat;
    
    private String timeFormatJava;
    
    
    public ExtViewConfig(){
        this.appName="";
        this.jmagrexsBuildCode="";
        this.extJsLib="";
        this.extJsVersion= "4";
        this.favicon="";
        this.dateFormat= "d/m/Y";
        this.dateFormatJava= "dd/MM/yyyy";
        this.datetimeFormat= "d/m/Y h:i:s A";
        this.datetimeFormatJava= "dd/MM/yyyy hh:mm:ss a";
        this.timeFormat= "h:i:s A";
        this.timeFormatJava= "hh:mm:ss a";
    }

    /**
     * @return the appName
     */
    public String getAppName() {
        return appName;
    }

    /**
     * @param appName the appName to set
     */
    public void setAppName(String appName) {
        this.appName = appName;
    }
    
    /**
     * 
     * @return jmagrexsBuildCode
     */
    public String getJmagrexsBuildCode() {
        if(jmagrexsBuildCode.equals("")){
            jmagrexsBuildCode= FileService.getPropertyInClasspath(ExtViewConfig.class, "config/library.properties", "jmagrexs.buildcode");
        }
        return jmagrexsBuildCode;
    }

    /**
     * @return the extJsLib
     */
    public String getExtJsLib() {
        return extJsLib;
    }

    /**
     * @param extJsLib the extJsLib to set
     */
    public void setExtJsLib(String extJsLib) {
        this.extJsLib = extJsLib;
    }

    /**
     * @return the extJsVersion
     */
    public String getExtJsVersion() {
        return extJsVersion;
    }

    /**
     * @param extJsVersion the extJsVersion to set
     */
    public void setExtJsVersion(String extJsVersion) {
        this.extJsVersion = extJsVersion;
    }

    /**
     * @return the favicon
     */
    public String getFavicon() {
        return favicon;
    }

    /**
     * @param favicon the favicon to set
     */
    public void setFavicon(String favicon) {
        this.favicon = favicon;
    }
    
    /**
     * @return the dateFormat
     */
    public String getDateFormat() {
        return dateFormat;
    }

    /**
     * @param dateFormat the dateFormat to set
     */
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }
    
    /**
     * @return the dateFormatJava
     */
    public String getDateFormatJava() {
        return dateFormatJava;
    }

    /**
     * @param dateFormatJava the dateFormatJava to set
     */
    public void setDateFormatJava(String dateFormatJava) {
        this.dateFormatJava = dateFormatJava;
    }
    
    /**
     * @return the datetimeFormat
     */
    public String getDatetimeFormat() {
        return datetimeFormat;
    }

    /**
     * @param datetimeFormat the datetimeFormat to set
     */
    public void setDatetimeFormat(String datetimeFormat) {
        this.datetimeFormat = datetimeFormat;
    }
    
    /**
     * @return the datetimeFormatJava
     */
    public String getDatetimeFormatJava() {
        return datetimeFormatJava;
    }

    /**
     * @param datetimeFormatJava the datetimeFormatJava to set
     */
    public void setDatetimeFormatJava(String datetimeFormatJava) {
        this.datetimeFormatJava = datetimeFormatJava;
    }
    
    /**
     * @return the timeFormat
     */
    public String getTimeFormat() {
        return timeFormat;
    }

    /**
     * @param timeFormat the timeFormat to set
     */
    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    /**
     * @return the timeFormatJava
     */
    public String getTimeFormatJava() {
        return timeFormatJava;
    }

    /**
     * @param timeFormatJava the timeFormatJava to set
     */
    public void setTimeFormatJava(String timeFormatJava) {
        this.timeFormatJava = timeFormatJava;
    }
    
}
