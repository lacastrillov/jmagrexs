package com.dot.gcpbasedot.controller;

import com.dot.gcpbasedot.domain.BaseEntity;
import com.dot.gcpbasedot.reflection.EntityReflection;
import com.dot.gcpbasedot.service.ConfigurationObjectService;
import com.dot.gcpbasedot.util.Util;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public abstract class RestConfigurationController {
    
    protected Long maxFileSizeToUpload=1024L;

    protected static final Logger LOGGER = Logger.getLogger(RestConfigurationController.class);
    
    protected final Map<String, ConfigurationObjectService> configurationObjectServices = new HashMap<>();
    
    
    protected void addControlConfigurationObject(String configurationObjectRef, ConfigurationObjectService configurationObjectService){
        configurationObjectServices.put(configurationObjectRef, configurationObjectService);
    }
    
    @RequestMapping(value = "/loadConfig/{configurationObjectRef}.htm", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public byte[] loadConfig(@PathVariable String configurationObjectRef) {
        BaseEntity dto = null;

        String resultData;
        try {
            Object configurationObject = configurationObjectServices.get(configurationObjectRef).load();
            resultData= Util.getOperationCallback(configurationObject, "Carga de " + configurationObjectRef + " realizada...", true);
        } catch (Exception e) {
            LOGGER.error("load " + configurationObjectRef, e);
            resultData= Util.getOperationCallback(dto, "Error en carga de " + configurationObjectRef + ": " + e.getMessage(), true);
        }
        return getStringBytes(resultData);
    }
    
    @RequestMapping(value = "/saveConfig.htm", method = RequestMethod.POST)
    @ResponseBody
    public byte[] saveConfig(HttpServletRequest request, HttpServletResponse response) {
        String jsonBody;
        try {
            jsonBody = IOUtils.toString(request.getInputStream());
            JSONObject jsonObject= new JSONObject(jsonBody);
            return saveConfig(jsonObject.getJSONObject("data").toString(), jsonObject.getString("configurationObjectRef"), request, response);
        } catch (IOException ex) {
            LOGGER.error("ERROR executeProcess", ex);
        }
        return getStringBytes("{success:false}");
    }
    
    @RequestMapping(value = "/saveConfig/{configurationObjectRef}.htm", method = RequestMethod.POST)
    @ResponseBody
    public byte[] saveConfig(@RequestParam(required = false) String data, @PathVariable String configurationObjectRef, HttpServletRequest request, HttpServletResponse response) {
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
            
        try {
            Class coClass= configurationObjectServices.get(configurationObjectRef).getConfigurationObjectClass();
            Object configurationObject= EntityReflection.jsonToObject(jsonIn, coClass);
            configurationObjectServices.get(configurationObjectRef).save(configurationObject);
            jsonOut= Util.getOperationCallback(configurationObject, "Actualización de " + configurationObjectRef + " realizada...", true);
        } catch (Exception e) {
            jsonOut= Util.getOperationCallback(null, "Error en actualizaci&oacute;n de " + configurationObjectRef + ": " + e.getMessage(), false);
            LOGGER.error("ERROR doProcess", e);
        }
        
        return getStringBytes(jsonOut);
    }
    
    @RequestMapping(value = "/diskupload/{configurationObjectRef}.htm")
    @ResponseBody
    public byte[] diskupload(HttpServletRequest request, @PathVariable String configurationObjectRef) {
        //50MB
        long maxFileSize= maxFileSizeToUpload * 1024 * 1024;

        String resultData;
        Class coClass= configurationObjectServices.get(configurationObjectRef).getConfigurationObjectClass();
        Object configurationObject = configurationObjectServices.get(configurationObjectRef).load();
        String configurationObjectJson= Util.objectToJson(configurationObject);
        JSONObject unremakeConfigurationObject= Util.unremakeJSONObject(configurationObjectJson);
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
                    Method method = this.getClass().getMethod(configurationObjectRef+"Files", coClass, String.class, String.class, String.class, int.class, InputStream.class);
                    if(method!=null){
                        String fileUrl = (String)method.invoke(this, configurationObject, item.getFieldName(), item.getName(), item.getContentType(), (int)item.getSize(), is);
                        unremakeConfigurationObject.put(item.getFieldName(), fileUrl);
                    }
                }
            }
            configurationObjectJson= Util.remakeJSONObject(unremakeConfigurationObject.toString());
            configurationObject= EntityReflection.jsonToObject(configurationObjectJson, coClass);
            configurationObjectServices.get(configurationObjectRef).save(configurationObject);
            
            resultData= Util.getOperationCallback(configurationObjectJson, "Carga de archivos en el configurationObject "+configurationObjectRef+" realizada...", true);
        } catch (Exception e) {
            LOGGER.error("upload " + configurationObjectRef, e);
            resultData= Util.getOperationCallback(null, "Error al cargar archivos en el configurationObject " + configurationObjectRef + ": " + e.getMessage(), false);
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
    
}
