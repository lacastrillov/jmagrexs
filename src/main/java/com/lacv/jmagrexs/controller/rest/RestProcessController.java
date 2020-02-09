package com.lacv.jmagrexs.controller.rest;

import com.lacv.jmagrexs.annotation.DoProcess;
import com.lacv.jmagrexs.dto.RESTServiceDto;
import com.lacv.jmagrexs.dto.SOAPServiceDto;
import com.lacv.jmagrexs.interfaces.LogProcesInterface;
import com.lacv.jmagrexs.reflection.EntityReflection;
import com.lacv.jmagrexs.service.EntityService;
import com.lacv.jmagrexs.service.ExternalService;
import com.lacv.jmagrexs.util.JSONService;
import com.lacv.jmagrexs.util.Util;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    
    private ExternalService externalService;
    
    private String mainProcessRef;
    
    private Class logProcessClass;
    
    private EntityService logProcessService;
    
    protected Map<String, String> envelopeMap= new HashMap<>();
    
    
    protected void addControlProcess(String mainProcessRef, Class logProcessClass, EntityService logProcessService){
        this.mainProcessRef= mainProcessRef;
        this.logProcessClass= logProcessClass;
        this.logProcessService= logProcessService;
    }
    
    protected void enableExternalService(ExternalService externalService){
        this.externalService= externalService;
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
    
    @RequestMapping(value = "/do/{processName}.htm", method = RequestMethod.POST)
    @ResponseBody
    public byte[] doProcess(@PathVariable String processName, HttpServletRequest request, HttpServletResponse response) {
        String data;
        try {
            data = IOUtils.toString(request.getInputStream());
            return doProcess(data, processName, request, response);
        } catch (IOException ex) {
            LOGGER.error("ERROR executeProcess", ex);
        }
        return Util.getStringBytes("{success:false}");
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
                jsonIn= JSONService.remakeJSONObject(jsonObject.toString());
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
            String responseDataFormat= RESTServiceDto.JSON;
            if(externalService!=null && externalService.isRESTService(processName)){
                RESTServiceDto restService= externalService.getRESTService(processName);
                responseDataFormat= restService.getResponseDataFormat();
                Object inObject= EntityReflection.jsonToObject(jsonIn, restService.getInClass());
                Object outObject= externalService.callRESTService(processName, inObject);
                if(restService.getOutClass().equals(String.class)){
                    jsonOut= (String) outObject;
                }else{
                    jsonOut= JSONService.objectToJson(outObject);
                }
            }else if(externalService!=null && externalService.isSOAPService(processName)){
                SOAPServiceDto soapService= externalService.getSOAPService(processName);
                Object inObject= EntityReflection.jsonToObject(jsonIn, soapService.getInClass());
                jsonOut= externalService.callSOAPService(processName, inObject);
            }else{
                Method method = this.getClass().getMethod(processName, inDtos.get(processName));
                Object inObject= EntityReflection.jsonToObject(jsonIn, inDtos.get(processName));
                Object outObject = method.invoke(this, inObject);
                jsonOut= JSONService.objectToJson(outObject);
            }
            if(response!=null){
                response.addHeader("response-data-format", responseDataFormat);
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
            if(response!=null && processId!=null){
                response.addHeader("Process-Id", processId.toString());
            }
        }
        
        return Util.getStringBytes(jsonOut);
    }
    
    public String doServerProcess(String processName, Object data){
        byte[] resultPse= doProcess(JSONService.objectToJson(data), processName, null, null);
        try {
            return new String(resultPse, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            return null;
        }
    }
    
    @RequestMapping(value = "/diskupload/{processName}/{processId}.htm")
    @ResponseBody
    public byte[] diskupload(HttpServletRequest request, @PathVariable String processName, @PathVariable Integer processId) {
        //50MB
        long maxFileSize= maxFileSizeToUpload * 1024 * 1024;

        String resultData;
        LogProcesInterface logProcess= (LogProcesInterface) logProcessService.loadById(processId);
        Object inObject= EntityReflection.jsonToObject(logProcess.getDataIn(), inDtos.get(processName));
        JSONObject unremakeDataIn= JSONService.unremakeJSONObject(logProcess.getDataIn());
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
            String dataIn= JSONService.remakeJSONObject(unremakeDataIn.toString());
            logProcess.setDataIn(dataIn);
            logProcessService.update(logProcess);
            
            resultData= Util.getOperationCallback(dataIn, "Carga de archivos en el proceso "+processName+" realizada...", true);
        } catch (Exception e) {
            LOGGER.error("upload " + processName, e);
            resultData= Util.getOperationCallback(null, "Error al cargar archivos en el proceso " + processName + ": " + e.getMessage(), false);
        }
        return Util.getStringBytes(resultData);
    }
    
    @Async
    private Integer createLogProcess(String processName, String dataIn, String dataOut,
            Date initDate, long initTime, String message, boolean success){
        
        try{
            Date endDate= new Date();
            long endTime= endDate.getTime();
            long remaining= endTime - initTime;

            LogProcesInterface logProcess= (LogProcesInterface) EntityReflection.getObjectForClass(logProcessClass);
            try{
                logProcess.setClientId(getClientId());
            }catch(Exception e){
                logProcess.setClientId("Anonimo");
            }
            Object inObject;
            if(externalService!=null && externalService.isRESTService(processName)){
                RESTServiceDto restService= externalService.getRESTService(processName);
                logProcess.setOutputDataFormat(restService.getResponseDataFormat());
                inObject= EntityReflection.jsonToObject(dataIn, restService.getInClass(), true);
                if(!restService.isSaveResponseInLog()){
                    logProcess.setDataOut("");
                }
            }else if(externalService!=null && externalService.isSOAPService(processName)){
                SOAPServiceDto soapService= externalService.getSOAPService(processName);
                logProcess.setOutputDataFormat(RESTServiceDto.JSON);
                inObject= EntityReflection.jsonToObject(dataIn, soapService.getInClass(), true);
                if(!soapService.isSaveResponseInLog()){
                    logProcess.setDataOut("");
                }
            }else{
                logProcess.setOutputDataFormat(RESTServiceDto.JSON);
                inObject= EntityReflection.jsonToObject(dataIn, inDtos.get(processName), true);
            }
            String minJsonIn= JSONService.objectToJson(inObject);
            logProcess.setDataIn(minJsonIn);
            logProcess.setDataOut(dataOut);
            logProcess.setDuration((int)remaining);
            logProcess.setMainProcessRef(mainProcessRef);
            logProcess.setMessage(message);
            logProcess.setProcessName(processName);
            logProcess.setRecordTime(Util.getCurrentTime(0));
            logProcess.setRegistrationDate(initDate);
            logProcess.setSuccess(success);
            
            logProcessService.create(logProcess);
            
            return (Integer) logProcess.getId();
        }catch(Exception e){
            LOGGER.error("ERROR doProcess", e);
        }
        return null;
    }
    
}
