/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dot.gcpbasedot.util;

import com.dot.gcpbasedot.dto.OperationCallback;
import com.dot.gcpbasedot.dto.ResultListCallback;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author lacastrillov
 */
public class Util {
    
    /**
     * 
     * @param listDto
     * @param messagge
     * @param success
     * @return 
     */
    public static String getResultListCallback(List listDto, String messagge, boolean success) {
        return getResultListCallback(listDto, (long) listDto.size(), messagge, success);
    }

    /**
     * 
     * @param listDto
     * @param totalCount
     * @param messagge
     * @param success
     * @return 
     */
    public static String getResultListCallback(List listDto, Long totalCount, String messagge, boolean success) {
        ResultListCallback resultListCallback = getResultList(listDto, totalCount, messagge, success);
        
        return objectToJson(resultListCallback);
    }

    /**
     * 
     * @param listDto
     * @param messagge
     * @param success
     * @return 
     */
    public static ResultListCallback getResultList(List listDto, String messagge, boolean success) {
        return getResultList(listDto, (long) listDto.size(), messagge, success);
    }

    /**
     * 
     * @param listDto
     * @param totalCount
     * @param messagge
     * @param success
     * @return 
     */
    public static ResultListCallback getResultList(List listDto, Long totalCount, String messagge, boolean success) {
        ResultListCallback resultListCallback = new ResultListCallback();

        resultListCallback.setData(listDto);
        resultListCallback.setMessage(messagge);
        resultListCallback.setSuccess(success);
        resultListCallback.setTotalCount(totalCount);

        return resultListCallback;
    }

    /**
     * 
     * @param dto
     * @param messagge
     * @param success
     * @return 
     */
    public static String getOperationCallback(Object dto, String messagge, boolean success) {
        OperationCallback operationCallback = getOperation(dto, messagge, success);

        return objectToJson(operationCallback);
    }

    /**
     * 
     * @param dto
     * @param messagge
     * @param success
     * @return 
     */
    public static OperationCallback getOperation(Object dto, String messagge, boolean success) {
        OperationCallback operationCallback = new OperationCallback();

        operationCallback.setData(dto);
        operationCallback.setMessage(messagge);
        operationCallback.setSuccess(success);

        return operationCallback;
    }

    /**
     * 
     * @param obj
     * @return 
     */
    public static String objectToJson(Object obj) {
        GsonBuilder gsonB = new GsonBuilder();
        gsonB.setDateFormat("dd/MM/yyyy");
        Gson gson = gsonB.create();

        return gson.toJson(obj);
    }
    
    /**
     * 
     * @param diffHour
     * @return 
     */
    public static Time getCurrentTime(int diffHour){
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR, cal.get(Calendar.HOUR) - diffHour);
        Date currentDate = cal.getTime();
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
        sdfTime.setTimeZone(TimeZone.getTimeZone("EST"));
        
