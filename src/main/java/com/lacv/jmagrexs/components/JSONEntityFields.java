/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.components;

import com.lacv.jmagrexs.enums.FieldType;
import java.util.HashMap;
import java.util.LinkedHashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author e11001a
 */
@Component
public class JSONEntityFields {
    
    @Autowired
    public ExtViewConfig extViewConfig;
    
    public void addJSONField(JSONArray jsonFormFields, String entityName, String type, String fieldName,
            String fieldTitle, String titleGroup, HashMap<String,String[]> typeFormFields, HashMap<String, Integer[]> sizeColumnMap, 
            LinkedHashMap<String,JSONObject> fieldGroups, HashMap<String, Integer> positionColumnForm, int numColumnsForm,
            boolean isEditableForm, boolean readOnly, boolean fieldNN){
        
        boolean addFormField= true;
        JSONObject formField= new JSONObject();
        formField.put("id", entityName + "_" +fieldName);
        formField.put("name", fieldName);
        formField.put("fieldLabel", fieldTitle);
        formField.put("allowBlank", !fieldNN);

        JSONObject rendererField= new JSONObject();
        rendererField.put("id", entityName + "_" + fieldName + "Renderer");
        rendererField.put("name", fieldName);
        rendererField.put("fieldLabel", fieldTitle);
        rendererField.put("xtype", "displayfield");

        if(!isEditableForm || readOnly){
            formField.put("readOnly", true);
        }
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
                if(!readOnly && isEditableForm){
                    String field= "#Instance.commonExtView.getSimpleCombobox('"+fieldName+"','"+fieldTitle+"','form',"+dataArray.toString().replaceAll("\"", "'")+","+(!fieldNN)+")#";
                    addFormField(field,jsonFormFields,numColumnsForm,titleGroup,fieldGroups,positionColumnForm);
                }else{
                    addFormField=true;
                }
            }else if(typeForm.equals(FieldType.RADIOS.name())){
                addFormField= false;
                String[] data= typeFormFields.get(fieldName);
                JSONArray dataArray = new JSONArray();
                for(int i=1; i<data.length; i++){
                    dataArray.put(data[i]);
                }
                String field= "#Instance.commonExtView.getRadioGroup('"+fieldName+"','"+fieldTitle+"',"+dataArray.toString().replaceAll("\"", "'")+")#";
                addFormField(field,jsonFormFields,numColumnsForm,titleGroup,fieldGroups,positionColumnForm);
            }else if(typeForm.equals(FieldType.FILE_SIZE.name())){
                formField.put("xtype", "numberfield");
                formField.put("fieldLabel", fieldTitle+" (bytes)");

                //Add file Size Text
                rendererField.put("renderer", "#Instance.commonExtView.fileSizeRender#");
                jsonFormFields.put(rendererField);
                if(!isEditableForm){
                    addFormField= false;
                }
            }else if(typeForm.equals(FieldType.PERCENTAJE.name())){
                formField.put("xtype", "numberfield");
                formField.put("fieldLabel", fieldTitle+" (%)");
                formField.put("minValue", 0);
                formField.put("maxValue", 100);
            }else if(typeForm.equals(FieldType.COLOR.name())){
                formField.put("xtype", "customcolorpicker");
            }else if(typeForm.equals(FieldType.VIDEO_YOUTUBE.name())){
                formField.put("fieldLabel", "Link "+fieldTitle);
                formField.put("emptyText", "Url Youtube");

                //Add Video Youtube
                rendererField.put("renderer", "#Instance.commonExtView.videoYoutubeRender#");
                addFormField(rendererField,jsonFormFields,numColumnsForm,titleGroup,fieldGroups,positionColumnForm);
            }else if(typeForm.equals(FieldType.GOOGLE_MAP.name())){
                formField.put("fieldLabel", "Coordenadas "+fieldTitle);
                formField.put("emptyText", "Google Maps Point");

                //Add GoogleMap
                rendererField.put("renderer", "#Instance.commonExtView.googleMapsRender#");
                addFormField(rendererField,jsonFormFields,numColumnsForm,titleGroup,fieldGroups,positionColumnForm);
            }else if(typeForm.equals(FieldType.FILE_UPLOAD.name())){
                formField.put("name", fieldName + "_File");
                formField.put("xtype", "filefield");
                formField.put("fieldLabel", "Subir "+fieldTitle);
                formField.put("emptyText", "Seleccione un archivo");

                //Add Url File
                rendererField.put("renderer", "#Instance.commonExtView.fileRender#");
                addFormField(rendererField,jsonFormFields,numColumnsForm,titleGroup,fieldGroups,positionColumnForm);
                if(!isEditableForm){
                    addFormField= false;
                }
            }else if(typeForm.equals(FieldType.IMAGE_FILE_UPLOAD.name())){
                formField.put("name", fieldName + "_File");
                formField.put("xtype", "filefield");
                formField.put("fieldLabel", "Subir "+fieldTitle);
                formField.put("emptyText", "Seleccione una imagen");

                //Add Image
                rendererField.put("renderer", "#Instance.commonExtView.imageRender#");
                addFormField(rendererField,jsonFormFields,numColumnsForm,titleGroup,fieldGroups,positionColumnForm);
                if(!isEditableForm){
                    addFormField= false;
                }
            }else if(typeForm.equals(FieldType.VIDEO_FILE_UPLOAD.name())){
                formField.put("name", fieldName + "_File");
                formField.put("xtype", "filefield");
                formField.put("fieldLabel", "Subir "+fieldTitle);
                formField.put("emptyText", "Seleccione un video");

                //Add Video
                rendererField.put("renderer", "#Instance.commonExtView.videoFileUploadRender#");
                addFormField(rendererField,jsonFormFields,numColumnsForm,titleGroup,fieldGroups,positionColumnForm);
                if(!isEditableForm){
                    addFormField= false;
                }
            }else if(typeForm.equals(FieldType.AUDIO_FILE_UPLOAD.name())){
                formField.put("name", fieldName + "_File");
                formField.put("xtype", "filefield");
                formField.put("fieldLabel", "Subir "+fieldTitle);
                formField.put("emptyText", "Seleccione un audio");

                //Add Audio
                rendererField.put("renderer", "#Instance.commonExtView.audioFileUploadRender#");
                addFormField(rendererField,jsonFormFields,numColumnsForm,titleGroup,fieldGroups,positionColumnForm);
                if(!isEditableForm){
                    addFormField= false;
                }
            }else if(typeForm.equals(FieldType.MULTI_FILE_TYPE.name())){
                formField.put("name", fieldName + "_File");
                formField.put("xtype", "filefield");
                formField.put("fieldLabel", "Subir "+fieldTitle);
                formField.put("emptyText", "Seleccione un archivo");

                //Add MultiFile
                rendererField.put("renderer", "#Instance.commonExtView.multiFileRender#");
                addFormField(rendererField,jsonFormFields,numColumnsForm,titleGroup,fieldGroups,positionColumnForm);
                if(!isEditableForm){
                    addFormField= false;
                }
            }
            if(typeForm.equals(FieldType.FILE_UPLOAD.name()) || typeForm.equals(FieldType.IMAGE_FILE_UPLOAD.name()) ||
                    typeForm.equals(FieldType.VIDEO_FILE_UPLOAD.name()) || typeForm.equals(FieldType.AUDIO_FILE_UPLOAD.name()) ||
                    typeForm.equals(FieldType.MULTI_FILE_TYPE.name())){

                formField.put("allowBlank", true);
                //Add link Field
                JSONObject linkField= new JSONObject();
                linkField.put("id", entityName+"_"+fieldName + "Link");
                linkField.put("name", fieldName);
                linkField.put("fieldLabel", "Link "+fieldTitle);
                linkField.put("allowBlank", !fieldNN);
                linkField.put("readOnly", readOnly);
                if(!isEditableForm){
                    linkField.put("xtype", "displayfield");
                }
                addFormField(linkField,jsonFormFields,numColumnsForm,titleGroup,fieldGroups,positionColumnForm);
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
        if(addFormField){
            if(!isEditableForm){
                formField.put("xtype", "displayfield");
            }
            addFormField(formField,jsonFormFields,numColumnsForm,titleGroup,fieldGroups,positionColumnForm);
        }
    }
    
