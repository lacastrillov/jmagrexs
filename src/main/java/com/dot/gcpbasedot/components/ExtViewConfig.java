/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dot.gcpbasedot.components;

/**
 *
 * @author grupot
 */
public class ExtViewConfig {
    
    private String appName;
    
    private String favicon;
    
    private Integer extJsVersion;
    
    
    public ExtViewConfig(){
        appName="";
        favicon="";
        extJsVersion= 4;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getFavicon() {
        return favicon;
    }

    public void setFavicon(String favicon) {
        this.favicon = favicon;
    }

    public Integer getExtJsVersion() {
        return extJsVersion;
    }

    public void setExtJsVersion(Integer extJsVersion) {
        this.extJsVersion = extJsVersion;
    }
    
}
