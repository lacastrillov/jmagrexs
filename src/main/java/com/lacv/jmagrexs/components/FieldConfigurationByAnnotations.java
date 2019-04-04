/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.components;

import com.lacv.jmagrexs.annotation.ColumnWidth;
import com.lacv.jmagrexs.annotation.DefaultValue;
import com.lacv.jmagrexs.annotation.EntityCombobox;
import com.lacv.jmagrexs.annotation.GroupField;
import com.lacv.jmagrexs.annotation.HideField;
import com.lacv.jmagrexs.annotation.NotNull;
import com.lacv.jmagrexs.annotation.Order;
import com.lacv.jmagrexs.annotation.ReadOnly;
import com.lacv.jmagrexs.annotation.Size;
import com.lacv.jmagrexs.annotation.TextField;
import com.lacv.jmagrexs.annotation.TypeFormField;
import com.lacv.jmagrexs.annotation.ValueMapField;
import com.lacv.jmagrexs.enums.FieldType;
import com.lacv.jmagrexs.enums.HideView;
import com.lacv.jmagrexs.reflection.EntityReflection;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 *
 * @author lacastrillov
 */
@Component
public class FieldConfigurationByAnnotations {
    
    public void orderPropertyDescriptor(PropertyDescriptor[] propertyDescriptors, Class dtoClass, String labelField){
        List<Field> annotatedFields= EntityReflection.getEntityAnnotatedFields(dtoClass, Order.class);
        PropertyDescriptor aux;
        
        if(annotatedFields.size()>0){
            for(Field f: annotatedFields){
                Order an= f.getAnnotation(Order.class);
                String fieldName= f.getName();
                int order= an.value()-1;
                if(order < 0){
                    order=0;
                }else if(order >= propertyDescriptors.length){
                    order=propertyDescriptors.length-1;
                }
                for(int j=0; j<propertyDescriptors.length; j++){
                    if(order!=j && fieldName.equals(propertyDescriptors[j].getName())){
                        aux= propertyDescriptors[order];
                        propertyDescriptors[order]= propertyDescriptors[j];
                        propertyDescriptors[j]= aux;
                        break;
                    }
                }
            }
        }else{
            String[] defaultOrder= {"id", labelField};
            for(int i=0; i<defaultOrder.length; i++){
                String fieldName= defaultOrder[i];
                for(int j=0; j<propertyDescriptors.length; j++){
                    if(i!=j && fieldName.equals(propertyDescriptors[j].getName())){
                        if(fieldName.equals(propertyDescriptors[j].getName())){
                            aux= propertyDescriptors[i];
                            propertyDescriptors[i]= propertyDescriptors[j];
                            propertyDescriptors[j]= aux;
                            break;
                        }
                    }
                }
            }
        }
    }
    
    public HashMap<String, String> getTitledFieldsMap(PropertyDescriptor[] propertyDescriptors, Class dtoClass){
        List<Field> annotatedFields= EntityReflection.getEntityAnnotatedFields(dtoClass, TextField.class);
        HashMap<String, String> map= new HashMap<>();
        
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String fieldName = propertyDescriptor.getName();
            map.put(fieldName, StringUtils.capitalize(fieldName));
        }
        
        for(Field f: annotatedFields){
            TextField an= f.getAnnotation(TextField.class);
            String fieldName= f.getName();
            map.put(fieldName, an.value());
        }
        