    public void addEntityCombobox(JSONArray jsonFormFields, String fieldEntity, boolean isEditableForm, int numColumnsForm,
            String titleGroup, LinkedHashMap<String,JSONObject> fieldGroups, HashMap<String,Integer> positionColumnForm,
            boolean readOnly, boolean fieldNN){
        
        String combobox="(function(){ ";
        if(!isEditableForm || readOnly){
            combobox+="Instance.formCombobox"+fieldEntity+".setDisabled(true); ";
        }
        if(fieldNN){
            combobox+="Instance.formCombobox"+fieldEntity+".allowBlank=false; ";
        }
        combobox+="return Instance.formCombobox"+fieldEntity+";" +
                        "})()";
        addFormField("#"+combobox+"#",jsonFormFields,numColumnsForm,titleGroup,fieldGroups,positionColumnForm);
    }
    
    private void addFormField(Object field, JSONArray jsonFormFields, int numColumnsForm, String titleGroup,
            LinkedHashMap<String,JSONObject> fieldGroups, HashMap<String,Integer> positionColumnForm){
        
        if(numColumnsForm<=1){
            if(titleGroup.equals("")){
                jsonFormFields.put(field);
            }else{
                fieldGroups.get(titleGroup).getJSONArray("items").put(field);
            }
        }else{ 
            if(positionColumnForm.get(titleGroup)==0 || positionColumnForm.get(titleGroup)==numColumnsForm){
                JSONObject defaults= new JSONObject();
                double columnWidth= (double)1/numColumnsForm-0.02;
                defaults.put("columnWidth", (double)Math.round(columnWidth * 100d) / 100d);
                defaults.put("margin", 7);
                
                JSONObject objectColumn= new JSONObject();
                objectColumn.put("xtype", "container");
                objectColumn.put("layout", "column");
                objectColumn.put("defaultType", "textfield");
                objectColumn.put("defaults", defaults);
                objectColumn.put("items", new JSONArray());
                if(titleGroup.equals("")){
                    jsonFormFields.put(objectColumn);
                }else{
                    fieldGroups.get(titleGroup).getJSONArray("items").put(objectColumn);
                }
                if(positionColumnForm.get(titleGroup)==numColumnsForm){
                    positionColumnForm.put(titleGroup,0);
                }
            }
            if(positionColumnForm.get(titleGroup) < numColumnsForm){
                if(titleGroup.equals("")){
                    int index= jsonFormFields.length()-1;
                    jsonFormFields.getJSONObject(index).getJSONArray("items").put(field);
                }else{
                    int index= fieldGroups.get(titleGroup).getJSONArray("items").length()-1;
                    fieldGroups.get(titleGroup).getJSONArray("items").getJSONObject(index).getJSONArray("items").put(field);
                }
            }
            positionColumnForm.put(titleGroup,positionColumnForm.get(titleGroup)+1);
        }
    }
    
}
