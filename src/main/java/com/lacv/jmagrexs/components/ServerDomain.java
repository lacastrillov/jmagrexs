/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author grupot
 */
public class ServerDomain {
    
    @Autowired
    private ServletContext context;
    
    private final String adminPath="/vista";
    
    private final String restPath="/rest";
    
    private String applicationContext="";
    
    private String portalContext="";
    
    private String adminContext="";
    
    private String restContext="";
    
    private String domain;
    
    private String domainWithPort;
    
    private final List<String> modules= new ArrayList<>();
    
    
    @PostConstruct
    public void init(){
        
    }
    
    public void initDomain(HttpServletRequest req){
        if(domain==null){
            setDomain(req.getScheme() + "://" + req.getServerName());
            setDomainWithPort(req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort());
        }
    }
    
    /**
     * @return the context
     */
    public ServletContext getContext() {
        return context;
    }

    /**
     * @param context the context to set
     */
    public void setContext(ServletContext context) {
        this.context = context;
    }
    
    /**
     * @return the adminPath
     */
    public String getAdminPath() {
        return adminPath;
    }

    /**
     * @return the restPath
     */
    public String getRestPath() {
        return restPath;
    }
    
    /**
     * @return the contextPath
     */
    public String getContextPath() {
        return context.getContextPath();
    }

    /**
     * @return the applicationContext
     */
    public String getApplicationContext() {
        return applicationContext;
    }
    
    /**
     * @param applicationContext
     */
    public void setApplicationContext(String applicationContext) {
        this.applicationContext= applicationContext;
    }
    
    /**
     * @return the adminContext
     */
    public String getPortalContext() {
        return portalContext;
    }

    /**
     * @param portalContext
     */
    public void setPortalContext(String portalContext) {
        this.portalContext = adminContext;
        this.modules.add(portalContext);
    }

    /**
     * @return the adminContext
     */
    public String getAdminContext() {
        return adminContext;
    }

    /**
     * @param adminContext the adminContext to set
     */
    public void setAdminContext(String adminContext) {
        this.adminContext = adminContext;
        this.modules.add(adminContext);
    }

    /**
     * @return the restContext
     */
    public String getRestContext() {
        return restContext;
    }

    /**
     * @param restContext the restContext to set
     */
    public void setRestContext(String restContext) {
        this.restContext = restContext;
        this.modules.add(restContext);
    }

    /**
     * @return the domain
     */
    public String getDomain() {
        return domain;
    }

    /**
     * @param domain the domain to set
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * @return the domainWithPort
     */
    public String getDomainWithPort() {
        return domainWithPort;
    }

    /**
     * @param domainWithPort the domainWithPort to set
     */
    public void setDomainWithPort(String domainWithPort) {
        this.domainWithPort = domainWithPort;
    }

    /**
     * @return the modules
     */
    public List<String> getModules() {
        return modules;
    }
    
    /**
     * @param modules the modules to set
     */
    public void setModules(String modules) {
        this.modules.addAll(Arrays.asList(modules.split(",")));
    }
    
    /**
     * @return the modules
     */
    public String getModulesJson() {
        JSONArray modulesJson= new JSONArray();
        for(String module: modules){
            modulesJson.put(module);
        }
        return modulesJson.toString();
    }
    
    
}
