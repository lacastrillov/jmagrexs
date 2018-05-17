package com.lacv.jmagrexs.util;

import com.lacv.jmagrexs.annotation.LabelField;
import com.lacv.jmagrexs.components.ExtViewConfig;
import com.lacv.jmagrexs.components.FieldConfigurationByAnnotations;
import com.lacv.jmagrexs.components.FieldConfigurationByTableColumns;
import com.lacv.jmagrexs.domain.BaseEntity;
import com.lacv.jmagrexs.dto.GenericTableColumn;
import com.lacv.jmagrexs.enums.FieldType;
import com.lacv.jmagrexs.enums.HideView;
import com.lacv.jmagrexs.reflection.EntityReflection;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.util.HtmlUtils;

public class ExcelService {
    
    protected static final Logger LOGGER = Logger.getLogger(ExcelService.class);
    
    private static final String TEMPLATES = "/excel/";
    
    private static final FieldConfigurationByAnnotations FCBA= new FieldConfigurationByAnnotations();
    
    private static final FieldConfigurationByTableColumns FCTC= new FieldConfigurationByTableColumns();
    

    public static void generateExcelReport(List<Object> list, OutputStream outputStream, Class dtoClass) throws Exception {
        ApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
        ExtViewConfig extViewConfig= (ExtViewConfig) ctx.getBean("extViewConfig");
        try (InputStream inputStream = ExcelService.class.getResourceAsStream(TEMPLATES + "report.xls")) {
            HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
            HSSFSheet sheet1 = workbook.getSheetAt(0);
            int colIndex = 0;
            int rowIndex = 0;
            
            PropertyDescriptor[] propertyDescriptors = EntityReflection.getPropertyDescriptors(dtoClass);
            FCBA.orderPropertyDescriptor(propertyDescriptors, dtoClass, "name");
            
            HashMap<String, String> titledFieldsMap= FCBA.getTitledFieldsMap(propertyDescriptors, dtoClass);
            HashMap<String,String[]> typeFormFields= FCBA.getTypeFormFields(dtoClass);
            HashSet<String> hideFields= FCBA.getHideFields(dtoClass);
            
            HSSFCellStyle style = workbook.createCellStyle();
            style.setBorderTop((short) 6);
            style.setBorderBottom((short) 1);
            HSSFFont font = workbook.createFont();
            font.setFontHeightInPoints((short) 13);
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            style.setFont(font);                 
            
            HSSFRow row = sheet1.createRow(rowIndex);
            
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                String fieldName= propertyDescriptor.getName();
                String type = propertyDescriptor.getPropertyType().getName();
                if(type.equals("java.util.List")==false && type.equals("java.lang.Class")==false && !hideFields.contains(fieldName + HideView.GRID.name())){
                    HSSFCell cell = row.createCell(colIndex++);
                    cell.setCellValue(new HSSFRichTextString(HtmlUtils.htmlUnescape(titledFieldsMap.get(fieldName))));
                    cell.setCellStyle(style);
                }
            }
            rowIndex++;
            
            for (Object object : list) {
                colIndex = 0;
                row = sheet1.createRow(rowIndex);
                
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
                                        row.createCell(colIndex++).setCellValue(new HSSFRichTextString(Formats.dateToString((Date)value, format)));
                                    }else if(typeWrapper.getName().equals("java.sql.Time")){
                                        row.createCell(colIndex++).setCellValue(new HSSFRichTextString(Formats.timeToString((Time)value, extViewConfig.getTimeFormatJava())));
                                    }else{
                                        row.createCell(colIndex++).setCellValue(new HSSFRichTextString(value.toString()));
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
                                    row.createCell(colIndex++).setCellValue(new HSSFRichTextString(textValue));
                                }
                            }catch(Exception e){
                                LOGGER.error("ERROR generateExcelReport1", e);
                                row.createCell(colIndex++).setCellValue(new HSSFRichTextString(""));
                            }
                        }else{
                            row.createCell(colIndex++).setCellValue(new HSSFRichTextString(""));
                        }
                    }
                }
                rowIndex++;
            }
            workbook.write(outputStream);
        }
    }
    
    public static void generateExcelReport(List<Map<String, Object>> list, OutputStream outputStream, List<GenericTableColumn> columns) throws Exception {
        ApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
        ExtViewConfig extViewConfig= (ExtViewConfig) ctx.getBean("extViewConfig");
        try (InputStream inputStream = ExcelService.class.getResourceAsStream(TEMPLATES + "report.xls")) {
            HashMap<String,String[]> typeFormFields= FCTC.getTypeFormFields(columns);
            HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
            HSSFSheet sheet1 = workbook.getSheetAt(0);
            int colIndex = 0;
            int rowIndex = 0;
            
            HSSFCellStyle style = workbook.createCellStyle();
            style.setBorderTop((short) 6);
            style.setBorderBottom((short) 1);
            HSSFFont font = workbook.createFont();
            font.setFontHeightInPoints((short) 13);
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            style.setFont(font);                 
            
            HSSFRow row = sheet1.createRow(rowIndex);
            for (GenericTableColumn column : columns) {
                String type = column.getDataType();
                if(type.equals("java.util.List")==false && type.equals("java.lang.Class")==false){
                    HSSFCell cell = row.createCell(colIndex++);
                    cell.setCellValue(new HSSFRichTextString(column.getColumnName()));
                    cell.setCellStyle(style);
                }
            }
            rowIndex++;
            
            for (Map<String, Object> object : list) {
                colIndex = 0;
                row = sheet1.createRow(rowIndex);

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
                                row.createCell(colIndex++).setCellValue(new HSSFRichTextString(Formats.dateToString((Date)value, format)));
                            }else if(propertyType.equals("java.sql.Time")){
                                row.createCell(colIndex++).setCellValue(new HSSFRichTextString(Formats.timeToString((Time)value, extViewConfig.getTimeFormatJava())));
                            }else{
                                row.createCell(colIndex++).setCellValue(new HSSFRichTextString(value.toString()));
                            }
                        }catch(Exception e){
                            LOGGER.error("ERROR generateExcelReport2", e);
                            row.createCell(colIndex++).setCellValue(new HSSFRichTextString(""));
                        }
                    }else{
                        row.createCell(colIndex++).setCellValue(new HSSFRichTextString(""));
                    }
                }
                rowIndex++;
            }
            workbook.write(outputStream);
        }
    }
    
    public static String xlsTableToJSON(InputStream is, Class dtoClass) throws IOException {
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
        HSSFWorkbook workbook = new HSSFWorkbook(is);
        HSSFSheet sheet1 = workbook.getSheetAt(0);
        DataFormatter formatter = new DataFormatter();
        int numberOfRows = sheet1.getPhysicalNumberOfRows();
        
        HSSFRow row0 = sheet1.getRow(0);
        int numberOfColumns = (row0 != null)?row0.getPhysicalNumberOfCells():0;
        
        String[] columns= new String[numberOfColumns];
        for(int i = 0; i < numberOfColumns; i++) {
            HSSFCell cell= row0.getCell(i);
            columns[i]= cell.getStringCellValue();
        }
        for(int i=0; i<columns.length; i++){
            if(invertedTitledFieldsMap.containsKey(columns[i])){
                columns[i]= invertedTitledFieldsMap.get(columns[i]);
            }
        }
        JSONArray objects= new JSONArray();
        for(int i = 1; i < numberOfRows; i++) {
            HSSFRow row = sheet1.getRow(i);
            JSONObject object= new JSONObject();
            if(row != null) {
                for(int j = 0; j < numberOfColumns; j++) {
                    HSSFCell cell = row.getCell(j);
                    String fieldName= columns[j];
                    String value= (cell!=null)?formatter.formatCellValue(cell):"";
                    if(baseEntityTypes.contains(fieldName)){
                        object.put(fieldName, value.split(" - ")[0]);
                    }else{
                        object.put(fieldName, value);
                    }
                }
                objects.put(object);
            }
        }

        return objects.toString();
    }
    
    public static String xlsTableToJSON(InputStream is, List<GenericTableColumn> tableColumns) throws IOException{
        HashMap<String, String> nameColumnsMap= new HashMap<>();
        for (GenericTableColumn column : tableColumns){
            nameColumnsMap.put(column.getColumnName(), column.getColumnAlias());
        }
        
        HSSFWorkbook workbook = new HSSFWorkbook(is);
        HSSFSheet sheet1 = workbook.getSheetAt(0);
        DataFormatter formatter = new DataFormatter();
        int numberOfRows = sheet1.getPhysicalNumberOfRows();
        
        HSSFRow row0 = sheet1.getRow(0);
        int numberOfColumns = (row0 != null)?row0.getPhysicalNumberOfCells():0;
        
        String[] columns= new String[numberOfColumns];
        for(int i = 0; i < numberOfColumns; i++) {
            HSSFCell cell= row0.getCell(i);
            columns[i]= cell.getStringCellValue();
        }
        for(int i=0; i<columns.length; i++){
            if(nameColumnsMap.containsKey(columns[i])){
                columns[i]= nameColumnsMap.get(columns[i]);
            }
        }
        JSONArray objects= new JSONArray();
        for(int i = 1; i < numberOfRows; i++) {
            HSSFRow row = sheet1.getRow(i);
            JSONObject object= new JSONObject();
            if(row != null) {
                for(int j = 0; j < numberOfColumns; j++) {
                    HSSFCell cell = row.getCell(j);
                    String fieldName= columns[j];
                    String value= (cell!=null)?formatter.formatCellValue(cell):"";
                    object.put(fieldName, value);
                }
                objects.put(object);
            }
        }

        return objects.toString();
    }

}
