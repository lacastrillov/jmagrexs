package com.lacv.jmagrexs.util;

import com.lacv.jmagrexs.annotation.LabelField;
import com.lacv.jmagrexs.components.ExtViewConfig;
import com.lacv.jmagrexs.components.FieldConfigurationByAnnotations;
import com.lacv.jmagrexs.components.FieldConfigurationByTableColumns;
import com.lacv.jmagrexs.domain.BaseDto;
import com.lacv.jmagrexs.dto.GenericTableColumn;
import com.lacv.jmagrexs.enums.FieldType;
import com.lacv.jmagrexs.enums.HideView;
import com.lacv.jmagrexs.reflection.EntityReflection;
import java.beans.PropertyDescriptor;
import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.util.HtmlUtils;

public class CSVService {
    
    protected static final Logger LOGGER = Logger.getLogger(CSVService.class);
    
    private static final String DEFAULT_SEPARATOR = ";";
    
    private static final FieldConfigurationByAnnotations FCBA= new FieldConfigurationByAnnotations();
    
    private static final FieldConfigurationByTableColumns FCTC= new FieldConfigurationByTableColumns();
    

    public static String generateCSVReport(List<Object> list, Class dtoClass) throws Exception {
        ApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
        ExtViewConfig extViewConfig= (ExtViewConfig) ctx.getBean("extViewConfig");
        StringBuilder report=new StringBuilder("");
        PropertyDescriptor[] propertyDescriptors = EntityReflection.getPropertyDescriptors(dtoClass);
        FCBA.orderPropertyDescriptor(propertyDescriptors, dtoClass, "name");

        HashMap<String, String> titledFieldsMap= FCBA.getTitledFieldsMap(propertyDescriptors, dtoClass);
        HashMap<String,String[]> typeFormFields= FCBA.getTypeFormFields(dtoClass);
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
                                if(typeWrapper.getName().equals("java.util.Date")){
                                    String format= extViewConfig.getDateFormatJava();
                                    if(typeFormFields.containsKey(fieldName) && typeFormFields.get(fieldName)[0].equals(FieldType.DATETIME.name())){
                                        format= extViewConfig.getDatetimeFormatJava();
                                    }
                                    report.append(Formats.dateToString((Date)value, format)).append(DEFAULT_SEPARATOR);
                                }else if(typeWrapper.getName().equals("java.sql.Time")){
                                    report.append(Formats.timeToString((Time)value, extViewConfig.getTimeFormatJava())).append(DEFAULT_SEPARATOR);
                                }else{
                                    report.append(value.toString()).append(DEFAULT_SEPARATOR);
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
                            LOGGER.error("ERROR generateCSVReport1", e);
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
        ApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
        ExtViewConfig extViewConfig= (ExtViewConfig) ctx.getBean("extViewConfig");
        StringBuilder report=new StringBuilder("");
        HashMap<String,String[]> typeFormFields= FCTC.getTypeFormFields(columns);
        
        for (GenericTableColumn column : columns) {
            String type = column.getDataType();
            if(type.equals("java.util.List")==false && type.equals("java.lang.Class")==false){
                report.append(column.getColumnName()).append(DEFAULT_SEPARATOR);
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
                            String format= extViewConfig.getDateFormatJava();
                            if(typeFormFields.containsKey(column.getColumnAlias()) && typeFormFields.get(column.getColumnAlias())[0].equals(FieldType.DATETIME.name())){
                                format= extViewConfig.getDatetimeFormatJava();
                            }
                            report.append(Formats.dateToString((Date)value, format)).append(DEFAULT_SEPARATOR);
                        }else if(propertyType.equals("java.sql.Time")){
                            report.append(Formats.timeToString((Time)value, extViewConfig.getTimeFormatJava())).append(DEFAULT_SEPARATOR);
                        }else{
                            report.append(value.toString()).append(DEFAULT_SEPARATOR);
                        }
                    }catch(Exception e){
                        LOGGER.error("ERROR generateCSVReport2", e);
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
        Set<String> baseDtoTypes= new HashSet<>();
        for(PropertyDescriptor pd: propertyDescriptors){
            if(BaseDto.class.isAssignableFrom(pd.getPropertyType())){
                baseDtoTypes.add(pd.getName());
            }
        }
        String[] records= csvRecords.split("\n");
        String SEPARATOR= DEFAULT_SEPARATOR;
        if(records[0].contains(",") && !records[0].contains(";")){
            SEPARATOR=",";
        }
        String[] columns= records[0].replace("ï»¿","").split(SEPARATOR);
        for(int i=0; i<columns.length; i++){
            if(columns[i].startsWith("\"") && columns[i].endsWith("\"")){
                columns[i]= columns[i].replaceAll("\"", "");
            }
            if(invertedTitledFieldsMap.containsKey(columns[i])){
                columns[i]= invertedTitledFieldsMap.get(columns[i]);
            }
        }
        JSONArray objects= new JSONArray();
        for(int i=1; i<records.length; i++){
            JSONObject object= new JSONObject();
            String[] record= records[i].split(SEPARATOR);
            for(int j=0; j<record.length; j++){
                String fieldName= columns[j];
                String value= record[j];
                if(value.startsWith("\"") && value.endsWith("\"")){
                    value= value.replaceAll("\"", "");
                }
                if(baseDtoTypes.contains(fieldName)){
                    object.put(fieldName, value.split(" - ")[0]);
                }else{
                    object.put(fieldName, value);
                }
            }
            objects.put(object);
        }

        return objects.toString();
    }
    
    public static String csvRecordsToJSON(String csvRecords, List<GenericTableColumn> tableColumns){
        HashMap<String, String> nameColumnsMap= new HashMap<>();
        for (GenericTableColumn column : tableColumns){
            nameColumnsMap.put(column.getColumnName(), column.getColumnAlias());
        }
        
        String[] records= csvRecords.split("\n");
        String SEPARATOR= DEFAULT_SEPARATOR;
        if(records[0].contains(",") && !records[0].contains(";")){
            SEPARATOR=",";
        }
        String[] columns= records[0].split(SEPARATOR);
        for(int i=0; i<columns.length; i++){
            if(nameColumnsMap.containsKey(columns[i])){
                columns[i]= nameColumnsMap.get(columns[i]);
            }
        }
        JSONArray objects= new JSONArray();
        for(int i=1; i<records.length; i++){
            JSONObject object= new JSONObject();
            String[] record= records[i].split(SEPARATOR);
            for(int j=0; j<record.length; j++){
                String fieldName= columns[j];
                String value= record[j];
                if(value.startsWith("\"") && value.endsWith("\"")){
                    value= value.replaceAll("\"", "");
                }
                object.put(fieldName, value);
            }
            objects.put(object);
        }

        return objects.toString();
    }

}
