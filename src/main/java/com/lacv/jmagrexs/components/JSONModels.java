/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.components;

import com.lacv.jmagrexs.annotation.Size;
import com.lacv.jmagrexs.dto.GenericTableColumn;
import com.lacv.jmagrexs.enums.FieldType;
import com.lacv.jmagrexs.reflection.EntityReflection;
import com.lacv.jmagrexs.util.Formats;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
            HashMap<String, String> defaultValueMap= fcba.getDefaultValueMap(propertyDescriptors, dtoClass);
            HashMap<String,String[]> typeFormFields= fcba.getTypeFormFields(dtoClass);
            
            if(type.equals("java.util.List")==false && type.equals("java.lang.Class")==false){
                JSONObject field= new JSONObject();
                String fieldName= propertyDescriptor.getName();
                String typeForm= "";
                if(typeFormFields.containsKey(fieldName)){
                    typeForm= typeFormFields.get(fieldName)[0];
                }
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
                        if(typeForm.equals(FieldType.DATETIME.name())){
                            field.put("dateFormat", extViewConfig.getDatetimeFormat());
                        }else{
                            field.put("dateFormat", extViewConfig.getDateFormat());
                        }
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
            HashMap<String,String[]> typeFormFields= fcba.getTypeFormFields(dtoClass);
            String type = propertyDescriptor.getPropertyType().getName();
            
            if(type.equals("java.util.List")==false && type.equals("java.lang.Class")==false){
                JSONObject field= new JSONObject();
                String fieldName= propertyDescriptor.getName();
                String typeForm= "";
                if(typeFormFields.containsKey(fieldName)){
                    typeForm= typeFormFields.get(fieldName)[0];
                }
                field.put("name", parent + fieldName);
                
                if(Formats.TYPES_LIST.contains(type)){
                    switch (type) {
                        case "java.lang.String":
                        case "char":
                        case "java.lang.Character":
                            field.put("type", "string");
                            break;
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
                            if(typeForm.equals(FieldType.DATETIME.name())){
                                field.put("dateFormat", extViewConfig.getDatetimeFormat());
                            }else{
                                field.put("dateFormat", extViewConfig.getDateFormat());
                            }
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
                        if(column.getFieldType()!=null && column.getFieldType().equals(FieldType.DATETIME.name())){
                            field.put("dateFormat", extViewConfig.getDatetimeFormat());
                        }else{
                            field.put("dateFormat", extViewConfig.getDateFormat());
                        }
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
        HashMap<String,JSONObject> validation= new HashMap<>();
        
        HashSet<String> fieldsNN= fcba.getNotNullFields(dtoClass);
        for (String f : fieldsNN) {
            JSONObject field= new JSONObject();
            field.put("type", "length");
            field.put("field", f);
            field.put("min", 1);
            validation.put(f,field);
        }
        
        List<Field> fieldsSize= EntityReflection.getEntityAnnotatedFields(dtoClass, Size.class);
        for(Field f: fieldsSize){
            Size annotation= f.getAnnotation(Size.class);
            JSONObject field= new JSONObject();
            field.put("type", "length");
            field.put("field", f.getName());
            if(annotation.min()==0 && fieldsNN.contains(f.getName())){
                field.put("min", 1);
            }else{
                field.put("min", annotation.min());
            }
            field.put("max", annotation.max());
            validation.put(f.getName(),field);
        }
        
        for (Map.Entry<String,JSONObject> entry : validation.entrySet()){
            jsonModelValidations.put(entry.getValue());
        }

        return jsonModelValidations;
    }
    
    public JSONArray getJSONRecursiveModelValidations(String parent, Class entityClass) {
        JSONArray jsonModelValidations= new JSONArray();
        HashMap<String,JSONObject> validation= new HashMap<>();
        
        HashSet<String> fieldsNN= fcba.getNotNullFields(entityClass);
        for (String f : fieldsNN) {
            JSONObject field= new JSONObject();
            field.put("type", "length");
            field.put("field", parent+f);
            field.put("min", 1);
            validation.put(f,field);
        }
        
        List<Field> fieldsSize= EntityReflection.getEntityAnnotatedFields(entityClass, Size.class);
        for(Field f: fieldsSize){
            Size annotation= f.getAnnotation(Size.class);
            JSONObject field= new JSONObject();
            field.put("type", "length");
            field.put("field", parent+f.getName());
            if(annotation.min()==0 && fieldsNN.contains(f.getName())){
                field.put("min", 1);
            }else{
                field.put("min", annotation.min());
            }
            field.put("max", annotation.max());
            validation.put(f.getName(),field);
        }
        
        for (Map.Entry<String,JSONObject> entry : validation.entrySet()){
            jsonModelValidations.put(entry.getValue());
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
    }
    
    public JSONArray getJSONModelValidations(List<GenericTableColumn> columns) {
        JSONArray jsonModelValidations= new JSONArray();
        HashMap<String,JSONObject> validation= new HashMap<>();
        
        HashSet<String> fieldsNN= fctc.getNotNullFields(columns);
        for (String f : fieldsNN) {
            JSONObject field= new JSONObject();
            field.put("type", "length");
            field.put("field", f);
            field.put("min", 1);
            validation.put(f,field);
        }
        
        HashMap<String, Integer> fieldsSize= fctc.getSizeColumnMap(columns);
        for (Map.Entry<String,Integer> entry : fieldsSize.entrySet()){
            JSONObject field= new JSONObject();
            field.put("type", "length");
            field.put("field", entry.getKey());
            if(fieldsNN.contains(entry.getKey())){
                field.put("min", 1);
            }else{
                field.put("min", 0);
            }
            field.put("max", entry.getValue());
            validation.put(entry.getKey(),field);
        }
        
        for (Map.Entry<String,JSONObject> entry : validation.entrySet()){
            jsonModelValidations.put(entry.getValue());
        }

        return jsonModelValidations;
    }
    
}
