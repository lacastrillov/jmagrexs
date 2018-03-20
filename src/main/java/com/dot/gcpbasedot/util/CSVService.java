package com.dot.gcpbasedot.util;

import com.dot.gcpbasedot.annotation.LabelField;
import com.dot.gcpbasedot.components.FieldConfigurationByAnnotations;
import com.dot.gcpbasedot.domain.BaseEntity;
import com.dot.gcpbasedot.dto.GenericTableColumn;
import com.dot.gcpbasedot.enums.HideView;
import com.dot.gcpbasedot.reflection.EntityReflection;
import java.beans.PropertyDescriptor;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.web.util.HtmlUtils;

public class CSVService {
    
    private static final String DEFAULT_SEPARATOR = ";";
    
    private static final FieldConfigurationByAnnotations FCBA= new FieldConfigurationByAnnotations();
    

    public static String generateCSVReport(List<Object> list, Class dtoClass) throws Exception {
        StringBuilder report=new StringBuilder("");
        PropertyDescriptor[] propertyDescriptors = EntityReflection.getPropertyDescriptors(dtoClass);
        FCBA.orderPropertyDescriptor(propertyDescriptors, dtoClass, "name");

        HashMap<String, String> titledFieldsMap= FCBA.getTitledFieldsMap(propertyDescriptors, dtoClass);
        HashSet<String> hideFields= FCBA.getHideFields(dtoClass);

        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String fieldName= propertyDescriptor.getName();
            String type = propertyDescriptor.getPropertyType().getName();
            if(type.equals("java.util.List")==false && type.equals("java.lang.Class")==false && !hideFields.contains(fieldName + HideView.GRID.name())){
                report.append(HtmlUtils.htmlUnescape(titledFieldsMap.get(fieldName))).append(DEFAULT_SEPARATOR);
            }
        }
        report.append("\n");

        for (Object object : list) {
            BeanWrapperImpl sourceWrapper = new BeanWrapperImpl(object);

            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                String fieldName= propertyDescriptor.getName();
                Object value = sourceWrapper.getPropertyValue(fieldName);
                Class<?> typeWrapper = propertyDescriptor.getPropertyType();
                if(typeWrapper.getName().equals("java.util.List")==false && typeWrapper.getName().equals("java.lang.Class")==false && !hideFields.contains(fieldName + HideView.GRID.name())){
                    if (value != null) {
                        try{
                            if(Formats.TYPES_LIST.contains(typeWrapper.getName())){
                                Object parseValue = typeWrapper.cast(value);
                                if(typeWrapper.getName().equals("java.util.Date")){
                                    report.append(Formats.dateToString((Date)parseValue, "dd-MM-yyyy")).append(DEFAULT_SEPARATOR);
                                }else{
                                    report.append(parseValue.toString()).append(DEFAULT_SEPARATOR);
                                }
                            }else{
                                BeanWrapperImpl internalWrapper = new BeanWrapperImpl(value);
                                String textValue= "";
                                if(internalWrapper.getPropertyValue("id")!=null){
                                    textValue= internalWrapper.getPropertyValue("id").toString();
                                }
                                LabelField ann= (LabelField) EntityReflection.getClassAnnotation(typeWrapper, LabelField.class);
                                if(ann!=null && internalWrapper.getPropertyValue(ann.value())!=null){
                                    textValue+= " - " + internalWrapper.getPropertyValue(ann.value()).toString();
                                }
                                report.append(textValue).append(DEFAULT_SEPARATOR);
                            }
                        }catch(Exception e){
                            report.append("").append(DEFAULT_SEPARATOR);
                        }
                    }else{
                        report.append("").append(DEFAULT_SEPARATOR);
                    }
                }
            }
            report.append("\n");
        }
        return report.toString().replaceAll(DEFAULT_SEPARATOR+"\n", "\n");
    }
    
    public static String generateCSVReport(List<Map<String, Object>> list, List<GenericTableColumn> columns) throws Exception {
        StringBuilder report=new StringBuilder("");
        for (GenericTableColumn column : columns) {
            String type = column.getDataType();
            if(type.equals("java.util.List")==false && type.equals("java.lang.Class")==false){
                report.append(column.getColumnAlias()).append(DEFAULT_SEPARATOR);
            }
        }
        report.append("\n");

        for (Map<String, Object> object : list) {
            for (GenericTableColumn column : columns) {
                Object value = object.get(column.getColumnAlias());
                String propertyType = column.getDataType();
                if (value != null) {
                    try{
                        if(propertyType.equals("java.util.Date")){
                            DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                            report.append(format.format(value)).append(DEFAULT_SEPARATOR);
                        }else{
                            report.append(value.toString()).append(DEFAULT_SEPARATOR);
                        }
                    }catch(Exception e){
                        report.append("").append(DEFAULT_SEPARATOR);
                    }
                }else{
                    report.append("").append(DEFAULT_SEPARATOR);
                }
            }
            report.append("\n");
        }
        return report.toString().replaceAll(DEFAULT_SEPARATOR+"\n", "\n");
    }
    
    public static String csvRecordsToJSON(String csvRecords, Class dtoClass){
        PropertyDescriptor[] propertyDescriptors = EntityReflection.getPropertyDescriptors(dtoClass);
        HashMap<String, String> titledFieldsMap= FCBA.getTitledFieldsMap(propertyDescriptors, dtoClass);
        HashMap<String, String> invertedTitledFieldsMap= new HashMap<>();
        
        for (Map.Entry<String, String> entry : titledFieldsMap.entrySet()){
            invertedTitledFieldsMap.put(HtmlUtils.htmlUnescape(entry.getValue()), entry.getKey());
        }
        Set<String> baseEntityTypes= new HashSet<>();
        for(PropertyDescriptor pd: propertyDescriptors){
            if(BaseEntity.class.isAssignableFrom(pd.getPropertyType())){
                baseEntityTypes.add(pd.getName());
            }
        }
        String[] records= csvRecords.split("\n");
        String[] columns= records[0].split(DEFAULT_SEPARATOR);
        for(int i=0; i<columns.length; i++){
            if(invertedTitledFieldsMap.containsKey(columns[i])){
                columns[i]= invertedTitledFieldsMap.get(columns[i]);
            }
        }
        JSONArray objects= new JSONArray();
        for(int i=1; i<records.length; i++){
            JSONObject object= new JSONObject();
            String[] record= records[i].split(DEFAULT_SEPARATOR);
            for(int j=0; j<record.length; j++){
                String fieldName= columns[j];
                String value= record[j];
                if(baseEntityTypes.contains(fieldName)){
                    object.put(fieldName, value.split(" - ")[0]);
                }else{
                    object.put(fieldName, value);
                }
            }
            objects.put(object);
        }

        return objects.toString();
    }

}
