/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dot.gcpbasedot.service;

import com.dot.gcpbasedot.annotation.HttpHeader;
import com.dot.gcpbasedot.annotation.PathVar;
import com.dot.gcpbasedot.dto.ExternalServiceDto;
import com.dot.gcpbasedot.dto.SOAPServiceDto;
import com.dot.gcpbasedot.reflection.EntityReflection;
import com.dot.gcpbasedot.util.ExternalServiceConnection;
import com.dot.gcpbasedot.util.FileService;
import com.dot.gcpbasedot.util.SimpleSOAPClient;
import com.dot.gcpbasedot.util.Util;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.soap.SOAPException;
import org.json.JSONObject;

/**
 *
 * @author grupot
 */
public abstract class ExternalServiceImpl implements ExternalService {
    
    protected final Map<String, ExternalServiceConnection> externalServiceConnections = new HashMap<>();
    
    protected final Map<String, SimpleSOAPClient> simpleSOAPClients = new HashMap<>();
    
    protected Map<String, String> envelopeMap= new HashMap<>();
    
    protected final Map<String, Class> inDtos = new HashMap<>();

    protected final Map<String, Class> outDtos = new HashMap<>();
    
    private String mainProcessRef;
    
    
    protected void enableExternalService(ExternalServiceDto externalService){
        ExternalServiceConnection externalServiceConnection= new ExternalServiceConnection(externalService);
        externalServiceConnections.put(externalService.getProcessName(), externalServiceConnection);
        inDtos.put(externalService.getProcessName(), externalService.getInClass());
        outDtos.put(externalService.getProcessName(), externalService.getOutClass());
    }
    
    protected void enableSOAPClient(SOAPServiceDto soapService){
        SimpleSOAPClient simpleSOATClient= new SimpleSOAPClient(soapService);
        simpleSOAPClients.put(soapService.getProcessName(), simpleSOATClient);
        inDtos.put(soapService.getProcessName(), soapService.getInClass());
    }
    
    protected String callExternalService(String processName, Object data) throws IOException{
        ExternalServiceConnection externalServiceConnection= externalServiceConnections.get(processName);
        ExternalServiceDto externalService= externalServiceConnection.getExternalService();
        JSONObject jsonData= new JSONObject(Util.objectToJson(data));
        /*if(response!=null){
            response.addHeader("response-data-format", externalService.getResponseDataFormat());
        }*/
        
        Map<String, String> headers= null;
        Map<String, String> pathVars= null;
        Map<String, String> parameters= null;
        Object body= null;
        
        //Headers
        List<Field> headerFields= EntityReflection.getEntityAnnotatedFields(externalService.getInClass(), HttpHeader.class);
        if(headerFields.size()>0){
            headers= new HashMap<>();
            for(Field field: headerFields){
                HttpHeader an= field.getAnnotation(HttpHeader.class);
                headers.put(an.value(), jsonData.get(field.getName()).toString());
                jsonData.remove(field.getName());
            }
        }
        
        //Path Vars
        List<Field> pathVarFields= EntityReflection.getEntityAnnotatedFields(externalService.getInClass(), PathVar.class);
        if(pathVarFields.size()>0){
            pathVars= new HashMap<>();
            for(Field field: pathVarFields){
                pathVars.put(field.getName(), jsonData.get(field.getName()).toString());
                jsonData.remove(field.getName());
            }
        }
        
        if(jsonData.names()!=null && jsonData.names().length()>0){
            if(externalService.getModeSendingData().equals(ExternalServiceDto.IN_BODY)){
                body= EntityReflection.jsonToObject(jsonData.toString(), externalService.getInClass());
            }else if(externalService.getModeSendingData().equals(ExternalServiceDto.IN_PARAMETERS)){
                parameters= new HashMap<>();
                for(int i = 0; i<jsonData.names().length(); i++){
                    String fieldName= jsonData.names().getString(i);
                    parameters.put(fieldName, jsonData.get(fieldName).toString());
                }
            }
        }
        
        String jsonOut;
        if(body!=null){
            jsonOut= externalServiceConnection.getStringResult(headers, pathVars, body);
        }else{
            jsonOut= externalServiceConnection.getStringResult(headers, pathVars, parameters);
        }
        
        return jsonOut;
    }
    
    protected String callSOAPService(String processName, Object data) throws SOAPException, IOException{
        SimpleSOAPClient simpleSOAPClient= simpleSOAPClients.get(processName);
        JSONObject jsonData= new JSONObject(Util.objectToJson(data));
        /*if(response!=null){
            response.addHeader("response-data-format", "JSON");
        }*/
        String xmlRequestBody= null;
        if(!envelopeMap.containsKey(processName)){
            InputStream is= this.getClass().getClassLoader().getResourceAsStream("soap_envelopes/"+mainProcessRef+"-"+processName+".xml");
            envelopeMap.put(processName, FileService.getStringFromInputStream(is));
            xmlRequestBody= envelopeMap.get(processName);
        }
        xmlRequestBody= simpleSOAPClient.mergeDataInEnvelope(jsonData, envelopeMap.get(processName));
        String jsonOut= simpleSOAPClient.sendMessageGetJSON(xmlRequestBody).toString();
        
        return jsonOut;
    }
    
}
