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
    
    private String applicationPath;
    
    private String extJsLib4;
    
    private String extJsLib6;
    
    private Integer extJsVersion;
    
    private String favicon;
    
    
    public ExtViewConfig(){
        appName="";
        applicationPath="";
        extJsLib4="";
        extJsLib6="";
        extJsVersion= 4;
        favicon="";
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
     * @return the applicationPath
     */
    public String getApplicationPath() {
        return applicationPath;
    }

    /**
     * @param applicationPath the applicationPath to set
     */
    public void setApplicationPath(String applicationPath) {
        this.applicationPath = applicationPath;
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
    
}
