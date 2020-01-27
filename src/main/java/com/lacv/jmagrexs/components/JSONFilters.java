/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.components;

import com.lacv.jmagrexs.dto.GenericTableColumn;
import com.lacv.jmagrexs.enums.FieldType;
import com.lacv.jmagrexs.enums.HideView;
import com.lacv.jmagrexs.enums.PageType;
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
import org.springframework.util.StringUtils;

/**
 *
 * @author lacastrillov
 */
@Component
public class JSONFilters {
    
    @Autowired
    public ExtViewConfig extViewConfig;
    
    @Autowired
    public FieldConfigurationByAnnotations fcba;
    
    @Autowired
    public FieldConfigurationByTableColumns fctc;
    
    @Autowired
    public RangeFunctions rf;
    
    private final double RANGE_COLUMN_WIDTH= 0.28;
    
    
    public JSONArray getFieldsFilters(Class dtoClass, String labelField, PageType pageType){
        JSONArray jsonFieldsFilters= new JSONArray();
        
        PropertyDescriptor[] propertyDescriptors = EntityReflection.getPropertyDescriptors(dtoClass);
        
        fcba.orderPropertyDescriptor(propertyDescriptors, dtoClass, labelField);
        HashMap<String, String> titledFieldsMap= fcba.getTitledFieldsMap(propertyDescriptors, dtoClass);
        HashSet<String> hideFields= fcba.getHideFields(dtoClass);
        HashMap<String,String[]> typeFormFields= fcba.getTypeFormFields(dtoClass);
        
        String container= "Instance.entityExtController.entityExtView";
        
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String type = propertyDescriptor.getPropertyType().getName();
            
            if(type.equals("java.util.List")==false && type.equals("java.lang.Class")==false){
                String fieldName= propertyDescriptor.getName();
                String fieldEntity= StringUtils.capitalize(fieldName);
                String fieldTitle= titledFieldsMap.get(fieldName);
                        
                // ADD TO jsonFormFields
                if(!hideFields.contains(fieldName + HideView.FILTER.name())){
                    if(Formats.TYPES_LIST.contains(type)){
                        this.addJSONField(jsonFieldsFilters, container, fieldName, fieldTitle, type, typeFormFields);
                    }else{
                        if(typeFormFields.containsKey(fieldName) && typeFormFields.get(fieldName)[0].equals(FieldType.MULTI_SELECT.name())){
                            String operatorCombobox= "@"+container+".commonExtView.getOperatorCombobox('"+fieldName+"','multiselect')@";
                            String entityMultiselect= "@"+container+".filterMultiselect"+fieldEntity+"@";
                            addFieldFilter(jsonFieldsFilters, fieldTitle, operatorCombobox, entityMultiselect, null);
                        }else{
                            String operatorCombobox= "@"+container+".commonExtView.getOperatorCombobox('"+fieldName+"','select')@";
                            String entityCombobox= "@"+container+".filterCombobox"+fieldEntity+"@";
                            addFieldFilter(jsonFieldsFilters, fieldTitle, operatorCombobox, entityCombobox, null);
                        }
                    }
                }
                    
            }
        }
        
