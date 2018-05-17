/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.util;

import com.lacv.jmagrexs.dao.Parameters;
import com.lacv.jmagrexs.dto.GenericTableColumn;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author lacastrillov
 */
public class FilterQueryDirectJSON {

    public static Parameters processFilters(String queryJson, List<GenericTableColumn> columns) {
        JSONObject filtersJson;
        if(queryJson.startsWith("(")){
            filtersJson = new JSONObject(queryJson.replaceAll("\\(", "{").replaceAll("\\)", "}"));
        }else{
            filtersJson = new JSONObject(queryJson);
        }
        Parameters parameters = new Parameters();

        if(filtersJson.has("eq")){
            processFiltersEqual(filtersJson.getJSONObject("eq"), parameters, columns);
        }
        if(filtersJson.has("gt")){
            processFiltersGreaterThan(filtersJson.getJSONObject("gt"), parameters, columns);
        }
        if(filtersJson.has("lt")){
            processFiltersLessThan(filtersJson.getJSONObject("lt"), parameters, columns);
        }
        if(filtersJson.has("gte")){
            processFiltersGreaterThanOrEqual(filtersJson.getJSONObject("gte"), parameters, columns);
        }
        if(filtersJson.has("lte")){
            processFiltersLessThanOrEqual(filtersJson.getJSONObject("lte"), parameters, columns);
        }
        if(filtersJson.has("dt")){
            processFiltersDifferentThan(filtersJson.getJSONObject("dt"), parameters, columns);
        }
        if(filtersJson.has("isn")){
            processFiltersIsNull(filtersJson.getJSONArray("isn"), parameters, columns);
        }
        if(filtersJson.has("isnn")){
            processFiltersIsNotNull(filtersJson.getJSONArray("isnn"), parameters, columns);
        }
        if(filtersJson.has("lk")){
            processFiltersLike(filtersJson.getJSONObject("lk"), parameters, columns);
        }
        if(filtersJson.has("in")){
            processFiltersIn(filtersJson.getJSONObject("in"), parameters, columns);
        }
        if(filtersJson.has("nin")){
            processFiltersNotIn(filtersJson.getJSONObject("nin"), parameters, columns);
        }
        if(filtersJson.has("btw")){
            processFiltersBetween(filtersJson.getJSONObject("btw"), parameters, columns);
        }
        if(filtersJson.has("vm")){
            processFiltersValueMap(filtersJson.getJSONObject("vm"), parameters, columns);
        }
        if(filtersJson.has("uv")){
            processUpdateValue(filtersJson.getJSONObject("uv"), parameters, columns);
        }

        return parameters;
    }

