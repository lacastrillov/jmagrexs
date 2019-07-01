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
    
    private final double RANGE_COLUMN_WIDTH= 0.33;
    
    
    public JSONArray getFieldsFilters(Class dtoClass, String labelField, PageType pageType){
        JSONArray jsonFieldsFilters= new JSONArray();
        
        PropertyDescriptor[] propertyDescriptors = EntityReflection.getPropertyDescriptors(dtoClass);
        
        fcba.orderPropertyDescriptor(propertyDescriptors, dtoClass, labelField);
        HashMap<String, String> titledFieldsMap= fcba.getTitledFieldsMap(propertyDescriptors, dtoClass);
        HashSet<String> hideFields= fcba.getHideFields(dtoClass);
        HashMap<String,String[]> typeFormFields= fcba.getTypeFormFields(dtoClass);
        
        String container= "Instance.entityExtController.entityExtView";
        if(pageType.equals(PageType.PROCESS)){
            container= "Instance";
        }
        
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String type = propertyDescriptor.getPropertyType().getName();
            String functionOnChange="";
            
            if(type.equals("java.util.List")==false && type.equals("java.lang.Class")==false){
                String fieldName= propertyDescriptor.getName();
                String fieldEntity= StringUtils.capitalize(fieldName);
                String fieldTitle= titledFieldsMap.get(fieldName);
                
                // ADD TO jsonFormFields
                if(!hideFields.contains(fieldName + HideView.FILTER.name())){
                    if(Formats.TYPES_LIST.contains(type)){
                        boolean addFormField= true;
                        JSONObject formField= new JSONObject();
                        String typeForm="";
                        if(typeFormFields.containsKey(fieldName)){
                            typeForm= typeFormFields.get(fieldName)[0];
                        }
                        if(typeForm.equals(FieldType.LIST.name()) || typeForm.equals(FieldType.MULTI_SELECT.name()) ||
                                typeForm.equals(FieldType.RADIOS.name())){
                            addFormField= false;
                            String[] data= typeFormFields.get(fieldName);
                            JSONArray dataArray = new JSONArray();
                            for(int i=1; i<data.length; i++){
                                dataArray.put(data[i]);
                            }
                            if(typeForm.equals(FieldType.LIST.name()) || typeForm.equals(FieldType.RADIOS.name())){
                                jsonFieldsFilters.put("#"+container+".commonExtView.getSimpleCombobox('"+fieldName+"','"+fieldTitle+"','filter',"+dataArray.toString().replaceAll("\"", "'")+", true)#");
                            }else{
                                jsonFieldsFilters.put("#"+container+".commonExtView.getSimpleMultiselect('"+fieldName+"','"+fieldTitle+"',"+dataArray.toString().replaceAll("\"", "'")+", true)#");
                            }
                        }else if (type.equals("java.lang.String") || type.equals("char") || type.equals("java.lang.Character")) {
                            formField.put("name", fieldName);
                            formField.put("xtype", "textfield");
                            formField.put("fieldLabel", fieldTitle);
                            functionOnChange= rf.getListenerFuntionSingleValue("lk", fieldName, pageType);

                        }else if (type.equals("java.util.Date")) {
                            addFormField= false;
                            String format= extViewConfig.getDateFormat();
                            if(typeForm.equals(FieldType.DATETIME.name())){
                                format= extViewConfig.getDatetimeFormat();
                            }
                            
                            JSONObject formField0= new JSONObject();
                            formField0.put("name", fieldName+"_start");
                            formField0.put("xtype", "datefield");
                            formField0.put("columnWidth", RANGE_COLUMN_WIDTH);
                            formField0.put("format", format);
                            formField0.put("tooltip", "Seleccione la fecha");

                            JSONObject listeners0= new JSONObject();
                            String functionOnChange0= rf.getListenerFuntionRangeValue(0, fieldName, "date", format, pageType);
                            listeners0.put("change", "#"+functionOnChange0+"#");
                            formField0.put("listeners", listeners0);

                            JSONObject formField1= new JSONObject();
                            formField1.put("name", fieldName+"_end");
                            formField1.put("xtype", "datefield");
                            formField1.put("columnWidth", RANGE_COLUMN_WIDTH);
                            formField1.put("format", format);
                            formField1.put("tooltip", "Seleccione la fecha");
                            
                            JSONObject listeners1= new JSONObject();
                            String functionOnChange1= rf.getListenerFuntionRangeValue(1, fieldName, "date", format, pageType);
                            listeners1.put("change", "#"+functionOnChange1+"#");
                            formField1.put("listeners", listeners1);
                            
                            JSONObject itemTitle= new JSONObject();
                            itemTitle.put("html", fieldTitle+":&nbsp;");
                            itemTitle.put("columnWidth", 0.30);
                            itemTitle.put("bodyStyle", "text-align:right; color:#666666;");
                            
                            JSONObject itemSeparator= new JSONObject();
                            itemSeparator.put("html", "&nbsp;-&nbsp;");
                            itemSeparator.put("columnWidth", 0.04);
                            
                            JSONArray fieldsRangeArray= new JSONArray();
                            fieldsRangeArray.put(itemTitle);
                            fieldsRangeArray.put(formField0);
                            fieldsRangeArray.put(itemSeparator);
                            fieldsRangeArray.put(formField1);
                            
                            formField.put("xtype", "panel");
                            formField.put("layout", "column");
                            formField.put("bodyStyle", "padding-bottom: 5px;");
                            formField.put("items", fieldsRangeArray);
                            
                            jsonFieldsFilters.put(formField);
                        }else if (type.equals("java.sql.Time")) {
                            addFormField= false;
                            String format= extViewConfig.getTimeFormat();
                            
                            JSONObject formField0= new JSONObject();
                            formField0.put("name", fieldName+"_start");
                            formField0.put("xtype", "timefield");
                            formField0.put("columnWidth", RANGE_COLUMN_WIDTH);
                            formField0.put("tooltip", "Seleccione la hora");

                            JSONObject listeners0= new JSONObject();
                            String functionOnChange0= rf.getListenerFuntionRangeValue(0, fieldName, "time", format, pageType);
                            listeners0.put("change", "#"+functionOnChange0+"#");
                            formField0.put("listeners", listeners0);

                            JSONObject formField1= new JSONObject();
                            formField1.put("name", fieldName+"_end");
                            formField1.put("xtype", "timefield");
                            formField1.put("columnWidth", RANGE_COLUMN_WIDTH);
                            formField1.put("tooltip", "Seleccione la hora");
                            
                            JSONObject listeners1= new JSONObject();
                            String functionOnChange1= rf.getListenerFuntionRangeValue(1, fieldName, "time", format, pageType);
                            listeners1.put("change", "#"+functionOnChange1+"#");
                            formField1.put("listeners", listeners1);
                            
                            JSONObject itemTitle= new JSONObject();
                            itemTitle.put("html", fieldTitle+":&nbsp;");
                            itemTitle.put("columnWidth", 0.30);
                            itemTitle.put("style", "text-align: right");
                            
                            JSONObject itemSeparator= new JSONObject();
                            itemSeparator.put("html", "&nbsp;-&nbsp;");
                            itemSeparator.put("columnWidth", 0.04);
                            
                            JSONArray fieldsRangeArray= new JSONArray();
                            fieldsRangeArray.put(itemTitle);
                            fieldsRangeArray.put(formField0);
                            fieldsRangeArray.put(itemSeparator);
                            fieldsRangeArray.put(formField1);
                            
                            formField.put("xtype", "panel");
                            formField.put("layout", "column");
                            formField.put("bodyStyle", "padding-bottom: 5px;");
                            formField.put("items", fieldsRangeArray);
                            
                            jsonFieldsFilters.put(formField);
                        }else if(type.equals("short") || type.equals("java.lang.Short") || type.equals("int") || type.equals("java.lang.Integer") || type.equals("long") || type.equals("java.lang.Long") ||
                                type.equals("java.math.BigInteger") || type.equals("double") || type.equals("java.lang.Double") || type.equals("float") || type.equals("java.lang.Float")){

                            if(fieldName.equals("id")){
                                formField.put("name", fieldName);
                                formField.put("xtype", "numberfield");
                                formField.put("fieldLabel", fieldTitle);
                                functionOnChange= rf.getListenerFuntionSingleValue("eq", fieldName, pageType);
                            }else{
                                addFormField= false;
                                
                                JSONObject formField0= new JSONObject();
                                formField0.put("name", fieldName+"_start");
                                formField0.put("xtype", "numberfield");
                                formField0.put("columnWidth", RANGE_COLUMN_WIDTH);

                                JSONObject listeners0= new JSONObject();
                                String functionOnChange0= rf.getListenerFuntionRangeValue(0, fieldName, "", "", pageType);
                                listeners0.put("change", "#"+functionOnChange0+"#");
                                formField0.put("listeners", listeners0);

                                JSONObject formField1= new JSONObject();
                                formField1.put("name", fieldName+"_end");
                                formField1.put("xtype", "numberfield");
                                formField1.put("columnWidth", RANGE_COLUMN_WIDTH);
                                
                                JSONObject listeners1= new JSONObject();
                                String functionOnChange1= rf.getListenerFuntionRangeValue(1, fieldName, "", "", pageType);
                                listeners1.put("change", "#"+functionOnChange1+"#");
                                formField1.put("listeners", listeners1);
                                
                                JSONObject itemTitle= new JSONObject();
                                itemTitle.put("html", fieldTitle+":&nbsp;");
                                itemTitle.put("columnWidth", 0.30);
                                itemTitle.put("bodyStyle", "text-align:right; color:#666666;");

                                JSONObject itemSeparator= new JSONObject();
                                itemSeparator.put("html", "&nbsp;-&nbsp;");
                                itemSeparator.put("columnWidth", 0.04);
                                
                                JSONArray fieldsRangeArray= new JSONArray();
                                fieldsRangeArray.put(itemTitle);
                                fieldsRangeArray.put(formField0);
                                fieldsRangeArray.put(itemSeparator);
                                fieldsRangeArray.put(formField1);

                                formField.put("xtype", "panel");
                                formField.put("layout", "column");
                                formField.put("bodyStyle", "padding-bottom: 5px;");
                                formField.put("items", fieldsRangeArray);
                                
                                jsonFieldsFilters.put(formField);
                            }

                        }else if(type.equals("boolean") || type.equals("java.lang.Boolean")){
                            formField.put("name", fieldName);
                            formField.put("xtype", "checkbox");
                            formField.put("fieldLabel", fieldTitle);
                            functionOnChange= rf.getListenerFuntionSingleValue("eq", fieldName, pageType);
                        }

                        if(addFormField){
                            JSONObject listeners= new JSONObject();
                            listeners.put("change", "#"+functionOnChange+"#");
                            formField.put("listeners", listeners);
                            jsonFieldsFilters.put(formField);
                        }
                    }else{
                        if(typeFormFields.containsKey(fieldName) && typeFormFields.get(fieldName)[0].equals(FieldType.MULTI_SELECT.name())){
                            jsonFieldsFilters.put("#"+container+".filterMultiselect"+fieldEntity+"#");
                        }else{
                            jsonFieldsFilters.put("#"+container+".filterCombobox"+fieldEntity+"#");
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
            String functionOnChange="";
            
            if(type.equals("java.util.List")==false && type.equals("java.lang.Class")==false){
                String fieldName= column.getColumnAlias();
                String fieldEntity= StringUtils.capitalize(fieldName);
                String fieldTitle= titledFieldsMap.get(fieldName);
                
                // ADD TO jsonFormFields
                
                if(Formats.TYPES_LIST.contains(type)){
                    boolean addFormField= true;
                    JSONObject formField= new JSONObject();
                    String typeForm="";
                    if(typeFormFields.containsKey(fieldName)){
                        typeForm= typeFormFields.get(fieldName)[0];
                    }
                    if(typeForm.equals(FieldType.LIST.name()) || typeForm.equals(FieldType.MULTI_SELECT.name()) ||
                            typeForm.equals(FieldType.RADIOS.name())){
                        addFormField= false;
                        String[] data= typeFormFields.get(fieldName);
                        JSONArray dataArray = new JSONArray();
                        for(int i=1; i<data.length; i++){
                            dataArray.put(data[i]);
                        }
                        if(typeForm.equals(FieldType.LIST.name()) || typeForm.equals(FieldType.RADIOS.name())){
                            jsonFieldsFilters.put("#"+container+".commonExtView.getSimpleCombobox('"+fieldName+"','"+fieldTitle+"','filter',"+dataArray.toString().replaceAll("\"", "'")+", true)#");
                        }else{
                            jsonFieldsFilters.put("#"+container+".commonExtView.getSimpleMultiselect('"+fieldName+"','"+fieldTitle+"',"+dataArray.toString().replaceAll("\"", "'")+", true)#");
                        }
                    }else if (type.equals("java.lang.String") || type.equals("char") || type.equals("java.lang.Character")) {
                        formField.put("name", fieldName);
                        formField.put("xtype", "textfield");
                        formField.put("fieldLabel", fieldTitle);
                        functionOnChange= rf.getListenerFuntionSingleValue("lk", fieldName, PageType.ENTITY);

                    }else if (type.equals("java.util.Date")) {
                        addFormField= false;
                        String format= extViewConfig.getDateFormat();
                        if(typeForm.equals(FieldType.DATETIME.name())){
                            format= extViewConfig.getDatetimeFormat();
                        }

                        JSONObject formField0= new JSONObject();
                        formField0.put("name", fieldName+"_start");
                        formField0.put("xtype", "datefield");
                        formField0.put("columnWidth", RANGE_COLUMN_WIDTH);
                        formField0.put("format", format);
                        formField0.put("tooltip", "Seleccione la fecha");

                        JSONObject listeners0= new JSONObject();
                        String functionOnChange0= rf.getListenerFuntionRangeValue(0, fieldName, "date", format, PageType.ENTITY);
                        listeners0.put("change", "#"+functionOnChange0+"#");
                        formField0.put("listeners", listeners0);

                        JSONObject formField1= new JSONObject();
                        formField1.put("name", fieldName+"_end");
                        formField1.put("xtype", "datefield");
                        formField1.put("columnWidth", RANGE_COLUMN_WIDTH);
                        formField1.put("format", format);
                        formField1.put("tooltip", "Seleccione la fecha");

                        JSONObject listeners1= new JSONObject();
                        String functionOnChange1= rf.getListenerFuntionRangeValue(1, fieldName, "date", format, PageType.ENTITY);
                        listeners1.put("change", "#"+functionOnChange1+"#");
                        formField1.put("listeners", listeners1);

                        JSONObject itemTitle= new JSONObject();
                        itemTitle.put("html", fieldTitle+":&nbsp;");
                        itemTitle.put("columnWidth", 0.30);
                        itemTitle.put("bodyStyle", "text-align:right; color:#666666;");

                        JSONObject itemSeparator= new JSONObject();
                        itemSeparator.put("html", "&nbsp;-&nbsp;");
                        itemSeparator.put("columnWidth", 0.04);

                        JSONArray fieldsRangeArray= new JSONArray();
                        fieldsRangeArray.put(itemTitle);
                        fieldsRangeArray.put(formField0);
                        fieldsRangeArray.put(itemSeparator);
                        fieldsRangeArray.put(formField1);

                        formField.put("xtype", "panel");
                        formField.put("layout", "column");
                        formField.put("bodyStyle", "padding-bottom: 5px;");
                        formField.put("items", fieldsRangeArray);

                        jsonFieldsFilters.put(formField);
                    }else if (type.equals("java.sql.Time")) {
                        addFormField= false;
                        String format= extViewConfig.getTimeFormat();

                        JSONObject formField0= new JSONObject();
                        formField0.put("name", fieldName+"_start");
                        formField0.put("xtype", "timefield");
                        formField0.put("columnWidth", RANGE_COLUMN_WIDTH);
                        formField0.put("tooltip", "Seleccione la hora");

                        JSONObject listeners0= new JSONObject();
                        String functionOnChange0= rf.getListenerFuntionRangeValue(0, fieldName, "time", format, PageType.ENTITY);
                        listeners0.put("change", "#"+functionOnChange0+"#");
                        formField0.put("listeners", listeners0);

                        JSONObject formField1= new JSONObject();
                        formField1.put("name", fieldName+"_end");
                        formField1.put("xtype", "timefield");
                        formField1.put("columnWidth", RANGE_COLUMN_WIDTH);
                        formField1.put("tooltip", "Seleccione la hora");

                        JSONObject listeners1= new JSONObject();
                        String functionOnChange1= rf.getListenerFuntionRangeValue(1, fieldName, "time", format, PageType.ENTITY);
                        listeners1.put("change", "#"+functionOnChange1+"#");
                        formField1.put("listeners", listeners1);

                        JSONObject itemTitle= new JSONObject();
                        itemTitle.put("html", fieldTitle+":&nbsp;");
                        itemTitle.put("columnWidth", 0.30);
                        itemTitle.put("bodyStyle", "text-align:right; color:#666666;");

                        JSONObject itemSeparator= new JSONObject();
                        itemSeparator.put("html", "&nbsp;-&nbsp;");
                        itemSeparator.put("columnWidth", 0.04);

                        JSONArray fieldsRangeArray= new JSONArray();
                        fieldsRangeArray.put(itemTitle);
                        fieldsRangeArray.put(formField0);
                        fieldsRangeArray.put(itemSeparator);
                        fieldsRangeArray.put(formField1);

                        formField.put("xtype", "panel");
                        formField.put("layout", "column");
                        formField.put("bodyStyle", "padding-bottom: 5px;");
                        formField.put("items", fieldsRangeArray);

                        jsonFieldsFilters.put(formField);
                    }else if(type.equals("short") || type.equals("java.lang.Short") || type.equals("int") || type.equals("java.lang.Integer") || type.equals("long")  || type.equals("java.lang.Long") ||
                            type.equals("java.math.BigInteger") || type.equals("double") || type.equals("java.lang.Double") || type.equals("float") || type.equals("java.lang.Float")){

                        if(fieldName.equals("id")){
                            formField.put("name", fieldName);
                            formField.put("xtype", "numberfield");
                            formField.put("fieldLabel", fieldTitle);
                            functionOnChange= rf.getListenerFuntionSingleValue("eq", fieldName, PageType.ENTITY);
                        }else{
                            addFormField= false;

                            JSONObject formField0= new JSONObject();
                            formField0.put("name", fieldName+"_start");
                            formField0.put("xtype", "numberfield");
                            formField0.put("columnWidth", RANGE_COLUMN_WIDTH);

                            JSONObject listeners0= new JSONObject();
                            String functionOnChange0= rf.getListenerFuntionRangeValue(0, fieldName, "", "", PageType.ENTITY);
                            listeners0.put("change", "#"+functionOnChange0+"#");
                            formField0.put("listeners", listeners0);

                            JSONObject formField1= new JSONObject();
                            formField1.put("name", fieldName+"_end");
                            formField1.put("xtype", "numberfield");
                            formField1.put("columnWidth", RANGE_COLUMN_WIDTH);

                            JSONObject listeners1= new JSONObject();
                            String functionOnChange1= rf.getListenerFuntionRangeValue(1, fieldName, "", "", PageType.ENTITY);
                            listeners1.put("change", "#"+functionOnChange1+"#");
                            formField1.put("listeners", listeners1);

                            JSONObject itemTitle= new JSONObject();
                            itemTitle.put("html", fieldTitle+":&nbsp;");
                            itemTitle.put("columnWidth", 0.30);
                            itemTitle.put("bodyStyle", "text-align:right; color:#666666;");

                            JSONObject itemSeparator= new JSONObject();
                            itemSeparator.put("html", "&nbsp;-&nbsp;");
                            itemSeparator.put("columnWidth", 0.04);

                            JSONArray fieldsRangeArray= new JSONArray();
                            fieldsRangeArray.put(itemTitle);
                            fieldsRangeArray.put(formField0);
                            fieldsRangeArray.put(itemSeparator);
                            fieldsRangeArray.put(formField1);

                            formField.put("xtype", "panel");
                            formField.put("layout", "column");
                            formField.put("bodyStyle", "padding-bottom: 5px;");
                            formField.put("items", fieldsRangeArray);

                            jsonFieldsFilters.put(formField);
                        }

                    }else if(type.equals("boolean") || type.equals("java.lang.Boolean")){
                        formField.put("name", fieldName);
                        formField.put("xtype", "checkbox");
                        formField.put("fieldLabel", fieldTitle);
                        functionOnChange= rf.getListenerFuntionSingleValue("eq", fieldName, PageType.ENTITY);
                    }

                    if(addFormField){
                        JSONObject listeners= new JSONObject();
                        listeners.put("change", "#"+functionOnChange+"#");
                        formField.put("listeners", listeners);
                        jsonFieldsFilters.put(formField);
                    }
                }else{
                    jsonFieldsFilters.put("#"+container+".filterCombobox"+fieldEntity+"#");
                }
                    
            }
        }
        
        return jsonFieldsFilters;
    }
    
}
