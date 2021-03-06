/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.components;

import com.lacv.jmagrexs.enums.HideView;
import com.lacv.jmagrexs.reflection.EntityReflection;
import com.lacv.jmagrexs.reflection.ReflectionUtils;
import com.lacv.jmagrexs.util.Formats;
import java.beans.PropertyDescriptor;
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
public class JSONForms {
    
    public final int MAX_LIST_ITEMS= 20;
    
    @Autowired
    public ExtViewConfig extViewConfig;
    
    @Autowired
    private FieldConfigurationByAnnotations fcba;
    
    @Autowired
    private JSONFields jfi;
    
    private final Map<String, List<String>> interfacesEntityRefMap= new HashMap<>();
    
    
    /**
     * @return the interfacesEntityRefMap
     */
    public Map<String, List<String>> getInterfacesEntityRefMap() {
        return interfacesEntityRefMap;
    }
    
    public JSONArray getJSONProcessForm(String processName, String parent, Class dtoClass, boolean hidden, boolean disabled){
        JSONArray jsonFormFields= new JSONArray();
        
        PropertyDescriptor[] propertyDescriptors = EntityReflection.getPropertyDescriptors(dtoClass);
        fcba.orderPropertyDescriptor(propertyDescriptors, dtoClass, "name");
        
        HashMap<String, String> titledFieldsMap= fcba.getTitledFieldsMap(propertyDescriptors, dtoClass);
        HashSet<String> hideFields= fcba.getHideFields(dtoClass);
        HashSet<String> fieldsNN= fcba.getNotNullFields(dtoClass);
        HashSet<String> fieldsRO= fcba.getReadOnlyFields(dtoClass);
        HashMap<String,Class> fieldsEC= fcba.getEntityComboboxFields(dtoClass);
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
                    if(fieldsEC.containsKey(fieldName)){
                        jfi.addEntityCombobox(jsonFormFields, processName, parent, fieldsEC.get(fieldName).getSimpleName(),
                                fieldName, fieldTitle, getInterfacesEntityRefMap(), readOnly, fieldsNN.contains(fieldName), hidden, disabled);
                        
                    }else if(Formats.TYPES_LIST.contains(type)){
                        jfi.addJSONField(jsonFormFields, processName, parent, type, fieldName,
                                titledFieldsMap.get(fieldName), typeFormFields, sizeColumnMap,
                                readOnly, fieldsNN.contains(fieldName), hidden, disabled);
                        
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
                        objectField.put("items", getJSONProcessForm(processName, parent+fieldName+".", childClass, hidden, disabled));
                        
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
                    hidden= (i>0);
                    disabled= (i>0);
                    fieldTitle= "Item "+(i+1);
                    if(fieldsEC.containsKey(fieldName)){
                        jfi.addEntityCombobox(jsonList, processName, parent, fieldsEC.get(fieldName).getSimpleName(),
                                fieldName + "["+i+"]", fieldTitle, getInterfacesEntityRefMap(), false,
                                fieldsNN.contains(fieldName), hidden, disabled);
                        
                    }else if(Formats.TYPES_LIST.contains(childClass.getName())){
                        jfi.addJSONField(jsonList, processName, parent, childClass.getName(),
                                fieldName + "["+i+"]", fieldTitle, typeFormFields, sizeColumnMap,
                                false, fieldsNN.contains(fieldName), hidden, disabled);
                        
                    }else{
                        JSONObject objectField= new JSONObject();
                        objectField.put("id", processName+"_"+parent+fieldName+"["+i+"]");
                        objectField.put("xtype", "fieldset");
                        objectField.put("title", fieldTitle);
                        objectField.put("collapsible", true);
                        objectField.put("layout", "anchor");
                        objectField.put("defaultType", "textfield");
                        objectField.put("hidden", hidden);
                        JSONObject fieldDefaultsChild= new JSONObject();
                        fieldDefaultsChild.put("anchor", "100%");
                        fieldDefaultsChild.put("labelAlign", "right");
                        fieldDefaultsChild.put("disabled", disabled);
                        objectField.put("fieldDefaults", fieldDefaultsChild);
                        objectField.put("items", getJSONProcessForm(processName, parent+fieldName+"["+i+"].", childClass, hidden, disabled));
                        jsonList.put(objectField);
                    }
                }
                
                JSONObject buttonAdd= new JSONObject();
                buttonAdd.put("xtype", "button");
                buttonAdd.put("text", "Agregar");
                buttonAdd.put("style", "margin:5px");
                buttonAdd.put("width", 100);
                buttonAdd.put("handler", "@function(){Instance.commonExtView.addListItem('"+processName+"','"+parent+"','"+fieldName+"')}@");
                jsonList.put(buttonAdd);
                
                JSONObject buttonQuit= new JSONObject();
                buttonQuit.put("xtype", "button");
                buttonQuit.put("text", "Quitar");
                buttonQuit.put("style", "margin:5px");
                buttonQuit.put("width", 100);
                buttonQuit.put("handler", "@function(){Instance.commonExtView.removeListItem('"+processName+"','"+parent+"','"+fieldName+"')}@");
                jsonList.put(buttonQuit);
                
                objectFieldGroup.put("items", jsonList);
                jsonFormFields.put(objectFieldGroup);
            }
        }
        
        return jsonFormFields;
    }
    
}
