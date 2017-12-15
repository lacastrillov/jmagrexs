package com.dot.gcpbasedot.controller;

import com.dot.gcpbasedot.annotation.DoProcess;
import com.dot.gcpbasedot.annotation.HttpHeader;
import com.dot.gcpbasedot.annotation.PathVar;
import com.dot.gcpbasedot.dto.ExternalServiceDto;
import com.dot.gcpbasedot.dto.SOAPServiceDto;
import com.dot.gcpbasedot.interfaces.LogProcesInterface;
import com.dot.gcpbasedot.reflection.EntityReflection;
import com.dot.gcpbasedot.service.EntityService;
import com.dot.gcpbasedot.util.Util;
import com.dot.gcpbasedot.util.ExternalServiceConnection;
import com.dot.gcpbasedot.util.FileService;
import com.dot.gcpbasedot.util.SimpleSOAPClient;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.SOAPException;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public abstract class RestProcessController {

    protected static final Logger LOGGER = Logger.getLogger(RestProcessController.class);
    
    protected Long maxFileSizeToUpload=1024L;
    
    protected HashSet<String> nameProcesses= null;
    
    protected final Map<String, Class> inDtos = new HashMap<>();

    protected final Map<String, Class> outDtos = new HashMap<>();
    
    protected final Map<String, ExternalServiceConnection> externalServiceConnections = new HashMap<>();
    
    protected final Map<String, SimpleSOAPClient> simpleSOAPClients = new HashMap<>();
    
    private String mainProcessRef;
    
    private Class logProcessClass;
    
    private EntityService logProcessService;
    
    protected Map<String, String> envelopeMap= new HashMap<>();
    
    
    protected void addControlProcess(String mainProcessRef, Class logProcessClass, EntityService logProcessService){
        this.mainProcessRef= mainProcessRef;
        this.logProcessClass= logProcessClass;
        this.logProcessService= logProcessService;
    }
    
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
    
    protected String getClientId(){
        return "GCP";
    }
    
    @PostConstruct
    protected void initProcesses(){
        List<Method> processMethods= EntityReflection.getClassAnnotatedMethods(this.getClass(), DoProcess.class);
        for(Method process: processMethods){
            Class[] inTypes= process.getParameterTypes();
            if(inTypes.length==1){
                inDtos.put(process.getName(), inTypes[0]);
                outDtos.put(process.getName(), process.getReturnType());
            }
        }
    }
    
    @RequestMapping(value = "/doProcess.htm", method = RequestMethod.POST)
    @ResponseBody
    public byte[] doProcess(HttpServletRequest request, HttpServletResponse response) {
        String jsonBody;
        try {
            jsonBody = IOUtils.toString(request.getInputStream());
            JSONObject jsonObject= new JSONObject(jsonBody);
            return doProcess(jsonObject.getJSONObject("data").toString(), jsonObject.getString("processName"), request, response);
        } catch (IOException ex) {
            LOGGER.error("ERROR executeProcess", ex);
        }
        return getStringBytes("{success:false}");
    }
    
    @RequestMapping(value = "/doProcess/{processName}.htm", method = RequestMethod.POST)
    @ResponseBody
    public byte[] doProcess(@RequestParam(required = false) String data, @PathVariable String processName, HttpServletRequest request, HttpServletResponse response) {
        String jsonIn= data;
        String jsonOut= "";
        JSONObject jsonResult= new JSONObject();
        
        if(data==null){
            try {
                Map<String, String[]> map= request.getParameterMap();
                JSONObject jsonObject= new JSONObject(map);
                jsonIn= Util.remakeJSONObject(jsonObject.toString());
            } catch (Exception e) {
                jsonResult.put("success", false);
                jsonResult.put("message", "ERROR doProcess remakeJSONObject - "+e.getMessage());
                jsonOut= jsonResult.toString();
                LOGGER.error("ERROR doProcess", e);
            }
        }
        
        Date initDate= new Date();
        long initTime= initDate.getTime();
            
        try {
            if(externalServiceConnections.get(processName)!=null){
                jsonOut= callExternalService(processName, jsonIn, response);
            }else if(simpleSOAPClients.get(processName)!=null){
                jsonOut= callSOAPService(processName, jsonIn, response);
            }else{
                Method method = this.getClass().getMethod(processName, inDtos.get(processName));
                Object inObject= EntityReflection.jsonToObject(jsonIn, inDtos.get(processName));
                Object outObject = method.invoke(this, inObject);
                jsonOut= Util.objectToJson(outObject);
                response.addHeader("response-data-format", ExternalServiceDto.JSON);
            }
            jsonResult.put("success", true);
            jsonResult.put("message", "Proceso realizado");
        } catch (Exception e) {
            jsonResult.put("success", false);
            jsonResult.put("message", "ERROR doProcess jsonToObject - "+e.getMessage());
            jsonOut= jsonOut+" "+jsonResult.toString();
            LOGGER.error("ERROR doProcess", e);
        }    
        
        if(logProcessClass!=null && logProcessService!=null){
            Integer processId= this.createLogProcess(processName, jsonIn, jsonOut, initDate, initTime,
                    jsonResult.getString("message"), jsonResult.getBoolean("success"));
            response.addHeader("Process-Id", processId.toString());
        }
        
        return getStringBytes(jsonOut);
    }
    
    private String callExternalService(String processName, String data, HttpServletResponse response) throws IOException{
        ExternalServiceConnection externalServiceConnection= externalServiceConnections.get(processName);
        ExternalServiceDto externalService= externalServiceConnection.getExternalService();
        JSONObject jsonData= new JSONObject(data);
        response.addHeader("response-data-format", externalService.getResponseDataFormat());
        
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
    
    private String callSOAPService(String processName, String data, HttpServletResponse response) throws SOAPException, IOException{
        SimpleSOAPClient simpleSOAPClient= simpleSOAPClients.get(processName);
        JSONObject jsonData= new JSONObject(data);
        response.addHeader("response-data-format", "JSON");
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
    
    @RequestMapping(value = "/diskupload/{processName}/{processId}.htm")
    @ResponseBody
    public byte[] diskupload(HttpServletRequest request, @PathVariable String processName, @PathVariable Integer processId) {
        //50MB
        long maxFileSize= maxFileSizeToUpload * 1024 * 1024;

        String resultData;
        LogProcesInterface logProcess= (LogProcesInterface) logProcessService.loadById(processId);
        Object inObject= EntityReflection.jsonToObject(logProcess.getDataIn(), inDtos.get(processName));
        JSONObject unremakeDataIn= Util.unremakeJSONObject(logProcess.getDataIn());
        try {
            FileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setSizeMax(maxFileSize);
            
            List items = upload.parseRequest(request);
            Iterator iterator = items.iterator();
            while (iterator.hasNext()) {
                FileItem item = (FileItem) iterator.next();
                InputStream is= item.getInputStream();
                if(!item.isFormField() && !item.getName().equals("")){
                    //ObjectIn, FieldName, FileName, ContentType, Size, InputStream
                    Method method = this.getClass().getMethod(processName+"Files", inDtos.get(processName), String.class, String.class, String.class, int.class, InputStream.class);
                    if(method!=null){
                        String fieldName= item.getFieldName().replaceAll("_File", "");
                        String fileUrl = (String)method.invoke(this, inObject, fieldName, item.getName(), item.getContentType(), (int)item.getSize(), is);
                        unremakeDataIn.put(fieldName, fileUrl);
                    }
                }
            }
            String dataIn= Util.remakeJSONObject(unremakeDataIn.toString());
            logProcess.setDataIn(dataIn);
            logProcessService.update(logProcess);
            
            resultData= Util.getOperationCallback(dataIn, "Carga de archivos en el proceso "+processName+" realizada...", true);
        } catch (Exception e) {
            LOGGER.error("upload " + processName, e);
            resultData= Util.getOperationCallback(null, "Error al cargar archivos en el proceso " + processName + ": " + e.getMessage(), false);
        }
        return getStringBytes(resultData);
    }
    
    protected byte[] getStringBytes(String data){
        try {
            return data.getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            LOGGER.error("getStringBytes", ex);
            return null;
        }
    }
    
    @Async
    private Integer createLogProcess(String processName, String dataIn, String dataOut,
            Date initDate, long initTime, String message, boolean success){
        
        try{
            Date endDate= new Date();
            long endTime= endDate.getTime();
            long remaining= endTime - initTime;

            Calendar cal = Calendar.getInstance();
            cal.setTime(initDate);
            cal.set(Calendar.HOUR, cal.get(Calendar.HOUR) - 5);

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("EST"));

            Object inObject= EntityReflection.jsonToObject(dataIn, inDtos.get(processName), true);
            String minJsonIn= Util.objectToJson(inObject);

            LogProcesInterface logProcess= (LogProcesInterface) EntityReflection.getObjectForClass(logProcessClass);
            try{
                logProcess.setClientId(getClientId());
            }catch(Exception e){
                logProcess.setClientId("Anonimo");
            }
            logProcess.setDataIn(minJsonIn);
            logProcess.setDataOut(dataOut);
            logProcess.setOutputDataFormat("JSON");
            logProcess.setDuration((int)remaining);
            logProcess.setMainProcessRef(mainProcessRef);
            logProcess.setMessage(message);
            logProcess.setProcessName(processName);
            logProcess.setRecordTime(Time.valueOf(sdf.format(new Date())));
            logProcess.setRegistrationDate(initDate);
            logProcess.setSuccess(success);
            
            if(externalServiceConnections.get(processName)!=null){
                ExternalServiceConnection externalServiceConnection= externalServiceConnections.get(processName);
                ExternalServiceDto externalService= externalServiceConnection.getExternalService();
                logProcess.setOutputDataFormat(externalService.getResponseDataFormat());
                if(!externalService.isSaveResponseInLog()){
                    logProcess.setDataOut("");
                }
            }
            
            logProcessService.create(logProcess);
            
            return (Integer) logProcess.getId();
        }catch(Exception e){
            LOGGER.error("ERROR doProcess", e);
        }
        return null;
    }
    
}
