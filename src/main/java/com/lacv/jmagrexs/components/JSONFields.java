/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.components;

import com.lacv.jmagrexs.enums.FieldType;
import java.util.ArrayList;
import java.util.HashMap;
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
public class JSONFields {
    
    @Autowired
    public ExtViewConfig extViewConfig;
    
    
    public void addJSONField(JSONArray jsonFormFields, String processName, String parent, String type, String fieldName,
            String fieldTitle, HashMap<String,String[]> typeFormFields, HashMap<String, Integer[]> sizeColumnMap, 
            boolean readOnly, boolean fieldNN, boolean hidden, boolean disabled){
        
        boolean addField= true;
        JSONObject formField= new JSONObject();
        formField.put("id", processName + "_" + parent + fieldName);
        formField.put("name", parent + fieldName);
        formField.put("fieldLabel", fieldTitle);
        formField.put("readOnly", readOnly);
        formField.put("hidden", hidden);
        formField.put("disabled", disabled);
        formField.put("allowBlank", !fieldNN);
        
        JSONObject rendererField= new JSONObject();
        rendererField.put("id", processName + "_" + parent + fieldName + "Renderer");
        rendererField.put("name", parent + fieldName);
        rendererField.put("fieldLabel", fieldTitle);
        rendererField.put("xtype", "displayfield");
        
        String checkFieldName= fieldName.split("\\[")[0];
        if(typeFormFields.containsKey(checkFieldName)){
            String typeField= typeFormFields.get(checkFieldName)[0];
            if(typeField.equals(FieldType.EMAIL.name())){
                formField.put("vtype", "email");
            }else if(typeField.equals(FieldType.PASSWORD.name())){
                formField.put("inputType", "password");
            }else if(typeField.equals(FieldType.TEXT_AREA.name())){
                formField.put("xtype", "textarea");
                formField.put("height", 200);
            }else if(typeField.equals(FieldType.DATETIME.name())){
                formField.put("xtype", "datefield");
                formField.put("format", extViewConfig.getDatetimeFormat());
                formField.put("tooltip", "Seleccione la fecha");
            }else if(typeField.equals(FieldType.HTML_EDITOR.name())){
                formField.put("xtype", "htmleditor");
                formField.put("enableColors", true);
                formField.put("enableAlignments", true);
                formField.put("height", 400);
            }else if(typeField.equals(FieldType.LIST.name()) || typeField.equals(FieldType.MULTI_SELECT.name())){
                addField= false;
                String[] data= typeFormFields.get(checkFieldName);
                JSONArray dataArray = new JSONArray();
                for(int i=1; i<data.length; i++){
                    dataArray.put(data[i]);
                }
                jsonFormFields.put("@Instance.commonExtView.getSimpleCombobox('"+parent + fieldName+"','"+fieldTitle+"','"+processName+"',"+dataArray.toString().replaceAll("\"", "'")+","+(!fieldNN)+")@");
            }else if(typeField.equals(FieldType.RADIOS.name())){
                addField= false;
                String[] data= typeFormFields.get(checkFieldName);
                JSONArray dataArray = new JSONArray();
                for(int i=1; i<data.length; i++){
                    dataArray.put(data[i]);
                }
                jsonFormFields.put("@Instance.commonExtView.getRadioGroup('"+parent + fieldName+"','"+fieldTitle+"',"+dataArray.toString().replaceAll("\"", "'")+")@");
            }else if(typeField.equals(FieldType.FILE_SIZE.name())){
                formField.put("xtype", "numberfield");
                formField.put("fieldLabel", fieldTitle+" (bytes)");

                //Add file Size Text
                rendererField.put("renderer", "@Instance.commonExtView.fileSizeRender@");
                jsonFormFields.put(rendererField);
            }else if(typeField.equals(FieldType.PERCENTAJE.name())){
                formField.put("xtype", "numberfield");
                formField.put("fieldLabel", fieldTitle+" (%)");
                formField.put("minValue", 0);
                formField.put("maxValue", 100);
            }else if(typeField.equals(FieldType.COLOR.name())){
                formField.put("xtype", "customcolorpicker");
            }else if(typeField.equals(FieldType.ON_OFF.name())){
                formField.put("xtype", "checkbox");
                formField.put("inputValue", "true");
                formField.put("uncheckedValue", "false");
                formField.put("cls", "hidden");
                
                //Add Button ON/OFF
                rendererField.put("renderer", "@Instance.commonExtView.onOffRender@");
                jsonFormFields.put(rendererField);
            }else if(typeField.equals(FieldType.VIDEO_YOUTUBE.name())){
                formField.put("fieldLabel", "Link "+fieldTitle);
                formField.put("emptyText", "Url Youtube");

                //Add Video Youtube
                rendererField.put("renderer", "@Instance.commonExtView.videoYoutubeRender@");
                jsonFormFields.put(rendererField);
            }else if(typeField.equals(FieldType.GOOGLE_MAP.name())){
                formField.put("fieldLabel", "Coordenadas "+fieldTitle);
                formField.put("emptyText", "Google Maps Point");

                //Add GoogleMap
                rendererField.put("renderer", "@Instance.commonExtView.googleMapsRender@");
                jsonFormFields.put(rendererField);
            }else if(typeField.equals(FieldType.FILE_UPLOAD.name())){
                formField.put("name", parent + fieldName + "_File");
                formField.put("xtype", "filefield");
                formField.put("fieldLabel", "Subir "+fieldTitle);
                formField.put("emptyText", "Seleccione un archivo");

                //Add Url File
                rendererField.put("renderer", "@Instance.commonExtView.fileRender@");
                jsonFormFields.put(rendererField);
            }else if(typeField.equals(FieldType.IMAGE_FILE_UPLOAD.name())){
                formField.put("name", parent + fieldName + "_File");
                formField.put("xtype", "filefield");
                formField.put("fieldLabel", "Subir "+fieldTitle);
                formField.put("emptyText", "Seleccione una imagen");

                //Add Image
                rendererField.put("renderer", "@Instance.commonExtView.imageRender@");
                jsonFormFields.put(rendererField);
            }else if(typeField.equals(FieldType.VIDEO_FILE_UPLOAD.name())){
                formField.put("name", parent + fieldName + "_File");
                formField.put("xtype", "filefield");
                formField.put("fieldLabel", "Subir "+fieldTitle);
                formField.put("emptyText", "Seleccione un video");

                //Add Video
                rendererField.put("renderer", "@Instance.commonExtView.videoFileUploadRender@");
                jsonFormFields.put(rendererField);
            }else if(typeField.equals(FieldType.AUDIO_FILE_UPLOAD.name())){
                formField.put("name", parent + fieldName + "_File");
                formField.put("xtype", "filefield");
                formField.put("fieldLabel", "Subir "+fieldTitle);
                formField.put("emptyText", "Seleccione un audio");

                //Add Audio
                rendererField.put("renderer", "@Instance.commonExtView.audioFileUploadRender@");
                jsonFormFields.put(rendererField);
            }else if(typeField.equals(FieldType.MULTI_FILE_TYPE.name())){
                formField.put("name", parent + fieldName + "_File");
                formField.put("xtype", "filefield");
                formField.put("fieldLabel", "Subir "+fieldTitle);
                formField.put("emptyText", "Seleccione un archivo");

                //Add MultiFile
                rendererField.put("renderer", "@Instance.commonExtView.multiFileRender@");
                jsonFormFields.put(rendererField);
            }
            if(typeField.equals(FieldType.FILE_UPLOAD.name()) || typeField.equals(FieldType.IMAGE_FILE_UPLOAD.name()) ||
                    typeField.equals(FieldType.VIDEO_FILE_UPLOAD.name()) || typeField.equals(FieldType.AUDIO_FILE_UPLOAD.name()) ||
                    typeField.equals(FieldType.MULTI_FILE_TYPE.name())){
                
                formField.put("allowBlank", true);
                //Add link Field
                JSONObject linkField= new JSONObject();
                linkField.put("id", processName+ "_" +parent + fieldName + "Link");
                linkField.put("name", parent + fieldName);
                linkField.put("fieldLabel", "Link "+fieldTitle);
                linkField.put("allowBlank", !fieldNN);
                jsonFormFields.put(linkField);
            }
        }else{
            switch (type) {
                case "java.util.Date":
                    formField.put("xtype", "datefield");
                    formField.put("format", extViewConfig.getDateFormat());
                    formField.put("altFormats", extViewConfig.getDatetimeFormat());
                    formField.put("tooltip", "Seleccione la fecha");
                    break;
                case "java.sql.Time":
                    formField.put("xtype", "timefield");
                    formField.put("format", extViewConfig.getTimeFormat());
                    formField.put("tooltip", "Seleccione la hora");
                    break;
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
        if(sizeColumnMap.containsKey(fieldName)){
            formField.put("minLength", sizeColumnMap.get(fieldName)[0]);
            formField.put("maxLength", sizeColumnMap.get(fieldName)[1]);
        }
        if(addField){
            jsonFormFields.put(formField);
        }
    }
    
    public void addEntityCombobox(JSONArray jsonFormFields, String processName, String parent, String simpleType, String fieldName,
            String fieldTitle, Map<String, List<String>> interfacesEntityRefMap, boolean readOnly, boolean fieldNN, boolean hidden, boolean disabled){
        
        String entityRef=  Character.toLowerCase(simpleType.charAt(0)) + simpleType.substring(1);
        if(!interfacesEntityRefMap.containsKey(processName)){
            interfacesEntityRefMap.put(processName, new ArrayList<>());
        }
        if(!interfacesEntityRefMap.get(processName).contains(entityRef)){
            interfacesEntityRefMap.get(processName).add(entityRef);
        }
        String combobox=
                "(function(){ "+
                "var combobox= Instance.extInterfaces['"+entityRef+"'].getCombobox('form'+util.getIndex('"+entityRef+"'), '" +processName+ "', '"+parent + fieldName+"', '"+fieldTitle+"');";
        if(readOnly || disabled){
            combobox+="combobox.setDisabled(true); ";
        }
        if(fieldNN){
            combobox+="combobox.allowBlank=false; ";
        }
        combobox+="return combobox;})()";
        jsonFormFields.put("@"+combobox+"@");
    }
    
    
}
