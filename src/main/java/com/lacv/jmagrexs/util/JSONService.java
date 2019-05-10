/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.util;

import com.lacv.jmagrexs.components.ExtViewConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author lacastrillov
 */
public class JSONService {
    
    private static Gson gson;
    
    /**
     * 
     * @return 
     */
    public static Gson getGson(){
        if(gson==null){
            GsonBuilder gsonB = new GsonBuilder();
            gsonB.setDateFormat(getExtViewConfig().getDatetimeFormatJava());
            gson = gsonB.create();
        }
        return gson;
    }
    
    /**
     * 
     * @param obj
     * @return 
     */
    public static String objectToJson(Object obj) {
        String json= getGson().toJson(obj);
        if(json.contains(" 00:00:00")){
            json= json.replaceAll(" 00:00:00", "");
        }
        if(json.contains(" 12:00:00 AM")){
            json= json.replaceAll(" 12:00:00 AM", "");
        }
        return json;
    }
    
    /**
     * 
     * @param json
     * @param objectClass
     * @return 
     */
    public static Object jsonToObject(String json, Class objectClass) {
        return getGson().fromJson(json, objectClass);
    }
    
    /**
     * 
     * @param test
     * @return 
     */
    public static boolean isJSONValid(String test) {
        try {
            JSONObject jsonObject = new JSONObject(test);
        } catch (JSONException ex) {
            try {
                JSONArray jsonArray = new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
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
                try {
                    result= result.getJSONObject(level);
                } catch (JSONException ex) {
                    return null;
                }
            }else{
                return null;
            }
        }
        return result;
    }
    
    /**
     * 
     * @param object
     * @return 
     */
    public static String objectToYaml(Object object){
        String jsonObject= objectToJson(object);
        return jsonToYaml(jsonObject);
    }
    
    /**ram object
     * @param jsonObject
     * @return 
     */
    public static String jsonToYaml(String jsonObject){
        Yaml yaml= new Yaml();
        Map<String, Object> map = jsonToHashMap(jsonObject);
        String output = yaml.dump(map);
        
        return output;
    }
    
    /**
     * 
     * @return extViewConfig
     */
    public static ExtViewConfig getExtViewConfig(){
        ApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
        ExtViewConfig extViewConfig= (ExtViewConfig) ctx.getBean("extViewConfig");
        
        return extViewConfig;
    }
    
}
