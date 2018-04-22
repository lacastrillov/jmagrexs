/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dot.gcpbasedot.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author grupot
 */
@Component("serverDomain")
public class ServerDomain {
    
    @Autowired
    private ServletContext context;
    
    private final String adminPath="/vista";
    
    private final String restPath="/rest";
    
    private String applicationContext;
    
    private String adminContext;
    
    private String restContext;
    
    private String domain;
    
    private String domainWithPort;
    
    private List<String> modules;
    
    
    @PostConstruct
    public void init(){
        modules= new ArrayList<>();
        setRestContext(getRestPath());
        setAdminContext(getAdminPath());
        if(!context.getContextPath().equals(restPath) && !context.getContextPath().equals(adminPath)){
            setApplicationContext(getContext().getContextPath());
        }else{
            modules.add(restPath);
            modules.add(adminPath);
            setApplicationContext("");
            if(context.getContextPath().equals(getRestPath())){
                setRestContext("");
            }
            if(context.getContextPath().equals(getAdminPath())){
                setAdminContext("");
            }
        }
    }
    
    public void initDomain(HttpServletRequest req){
        if(domain==null){
            setDomain(req.getScheme() + "://" + req.getServerName());
            setDomainWithPort(req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort());
        }
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
     * @return the applicationContext
     */
    public String getApplicationContext() {
        return applicationContext;
    }

    /**
     * @param applicationContext the applicationContext to set
     */
    public void setApplicationContext(String applicationContext) {
        this.applicationContext = applicationContext;
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
     * @return the modules
     */
    public String getModulesJson() {
        JSONArray modulesJson= new JSONArray();
        for(String module: modules){
            modulesJson.put(module);
        }
        return modulesJson.toString();
    }

    /**
     * @param modules the modules to set
     */
    public void addModules(String modules) {
        this.modules.addAll(Arrays.asList(modules.split(",")));
    }
    
}
