/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.components;

import com.lacv.jmagrexs.enums.FieldType;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author lacastrillov
 */
@Component
public class JSONColumns {
    
    @Autowired
    public ExtViewConfig extViewConfig;
    
    
    public void addJSONColumn(JSONArray jsonGridColumns, String type, String fieldName, String fieldTitle, Integer widthColumn,
            HashMap<String,String[]> typeFormFields, String labelField, HashMap<String, Integer[]> sizeColumnMap,
            boolean isEditableGrid, boolean readOnly, boolean fieldNN){
        
        JSONObject gridColumn= new JSONObject();
        gridColumn.put("dataIndex", fieldName);
        gridColumn.put("header", fieldTitle);
        gridColumn.put("width", widthColumn);
        gridColumn.put("sortable", true);
        JSONObject field= null;
        JSONObject editor= null;
        if(typeFormFields.containsKey(fieldName)){
            String typeField= typeFormFields.get(fieldName)[0];
            if(typeField.equals(FieldType.EMAIL.name())){
                editor= new JSONObject();
                editor.put("vtype", "email");
            }else if(typeField.equals(FieldType.PASSWORD.name())){
                gridColumn.put("renderer", "@Instance.commonExtView.passwordGridRender@");
                editor= new JSONObject();
                editor.put("inputType", "password");
            }else if(typeField.equals(FieldType.DURATION.name())){
                gridColumn.put("renderer", "@Instance.commonExtView.durationGridRender@");
                field= new JSONObject();
                field.put("type", "textfield");
            }else if(typeField.equals(FieldType.PRICE.name())){
                gridColumn.put("renderer", "@Instance.commonExtView.priceGridRender@");
                field= new JSONObject();
                field.put("type", "textfield");
            }else if(typeField.equals(FieldType.DATETIME.name())){
                gridColumn.put("xtype", "datecolumn");
                gridColumn.put("format", extViewConfig.getDatetimeFormat());
                editor = new JSONObject();
                editor.put("xtype", "datefield");
                editor.put("format", extViewConfig.getDatetimeFormat());
            }else if(typeField.equals(FieldType.LIST.name()) || typeField.equals(FieldType.MULTI_SELECT.name()) ||
                    typeField.equals(FieldType.RADIOS.name())){
                String[] data= typeFormFields.get(fieldName);
                JSONArray dataArray = new JSONArray();
                for(int i=1; i<data.length; i++){
                    dataArray.put(data[i]);
                }
                if(isEditableGrid && !readOnly){
                    gridColumn.put("renderer", "@Instance.commonExtView.getSimpleComboboxRender('grid','"+fieldName+"')@");
                    gridColumn.put("editor", "@Instance.commonExtView.getSimpleCombobox('"+fieldName+"','"+fieldTitle+"','grid',"+dataArray.toString().replaceAll("\"", "'")+","+(!fieldNN)+")@");
                }
            }else if(typeField.equals(FieldType.FILE_SIZE.name())){
                gridColumn.put("renderer", "@Instance.commonExtView.fileSizeGridRender@");
                field= new JSONObject();
                field.put("type", "textfield");
            }else if(typeField.equals(FieldType.PERCENTAJE.name())){
                gridColumn.put("renderer", "@Instance.commonExtView.percentageGridRender@");
                field= new JSONObject();
                field.put("type", "textfield");
            }else if(typeField.equals(FieldType.COLOR.name())){
                gridColumn.put("renderer", "@Instance.commonExtView.colorGridRender@");
                field= new JSONObject();
                field.put("type", "textfield");
            }else if(typeField.equals(FieldType.CONDITIONAL_COLOR.name())){
                String[] data= typeFormFields.get(fieldName);
                JSONArray dataArray = new JSONArray();
                for(int i=1; i<data.length; i++){
                    dataArray.put(new JSONObject(data[i]));
                }
                gridColumn.put("conditionalColor", dataArray);
                gridColumn.put("renderer", "@Instance.commonExtView.conditionalColorGridRender@");
                field= new JSONObject();
                field.put("type", "textfield");
            }else if(typeField.equals(FieldType.ON_OFF.name())){
                gridColumn.put("xtype", "checkcolumn");
                gridColumn.put("disabled", readOnly);
                gridColumn.put("renderer", "@Instance.commonExtView.onOffGridRender@");
            }else if(typeField.equals(FieldType.URL.name()) || typeField.equals(FieldType.FILE_UPLOAD.name()) ||
                    typeField.equals(FieldType.VIDEO_YOUTUBE.name()) || typeField.equals(FieldType.VIDEO_FILE_UPLOAD.name()) || 
                    typeField.equals(FieldType.MULTI_FILE_TYPE.name())){

                gridColumn.put("renderer", "@Instance.commonExtView.urlGridRender@");
                field= new JSONObject();
                field.put("type", "textfield");
            }else if(typeField.equals(FieldType.IMAGE_FILE_UPLOAD.name())){
                gridColumn.put("renderer", "@Instance.commonExtView.imageGridRender@");
                field= new JSONObject();
                field.put("type", "textfield");
            }else if(typeField.equals(FieldType.AUDIO_FILE_UPLOAD.name())){
                gridColumn.put("renderer", "@Instance.commonExtView.audioGridRender@");
                field= new JSONObject();
                field.put("type", "textfield");
            }else if(typeField.equals(FieldType.HTML_EDITOR.name())){
            }else{
                field= new JSONObject();
                field.put("type", "textfield");
            }
        }else{
            if(fieldName.equals(labelField)){
                gridColumn.put("renderer", "@"+labelField+"EntityRender@");
            }
            switch (type) {
                case "java.util.Date": {
                    gridColumn.put("xtype", "datecolumn");
                    gridColumn.put("format", extViewConfig.getDatetimeFormat());
                    gridColumn.put("renderer", "@Ext.util.Format.dateRenderer('"+extViewConfig.getDateFormat()+"')@");
                    editor = new JSONObject();
                    editor.put("xtype", "datefield");
                    editor.put("format", extViewConfig.getDateFormat());
                    break;
                }
                case "java.sql.Time": {
                    editor = new JSONObject();
                    editor.put("xtype", "timefield");
                    editor.put("format", extViewConfig.getTimeFormat());
                    break;
                }
                case "short":
                case "java.lang.Short":
                case "int":
                case "java.lang.Integer":
                case "long":
                case "java.lang.Long":
                case "java.math.BigInteger":
                case "double":
                case "java.lang.Double":
                case "float":
                case "java.lang.Float": {
                    editor = new JSONObject();
                    editor.put("xtype", "numberfield");
                    break;
                }
                case "boolean":
                case "java.lang.Boolean": {
                    editor = new JSONObject();
                    editor.put("xtype", "checkbox");
                    editor.put("cls", "x-grid-checkheader-editor");
                    break;
                }
                default:
                    field = new JSONObject();
                    field.put("type", "textfield");
                    break;
            }
        }
        if(field!=null){
            if(fieldNN){
                field.put("allowBlank", false);
            }
            if(sizeColumnMap.containsKey(fieldName)){
                field.put("minLength", sizeColumnMap.get(fieldName)[0]);
                field.put("maxLength", sizeColumnMap.get(fieldName)[1]);
            }
            if(isEditableGrid && !readOnly){
                gridColumn.put("field", field);
            }
        }else if(editor!=null){
            if(fieldNN){
                editor.put("allowBlank", false);
            }
            if(sizeColumnMap.containsKey(fieldName)){
                editor.put("minLength", sizeColumnMap.get(fieldName)[0]);
                editor.put("maxLength", sizeColumnMap.get(fieldName)[1]);
            }
            if(isEditableGrid && !readOnly){
                gridColumn.put("editor", editor);
            }
        }
        jsonGridColumns.put(gridColumn);
    }
    
    public void addEntityCombobox(JSONArray jsonGridColumns, String fieldName, String fieldTitle, String fieldEntity, Integer widthColumn,
            boolean isEditableGrid, boolean readOnly, boolean fieldNN){
        
        JSONObject gridColumn= new JSONObject();
        gridColumn.put("dataIndex", fieldName);
        gridColumn.put("header", fieldTitle);
        gridColumn.put("width", widthColumn);
        gridColumn.put("sortable", true);
        gridColumn.put("renderer", "@Instance.combobox"+fieldEntity+"Render@");
        String combobox="(function(){ ";
        if(!isEditableGrid || readOnly){
            combobox+="Instance.gridCombobox"+fieldEntity+".setDisabled(true); ";
        }
        if(fieldNN){
            combobox+="Instance.gridCombobox"+fieldEntity+".allowBlank=false; ";
        }
        combobox+="return Instance.gridCombobox"+fieldEntity+";" +
                        "})()";
        gridColumn.put("editor", "@"+combobox+"@");
        jsonGridColumns.put(gridColumn);
    }
    
}
