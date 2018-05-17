/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.modules.leadtable.dtos;

import com.lacv.jmagrexs.annotation.ColumnWidth;
import com.lacv.jmagrexs.annotation.HideField;
import com.lacv.jmagrexs.annotation.LabelField;
import com.lacv.jmagrexs.annotation.NotNull;
import com.lacv.jmagrexs.annotation.Order;
import com.lacv.jmagrexs.annotation.ReadOnly;
import com.lacv.jmagrexs.annotation.Size;
import com.lacv.jmagrexs.annotation.TextField;
import com.lacv.jmagrexs.annotation.TypeFormField;
import com.lacv.jmagrexs.domain.BaseEntity;
import com.lacv.jmagrexs.enums.FieldType;
import com.lacv.jmagrexs.enums.HideView;
import java.util.List;

/**
 *
 * @author grupot
 */
@LabelField("name")
public class LeadTableDto implements BaseEntity {

    private static final long serialVersionUID = 1L;
    
    @ColumnWidth(100)
    @Order(1)
    @ReadOnly
    private Integer id;
    
    @Size(max=100)
    @Order(2)
    @NotNull
    @TextField("Nombre")
    private String name;
    
    @Size(max=100)
    @Order(3)
    @NotNull
    @TextField("Alias")
    private String tableAlias;
    
    @Size(max=200)
    @Order(4)
    @TextField("Descripci&oacute;n")
    @TypeFormField(value= FieldType.TEXT_AREA)
    private String description;
    
    @Size(max=45)
    @Order(5)
    @TypeFormField(value = FieldType.LIST, data = {"Active", "Inactive"})
    @TextField("Estado")
    private String status;
    
    @Order(5)
    @TextField("Subir Archivos")
    private Boolean fileUpload;
    
    @TextField("Ingresar")
    @ReadOnly
    @HideField({HideView.FILTER, HideView.FORM})
    private String link;
    
    private List<TableColumnDto> tableColumnList;

    public LeadTableDto() {
    }

    public LeadTableDto(Integer id) {
        this.id = id;
    }

    public LeadTableDto(Integer id, String name, String tableAlias) {
        this.id = id;
        this.name = name;
        this.tableAlias = tableAlias;
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

    public String getTableAlias() {
        return tableAlias;
    }

    public void setTableAlias(String tableAlias) {
        this.tableAlias = tableAlias;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public Boolean getFileUpload() {
        return fileUpload;
    }

    public void setFileUpload(Boolean fileUpload) {
        this.fileUpload = fileUpload;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public List<TableColumnDto> getTableColumnList() {
        return tableColumnList;
    }

    public void setTableColumnList(List<TableColumnDto> tableColumnList) {
        this.tableColumnList = tableColumnList;
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
        if (!(object instanceof LeadTableDto)) {
            return false;
        }
        LeadTableDto other = (LeadTableDto) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.lacv.marketplatform.entities.LeadTableDto[ id=" + id + " ]";
    }
    
}
