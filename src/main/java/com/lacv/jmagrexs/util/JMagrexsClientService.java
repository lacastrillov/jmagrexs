/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.util;

import com.lacv.jmagrexs.dto.ConnectionResponse;
import com.lacv.jmagrexs.dto.RESTServiceDto;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import org.springframework.http.HttpMethod;

/**
 *
 * @author grupot
 */
public class JMagrexsClientService {
    
    private final String domain;
    
    private final String contextPath;
    
    private final String entityRef;
    
    private String authorization;
    
    
    public JMagrexsClientService(String domain, String contextPath, String entityRef){
        this.domain= domain;
        this.contextPath= contextPath;
        this.entityRef= entityRef;
    }
    
    private String getBaseURL(){
        return this.domain+this.contextPath+"/rest/"+this.entityRef+"/";
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }
    
    public JSONObject find(JSONObject filter, String query, Long start, Long limit, Long page, String sort, String dir){
        String endpoint= getBaseURL()+"find.htm";
        RESTServiceDto restService= new RESTServiceDto("find", endpoint, HttpMethod.GET, null);
        RESTServiceConnection restServiceConnection= new RESTServiceConnection(restService);
        
        Map<String, String> headers= new HashMap<>();
        Map<String, String> parameters= new HashMap<>();
        
        if(authorization!=null){
            headers.put("Authorization", authorization);
        }
        addParameter(parameters, "filter", filter);
        addParameter(parameters, "query", query);
        addParameter(parameters, "start", start);
        addParameter(parameters, "limit", limit);
        addParameter(parameters, "page", page);
        addParameter(parameters, "sort", sort);
        addParameter(parameters, "dir", dir);
        
        try {
            String result= restServiceConnection.getStringResult(headers, null, parameters);
            return new JSONObject(result);
        } catch (IOException ex) {
            return null;
        }
    }
    
    public String findXml(JSONObject filter, String query, Long start, Long limit, Long page, String sort, String dir) {
        String endpoint= getBaseURL()+"find/xml.htm";
        RESTServiceDto restService= new RESTServiceDto("findXml", endpoint, HttpMethod.GET, null);
        RESTServiceConnection restServiceConnection= new RESTServiceConnection(restService);
        
        Map<String, String> headers= new HashMap<>();
        Map<String, String> parameters= new HashMap<>();
        
        if(authorization!=null){
            headers.put("Authorization", authorization);
        }
        addParameter(parameters, "filter", filter);
        addParameter(parameters, "query", query);
        addParameter(parameters, "start", start);
        addParameter(parameters, "limit", limit);
        addParameter(parameters, "page", page);
        addParameter(parameters, "sort", sort);
        addParameter(parameters, "dir", dir);
        
        try {
            String result= restServiceConnection.getStringResult(headers, null, parameters);
            return result;
        } catch (IOException ex) {
            return null;
        }
    }
    
    public JSONObject report(JSONObject filter, Long start, Long limit, Long page, String sort, String dir,
            String dtoName, String reportName){
        String endpoint= getBaseURL()+"report/"+reportName+".htm";
        RESTServiceDto restService= new RESTServiceDto("report", endpoint, HttpMethod.GET, null);
        RESTServiceConnection restServiceConnection= new RESTServiceConnection(restService);
        
        Map<String, String> headers= new HashMap<>();
        Map<String, String> parameters= new HashMap<>();
        
        if(authorization!=null){
            headers.put("Authorization", authorization);
        }
        addParameter(parameters, "filter", filter);
        addParameter(parameters, "start", start);
        addParameter(parameters, "limit", limit);
        addParameter(parameters, "page", page);
        addParameter(parameters, "sort", sort);
        addParameter(parameters, "dir", dir);
        addParameter(parameters, "dtoName", dtoName);
        addParameter(parameters, "reportName", reportName);
        
        try {
            String result= restServiceConnection.getStringResult(headers, null, parameters);
            return new JSONObject(result);
        } catch (IOException ex) {
            return null;
        }
    }
    
    public String reportXml(JSONObject filter, Long start, Long limit, Long page, String sort, String dir,
            String dtoName, String reportName) {
        String endpoint= getBaseURL()+"report/xml/"+reportName+".htm";
        RESTServiceDto restService= new RESTServiceDto("reportXml", endpoint, HttpMethod.GET, null);
        RESTServiceConnection restServiceConnection= new RESTServiceConnection(restService);
        
        Map<String, String> headers= new HashMap<>();
        Map<String, String> parameters= new HashMap<>();
        
        if(authorization!=null){
            headers.put("Authorization", authorization);
        }
        addParameter(parameters, "filter", filter);
        addParameter(parameters, "start", start);
        addParameter(parameters, "limit", limit);
        addParameter(parameters, "page", page);
        addParameter(parameters, "sort", sort);
        addParameter(parameters, "dir", dir);
        addParameter(parameters, "dtoName", dtoName);
        addParameter(parameters, "reportName", reportName);
        
        try {
            String result= restServiceConnection.getStringResult(headers, null, parameters);
            return result;
        } catch (IOException ex) {
            return null;
        }
    }
    
