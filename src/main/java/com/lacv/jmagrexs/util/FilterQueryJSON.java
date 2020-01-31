/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.util;

import com.lacv.jmagrexs.dao.Parameters;
import com.lacv.jmagrexs.domain.BaseEntity;
import com.lacv.jmagrexs.reflection.EntityReflection;

import java.beans.PropertyDescriptor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Embeddable;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author lacastrillov
 */
public class FilterQueryJSON {

    public static Parameters processFilters(String queryJson, Class<?> entityClass) {
        JSONObject filtersJson;
        if(queryJson.startsWith("(")){
            queryJson= queryJson.replaceAll("\\(", "{").replaceAll("\\)", "}");
            queryJson= queryJson.replaceAll("<", "[").replaceAll(">", "]");
            filtersJson = new JSONObject(queryJson);
        }else{
            filtersJson = new JSONObject(queryJson);
        }
        Parameters parameters = new Parameters();
        PropertyDescriptor[] properties = EntityReflection.getPropertyDescriptors(entityClass);

        if(filtersJson.has("eq")){
            processFiltersEqual(filtersJson.getJSONObject("eq"), parameters, properties);
        }
        if(filtersJson.has("gt")){
            processFiltersGreaterThan(filtersJson.getJSONObject("gt"), parameters, properties);
        }
        if(filtersJson.has("lt")){
            processFiltersLessThan(filtersJson.getJSONObject("lt"), parameters, properties);
        }
        if(filtersJson.has("gte")){
            processFiltersGreaterThanOrEqual(filtersJson.getJSONObject("gte"), parameters, properties);
        }
        if(filtersJson.has("lte")){
            processFiltersLessThanOrEqual(filtersJson.getJSONObject("lte"), parameters, properties);
        }
        if(filtersJson.has("dt")){
            processFiltersDifferentThan(filtersJson.getJSONObject("dt"), parameters, properties);
        }
        if(filtersJson.has("isn")){
            processFiltersIsNull(filtersJson.getJSONArray("isn"), parameters, properties);
        }
        if(filtersJson.has("isnn")){
            processFiltersIsNotNull(filtersJson.getJSONArray("isnn"), parameters, properties);
        }
        if(filtersJson.has("lk")){
            processFiltersLike(filtersJson.getJSONObject("lk"), parameters, properties);
        }
        if(filtersJson.has("in")){
            processFiltersIn(filtersJson.getJSONObject("in"), parameters, properties);
        }
        if(filtersJson.has("nin")){
            processFiltersNotIn(filtersJson.getJSONObject("nin"), parameters, properties);
        }
        if(filtersJson.has("btw")){
            processFiltersBetween(filtersJson.getJSONObject("btw"), parameters, properties);
        }
        if(filtersJson.has("vm")){
            processFiltersValueMap(filtersJson.getJSONObject("vm"), parameters, properties);
        }
        if(filtersJson.has("uv")){
            processUpdateValue(filtersJson.getJSONObject("uv"), parameters, properties);
        }

        return parameters;
    }

