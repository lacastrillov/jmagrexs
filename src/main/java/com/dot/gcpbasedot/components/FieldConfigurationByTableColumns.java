/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dot.gcpbasedot.components;

import com.dot.gcpbasedot.dto.GenericTableColumn;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.stereotype.Component;

/**
 *
 * @author lacastrillov
 */
@Component
public class FieldConfigurationByTableColumns {
    
    public void orderTableColumns(List<GenericTableColumn> columns){
        Collections.sort(columns, new TableColumnComparator());
    }
    
    public HashMap<String, String> getTitledFieldsMap(List<GenericTableColumn> columns){
        HashMap<String, String> map= new HashMap<>();
        
        for(GenericTableColumn column: columns){
            map.put(column.getColumnAlias(), column.getColumnName());
        }
        
        return map;
    }
    
    /*public HashSet<String> getHideFields(Class dtoClass){
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
    }*/
    
    public HashSet<String> getNotNullFields(List<GenericTableColumn> columns){
        HashSet<String> fieldsNN= new HashSet<>();
        
        for(GenericTableColumn column: columns){
            if(column.isNotNull()){
                fieldsNN.add(column.getColumnAlias());
            }
        }
        
        return fieldsNN;
    }
    
    /*public HashSet<String> getReadOnlyFields(Class dtoClass){
        HashSet<String> fieldsRO= new HashSet<>();
        List<Field> fieldsReadOnly= EntityReflection.getEntityAnnotatedFields(dtoClass, ReadOnly.class);
        for(Field f: fieldsReadOnly){
            fieldsRO.add(f.getName());
        }
        
        return fieldsRO;
    }*/
    
    public HashMap<String, String[]> getTypeFormFields(List<GenericTableColumn> columns){
        HashMap<String, String[]> map= new HashMap<>();
        
        for(GenericTableColumn column: columns){
            String[] typeData= new String[]{column.getFieldType()};
            if(column.getFieldType()!=null){
                if(column.getFieldType().equals("LIST") || column.getFieldType().equals("MULTI_SELECT") || column.getFieldType().equals("RADIOS")){
                    String[] options= column.getOptions().replaceAll(", ", ",").split(",");
                    map.put(column.getColumnAlias(), (String[]) ArrayUtils.addAll(typeData, options));
                }else{
                    map.put(column.getColumnAlias(), typeData);
                }
            }
        }
        
        return map;
    }
    
    public HashMap<String, Integer> getSizeColumnMap(List<GenericTableColumn> columns){
        HashMap<String, Integer> map= new HashMap<>();
        
        for(GenericTableColumn column: columns){
            if(column.getColumnSize()!=null && column.getColumnSize()!=0){
                map.put(column.getColumnAlias(), column.getColumnSize());
            }
        }
        
        return map;
    }
    
    public HashMap<String, Integer> getWidthColumnMap(List<GenericTableColumn> columns){
        HashMap<String, Integer> map= new HashMap<>();
        
        for(GenericTableColumn column: columns){
            if(column.getWidth()!=null && column.getWidth()!=0){
                map.put(column.getColumnAlias(), column.getWidth());
            }else{
                map.put(column.getColumnAlias(), 200);
            }
        }
        
        return map;
    }
    
    public HashMap<String, String> getDefaultValueMap(List<GenericTableColumn> columns){
        HashMap<String, String> map= new HashMap<>();
        
        for(GenericTableColumn column: columns){
            if(column.getDefaultValue()!=null && !column.getDefaultValue().equals("")){
                map.put(column.getColumnAlias(), column.getDefaultValue());
            }
        }
        
        return map;
    }
    
    class TableColumnComparator implements Comparator<GenericTableColumn> {

        @Override
        public int compare(GenericTableColumn o1, GenericTableColumn o2) {
            if(o1.getColumnOrder() < o2.getColumnOrder()){
                return -1;
            }else if(Objects.equals(o1.getColumnOrder(), o2.getColumnOrder())){
                return 0;
            }else{
                return 1;
            }
        }
    }
    
}
