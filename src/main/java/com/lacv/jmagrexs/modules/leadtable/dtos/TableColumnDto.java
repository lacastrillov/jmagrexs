/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.modules.leadtable.dtos;

import com.lacv.jmagrexs.annotation.ColumnWidth;
import com.lacv.jmagrexs.annotation.DefaultValue;
import com.lacv.jmagrexs.annotation.LabelField;
import com.lacv.jmagrexs.annotation.NotNull;
import com.lacv.jmagrexs.annotation.Order;
import com.lacv.jmagrexs.annotation.ReadOnly;
import com.lacv.jmagrexs.annotation.Size;
import com.lacv.jmagrexs.annotation.TextField;
import com.lacv.jmagrexs.annotation.TypeFormField;
import com.lacv.jmagrexs.domain.BaseEntity;
import com.lacv.jmagrexs.enums.FieldType;

/**
 *
 * @author grupot
 */
@LabelField("name")
public class TableColumnDto implements BaseEntity {

    private static final long serialVersionUID = 1L;
    
    @Order(1)
    @ColumnWidth(100)
    @ReadOnly
    private Integer id;
    
    @Order(2)
    @Size(max=100)
    @ColumnWidth(150)
    @NotNull
    @TextField("Nombre")
    private String name;
    
    @Order(3)
    @Size(max=100)
    @NotNull
    @ColumnWidth(150)
    @TextField("Alias")
    private String columnAlias;
    
    @Order(4)
    @Size(max=45)
    @NotNull
    @ColumnWidth(150)
    @TypeFormField(value = FieldType.LIST, data = {"java.lang.String", "char", "int", "long", "double", "float", "boolean", "java.util.Date", "java.sql.Time"})
    @TextField("Tipo Dato")
    private String dataType;
    
    @Order(5)
    @Size(max=45)
    @ColumnWidth(170)
    @TypeFormField(value = FieldType.LIST, data = {
        "EMAIL", "PASSWORD", "DATETIME", "TEXT_AREA", "LIST", "MULTI_SELECT", "URL", "HTML_EDITOR", "FILE_UPLOAD", "IMAGE_FILE_UPLOAD",
        "VIDEO_YOUTUBE", "VIDEO_FILE_UPLOAD", "AUDIO_FILE_UPLOAD", "GOOGLE_MAP", "MULTI_FILE_TYPE"
    })
    @TextField("Tipo Campo")
    private String fieldType;
    
    @Order(6)
    @ColumnWidth(80)
    @DefaultValue("45")
    @TextField("Longitud")
    private Integer columnSize;
    
    @Order(7)
    @ColumnWidth(80)
    @TextField("No nulo")
    private Boolean notNull;
    
    @Order(8)
    @ColumnWidth(80)
    @DefaultValue("200")
    @TextField("Ancho")
    private Integer width;
    
    @Order(9)
    @ColumnWidth(80)
    @DefaultValue("1")
    @TextField("Orden")
    private Integer columnOrder;
    
    @Order(10)
    @ColumnWidth(150)
    @TextField("Valor por defecto")
    private String defaultValue;
    
    @Order(11)
    @TextField("Opciones")
    private String options;
    
    @Order(12)
    @NotNull
    @TextField("Tabla Lead")
    private LeadTableDto leadTable;
    

    public TableColumnDto() {
    }

    public TableColumnDto(Integer id) {
        this.id = id;
    }

    public TableColumnDto(Integer id, String name, String columnAlias, String dataType) {
        this.id = id;
        this.name = name;
        this.columnAlias = columnAlias;
        this.dataType = dataType;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Object id) {
        this.id = (Integer) id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColumnAlias() {
        return columnAlias;
    }

    public void setColumnAlias(String columnAlias) {
        this.columnAlias = columnAlias;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public Integer getColumnSize() {
        return columnSize;
    }

    public void setColumnSize(Integer columnSize) {
        this.columnSize = columnSize;
    }
    
    public Boolean getNotNull() {
        return notNull;
    }

    public void setNotNull(Boolean notNull) {
        this.notNull = notNull;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getColumnOrder() {
        return columnOrder;
    }

    public void setColumnOrder(Integer columnOrder) {
        this.columnOrder = columnOrder;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public LeadTableDto getLeadTable() {
        return leadTable;
    }

    public void setLeadTable(LeadTableDto leadTable) {
        this.leadTable = leadTable;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TableColumnDto)) {
            return false;
        }
        TableColumnDto other = (TableColumnDto) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.lacv.marketplatform.entities.TableColumnDto[ id=" + id + " ]";
    }
    
}
