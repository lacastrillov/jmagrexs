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
public class JSONFields {
    
    @Autowired
    public ExtViewConfig extViewConfig;
    
    
    public void addJSONField(JSONArray jsonFormFields, String processName, String parent, String type, String fieldName,
            String fieldTitle, HashMap<String,String[]> typeFormFields, HashMap<String, Integer[]> sizeColumnMap, 
            boolean readOnly, boolean fieldNN, boolean hidden, boolean disabled){
        
        boolean addFormField= true;
        JSONObject formField= new JSONObject();
        formField.put("name", parent + fieldName);
        formField.put("fieldLabel", fieldTitle);
        formField.put("readOnly", readOnly);
        formField.put("hidden", hidden);
        formField.put("disabled", disabled);
        if(typeFormFields.containsKey(fieldName)){
            String typeForm= typeFormFields.get(fieldName)[0];
            if(typeForm.equals(FieldType.EMAIL.name())){
                formField.put("vtype", "email");
            }else if(typeForm.equals(FieldType.PASSWORD.name())){
                formField.put("inputType", "password");
            }else if(typeForm.equals(FieldType.TEXT_AREA.name())){
                formField.put("xtype", "textarea");
                formField.put("height", 200);
            }else if(typeForm.equals(FieldType.DATETIME.name())){
                formField.put("xtype", "datefield");
                formField.put("format", extViewConfig.getDatetimeFormat());
                formField.put("tooltip", "Seleccione la fecha");
            }else if(typeForm.equals(FieldType.HTML_EDITOR.name())){
                formField.put("xtype", "htmleditor");
                formField.put("enableColors", true);
                formField.put("enableAlignments", true);
                formField.put("height", 400);
            }else if(typeForm.equals(FieldType.LIST.name()) || typeForm.equals(FieldType.MULTI_SELECT.name())){
                addFormField= false;
                String[] data= typeFormFields.get(fieldName);
                JSONArray dataArray = new JSONArray();
                for(int i=1; i<data.length; i++){
                    dataArray.put(data[i]);
                }
                jsonFormFields.put("#Instance.commonExtView.getSimpleCombobox('"+parent + fieldName+"','"+fieldTitle+"','form',"+dataArray.toString().replaceAll("\"", "'")+","+(!fieldNN)+")#");
            }else if(typeForm.equals(FieldType.RADIOS.name())){
                addFormField= false;
                String[] data= typeFormFields.get(fieldName);
                JSONArray dataArray = new JSONArray();
                for(int i=1; i<data.length; i++){
                    dataArray.put(data[i]);
                }
                jsonFormFields.put("#Instance.commonExtView.getRadioGroup('"+parent + fieldName+"','"+fieldTitle+"',"+dataArray.toString().replaceAll("\"", "'")+")#");
            }else if(typeForm.equals(FieldType.FILE_SIZE.name())){
                formField.put("id", "form" + processName+ "_" + parent + fieldName + "LinkField");
                formField.put("xtype", "numberfield");
                formField.put("fieldLabel", "&nbsp;");

                //Add file Size Text
                JSONObject renderField= new JSONObject();
                renderField.put("name", fieldName);
                renderField.put("fieldLabel", fieldTitle);
                renderField.put("xtype", "displayfield");
                renderField.put("renderer", "#Instance.commonExtView.fileSizeRender#");
                jsonFormFields.put(renderField);
            }else if(typeForm.equals(FieldType.VIDEO_YOUTUBE.name())){
                formField.put("id", "form" + processName+ "_" + parent + fieldName + "LinkField");
                formField.put("fieldLabel", "&nbsp;");
                formField.put("emptyText", "Url Youtube");

                //Add Video Youtube
                JSONObject renderField= new JSONObject();
                renderField.put("name", parent + fieldName);
                renderField.put("fieldLabel", fieldTitle);
                renderField.put("xtype", "displayfield");
                renderField.put("renderer", "#Instance.commonExtView.videoYoutubeRender#");
                jsonFormFields.put(renderField);
            }else if(typeForm.equals(FieldType.GOOGLE_MAP.name())){
                formField.put("fieldLabel", "&nbsp;");
                formField.put("emptyText", "Google Maps Point");

                //Add GoogleMap
                JSONObject renderField= new JSONObject();
                renderField.put("name", parent + fieldName);
                renderField.put("fieldLabel", fieldTitle);
                renderField.put("xtype", "displayfield");
                renderField.put("renderer", "#Instance.commonExtView.googleMapsRender#");
                jsonFormFields.put(renderField);
            }else if(typeForm.equals(FieldType.FILE_UPLOAD.name())){
                formField.put("name", parent + fieldName + "_File");
                formField.put("xtype", "filefield");
                formField.put("fieldLabel", "&nbsp;");
                formField.put("emptyText", "Seleccione un archivo");

                //Add Url File
                JSONObject renderField= new JSONObject();
                renderField.put("name", parent + fieldName);
                renderField.put("fieldLabel", fieldTitle);
                renderField.put("xtype", "displayfield");
                renderField.put("renderer", "#Instance.commonExtView.fileRender#");
                jsonFormFields.put(renderField);
            }else if(typeForm.equals(FieldType.IMAGE_FILE_UPLOAD.name())){
                formField.put("name", parent + fieldName + "_File");
                formField.put("xtype", "filefield");
                formField.put("fieldLabel", "&nbsp;");
                formField.put("emptyText", "Seleccione una imagen");

                //Add Image
                JSONObject renderField= new JSONObject();
                renderField.put("name", parent + fieldName);
                renderField.put("fieldLabel", fieldTitle);
                renderField.put("xtype", "displayfield");
                renderField.put("renderer", "#Instance.commonExtView.imageRender#");
                jsonFormFields.put(renderField);
            }else if(typeForm.equals(FieldType.VIDEO_FILE_UPLOAD.name())){
                formField.put("name", parent + fieldName + "_File");
                formField.put("xtype", "filefield");
                formField.put("fieldLabel", "&nbsp;");
                formField.put("emptyText", "Seleccione un video");

                //Add Video
                JSONObject renderField= new JSONObject();
                renderField.put("name", parent + fieldName);
                renderField.put("fieldLabel", fieldTitle);
                renderField.put("xtype", "displayfield");
                renderField.put("renderer", "#Instance.commonExtView.videoFileUploadRender#");
                jsonFormFields.put(renderField);
            }else if(typeForm.equals(FieldType.AUDIO_FILE_UPLOAD.name())){
                formField.put("name", parent + fieldName + "_File");
                formField.put("xtype", "filefield");
                formField.put("fieldLabel", "&nbsp;");
                formField.put("emptyText", "Seleccione un audio");

                //Add Audio
                JSONObject renderField= new JSONObject();
                renderField.put("name", parent + fieldName);
                renderField.put("fieldLabel", fieldTitle);
                renderField.put("xtype", "displayfield");
                renderField.put("renderer", "#Instance.commonExtView.audioFileUploadRender#");
                jsonFormFields.put(renderField);
            }else if(typeForm.equals(FieldType.MULTI_FILE_TYPE.name())){
                formField.put("name", parent + fieldName + "_File");
                formField.put("xtype", "filefield");
                formField.put("fieldLabel", "&nbsp;");
                formField.put("emptyText", "Seleccione un archivo");

                //Add MultiFile
                JSONObject renderField= new JSONObject();
                renderField.put("name", parent + fieldName);
                renderField.put("fieldLabel", fieldTitle);
                renderField.put("xtype", "displayfield");
                renderField.put("renderer", "#Instance.commonExtView.multiFileRender#");
                jsonFormFields.put(renderField);
            }
            if(typeForm.equals(FieldType.FILE_UPLOAD.name()) || typeForm.equals(FieldType.IMAGE_FILE_UPLOAD.name()) ||
                    typeForm.equals(FieldType.VIDEO_FILE_UPLOAD.name()) || typeForm.equals(FieldType.AUDIO_FILE_UPLOAD.name()) ||
                    typeForm.equals(FieldType.MULTI_FILE_TYPE.name())){
                //Add link Field
                JSONObject linkField= new JSONObject();
                linkField.put("id", "form" + processName+ "_" +parent + fieldName + "LinkField");
                linkField.put("name", parent + fieldName);
                linkField.put("fieldLabel", "&nbsp;");
                jsonFormFields.put(linkField);
            }
        }else{
            switch (type) {
                case "java.util.Date":
                    formField.put("xtype", "datefield");
                    formField.put("format", extViewConfig.getDateFormat());
                    formField.put("tooltip", "Seleccione la fecha");
                    break;
                case "java.sql.Time":
                    formField.put("xtype", "timefield");
                    formField.put("format", extViewConfig.getTimeFormat());
                    formField.put("tooltip", "Seleccione la hora");
                    break;
                case "short":
                case "int":
                case "java.lang.Integer":
                case "long":
                case "java.lang.Long":
                case "java.math.BigInteger":
                case "double":
                case "java.lang.Double":
                case "float":
                case "java.lang.Float":
                    formField.put("xtype", "numberfield");
                    break;
                case "boolean":
                case "java.lang.Boolean":
                    formField.put("xtype", "checkbox");
                    formField.put("inputValue", "true");
                    formField.put("uncheckedValue", "false");
                    break;
            }
        }

        if(fieldNN){
            formField.put("allowBlank", false);
        }
        if(sizeColumnMap.containsKey(fieldName)){
            formField.put("minLength", sizeColumnMap.get(fieldName)[0]);
            formField.put("maxLength", sizeColumnMap.get(fieldName)[1]);
        }
        if(addFormField){
            jsonFormFields.put(formField);
        }
    }
    
}