    private static void processFiltersEqual(JSONObject filters, Parameters parameters, PropertyDescriptor[] properties) {
        for (Object key : filters.keySet()) {
            String filterName = (String)key;
            String value = filters.get(filterName).toString();
            Class typeField = EntityReflection.getPropertyType(properties, filterName);
            try {
                if(typeField!=null){
                    Object parseValue = Formats.castParameter(typeField.getName(), value);
                    if (parseValue != null) {
                        parameters.whereEqual(filterName, parseValue);
                    } else if(typeField.getAnnotation(Embeddable.class)!=null){
                        parameters.whereEqual(filterName, Util.decodeObject(value, typeField));
                    } else {
                        PropertyDescriptor[] propertiesParam = EntityReflection.getPropertyDescriptors(typeField);
                        BaseEntity entityObject = (BaseEntity) EntityReflection.getObjectForClass(typeField);
                        if (entityObject != null) {
                            Class typeParam = EntityReflection.getPropertyType(propertiesParam, "id");
                            entityObject.setId(Formats.castParameter(typeParam.getName(), value));
                            parameters.whereEqual(filterName, entityObject);
                        }
                    }
                }
            } catch (NumberFormatException | ClassNotFoundException ex) {
                Logger.getLogger(FilterQueryJSON.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private static void processFiltersGreaterThan(JSONObject filters, Parameters parameters, PropertyDescriptor[] properties) {
        for (Object key : filters.keySet()) {
            String filterName = (String)key;
            String value = filters.get(filterName).toString();
            Class typeField = EntityReflection.getPropertyType(properties, filterName);
            try {
                if(typeField!=null){
                    Object parseValue = Formats.castParameter(typeField.getName(), value);
                    if(parseValue!=null){
                        parameters.whereGreaterThan(filterName, parseValue);
                    }
                }
            } catch (NumberFormatException | ClassNotFoundException ex) {
                Logger.getLogger(FilterQueryJSON.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private static void processFiltersLessThan(JSONObject filters, Parameters parameters, PropertyDescriptor[] properties) {
        for (Object key : filters.keySet()) {
            String filterName = (String)key;
            String value = filters.get(filterName).toString();
            Class typeField = EntityReflection.getPropertyType(properties, filterName);
            try {
                if(typeField!=null){
                    Object parseValue = Formats.castParameter(typeField.getName(), value);
                    if(parseValue!=null){
                        parameters.whereLessThan(filterName, parseValue);
                    }
                }
            } catch (NumberFormatException | ClassNotFoundException ex) {
                Logger.getLogger(FilterQueryJSON.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private static void processFiltersGreaterThanOrEqual(JSONObject filters, Parameters parameters, PropertyDescriptor[] properties) {
        for (Object key : filters.keySet()) {
            String filterName = (String)key;
            String value = filters.get(filterName).toString();
            Class typeField = EntityReflection.getPropertyType(properties, filterName);
            try {
                if(typeField!=null){
                    Object parseValue = Formats.castParameter(typeField.getName(), value);
                    if(parseValue!=null){
                        parameters.whereGreaterThanOrEqual(filterName, parseValue);
                    }
                }
            } catch (NumberFormatException | ClassNotFoundException ex) {
                Logger.getLogger(FilterQueryJSON.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private static void processFiltersLessThanOrEqual(JSONObject filters, Parameters parameters, PropertyDescriptor[] properties) {
        for (Object key : filters.keySet()) {
            String filterName = (String)key;
            String value = filters.get(filterName).toString();
            Class typeField = EntityReflection.getPropertyType(properties, filterName);
            try {
                if(typeField!=null){
                    Object parseValue = Formats.castParameter(typeField.getName(), value);
                    if(parseValue!=null){
                        parameters.whereLessThanOrEqual(filterName, parseValue);
                    }
                }
            } catch (NumberFormatException | ClassNotFoundException ex) {
                Logger.getLogger(FilterQueryJSON.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private static void processFiltersDifferentThan(JSONObject filters, Parameters parameters, PropertyDescriptor[] properties) {
        for (Object key : filters.keySet()) {
            String filterName = (String)key;
            String value = filters.get(filterName).toString();
            Class typeField = EntityReflection.getPropertyType(properties, filterName);
            try {
                if(typeField!=null){
                    Object parseValue = Formats.castParameter(typeField.getName(), value);
                    if(parseValue!=null){
                        parameters.whereDifferentThan(filterName, parseValue);
                    }else {
                        PropertyDescriptor[] propertiesParam = EntityReflection.getPropertyDescriptors(typeField);
                        BaseEntity entityObject = (BaseEntity) EntityReflection.getObjectForClass(typeField);
                        if (entityObject != null) {
                            Class typeParam = EntityReflection.getPropertyType(propertiesParam, "id");
                            entityObject.setId(Formats.castParameter(typeParam.getName(), value));
                            parameters.whereDifferentThan(filterName, entityObject);
                        }
                    }
                }
            } catch (NumberFormatException | ClassNotFoundException ex) {
                Logger.getLogger(FilterQueryJSON.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private static void processFiltersIsNull(JSONArray filters, Parameters parameters, PropertyDescriptor[] properties) {
        for(int i = 0 ; i < filters.length(); i++){
            String filterName = filters.get(i).toString();
            Class typeField = EntityReflection.getPropertyType(properties, filterName);
            if(typeField!=null){
                parameters.whereIsNull(filterName);
            }
        }
    }
    
    private static void processFiltersIsNotNull(JSONArray filters, Parameters parameters, PropertyDescriptor[] properties) {
        for(int i = 0 ; i < filters.length(); i++){
            String filterName = filters.get(i).toString();
            Class typeField = EntityReflection.getPropertyType(properties, filterName);
            if(typeField!=null){
                parameters.whereIsNotNull(filterName);
            }
        }
    }
    
    private static void processFiltersLike(JSONObject filters, Parameters parameters, PropertyDescriptor[] properties) {
        for (Object key : filters.keySet()) {
            String filterName = (String)key;
            String value = filters.get(filterName).toString();
            Class typeField = EntityReflection.getPropertyType(properties, filterName);
            if(typeField!=null && (typeField.getName().equals("java.lang.String") ||
                    typeField.getName().equals("char") || typeField.getName().equals("java.lang.Character"))){
                parameters.whereLike(filterName, value);
            }
        }
    }
    
    private static void processFiltersIn(JSONObject filters, Parameters parameters, PropertyDescriptor[] properties) {
        for (Object key : filters.keySet()) {
            String filterName = (String)key;
            JSONArray values = filters.getJSONArray(filterName);
            Class typeField = EntityReflection.getPropertyType(properties, filterName);
            try {
                if(typeField!=null && values.length()>0){
                    Object[] inValues= new Object[values.length()];
                    if(Formats.castParameter(typeField.getName(), values.get(0).toString())!=null){
                        for(int i = 0 ; i < values.length(); i++){
                            inValues[i]= Formats.castParameter(typeField.getName(), values.get(i).toString());
                        }
                    }else if(typeField.getAnnotation(Embeddable.class)!=null){
                        for(int i = 0 ; i < values.length(); i++){
                            inValues[i]= Util.decodeObject(values.get(i).toString(), typeField);
                        }
                    }else{
                        PropertyDescriptor[] propertiesParam = EntityReflection.getPropertyDescriptors(typeField);
                        for(int i = 0 ; i < values.length(); i++){
                            BaseEntity entityObject = (BaseEntity) EntityReflection.getObjectForClass(typeField);
                            if (entityObject != null) {
                                Class typeParam = EntityReflection.getPropertyType(propertiesParam, "id");
                                entityObject.setId(Formats.castParameter(typeParam.getName(), values.get(i).toString()));
                                inValues[i]= entityObject;
                            }
                        }
                    }
                    parameters.whereIn(filterName, inValues);
                }
            } catch (NumberFormatException | ClassNotFoundException ex) {
                Logger.getLogger(FilterQueryJSON.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private static void processFiltersNotIn(JSONObject filters, Parameters parameters, PropertyDescriptor[] properties) {
        for (Object key : filters.keySet()) {
            String filterName = (String)key;
            JSONArray values = filters.getJSONArray(filterName);
            Class typeField = EntityReflection.getPropertyType(properties, filterName);
            try {
                if(typeField!=null && values.length()>0){
                    Object[] inValues= new Object[values.length()];
                    if(Formats.castParameter(typeField.getName(), values.get(0).toString())!=null){
                        for(int i = 0 ; i < values.length(); i++){
                            inValues[i]= Formats.castParameter(typeField.getName(), values.get(i).toString());
                        }
                    }else if(typeField.getAnnotation(Embeddable.class)!=null){
                        for(int i = 0 ; i < values.length(); i++){
                            inValues[i]= Util.decodeObject(values.get(i).toString(), typeField);
                        }
                    }else{
                        PropertyDescriptor[] propertiesParam = EntityReflection.getPropertyDescriptors(typeField);
                        for(int i = 0 ; i < values.length(); i++){
                            BaseEntity entityObject = (BaseEntity) EntityReflection.getObjectForClass(typeField);
                            if (entityObject != null) {
                                Class typeParam = EntityReflection.getPropertyType(propertiesParam, "id");
                                entityObject.setId(Formats.castParameter(typeParam.getName(), values.get(i).toString()));
                                inValues[i]= entityObject;
                            }
                        }
                    }
                    parameters.whereNotIn(filterName, inValues);
                }
            } catch (NumberFormatException | ClassNotFoundException ex) {
                Logger.getLogger(FilterQueryJSON.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private static void processFiltersBetween(JSONObject filters, Parameters parameters, PropertyDescriptor[] properties) {
        for (Object key : filters.keySet()) {
            String filterName = (String)key;
            JSONArray values = filters.getJSONArray(filterName);
            Class typeField = EntityReflection.getPropertyType(properties, filterName);
            try {
                if(typeField!=null){
                    Object start= Formats.castParameter(typeField.getName(), values.get(0).toString());
                    Object end= Formats.castParameter(typeField.getName(), values.get(1).toString());
                    if(start!=null && end!=null){
                        parameters.whereBetween(filterName, start, end);
                    }
                }
            } catch (NumberFormatException | ClassNotFoundException ex) {
                Logger.getLogger(FilterQueryJSON.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private static void processFiltersValueMap(JSONObject filters, Parameters parameters, PropertyDescriptor[] properties) {
        for (Object key : filters.keySet()) {
            String filterName = (String)key;
            String value = filters.get(filterName).toString();
            Class typeField = EntityReflection.getPropertyType(properties, filterName);
            try {
                if(typeField!=null){
                    Object parseValue = Formats.castParameter(typeField.getName(), value);
                    if (parseValue != null) {
                        parameters.addValueMapParameter(filterName, parseValue);
                    }
                }
            } catch (NumberFormatException | ClassNotFoundException ex) {
                Logger.getLogger(FilterQueryJSON.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private static void processUpdateValue(JSONObject filters, Parameters parameters, PropertyDescriptor[] properties) {
        for (Object key : filters.keySet()) {
            String filterName = (String)key;
            String value = filters.get(filterName).toString();
            Class typeField = EntityReflection.getPropertyType(properties, filterName);
            try {
                if(typeField!=null){
                    Object parseValue = Formats.castParameter(typeField.getName(), value);
                    if (parseValue != null) {
                        parameters.updateValue(filterName, parseValue);
                    } else if(value.equals("0") || value.equals("")) {
                         parameters.updateValue(filterName, null);
                    } else {
                        PropertyDescriptor[] propertiesParam = EntityReflection.getPropertyDescriptors(typeField);
                        BaseEntity entityObject = (BaseEntity) EntityReflection.getObjectForClass(typeField);
                        if (entityObject != null) {
                            Class typeParam = EntityReflection.getPropertyType(propertiesParam, "id");
                            entityObject.setId(Formats.castParameter(typeParam.getName(), value));
                            parameters.updateValue(filterName, entityObject);
                        }
                    }
                }
            } catch (NumberFormatException | ClassNotFoundException ex) {
                Logger.getLogger(FilterQueryJSON.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
