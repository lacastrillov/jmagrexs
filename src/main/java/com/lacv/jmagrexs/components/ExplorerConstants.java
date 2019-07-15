/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.components;

import org.apache.commons.lang3.SystemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author desarrollador
 */
@Component("explorerConstants")
public class ExplorerConstants {
    
    @Autowired
    @Value("${local.static.domain}")
    private String LOCAL_STATIC_DOMAIN;
    
    @Autowired
    @Value("${local.static.folder.linux}")
    private String LOCAL_STATIC_FOLDER_LINUX;
    
    @Autowired
    @Value("${local.static.folder.windows}")
    private String LOCAL_STATIC_FOLDER_WINDOWS;
    
    @Autowired
    @Value("${local.rootfolder}")
    private String LOCAL_ROOT_FOLDER;
    
    private final String LOCAL_ROOT_USER_FOLDER="uf/";
    
    
    public String getLocalStaticDomain(){
        return LOCAL_STATIC_DOMAIN;
    }
    
    public String getLocalStaticFolder(){
        if(SystemUtils.IS_OS_WINDOWS){
            return LOCAL_STATIC_FOLDER_WINDOWS;
        }else{
            return LOCAL_STATIC_FOLDER_LINUX;
        }
    }
    
    public String getLocalRootFolder(){
        return LOCAL_ROOT_FOLDER;
    }
    
    public String getLocalRootUserFolder(){
        return LOCAL_ROOT_USER_FOLDER;
    }

}