    public JSONObject create(JSONObject data){
        String endpoint= getBaseURL()+"create.htm";
        RESTServiceDto restService= new RESTServiceDto("create", endpoint, HttpMethod.POST, null);
        RESTServiceConnection restServiceConnection= new RESTServiceConnection(restService);
        
        Map<String, String> headers= new HashMap<>();
        if(authorization!=null){
            headers.put("Authorization", authorization);
            headers.put("Content-Type", "application/json");
        }
        try {
            ConnectionResponse connectionResponse= restServiceConnection.post(headers, null, data.toString());
            return new JSONObject(connectionResponse.getRawBody());
        } catch (IOException ex) {
            return null;
        }
    }
    
    public JSONObject update(JSONObject data){
        String endpoint= getBaseURL()+"update.htm";
        System.out.println(endpoint);
        RESTServiceDto restService= new RESTServiceDto("update", endpoint, HttpMethod.POST, null);
        RESTServiceConnection restServiceConnection= new RESTServiceConnection(restService);
        
        Map<String, String> headers= new HashMap<>();
        if(authorization!=null){
            headers.put("Authorization", authorization);
            headers.put("Content-Type", "application/json");
        }
        try {
            ConnectionResponse connectionResponse= restServiceConnection.post(headers, null, data.toString());
            return new JSONObject(connectionResponse.getRawBody());
        } catch (IOException ex) {
            return null;
        }
    }
    
    public JSONObject updateByFilter(JSONObject filter){
        String endpoint= getBaseURL()+"/update/byfilter.htm";
        RESTServiceDto restService= new RESTServiceDto("updateByFilter", endpoint, HttpMethod.POST, null);
        RESTServiceConnection restServiceConnection= new RESTServiceConnection(restService);
        
        Map<String, String> headers= new HashMap<>();
        if(authorization!=null){
            headers.put("Authorization", authorization);
            headers.put("Content-Type", "application/json");
        }
        try {
            ConnectionResponse connectionResponse= restServiceConnection.post(headers, null, filter.toString());
            return new JSONObject(connectionResponse.getRawBody());
        } catch (IOException ex) {
            return null;
        }
    }
    
    public JSONObject load(String idEntity){
        String endpoint= getBaseURL()+"load.htm";
        RESTServiceDto restService= new RESTServiceDto("load", endpoint, HttpMethod.GET, null);
        RESTServiceConnection restServiceConnection= new RESTServiceConnection(restService);
        
        Map<String, String> headers= new HashMap<>();
        Map<String, String> parameters= new HashMap<>();
        
        if(authorization!=null){
            headers.put("Authorization", authorization);
        }
        addParameter(parameters, "idEntity", idEntity);
        
        try {
            String result= restServiceConnection.getStringResult(headers, null, parameters);
            return new JSONObject(result);
        } catch (IOException ex) {
            return null;
        }
    }
    
    public JSONObject delete(String idEntity){
        String endpoint= getBaseURL()+"delete.htm";
        RESTServiceDto restService= new RESTServiceDto("delete", endpoint, HttpMethod.GET, null);
        RESTServiceConnection restServiceConnection= new RESTServiceConnection(restService);
        
        Map<String, String> headers= new HashMap<>();
        Map<String, String> parameters= new HashMap<>();
        
        if(authorization!=null){
            headers.put("Authorization", authorization);
        }
        addParameter(parameters, "idEntity", idEntity);
        
        try {
            String result= restServiceConnection.getStringResult(headers, null, parameters);
            return new JSONObject(result);
        } catch (IOException ex) {
            return null;
        }
    }
    
    public JSONObject deleteByFilter(JSONObject filter){
        String endpoint= getBaseURL()+"/delete/byfilter.htm";
        RESTServiceDto restService= new RESTServiceDto("deleteByFilter", endpoint, HttpMethod.GET, null);
        RESTServiceConnection restServiceConnection= new RESTServiceConnection(restService);
        
        Map<String, String> headers= new HashMap<>();
        Map<String, String> parameters= new HashMap<>();
        
        if(authorization!=null){
            headers.put("Authorization", authorization);
        }
        addParameter(parameters, "filter", filter);
        
        try {
            String result= restServiceConnection.getStringResult(headers, null, parameters);
            return new JSONObject(result);
        } catch (IOException ex) {
            return null;
        }
    }
    
    private void addParameter(Map<String, String> parameters, String nameParameter, Object valueParameter){
        if(valueParameter!=null){
            parameters.put(nameParameter, valueParameter.toString());
        }
    }
    
}
