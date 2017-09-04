/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dot.gcpbasedot.dto;

/**
 *
 * @author grupot
 */
public class GenericTableColumn {
    
    // Database properties
    
    private String tableAlias;
    
    private String columnAlias;
    
    private String dataType;
    
    private String dataTypeDB;
    
    private Integer columnSize;
    
    private String defaultValue;

    private boolean primaryKey;
    
    private boolean autoIncrement;
    
    private boolean unique;
    
    private boolean notNull;
    
    // View properties
    
    private String tableName;
    
    private Boolean fileUpload;
    
    private String columnName;
    
    private String fieldType;
    
    private Integer width;
    
    private Integer columnOrder;
    
    private String options;
    
    
    public GenericTableColumn(){
        columnSize= null;
        defaultValue= null;
        primaryKey= false;
        autoIncrement= false;
        unique= false;
        notNull= false;
    }

    /**
     * @return the tableAlias
     */
    public String getTableAlias() {
        return tableAlias;
    }

    /**
     * @param tableAlias the tableAlias to set
     */
    public void setTableAlias(String tableAlias) {
        this.tableAlias = tableAlias;
    }

    /**
     * @return the columnAlias
     */
    public String getColumnAlias() {
        return columnAlias;
    }

    /**
     * @param columnAlias the columnAlias to set
     */
    public void setColumnAlias(String columnAlias) {
        this.columnAlias = columnAlias;
    }

    /**
     * @return the dataType
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * @param dataType the dataType to set
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    /**
     * @return the dataTypeDB
     */
    public String getDataTypeDB() {
        return dataTypeDB;
    }

    /**
     * @param dataTypeDB the dataTypeDB to set
     */
    public void setDataTypeDB(String dataTypeDB) {
        this.dataTypeDB = dataTypeDB;
    }

    /**
     * @return the columnSize
     */
    public Integer getColumnSize() {
        return columnSize;
    }

    /**
     * @param columnSize the columnSize to set
     */
    public void setColumnSize(Integer columnSize) {
        this.columnSize = columnSize;
    }

    /**
     * @return the defaultValue
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * @param defaultValue the defaultValue to set
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * @return the primaryKey
     */
    public boolean isPrimaryKey() {
        return primaryKey;
    }

    /**
     * @param primaryKey the primaryKey to set
     */
    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    /**
     * @return the autoIncrement
     */
    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    /**
     * @param autoIncrement the autoIncrement to set
     */
    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    /**
     * @return the unique
     */
    public boolean isUnique() {
        return unique;
    }

    /**
     * @param unique the unique to set
     */
    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    /**
     * @return the notNull
     */
    public boolean isNotNull() {
        return notNull;
    }

    /**
     * @param notNull the notNull to set
     */
    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    /**
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @param tableName the tableName to set
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    /**
     * 
     * @return fileUpload
     */
    public Boolean getFileUpload() {
        return fileUpload;
    }

    /**
     * 
     * @param fileUpload 
     */
    public void setFileUpload(Boolean fileUpload) {
        this.fileUpload = fileUpload;
    }

    /**
     * @return the columnName
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * @param columnName the columnName to set
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    /**
     * @return the fieldType
     */
    public String getFieldType() {
        return fieldType;
    }

    /**
     * @param fieldType the fieldType to set
     */
    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    /**
     * @return the width
     */
    public Integer getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(Integer width) {
        this.width = width;
    }

    /**
     * @return the columnOrder
     */
    public Integer getColumnOrder() {
        return columnOrder;
    }

    /**
     * @param columnOrder the columnOrder to set
     */
    public void setColumnOrder(Integer columnOrder) {
        this.columnOrder = columnOrder;
    }

    /**
     * @return the options
     */
    public String getOptions() {
        return options;
    }

    /**
     * @param options the options to set
     */
    public void setOptions(String options) {
        this.options = options;
    }

        
}
