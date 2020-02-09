/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.components;

import com.lacv.jmagrexs.dto.GenericTableColumn;
import com.lacv.jmagrexs.reflection.EntityReflection;
import com.lacv.jmagrexs.util.Formats;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author lacastrillov
 */
@Component
public class JSONModels {
    
    @Autowired
    public ExtViewConfig extViewConfig;
    
    @Autowired
    public FieldConfigurationByAnnotations fcba;
    
    @Autowired
    public FieldConfigurationByTableColumns fctc;
    
    
    public JSONArray getJSONModel(Class dtoClass) {
        JSONArray jsonModel= new JSONArray();
        PropertyDescriptor[] propertyDescriptors = EntityReflection.getPropertyDescriptors(dtoClass);
        
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String type = propertyDescriptor.getPropertyType().getName();
            HashMap<String, String> defaultValueMap= fcba.getDefaultValueMap(dtoClass);
            
            if(type.equals("java.util.List")==false && type.equals("java.lang.Class")==false){
                JSONObject field= new JSONObject();
                String fieldName= propertyDescriptor.getName();
                field.put("name", fieldName);
                if(propertyDescriptor.getName().equals("id")){
                    field.put("useNull", true);
                }

                switch (type) {
                    case "java.lang.String":
                    case "char":
                    case "java.lang.Character":
                        field.put("type", "string");
                        break;
                    case "short":
                    case "java.lang.Short":
                    case "int":
                    case "java.lang.Integer":
                    case "long":
                    case "java.lang.Long":
                    case "java.math.BigInteger":
                        field.put("type", "int");
                        break;
                    case "double":
                    case "java.lang.Double":
                    case "float":
                    case "java.lang.Float":
                        field.put("type", "float");
                        break;
                    case "boolean":
                    case "java.lang.Boolean":
                        field.put("type", "bool");
                        break;
                    case "java.util.Date":
                        field.put("type", "date");
                        field.put("dateFormat", extViewConfig.getDatetimeFormat());
                        break;
                    default:
                        break;
                }
                if(defaultValueMap.containsKey(propertyDescriptor.getName())){
                    field.put("defaultValue", defaultValueMap.get(propertyDescriptor.getName()));
                }

                jsonModel.put(field);
            }
        }