        return map;
    }
    
    public HashMap<String, String> getGroupFieldsMap(Class dtoClass){
        List<Field> annotatedFields= EntityReflection.getEntityAnnotatedFields(dtoClass, GroupField.class);
        HashMap<String, String> map= new HashMap<>();
        
        for(Field f: annotatedFields){
            GroupField an= f.getAnnotation(GroupField.class);
            String fieldName= f.getName();
            map.put(fieldName, an.value());
        }
        
        return map;
    }
    
    public HashSet<String> getHideFields(Class dtoClass){
        List<Field> annotatedFields= EntityReflection.getEntityAnnotatedFields(dtoClass, HideField.class);
        HashSet<String> map= new HashSet<>();
        
        for(Field f: annotatedFields){
            String fieldName= f.getName();
            HideField an= f.getAnnotation(HideField.class);
            for(HideView hv: an.value()){
                map.add(fieldName+hv.name());
            }
        }
        
        return map;
    }
    
    public HashSet<String> getNotNullFields(Class dtoClass){
        HashSet<String> fieldsNN= new HashSet<>();
        List<Field> fieldsNotNull= EntityReflection.getEntityAnnotatedFields(dtoClass, NotNull.class);
        for(Field f: fieldsNotNull){
            fieldsNN.add(f.getName());
        }
        
        return fieldsNN;
    }
    
    public HashSet<String> getReadOnlyFields(Class dtoClass){
        HashSet<String> fieldsRO= new HashSet<>();
        List<Field> fieldsReadOnly= EntityReflection.getEntityAnnotatedFields(dtoClass, ReadOnly.class);
        for(Field f: fieldsReadOnly){
            fieldsRO.add(f.getName());
        }
        
        return fieldsRO;
    }
    
    public HashMap<String, Class> getEntityComboboxFields(Class dtoClass){
        HashMap<String, Class> fieldsEC= new HashMap<>();
        List<Field> entityComboboxFields= EntityReflection.getEntityAnnotatedFields(dtoClass, EntityCombobox.class);
        
        for(Field f: entityComboboxFields){
            EntityCombobox an= f.getAnnotation(EntityCombobox.class);
            String fieldName= f.getName();
            fieldsEC.put(fieldName, an.value());
        }
        
        return fieldsEC;
    }
    
    public HashMap<String, String[]> getTypeFormFields(Class dtoClass){
        List<Field> annotatedFields= EntityReflection.getEntityAnnotatedFields(dtoClass, TypeFormField.class);
        HashMap<String, String[]> map= new HashMap<>();
        
        for(Field f: annotatedFields){
            TypeFormField an= f.getAnnotation(TypeFormField.class);
            String fieldName= f.getName();
            FieldType ft= an.value();
            String[] typeData= new String[]{ft.name()};
            
            if(ft.equals(FieldType.LIST) || ft.equals(FieldType.MULTI_SELECT) || ft.equals(FieldType.RADIOS)){
                map.put(fieldName, (String[]) ArrayUtils.addAll(typeData, an.data()));
            }else{
                map.put(fieldName, typeData);
            }
        }
        
        return map;
    }
    
    public HashMap<String, Integer> getWidthColumnMap(PropertyDescriptor[] propertyDescriptors, Class dtoClass){
        List<Field> annotatedFields= EntityReflection.getEntityAnnotatedFields(dtoClass, ColumnWidth.class);
        HashMap<String, Integer> map= new HashMap<>();
        
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String fieldName = propertyDescriptor.getName();
            map.put(fieldName, 200);
        }
        
        for(Field f: annotatedFields){
            ColumnWidth an= f.getAnnotation(ColumnWidth.class);
            String fieldName= f.getName();
            map.put(fieldName, an.value());
        }
        
        return map;
    }
    
    public HashMap<String, Integer[]> getSizeColumnMap(Class dtoClass){
        List<Field> annotatedFields= EntityReflection.getEntityAnnotatedFields(dtoClass, Size.class);
        HashMap<String, Integer[]> map= new HashMap<>();
        
        for(Field f: annotatedFields){
            Size annotation= f.getAnnotation(Size.class);
            String fieldName= f.getName();
            map.put(fieldName, new Integer[]{annotation.min(), annotation.max()});
        }
        
        return map;
    }
    
    public HashMap<String, String> getDefaultValueMap(Class dtoClass){
        List<Field> annotatedFields= EntityReflection.getEntityAnnotatedFields(dtoClass, DefaultValue.class);
        HashMap<String, String> map= new HashMap<>();
        
        for(Field f: annotatedFields){
            DefaultValue an= f.getAnnotation(DefaultValue.class);
            String fieldName= f.getName();
            map.put(fieldName, an.value());
        }
        
        return map;
    }
    
    public HashSet<String> getValueMapFields(Class dtoClass){
        List<Field> annotatedFields= EntityReflection.getEntityAnnotatedFields(dtoClass, ValueMapField.class);
        HashSet<String> map= new HashSet<>();
        
        for(Field f: annotatedFields){
            String fieldName= f.getName();
            map.add(fieldName);
        }
        
        return map;
    }
    
}
