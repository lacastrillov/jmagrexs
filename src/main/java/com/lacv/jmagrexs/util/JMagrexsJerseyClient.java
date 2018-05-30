/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.util;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import org.json.JSONObject;

/**
 *
 * @author grupot
 */
public class JMagrexsJerseyClient {
    
    private final String domain;
    
    private final String contextPath;
    
    private final String entityRef;
    
    private String authorization;
    
    
    public JMagrexsJerseyClient(String domain, String contextPath, String entityRef){
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
    
    public JSONObject find(JSONObject filter, String query, Long limit, Long page, String sort, String dir){
        String endpoint= getBaseURL()+"find.htm";
        Map<String, String> headers= new HashMap<>();
        Map<String, String> parameters= new HashMap<>();
        
        if(authorization!=null){
            headers.put("Authorization", authorization);
        }
        addParameter(parameters, "filter", filter);
        addParameter(parameters, "query", query);
        addParameter(parameters, "limit", limit);
        addParameter(parameters, "page", page);
        addParameter(parameters, "sort", sort);
        addParameter(parameters, "dir", dir);
        
        try {
            ClientResponse response= execute(endpoint, parameters, headers, "GET");
            return new JSONObject(getContentResponse(response));
        } catch (IOException ex) {
            return null;
        }
    }
    
    public String findXml(JSONObject filter, String query, Long limit, Long page, String sort, String dir) {
        String endpoint= getBaseURL()+"find/xml.htm";
        Map<String, String> headers= new HashMap<>();
        Map<String, String> parameters= new HashMap<>();
        
        if(authorization!=null){
            headers.put("Authorization", authorization);
        }
        addParameter(parameters, "filter", filter);
        addParameter(parameters, "query", query);
        addParameter(parameters, "limit", limit);
        addParameter(parameters, "page", page);
        addParameter(parameters, "sort", sort);
        addParameter(parameters, "dir", dir);
        
        try {
            ClientResponse response= execute(endpoint, parameters, headers, "GET");
            return getContentResponse(response);
        } catch (IOException ex) {
            return null;
        }
    }
    
    public JSONObject report(JSONObject filter, Long limit, Long page, String sort, String dir,
            String dtoName, String reportName){
        String endpoint= getBaseURL()+"report/"+reportName+".htm";
        Map<String, String> headers= new HashMap<>();
        Map<String, String> parameters= new HashMap<>();
        
        if(authorization!=null){
            headers.put("Authorization", authorization);
        }
        addParameter(parameters, "filter", filter);
        addParameter(parameters, "limit", limit);
        addParameter(parameters, "page", page);
        addParameter(parameters, "sort", sort);
        addParameter(parameters, "dir", dir);
        addParameter(parameters, "dtoName", dtoName);
        addParameter(parameters, "reportName", reportName);
        
        try {
            ClientResponse response= execute(endpoint, parameters, headers, "GET");
            return new JSONObject(getContentResponse(response));
        } catch (IOException ex) {
            return null;
        }
    }
    
    public String reportXml(JSONObject filter, Long limit, Long page, String sort, String dir,
            String dtoName, String reportName) {
        String endpoint= getBaseURL()+"report/xml/"+reportName+".htm";
        Map<String, String> headers= new HashMap<>();
        Map<String, String> parameters= new HashMap<>();
        
        if(authorization!=null){
            headers.put("Authorization", authorization);
        }
        addParameter(parameters, "filter", filter);
        addParameter(parameters, "limit", limit);
        addParameter(parameters, "page", page);
        addParameter(parameters, "sort", sort);
        addParameter(parameters, "dir", dir);
        addParameter(parameters, "dtoName", dtoName);
        addParameter(parameters, "reportName", reportName);
        
        try {
            ClientResponse response= execute(endpoint, parameters, headers, "GET");
            return getContentResponse(response);
        } catch (IOException ex) {
            return null;
        }
    }
    
    public JSONObject create(JSONObject data){
        String endpoint= getBaseURL()+"create.htm";
        Map<String, String> headers= new HashMap<>();
        if(authorization!=null){
            headers.put("Authorization", authorization);
            headers.put("Content-Type", "application/json");
        }
        try {
            ClientResponse response= execute(endpoint, data.toString(), headers, "POST");
            return new JSONObject(getContentResponse(response));
        } catch (IOException ex) {
            return null;
        }
    }
    
    public JSONObject update(JSONObject data){
        String endpoint= getBaseURL()+"update.htm";
        Map<String, String> headers= new HashMap<>();
        if(authorization!=null){
            headers.put("Authorization", authorization);
            headers.put("Content-Type", "application/json");
        }
        try {
            ClientResponse response= execute(endpoint, data.toString(), headers, "POST");
            return new JSONObject(getContentResponse(response));
        } catch (IOException ex) {
            return null;
        }
    }
    
    public JSONObject updateByFilter(JSONObject filter){
        String endpoint= getBaseURL()+"/update/byfilter.htm";
        Map<String, String> headers= new HashMap<>();
        if(authorization!=null){
            headers.put("Authorization", authorization);
            headers.put("Content-Type", "application/json");
        }
        try {
            ClientResponse response= execute(endpoint, filter.toString(), headers, "POST");
            return new JSONObject(getContentResponse(response));
        } catch (IOException ex) {
            return null;
        }
    }
    
    public JSONObject load(String idEntity){
        String endpoint= getBaseURL()+"load.htm";
        Map<String, String> headers= new HashMap<>();
        Map<String, String> parameters= new HashMap<>();
        
        if(authorization!=null){
            headers.put("Authorization", authorization);
        }
        addParameter(parameters, "idEntity", idEntity);
        
        try {
            ClientResponse response= execute(endpoint, parameters, headers, "GET");
            return new JSONObject(getContentResponse(response));
        } catch (IOException ex) {
            return null;
        }
    }
    
    public JSONObject delete(String idEntity){
        String endpoint= getBaseURL()+"delete.htm";
        Map<String, String> headers= new HashMap<>();
        Map<String, String> parameters= new HashMap<>();
        
        if(authorization!=null){
            headers.put("Authorization", authorization);
        }
        addParameter(parameters, "idEntity", idEntity);
        
        try {
            ClientResponse response= execute(endpoint, parameters, headers, "GET");
            return new JSONObject(getContentResponse(response));
        } catch (IOException ex) {
            return null;
        }
    }
    
    public JSONObject deleteByFilter(JSONObject filter){
        String endpoint= getBaseURL()+"/delete/byfilter.htm";
        Map<String, String> headers= new HashMap<>();
        Map<String, String> parameters= new HashMap<>();
        
        if(authorization!=null){
            headers.put("Authorization", authorization);
        }
        addParameter(parameters, "filter", filter);
        
        try {
            ClientResponse response= execute(endpoint, parameters, headers, "GET");
            return new JSONObject(getContentResponse(response));
        } catch (IOException ex) {
            return null;
        }
    }
    
    private void addParameter(Map<String, String> parameters, String nameParameter, Object valueParameter){
        if(valueParameter!=null){
            try {
                parameters.put(nameParameter, URLEncoder.encode(valueParameter.toString(), "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(JMagrexsJerseyClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public ClientResponse execute(String endpoint, String data, Map<String, String> headers, String httpMethod){
        WebResource resource = Client.create(new DefaultClientConfig()).resource(endpoint);
        Builder builder = resource.getRequestBuilder();
        addHeaders(builder, headers);

        ClientResponse response= null;
        switch(httpMethod){
            case "POST":
                response=  builder.post(ClientResponse.class, data);
                break;
            case "PUT":
                response=  builder.put(ClientResponse.class, data);
                break;
        }
         
        return response;
    }
    
    public ClientResponse execute(String endpoint, Map<String, String> parameters, Map<String, String> headers, String httpMethod){
        ClientResponse response=null;
        String url= endpoint;
        WebResource resource;
        Builder builder;
        switch(httpMethod){
            case "POST":
                resource = Client.create(new DefaultClientConfig()).resource(url);
                builder = resource.getRequestBuilder();
                addHeaders(builder, headers);
                response=  builder.post(ClientResponse.class, getFormParameters(parameters));
                break;
            case "PUT":
                resource = Client.create(new DefaultClientConfig()).resource(url);
                builder = resource.getRequestBuilder();
                addHeaders(builder, headers);
                response=  builder.put(ClientResponse.class, getFormParameters(parameters));
                break;
            case "GET":
                url+= getUrlParameters(parameters);
                resource = Client.create(new DefaultClientConfig()).resource(url);
                builder = resource.getRequestBuilder();
                addHeaders(builder, headers);
                response=  builder.get(ClientResponse.class);
                break;
            case "DELETE":
                url+= getUrlParameters(parameters);
                resource = Client.create(new DefaultClientConfig()).resource(url);
                builder = resource.getRequestBuilder();
                addHeaders(builder, headers);
                response=  builder.delete(ClientResponse.class);
                break;
        }
         
        return response;
    }
    
    private void addHeaders(Builder builder, Map<String, String> headers) {
        builder.header(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml,application/json;q=0.9,*/*;q=0.8");
        builder.header(HttpHeaders.ACCEPT_LANGUAGE, "es-CO,en-US;q=0.7,en;q=0.3");
        builder.header("Connection", "keep-alive");
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                builder.header(header.getKey(), header.getValue());
            }
        }
    }
    
    private String getUrlParameters(Map<String, String> parameters){
        String url="";
        if(parameters!=null){
            if(parameters.entrySet().size()>0){
                url+="?";
            }
            for (Map.Entry<String, String> parameter : parameters.entrySet()) {
                url+=parameter.getKey()+"="+parameter.getValue()+"&";
            }
            if(parameters.entrySet().size()>0){
                url= url.substring(0,url.length()-1);
            }
        }
        System.out.println(url);
        return url;
    }
    
    private MultivaluedMap getFormParameters(Map<String, String> parameters){
        MultivaluedMap formParameters = new MultivaluedMapImpl();
        if(parameters!=null){
            for (Map.Entry<String, String> parameter : parameters.entrySet()) {
                formParameters.add(parameter.getKey(), parameter.getValue());
            }
        }
        return formParameters;
    }
    
    private String getContentResponse(ClientResponse response) throws IOException{
        // get the response-body
        Writer writer = new StringWriter();
        if (response != null) {

            try {
                InputStream is = response.getEntityInputStream();
                char[] buffer = new char[1024];
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }

            } catch (IOException t) {
                String msg = "unable to read response-content; read up to this point: '" + writer.toString() + "'";
                writer = new StringWriter(); // don't want to later give jackson partial JSON it might choke on
                throw new IOException(msg, t);

            } catch (IllegalStateException t) {
                String msg = "unable to read response-content; read up to this point: '" + writer.toString() + "'";
                writer = new StringWriter(); // don't want to later give jackson partial JSON it might choke on
                throw new IllegalStateException(msg, t);
            }
        }

        return writer.toString();
    }
    
    public static void main(String[] args){
        JMagrexsJerseyClient client= new JMagrexsJerseyClient("http://35.192.86.195:8080", "/processTransaction", "pedido");
        client.setAuthorization("Basic bGNhc3RyaWxsbzpjYWxzYXQzMjE=");
        
        JSONObject filter= new JSONObject();
        JSONObject filterEQ= new JSONObject();
        filterEQ.put("facturada", true);
        filterEQ.put("modoPago", "MASTERCARD");
        filter.put("eq", filterEQ);
        JSONObject result= client.find(filter, null, 5L, 1L, null, null);
        System.out.println(result.toString());
        
        JSONObject pedido= new JSONObject();
        pedido.put("id", "1210001085046910");
        pedido.put("campoExtra1", "002 - Se creo correctamente la factura");
        pedido.put("direccion", "Carrera 6A # 149 - 43");
        //client.update(pedido);
        
        JSONObject entity= client.load("1210001085046910");
        System.out.println(entity.toString());
    }
    
}