        return Time.valueOf(sdfTime.format(currentDate));
    }
    
    /**
     * 
     * @param object
     * @return 
     */
    public static String jsonToYaml(Object object){
        Yaml yaml= new Yaml();
        Map<String, Object> map = jsonToHashMap(objectToJson(object));
        String output = yaml.dump(map);
        
        return output;
    }

    /**
     * 
     * @param json
     * @return 
     */
    public static HashMap<String, Object> jsonToHashMap(String json) {
        HashMap<String, Object> data = new HashMap();
        try {
            JSONObject jsonObject = new JSONObject(json);
            Iterator fields = jsonObject.keys();
            while (fields.hasNext()) {
                String field = fields.next().toString();
                if (!jsonObject.isNull(field)) {
                    Object fieldObj = jsonObject.get(field);
                    if (fieldObj instanceof JSONArray) {
                        JSONArray fieldJsonArray = (JSONArray)fieldObj;
                        List array= new ArrayList();
                        for(int i=0; i<fieldJsonArray.length(); i++){
                            Object fieldChildObj = fieldJsonArray.get(i);
                            if (fieldChildObj instanceof JSONObject) {
                                array.add(jsonToHashMap(fieldChildObj.toString()));
                            }else{
                                array.add(fieldChildObj);
                            }
                        }
                        data.put(field, array);
                    }else if (fieldObj instanceof JSONObject) {
                        JSONObject fieldJsonObject = (JSONObject)fieldObj;
                        data.put(field, jsonToHashMap(fieldJsonObject.toString()));
                    }else {
                        data.put(field, jsonObject.get(field));
                    }
                } else {
                    data.put(field, null);
                }
            }
        } catch (JSONException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        return data;
    }
    
    /**
     * 
     * @param json
     * @return 
     */
    public static String remakeJSONObject(String json){
        JSONObject source= new JSONObject(json);
        JSONObject finalObject= new JSONObject();
        
        for (Object keyObj : source.keySet()) {
            String key= keyObj.toString();
            if(source.get(key).toString().startsWith("[")){
                JSONArray value = source.getJSONArray(key);
                assignValue(finalObject, key, value.get(0));
            }else{
                assignValue(finalObject, key, source.get(key).toString());
            }
        }
        
        return finalObject.toString();
    };
    
    /**
     * 
     * @param obj
     * @param key
     * @param value 
     */
    public static void assignValue(JSONObject obj, String key, Object value){
        if(key.contains(".")){
            String firstKey= key.substring(0, key.indexOf("."));
            String remainingKey= key.substring(key.indexOf(".")+1, key.length());
            if(firstKey.contains("[") && firstKey.contains("]")){
                String secondKey= firstKey.substring(0, firstKey.indexOf("["));
                if(!obj.has(secondKey)){
                    obj.put(secondKey, new JSONArray());
                }
                int index= Integer.parseInt(firstKey.replace(secondKey,"").replace("[","").replace("]",""));
                if(((JSONArray) obj.get(secondKey)).isNull(index)){
                    ((JSONArray) obj.get(secondKey)).put(index, new JSONObject());
                }
                assignValue(((JSONArray) obj.get(secondKey)).getJSONObject(index), remainingKey, value);
            }else{
                if(!obj.has(firstKey)){
                    obj.put(firstKey, new JSONObject());
                }
                assignValue(obj.getJSONObject(firstKey), remainingKey, value );
            }
            
        }else if(key.contains("[") && key.contains("]")){
            String firstKey= key.substring(0, key.indexOf("["));
            if(!obj.has(firstKey)){
                obj.put(firstKey, new JSONArray());
            }
            int index= Integer.parseInt(key.replace(firstKey,"").replace("[","").replace("]",""));
            ((JSONArray) obj.get(firstKey)).put(index, value);
        }else{
            obj.put(key, value);
        }
    };
    
    /**
     * 
     * @param json
     * @return 
     */
    public static JSONObject unremakeJSONObject(String json){
        JSONObject source= new JSONObject(json);
        JSONObject finalObject= new JSONObject();
        
        for (Object keyObj : source.keySet()) {
            String key= keyObj.toString();
            assignSingleLevelValue(key, finalObject, source.get(key));
        }
        
        return finalObject;
    };
    
    /**
     * 
     * @param level
     * @param finalObject
     * @param object 
     */
    public static void assignSingleLevelValue(String level, JSONObject finalObject, Object object){
        if(object instanceof JSONArray){
            JSONArray jsonArray= (JSONArray) object;
            for(int i=0; i<jsonArray.length(); i++){
                assignSingleLevelValue(level+"["+i+"]", finalObject, jsonArray.get(i));
            }
        }else if(object instanceof JSONObject){
            JSONObject jsonObject= (JSONObject) object;
            for(Object keyObj : jsonObject.keySet()) {
                String key= keyObj.toString();
                assignSingleLevelValue(level+"."+key, finalObject, jsonObject.get(key));
            }
        }else{
            finalObject.put(level, object);
        }
    };
    
    /**
     * 
     * @param parent
     * @param levels
     * @return 
     */
    public static JSONObject getRecursiveJSONObject(JSONObject parent, String levels){
        String[] arrayLevels= levels.split(">");
        JSONObject result= parent;
        for(String level: arrayLevels){
            if(result.has(level)){
                result= result.getJSONObject(level);
            }else{
                return null;
            }
        }
        return result;
    }
    
    /**
     * 
     * @param contentType
     * @return 
     */
    public static String getSimpleContentType(String contentType){
        List extensions= Arrays.asList(
                new String[] {"conf","css","csv","html","java","js","json","jsp","php","properties","txt","vm","xml"});
        if(extensions.contains(contentType)){
            return contentType;
        }
        switch(contentType){
            case "image/gif":
            case "image/png":
            case "image/jpeg":
                return "image";
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
            case "application/vnd.oasis.opendocument.text":
            case "application/msword":
                return "doc";
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
            case "application/vnd.ms-excel":
                return "xls";
            case "application/pdf":
                return "pdf";
            case "application/gzip":
            case "application/x-tar":
            case "application/x-rar":
            case "application/zip":
                return "compress";
            case "application/x-java-archive":
            case "application/x-webarchive":
                return "java";
            case "application/vnd.openxmlformats-officedocument.presentationml.presentation":
            case "application/vnd.oasis.opendocument.presentation":
            case "application/vnd.ms-powerpoint":
                return "ppt";
            case "audio/mpeg":
                return "audio";
            case "video/mp4":
                return "video";
            case "application/javascript":
                return "js";
            case "text/css":
                return "css";
            case "text/plain":
                return "txt";
            case "text/html":
                return "html";
            case "application/x-php":
                return "php";
            case "text/xml":
                return "xml";
            case "application/json":
                return "json";
            default:
                return "file";
        }
    }

}
