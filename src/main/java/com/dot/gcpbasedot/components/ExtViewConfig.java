/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dot.gcpbasedot.components;

/**
 *
 * @author lcastrillo
 */
public class ExtViewConfig {
    
    private String appName;
    
    private String extJsLib4;
    
    private String extJsLib6;
    
    private Integer extJsVersion;
    
    private String favicon;
    
    private String dateFormat;
    
    private String datetimeFormat;
    
    private String timeFormat;
    
    private String dateFormatJava;
    
    private String datetimeFormatJava;
    
    private String timeFormatJava;
    
    
    public ExtViewConfig(){
        this.appName="";
        this.extJsLib4="";
        this.extJsLib6="";
        this.extJsVersion= 4;
        this.favicon="";
        this.dateFormat= "d/m/Y";
        this.datetimeFormat= "d/m/Y h:i:s A";
        this.timeFormat= "h:i:s A";
        this.dateFormatJava= "dd/MM/yyyy";
        this.datetimeFormatJava= "dd/MM/yyyy hh:mm:ss a";
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
     * @return the extJsLib4
     */
    public String getExtJsLib4() {
        return extJsLib4;
    }

    /**
     * @param extJsLib4 the extJsLib4 to set
     */
    public void setExtJsLib4(String extJsLib4) {
        this.extJsLib4 = extJsLib4;
    }

    /**
     * @return the extJsLib6
     */
    public String getExtJsLib6() {
        return extJsLib6;
    }

    /**
     * @param extJsLib6 the extJsLib6 to set
     */
    public void setExtJsLib6(String extJsLib6) {
        this.extJsLib6 = extJsLib6;
    }

    /**
     * @return the extJsVersion
     */
    public Integer getExtJsVersion() {
        return extJsVersion;
    }

    /**
     * @param extJsVersion the extJsVersion to set
     */
    public void setExtJsVersion(Integer extJsVersion) {
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