        return jsonModel;
    }
    
    public JSONArray getJSONRecursiveModel(String parent, Class<?> dtoClass) {
        JSONArray jsonModel= new JSONArray();
        PropertyDescriptor[] propertyDescriptors = EntityReflection.getPropertyDescriptors(dtoClass);
        
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String type = propertyDescriptor.getPropertyType().getName();
            
            if(type.equals("java.util.List")==false && type.equals("java.lang.Class")==false){
                JSONObject field= new JSONObject();
                String fieldName= propertyDescriptor.getName();
                field.put("name", parent + fieldName);
                
                if(Formats.TYPES_LIST.contains(type)){
                    switch (type) {
                        case "java.lang.String":
                        case "char":
                        case "java.lang.Character":
                            field.put("type", "string");
                            break;
                        case "short":
                        case "java.lang.Short":
                        case "int":
                        case "java.lang.Integer":
                        case "long":
                        case "java.lang.Long":
                        case "java.math.BigInteger":
                            field.put("type", "int");
                            break;
                        case "double":
                        case "java.lang.Double":
                        case "float":
                        case "java.lang.Float":
                            field.put("type", "float");
                            break;
                        case "boolean":
                        case "java.lang.Boolean":
                            field.put("type", "bool");
                            break;
                        case "java.util.Date":
                            field.put("type", "date");
                            field.put("dateFormat", extViewConfig.getDatetimeFormat());
                            break;
                        default:
                            break;
                    }

                    jsonModel.put(field);
                }else{
                    Class childClass = propertyDescriptor.getPropertyType();
                    JSONArray childModel= getJSONRecursiveModel(parent+fieldName+".", childClass);
                    for(int i=0; i<childModel.length(); i++){
                        jsonModel.put(childModel.get(i));
                    }
                }
            }
        }

        return jsonModel;
    }
    
    public JSONArray getJSONModel(List<GenericTableColumn> columns) {
        JSONArray jsonModel= new JSONArray();
        
        for (GenericTableColumn column : columns) {
            String type = column.getDataType();
            HashMap<String, String> defaultValueMap= fctc.getDefaultValueMap(columns);
            
            if(type.equals("java.util.List")==false && type.equals("java.lang.Class")==false){
                JSONObject field= new JSONObject();
                field.put("name", column.getColumnAlias());
                if(column.getColumnAlias().equals("id")){
                    field.put("useNull", true);
                }

                switch (type) {
                    case "java.lang.String":
                    case "char":
                    case "java.lang.Character":
                        field.put("type", "string");
                        break;
                    case "short":
                    case "java.lang.Short":
                    case "int":
                    case "java.lang.Integer":
                    case "long":
                    case "java.lang.Long":
                    case "java.math.BigInteger":
                        field.put("type", "int");
                        break;
                    case "double":
                    case "java.lang.Double":
                    case "float":
                    case "java.lang.Float":
                        field.put("type", "float");
                        break;
                    case "boolean":
                    case "java.lang.Boolean":
                        field.put("type", "bool");
                        break;
                    case "java.util.Date":
                        field.put("type", "date");
                        field.put("dateFormat", extViewConfig.getDatetimeFormat());
                        break;
                    default:
                        break;
                }
                if(defaultValueMap.containsKey(column.getColumnAlias())){
                    field.put("defaultValue", defaultValueMap.get(column.getColumnAlias()));
                }

                jsonModel.put(field);
            }
        }

        return jsonModel;
    }
    
    public JSONArray getJSONModelValidations(Class<?> dtoClass) {
        JSONArray jsonModelValidations= new JSONArray();
        
        HashSet<String> fieldsNN= fcba.getNotNullFields(dtoClass);
        for (String f : fieldsNN) {
            JSONObject field= new JSONObject();
            field.put("type", "length");
            field.put("field", f);
            field.put("min", 1);
            jsonModelValidations.put(field);
        }

        return jsonModelValidations;
    }
    
    /*public JSONArray getJSONRecursiveModelValidations(String parent, Class entityClass) {
        JSONArray jsonModelValidations= new JSONArray();
        
        HashSet<String> fieldsNN= fcba.getNotNullFields(entityClass);
        for (String f : fieldsNN) {
            JSONObject field= new JSONObject();
            field.put("type", "length");
            field.put("field", parent+f);
            field.put("min", 1);
            jsonModelValidations.put(field);
        }
        
        PropertyDescriptor[] propertyDescriptors = EntityReflection.getPropertyDescriptors(entityClass);
        
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String type = propertyDescriptor.getPropertyType().getName();
            String fieldName= propertyDescriptor.getName();
            
            if(type.equals("java.util.List")==false && type.equals("java.lang.Class")==false){
                if(!Formats.TYPES_LIST.contains(type)){
                    Class childClass = propertyDescriptor.getPropertyType();
                    JSONArray childModelValidations= getJSONRecursiveModelValidations(parent+fieldName+".", childClass);
                    for(int i=0; i<childModelValidations.length(); i++){
                        jsonModelValidations.put(childModelValidations.get(i));
                    }
                }
            }
        }

        return jsonModelValidations;
    }*/
    
    public JSONArray getJSONModelValidations(List<GenericTableColumn> columns) {
        JSONArray jsonModelValidations= new JSONArray();
        
        HashSet<String> fieldsNN= fctc.getNotNullFields(columns);
        for (String f : fieldsNN) {
            JSONObject field= new JSONObject();
            field.put("type", "length");
            field.put("field", f);
            field.put("min", 1);
            jsonModelValidations.put(field);
        }

        return jsonModelValidations;
    }
    
}