    private static void processFiltersEqual(JSONObject filters, Parameters parameters, List<GenericTableColumn> columns) {
        for (Object key : filters.keySet()) {
            String filterName = (String)key;
            String value = filters.get(filterName).toString();
            String typeField = getPropertyType(columns, filterName);
            try {
                if(typeField!=null){
                    Object parseValue = Formats.castParameter(typeField, value);
                    if (parseValue != null) {
                        parameters.whereEqual(filterName, parseValue);
                    } 
                }
            } catch (NumberFormatException | ClassNotFoundException ex) {
                Logger.getLogger(FilterQueryDirectJSON.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private static void processFiltersGreaterThan(JSONObject filters, Parameters parameters, List<GenericTableColumn> columns) {
        for (Object key : filters.keySet()) {
            String filterName = (String)key;
            String value = filters.get(filterName).toString();
            String typeField = getPropertyType(columns, filterName);
            try {
                if(typeField!=null){
                    Object parseValue = Formats.castParameter(typeField, value);
                    if(parseValue!=null){
                        parameters.whereGreaterThan(filterName, parseValue);
                    }
                }
            } catch (NumberFormatException | ClassNotFoundException ex) {
                Logger.getLogger(FilterQueryDirectJSON.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private static void processFiltersLessThan(JSONObject filters, Parameters parameters, List<GenericTableColumn> columns) {
        for (Object key : filters.keySet()) {
            String filterName = (String)key;
            String value = filters.get(filterName).toString();
            String typeField = getPropertyType(columns, filterName);
            try {
                if(typeField!=null){
                    Object parseValue = Formats.castParameter(typeField, value);
                    if(parseValue!=null){
                        parameters.whereLessThan(filterName, parseValue);
                    }
                }
            } catch (NumberFormatException | ClassNotFoundException ex) {
                Logger.getLogger(FilterQueryDirectJSON.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private static void processFiltersGreaterThanOrEqual(JSONObject filters, Parameters parameters, List<GenericTableColumn> columns) {
        for (Object key : filters.keySet()) {
            String filterName = (String)key;
            String value = filters.get(filterName).toString();
            String typeField = getPropertyType(columns, filterName);
            try {
                if(typeField!=null){
                    Object parseValue = Formats.castParameter(typeField, value);
                    if(parseValue!=null){
                        parameters.whereGreaterThanOrEqual(filterName, parseValue);
                    }
                }
            } catch (NumberFormatException | ClassNotFoundException ex) {
                Logger.getLogger(FilterQueryDirectJSON.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private static void processFiltersLessThanOrEqual(JSONObject filters, Parameters parameters, List<GenericTableColumn> columns) {
        for (Object key : filters.keySet()) {
            String filterName = (String)key;
            String value = filters.get(filterName).toString();
            String typeField = getPropertyType(columns, filterName);
            try {
                if(typeField!=null){
                    Object parseValue = Formats.castParameter(typeField, value);
                    if(parseValue!=null){
                        parameters.whereLessThanOrEqual(filterName, parseValue);
                    }
                }
            } catch (NumberFormatException | ClassNotFoundException ex) {
                Logger.getLogger(FilterQueryDirectJSON.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private static void processFiltersDifferentThan(JSONObject filters, Parameters parameters, List<GenericTableColumn> columns) {
        for (Object key : filters.keySet()) {
            String filterName = (String)key;
            String value = filters.get(filterName).toString();
            String typeField = getPropertyType(columns, filterName);
            try {
                if(typeField!=null){
                    Object parseValue = Formats.castParameter(typeField, value);
                    if(parseValue!=null){
                        parameters.whereDifferentThan(filterName, parseValue);
                    }
                }
            } catch (NumberFormatException | ClassNotFoundException ex) {
                Logger.getLogger(FilterQueryDirectJSON.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private static void processFiltersIsNull(JSONArray filters, Parameters parameters, List<GenericTableColumn> columns) {
        for(int i = 0 ; i < filters.length(); i++){
            String filterName = filters.get(i).toString();
            String typeField = getPropertyType(columns, filterName);
            if(typeField!=null){
                parameters.whereIsNull(filterName);
            }
        }
    }
    
    private static void processFiltersIsNotNull(JSONArray filters, Parameters parameters, List<GenericTableColumn> columns) {
        for(int i = 0 ; i < filters.length(); i++){
            String filterName = filters.get(i).toString();
            String typeField = getPropertyType(columns, filterName);
            if(typeField!=null){
                parameters.whereIsNotNull(filterName);
            }
        }
    }
    
    private static void processFiltersLike(JSONObject filters, Parameters parameters, List<GenericTableColumn> columns) {
        for (Object key : filters.keySet()) {
            String filterName = (String)key;
            String value = filters.get(filterName).toString();
            String typeField = getPropertyType(columns, filterName);
            if(typeField!=null && typeField.equals("java.lang.String")){
                parameters.whereLike(filterName, value);
            }
        }
    }
    
    private static void processFiltersIn(JSONObject filters, Parameters parameters, List<GenericTableColumn> columns) {
        for (Object key : filters.keySet()) {
            String filterName = (String)key;
            JSONArray values = filters.getJSONArray(filterName);
            String typeField = getPropertyType(columns, filterName);
            try {
                if(typeField!=null){
                    Object[] inValues= new Object[values.length()];
                    if(Formats.castParameter(typeField, values.get(0).toString())!=null){
                        for(int i = 0 ; i < values.length(); i++){
                            inValues[i]= Formats.castParameter(typeField, values.get(i).toString());
                        }
                    }
                    parameters.whereIn(filterName, inValues);
                }
            } catch (NumberFormatException | ClassNotFoundException ex) {
                Logger.getLogger(FilterQueryDirectJSON.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private static void processFiltersNotIn(JSONObject filters, Parameters parameters, List<GenericTableColumn> columns) {
        for (Object key : filters.keySet()) {
            String filterName = (String)key;
            JSONArray values = filters.getJSONArray(filterName);
            String typeField = getPropertyType(columns, filterName);
            try {
                if(typeField!=null){
                    Object[] inValues= new Object[values.length()];
                    if(Formats.castParameter(typeField, values.get(0).toString())!=null){
                        for(int i = 0 ; i < values.length(); i++){
                            inValues[i]= Formats.castParameter(typeField, values.get(i).toString());
                        }
                    }
                    parameters.whereNotIn(filterName, inValues);
                }
            } catch (NumberFormatException | ClassNotFoundException ex) {
                Logger.getLogger(FilterQueryDirectJSON.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private static void processFiltersBetween(JSONObject filters, Parameters parameters, List<GenericTableColumn> columns) {
        for (Object key : filters.keySet()) {
            String filterName = (String)key;
            JSONArray values = filters.getJSONArray(filterName);
            String typeField = getPropertyType(columns, filterName);
            try {
                if(typeField!=null){
                    Object start= Formats.castParameter(typeField, values.get(0).toString());
                    Object end= Formats.castParameter(typeField, values.get(1).toString());
                    if(start!=null && end!=null){
                        parameters.whereBetween(filterName, start, end);
                    }
                }
            } catch (NumberFormatException | ClassNotFoundException ex) {
                Logger.getLogger(FilterQueryDirectJSON.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private static void processFiltersValueMap(JSONObject filters, Parameters parameters, List<GenericTableColumn> columns) {
        for (Object key : filters.keySet()) {
            String filterName = (String)key;
            String value = filters.get(filterName).toString();
            String typeField = getPropertyType(columns, filterName);
            try {
                if(typeField!=null){
                    Object parseValue = Formats.castParameter(typeField, value);
                    if (parseValue != null) {
                        parameters.addValueMapParameter(filterName, parseValue);
                    }
                }
            } catch (NumberFormatException | ClassNotFoundException ex) {
                Logger.getLogger(FilterQueryDirectJSON.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private static void processUpdateValue(JSONObject filters, Parameters parameters, List<GenericTableColumn> columns) {
        for (Object key : filters.keySet()) {
            String filterName = (String)key;
            String value = filters.get(filterName).toString();
            String typeField = getPropertyType(columns, filterName);
            try {
                if(typeField!=null){
                    Object parseValue = Formats.castParameter(typeField, value);
                    if (parseValue != null) {
                        parameters.updateValue(filterName, parseValue);
                    }
                }
            } catch (NumberFormatException | ClassNotFoundException ex) {
                Logger.getLogger(FilterQueryDirectJSON.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static String getPropertyType(List<GenericTableColumn> columns, String filterName){
        for(GenericTableColumn column: columns){
            if(column.getColumnAlias().equals(filterName)){
                return column.getDataType();
            }
        }
        return null;
    }
    
}
