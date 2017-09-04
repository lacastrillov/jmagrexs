package com.dot.gcpbasedot.util;

import com.dot.gcpbasedot.dto.GenericTableColumn;
import com.dot.gcpbasedot.reflection.EntityReflection;
import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

public class ExcelService {
    
    private static final String TEMPLATES = "/excel/";

    public static void generateExcelReport(List<Object> list, OutputStream outputStream, Class entityClass) throws Exception {
        try (InputStream inputStream = ExcelService.class.getResourceAsStream(TEMPLATES + "report.xls")) {
            HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
            HSSFSheet sheet1 = workbook.getSheetAt(0);
            int colIndex = 0;
            int rowIndex = 0;
            
            PropertyDescriptor[] propertyDescriptors = EntityReflection.getPropertyDescriptors(entityClass);
            
            HSSFCellStyle style = workbook.createCellStyle();
            style.setBorderTop((short) 6);
            style.setBorderBottom((short) 1);
            HSSFFont font = workbook.createFont();
            font.setFontHeightInPoints((short) 15);
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            style.setFont(font);                 
            
            HSSFRow row = sheet1.createRow(rowIndex);
            
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                String type = propertyDescriptor.getPropertyType().getName();
                if(type.equals("java.util.List")==false && type.equals("java.lang.Class")==false){
                    Cell cell = row.createCell(colIndex++);
                    cell.setCellValue(new HSSFRichTextString(propertyDescriptor.getName()));
                    cell.setCellStyle(style);
                }
            }
            rowIndex++;
            
            for (Object object : list) {
                colIndex = 0;
                row = sheet1.createRow(rowIndex);
                
                BeanWrapperImpl sourceWrapper = new BeanWrapperImpl(object);

                for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                    Object value = sourceWrapper.getPropertyValue(propertyDescriptor.getName());
                    Class<?> typeWrapper = propertyDescriptor.getPropertyType();
                    if(typeWrapper.getName().equals("java.util.List")==false && typeWrapper.getName().equals("java.lang.Class")==false){
                        if (value != null) {
                            try{
                                Object parseValue = typeWrapper.cast(value);
                                if (parseValue != null) {
                                    if(typeWrapper.getName().equals("java.util.Date")){
                                        DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                                        row.createCell(colIndex++).setCellValue(new HSSFRichTextString(format.format(parseValue)));
                                    }else{
                                        row.createCell(colIndex++).setCellValue(new HSSFRichTextString(parseValue.toString()));
                                    }
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