        return jsonFieldsFilters;
    }
    
    public JSONArray getFieldsFilters(List<GenericTableColumn> columns){
        JSONArray jsonFieldsFilters= new JSONArray();
        
        fctc.orderTableColumns(columns);
        HashMap<String, String> titledFieldsMap= fctc.getTitledFieldsMap(columns);
        HashMap<String,String[]> typeFormFields= fctc.getTypeFormFields(columns);
        
        String container= "Instance.entityExtController.entityExtView";
        
        for (GenericTableColumn column : columns) {
            String type = column.getDataType();
            
            if(type.equals("java.util.List")==false && type.equals("java.lang.Class")==false){
                String fieldName= column.getColumnAlias();
                String fieldTitle= titledFieldsMap.get(fieldName);
                
                // ADD TO jsonFormFields
                if(Formats.TYPES_LIST.contains(type)){
                    this.addJSONField(jsonFieldsFilters, container, fieldName, fieldTitle, type, typeFormFields);
                }/*else{
                    if(typeFormFields.containsKey(fieldName) && typeFormFields.get(fieldName)[0].equals(FieldType.MULTI_SELECT.name())){
                        String operatorCombobox= "@"+container+".commonExtView.getOperatorCombobox('"+fieldName+"','multiselect')@";
                        String entityMultiselect= "@"+container+".filterMultiselect"+fieldEntity+"@";
                        addFieldFilter(jsonFieldsFilters, fieldTitle, operatorCombobox, entityMultiselect, null);
                    }else{
                        String operatorCombobox= "@"+container+".commonExtView.getOperatorCombobox('"+fieldName+"','select')@";
                        String entityCombobox= "@"+container+".filterCombobox"+fieldEntity+"@";
                        addFieldFilter(jsonFieldsFilters, fieldTitle, operatorCombobox, entityCombobox, null);
                    }
                }*/
            }
        }
        
        return jsonFieldsFilters;
    }
    
    private void addJSONField(JSONArray jsonFieldsFilters, String container, String fieldName, String fieldTitle, String type,
            HashMap<String,String[]> typeFormFields){
        
        JSONObject formField= new JSONObject();
        String typeField="";
        if(typeFormFields.containsKey(fieldName)){
            typeField= typeFormFields.get(fieldName)[0];
        }
        if(typeField.equals(FieldType.LIST.name()) || typeField.equals(FieldType.MULTI_SELECT.name()) ||
                typeField.equals(FieldType.RADIOS.name())){
            String[] data= typeFormFields.get(fieldName);
            JSONArray dataArray = new JSONArray();
            for(int i=1; i<data.length; i++){
                dataArray.put(data[i]);
            }
            if(typeField.equals(FieldType.LIST.name()) || typeField.equals(FieldType.RADIOS.name())){
                String operatorCombobox= "@"+container+".commonExtView.getOperatorCombobox('"+fieldName+"','select')@";
                String simpleCombobox= "@"+container+".commonExtView.getSimpleCombobox('"+fieldName+"','','filter',"+dataArray.toString().replaceAll("\"", "'")+", true)@";
                addFieldFilter(jsonFieldsFilters, fieldTitle, operatorCombobox, simpleCombobox, null);
            }else{
                String operatorCombobox= "@"+container+".commonExtView.getOperatorCombobox('"+fieldName+"','multiselect')@";
                String simpleMultiselec= "@"+container+".commonExtView.getSimpleMultiselect('"+fieldName+"','',"+dataArray.toString().replaceAll("\"", "'")+", true)@";
                addFieldFilter(jsonFieldsFilters, fieldTitle, operatorCombobox, simpleMultiselec, null);
            }
        }else if (type.equals("java.lang.String") || type.equals("char") || type.equals("java.lang.Character")) {
            formField.put("name", fieldName);
            formField.put("xtype", "textfield");
            formField.put("columnWidth", "0.6");

            String operatorCombobox= "@"+container+".commonExtView.getOperatorCombobox('"+fieldName+"','string')@";
            addFieldFilter(jsonFieldsFilters, fieldTitle, operatorCombobox, formField, null);
        }else if (type.equals("java.util.Date")) {
            String format= extViewConfig.getDateFormat();
            if(typeField.equals(FieldType.DATETIME.name())){
                format= extViewConfig.getDatetimeFormat();
            }

            JSONObject formField0= new JSONObject();
            formField0.put("name", fieldName+"_start");
            formField0.put("xtype", "datefield");
            formField0.put("columnWidth", RANGE_COLUMN_WIDTH);
            formField0.put("format", format);
            formField0.put("tooltip", "Seleccione la fecha");

            JSONObject formField1= new JSONObject();
            formField1.put("name", fieldName+"_end");
            formField1.put("xtype", "datefield");
            formField1.put("columnWidth", RANGE_COLUMN_WIDTH);
            formField1.put("format", format);
            formField1.put("tooltip", "Seleccione la fecha");

            String operatorCombobox= "@"+container+".commonExtView.getOperatorCombobox('"+fieldName+"','range')@";
            addFieldFilter(jsonFieldsFilters, fieldTitle, operatorCombobox, formField0, formField1);
        }else if (type.equals("java.sql.Time")) {
            JSONObject formField0= new JSONObject();
            formField0.put("name", fieldName+"_start");
            formField0.put("xtype", "timefield");
            formField0.put("columnWidth", RANGE_COLUMN_WIDTH);
            formField0.put("tooltip", "Seleccione la hora");

            JSONObject formField1= new JSONObject();
            formField1.put("name", fieldName+"_end");
            formField1.put("xtype", "timefield");
            formField1.put("columnWidth", RANGE_COLUMN_WIDTH);
            formField1.put("tooltip", "Seleccione la hora");

            String operatorCombobox= "@"+container+".commonExtView.getOperatorCombobox('"+fieldName+"','range')@";
            addFieldFilter(jsonFieldsFilters, fieldTitle, operatorCombobox, formField0, formField1);
        }else if(type.equals("short") || type.equals("java.lang.Short") || type.equals("int") || type.equals("java.lang.Integer") || type.equals("long") || type.equals("java.lang.Long") ||
                type.equals("java.math.BigInteger") || type.equals("double") || type.equals("java.lang.Double") || type.equals("float") || type.equals("java.lang.Float")){

            if(fieldName.equals("id")){
                formField.put("name", fieldName);
                formField.put("xtype", "textfield");
                formField.put("columnWidth", "0.6");

                String operatorCombobox= "@"+container+".commonExtView.getOperatorCombobox('"+fieldName+"','number')@";
                addFieldFilter(jsonFieldsFilters, fieldTitle, operatorCombobox, formField, null);
            }else{
                JSONObject formField0= new JSONObject();
                formField0.put("name", fieldName+"_start");
                formField0.put("xtype", "numberfield");
                formField0.put("columnWidth", RANGE_COLUMN_WIDTH);

                JSONObject formField1= new JSONObject();
                formField1.put("name", fieldName+"_end");
                formField1.put("xtype", "numberfield");
                formField1.put("columnWidth", RANGE_COLUMN_WIDTH);

                String operatorCombobox= "@"+container+".commonExtView.getOperatorCombobox('"+fieldName+"','range')@";
                addFieldFilter(jsonFieldsFilters, fieldTitle, operatorCombobox, formField0, formField1);
            }

        }else if(type.equals("boolean") || type.equals("java.lang.Boolean")){
            JSONArray dataArray = new JSONArray();
            dataArray.put(true);
            dataArray.put(false);
            String operatorCombobox= "@"+container+".commonExtView.getOperatorCombobox('"+fieldName+"','boolean')@";
            String simpleCombobox= "@"+container+".commonExtView.getSimpleCombobox('"+fieldName+"','','filter',"+dataArray.toString().replaceAll("\"", "'")+", true)@";
            addFieldFilter(jsonFieldsFilters, fieldTitle, operatorCombobox, simpleCombobox, null);
        }
    }
    
    private void addFieldFilter(JSONArray jsonFieldsFilters, String fieldTitle, String operatorCombobox, Object fieldFilter0, Object fieldFilter1){
        JSONObject itemTitle= new JSONObject();
        itemTitle.put("html", fieldTitle+":&nbsp;");
        itemTitle.put("columnWidth", 0.3);
        itemTitle.put("bodyStyle", "text-align:right; color:#666666;");

        JSONObject itemSeparator= new JSONObject();
        itemSeparator.put("html", "&nbsp;-&nbsp;");
        itemSeparator.put("columnWidth", 0.04);

        JSONArray fieldsRangeArray= new JSONArray();
        fieldsRangeArray.put(itemTitle);
        fieldsRangeArray.put(operatorCombobox);
        fieldsRangeArray.put(fieldFilter0);
        if(fieldFilter1!=null){
            fieldsRangeArray.put(itemSeparator);
            fieldsRangeArray.put(fieldFilter1);
        }

        JSONObject formField= new JSONObject();
        formField.put("xtype", "panel");
        formField.put("layout", "column");
        formField.put("bodyStyle", "padding-bottom: 5px;");
        formField.put("items", fieldsRangeArray);

        jsonFieldsFilters.put(formField);
    }
    
}
