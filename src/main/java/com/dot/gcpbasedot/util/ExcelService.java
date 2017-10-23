package com.dot.gcpbasedot.util;

import com.dot.gcpbasedot.annotation.LabelField;
import com.dot.gcpbasedot.components.FieldConfigurationByAnnotations;
import com.dot.gcpbasedot.dto.GenericTableColumn;
import com.dot.gcpbasedot.enums.HideView;
import com.dot.gcpbasedot.reflection.EntityReflection;
import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;

import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.web.util.HtmlUtils;

public class ExcelService {
    
    private static final String TEMPLATES = "/excel/";
    
    private static final FieldConfigurationByAnnotations FCBA= new FieldConfigurationByAnnotations();
    

    public static void generateExcelReport(List<Object> list, OutputStream outputStream, Class dtoClass) throws Exception {
        try (InputStream inputStream = ExcelService.class.getResourceAsStream(TEMPLATES + "report.xls")) {
            HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
            HSSFSheet sheet1 = workbook.getSheetAt(0);
            int colIndex = 0;
            int rowIndex = 0;
            
            PropertyDescriptor[] propertyDescriptors = EntityReflection.getPropertyDescriptors(dtoClass);
            FCBA.orderPropertyDescriptor(propertyDescriptors, dtoClass, "name");
            
            HashMap<String, String> titledFieldsMap= FCBA.getTitledFieldsMap(propertyDescriptors, dtoClass);
            HashSet<String> hideFields= FCBA.getHideFields(dtoClass);
            
            HSSFCellStyle style = workbook.createCellStyle();
            style.setBorderTop((short) 6);
            style.setBorderBottom((short) 1);
            HSSFFont font = workbook.createFont();
            font.setFontHeightInPoints((short) 15);
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            style.setFont(font);                 
            
            HSSFRow row = sheet1.createRow(rowIndex);
            
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                String fieldName= propertyDescriptor.getName();
                String type = propertyDescriptor.getPropertyType().getName();
                if(type.equals("java.util.List")==false && type.equals("java.lang.Class")==false && !hideFields.contains(fieldName + HideView.GRID.name())){
                    Cell cell = row.createCell(colIndex++);
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
                                    Object parseValue = typeWrapper.cast(value);
                                    if(typeWrapper.getName().equals("java.util.Date")){
                                        row.createCell(colIndex++).setCellValue(new HSSFRichTextString(Formats.dateToString((Date)parseValue, "dd-MM-yyyy")));
                                    }else{
                                        row.createCell(colIndex++).setCellValue(new HSSFRichTextString(parseValue.toString()));
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
        try (InputStream inputStream = ExcelService.class.getResourceAsStream(TEMPLATES + "report.xls")) {
            HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
            HSSFSheet sheet1 = workbook.getSheetAt(0);
            int colIndex = 0;
            int rowIndex = 0;
            
            HSSFCellStyle style = workbook.createCellStyle();
            style.setBorderTop((short) 6);
            style.setBorderBottom((short) 1);
            HSSFFont font = workbook.createFont();
            font.setFontHeightInPoints((short) 15);
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            style.setFont(font);                 
            
            HSSFRow row = sheet1.createRow(rowIndex);
            
            for (GenericTableColumn column : columns) {
                String type = column.getDataType();
                if(type.equals("java.util.List")==false && type.equals("java.lang.Class")==false){
                    Cell cell = row.createCell(colIndex++);
                    cell.setCellValue(new HSSFRichTextString(column.getColumnAlias()));
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
                                DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                                row.createCell(colIndex++).setCellValue(new HSSFRichTextString(format.format(value)));
                            }else{
                                row.createCell(colIndex++).setCellValue(new HSSFRichTextString(value.toString()));
                            }
                        }catch(Exception e){
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

}
