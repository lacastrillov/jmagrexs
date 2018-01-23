/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dot.gcpbasedot.util;

import com.dot.gcpbasedot.reflection.EntityReflection;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

/**
 *
 * @author lacastrillov
 */
public class XMLMarshaller {
    
    private static final int PRETTY_PRINT_INDENT_FACTOR = 4;
    
    
    /**
     * 
     * @param json
     * @param rootNode
     * @return 
     */
    public static String convertJSONToXML(String json, String rootNode){
        try{
            return XML.toString(new JSONObject(json), rootNode);
        }catch(JSONException e){
            try{
                return XML.toString(new JSONArray(json), rootNode);
            }catch(JSONException ex){
                return null;
            }
        }
    }

    /**
     * 
     * @param object
     * @return 
     */
    public static String convertObjectToXML(Object object) {
        String simpleType= object.getClass().getSimpleName();
        if(simpleType.equals("ArrayList")){
            ArrayList<?> list= (ArrayList<?>) object;
            simpleType= list.get(0).getClass().getSimpleName();
        }
        String xml= convertJSONToXML(Util.objectToJson(object), simpleType);
        
        return xml;
    }

    /**
     * 
     * @param object
     * @param filepath 
     * @throws java.io.IOException 
     */
    public static void convertObjectToXMLFile(Object object, String filepath) throws IOException {
        String xml= convertObjectToXML(object);
        FileService.setTextFile(xml, filepath);
    }
    
    /**
     * 
     * @param xml
     * @return 
     */
    public static String convertXMLToJSON(String xml){
        JSONObject xmlJSONObj = XML.toJSONObject(xml);
        String json= xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);
        JSONObject root= new JSONObject(json);
        Iterator keys = root.keys();
        if (!root.keySet().isEmpty() && root.keySet().size()==1) {
            return root.get(keys.next().toString()).toString();
        }
        return json;
    }

    /**
     * 
     * @param xml
     * @param objectClass
     * @return 
     */
    public static Object convertXMLToObject(String xml, Class objectClass) {
        String jsonPrettyPrintString = convertXMLToJSON(xml);
        
        return EntityReflection.jsonToObject(jsonPrettyPrintString, objectClass);
    }
    
    /**
     * 
     * @param xml
     * @param objectClass
     * @return 
     */
    public static List convertXMLToList(String xml, Class objectClass) {
        List<Object> result = new ArrayList<>();
        String jsonPrettyPrintString = convertXMLToJSON(xml);
        
        JSONArray array= new JSONArray(jsonPrettyPrintString);
        for (int i = 0; i < array.length(); i++) {
            result.add(EntityReflection.jsonToObject(array.getJSONObject(i).toString(), objectClass));
        }
        return result;
    }
    
    /**
     * 
     * @param xmlfile
     * @param objectClass
     * @return
     * @throws IOException 
     */
    public static Object convertXMLFileToObject(String xmlfile, Class objectClass) throws IOException {
        String xml= FileService.getTextFile(xmlfile);
        return convertXMLToObject(xml, objectClass);
    }
    
    /**
     * 
     * @param xmlfile
     * @param objectClass
     * @return
     * @throws IOException 
     */
    public static List convertXMLFileToList(String xmlfile, Class objectClass) throws IOException {
        String xml= FileService.getTextFile(xmlfile);
        return convertXMLToList(xml, objectClass);
    }
    
}
