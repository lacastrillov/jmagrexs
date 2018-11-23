/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.components;

import com.lacv.jmagrexs.enums.FieldType;
import com.lacv.jmagrexs.enums.HideView;
import com.lacv.jmagrexs.reflection.EntityReflection;
import com.lacv.jmagrexs.reflection.ReflectionUtils;
import com.lacv.jmagrexs.util.Formats;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.HashSet;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author lacastrillov
 */
@Component
public class JSONForms {
    
    public final int MAX_LIST_ITEMS= 20;
    
    @Autowired
    public ExtViewConfig extViewConfig;
    
    @Autowired
    private FieldConfigurationByAnnotations fcba;
    
    
    public JSONArray getJSONProcessForm(String processName, String parent, Class dtoClass){
        JSONArray jsonFormFields= new JSONArray();
        
        PropertyDescriptor[] propertyDescriptors = EntityReflection.getPropertyDescriptors(dtoClass);
        fcba.orderPropertyDescriptor(propertyDescriptors, dtoClass, "name");
        
        HashMap<String, String> titledFieldsMap= fcba.getTitledFieldsMap(propertyDescriptors, dtoClass);
        HashSet<String> hideFields= fcba.getHideFields(dtoClass);
        HashSet<String> fieldsNN= fcba.getNotNullFields(dtoClass);
        HashSet<String> fieldsRO= fcba.getReadOnlyFields(dtoClass);
        HashMap<String,String[]> typeFormFields= fcba.getTypeFormFields(dtoClass);
        HashMap<String, Integer[]> sizeColumnMap= fcba.getSizeColumnMap(dtoClass);
        
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String type = propertyDescriptor.getPropertyType().getName();
            String fieldName= propertyDescriptor.getName();
            String fieldTitle= titledFieldsMap.get(fieldName);
            
            if(type.equals("java.util.List")==false && type.equals("java.lang.Class")==false){
                boolean readOnly= fieldsRO.contains(fieldName);

                // ADD TO jsonFormFields
                if(!hideFields.contains(fieldName + HideView.FORM.name())){
                    if(Formats.TYPES_LIST.contains(type)){
                        boolean addFormField= true;
                        JSONObject formField= new JSONObject();
                        formField.put("name", parent + fieldName);
                        formField.put("fieldLabel", fieldTitle);
                        if(readOnly){
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
                                jsonFormFields.put("#Instance.commonExtView.getSimpleCombobox('"+parent + fieldName+"','"+fieldTitle+"','form',"+dataArray.toString().replaceAll("\"", "'")+","+(!fieldsNN.contains(fieldName))+")#");
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

                        if(fieldsNN.contains(fieldName)){
                            formField.put("allowBlank", false);
                        }
                        if(sizeColumnMap.containsKey(fieldName)){
                            formField.put("minLength", sizeColumnMap.get(fieldName)[0]);
                            formField.put("maxLength", sizeColumnMap.get(fieldName)[1]);
                        }
                        if(addFormField){
                            jsonFormFields.put(formField);
                        }
                    }else{
                        Class childClass = propertyDescriptor.getPropertyType();
                        JSONObject fieldDefaults= new JSONObject();
                        fieldDefaults.put("anchor", "100%");
                        fieldDefaults.put("labelAlign", "right");
                        
                        JSONObject objectField= new JSONObject();
                        objectField.put("xtype", "fieldset");
                        objectField.put("title", fieldTitle);
                        objectField.put("collapsible", true);
                        objectField.put("layout", "anchor");
                        objectField.put("defaultType", "textfield");
                        objectField.put("minWidth", 300);
                        objectField.put("fieldDefaults", fieldDefaults);
                        objectField.put("items", getJSONProcessForm(processName, parent+fieldName+".", childClass));
                        
                        jsonFormFields.put(objectField);
                    }
                }
            }else if(type.equals("java.util.List")){
                Class childClass = ReflectionUtils.getParametrizedTypeList(dtoClass, fieldName);
                
                JSONObject fieldDefaults= new JSONObject();
                fieldDefaults.put("anchor", "100%");
                fieldDefaults.put("labelAlign", "right");
                
                JSONObject objectFieldGroup= new JSONObject();
                objectFieldGroup.put("id", processName+"_"+parent+fieldName);
                objectFieldGroup.put("xtype", "fieldset");
                objectFieldGroup.put("title", fieldTitle+":");
                objectFieldGroup.put("itemTop", 0);
                objectFieldGroup.put("collapsible", true);
                objectFieldGroup.put("layout", "anchor");
                objectFieldGroup.put("defaultType", "textfield");
                objectFieldGroup.put("minWidth", 300);
                objectFieldGroup.put("fieldDefaults", fieldDefaults);
                
                JSONArray jsonList= new JSONArray();
                for(int i=0; i<MAX_LIST_ITEMS; i++){
                    JSONObject objectField= new JSONObject();
                    objectField.put("id", processName+"_"+parent+fieldName+"["+i+"]");
                    if(!Formats.TYPES_LIST.contains(childClass.getName())){
                        objectField.put("xtype", "fieldset");
                        objectField.put("title", "Item "+i);
                        objectField.put("collapsible", true);
                        objectField.put("layout", "anchor");
                        objectField.put("defaultType", "textfield");
                        JSONObject fieldDefaultsChild= new JSONObject();
                        fieldDefaultsChild.put("anchor", "100%");
                        fieldDefaultsChild.put("labelAlign", "right");
                        if(i>0){
                            objectField.put("hidden", true);
                            fieldDefaultsChild.put("disabled", true);
                        }
                        objectField.put("fieldDefaults", fieldDefaultsChild);
                        objectField.put("items", getJSONProcessForm(processName, parent+fieldName+"["+i+"].", childClass));
                    }else{
                        objectField.put("name", parent + fieldName + "["+i+"]");
                        objectField.put("fieldLabel", "Item "+i);
                        if(i>0){
                            objectField.put("hidden", true);
                            objectField.put("disabled", true);
                        }
                        switch (childClass.getName()) {
                            case "java.util.Date":
                                objectField.put("xtype", "datefield");
                                objectField.put("format", extViewConfig.getDateFormat());
                                objectField.put("tooltip", "Seleccione la fecha");
                                break;
                            case "java.sql.Time":
                                objectField.put("xtype", "timefield");
                                objectField.put("format", extViewConfig.getTimeFormat());
                                objectField.put("tooltip", "Seleccione la hora");
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
                                objectField.put("xtype", "numberfield");
                                break;
                            case "boolean":
                            case "java.lang.Boolean":
                                objectField.put("xtype", "checkbox");
                                objectField.put("inputValue", "true");
                                objectField.put("uncheckedValue", "false");
                                break;
                        }
                    }
                    
                    jsonList.put(objectField);
                }
                
                JSONObject buttonAdd= new JSONObject();
                buttonAdd.put("xtype", "button");
                buttonAdd.put("text", "Agregar");
                buttonAdd.put("style", "margin:5px");
                buttonAdd.put("width", 100);
                buttonAdd.put("handler", "#function(){"
                                       + "    var itemsGroup= Ext.getCmp('"+processName+"_"+parent+fieldName+"');"
                                       + "    if(itemsGroup.itemTop<"+(MAX_LIST_ITEMS-1)+"){"
                                       + "        itemsGroup.itemTop+= 1;"
                                       + "        var itemEntity= Ext.getCmp('"+processName+"_"+parent+fieldName+"['+itemsGroup.itemTop+']');"
                                       + "        itemEntity.setVisible(true);"
                                       + "        itemEntity.setDisabled(false);"
                                       + "        if(itemEntity.query){"
                                       + "            itemEntity.query('.field').forEach(function(c){"
                                       + "                var visible= true;"
                                       + "                var upFieldset=c.up('fieldset');"
                                       + "                while(upFieldset!==undefined && visible===true){"
                                       + "                    visible=upFieldset.isVisible();"
                                       + "                    upFieldset= upFieldset.up('fieldset');"
                                       + "                };"                       
                                       + "                c.setDisabled(!c.isVisible() || !visible);"
                                       + "            });"
                                       + "        }"
                                       + "    }"
                                       + "}#");
                jsonList.put(buttonAdd);
                
                
                JSONObject buttonQuit= new JSONObject();
                buttonQuit.put("xtype", "button");
                buttonQuit.put("text", "Quitar");
                buttonQuit.put("style", "margin:5px");
                buttonQuit.put("width", 100);
                buttonQuit.put("handler", "#function(){"
                        + "                   var itemsGroup= Ext.getCmp('"+processName+"_"+parent+fieldName+"');"
                        + "                   if(itemsGroup.itemTop>=0){"
                        + "                       var itemEntity= Ext.getCmp('"+processName+"_"+parent+fieldName+"['+itemsGroup.itemTop+']');"
                        + "                       itemsGroup.itemTop-= 1;"
                        + "                       itemEntity.setVisible(false);"
                        + "                       itemEntity.setDisabled(true);"
                        + "                       if(itemEntity.query){"
                        + "                           itemEntity.query('.field').forEach(function(c){"
                        + "                               c.setDisabled(true);"
                        + "                           });"
                        + "                       }"
                        + "                   }"
                        + "               }#");
                jsonList.put(buttonQuit);
                
                objectFieldGroup.put("items", jsonList);
                jsonFormFields.put(objectFieldGroup);
            }
        }
        
        return jsonFormFields;
    }
    
}
