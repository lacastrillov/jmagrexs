/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.modules.entityexplorer.dtos;

import com.lacv.jmagrexs.annotation.ColumnWidth;
import com.lacv.jmagrexs.annotation.HideField;
import com.lacv.jmagrexs.annotation.Order;
import com.lacv.jmagrexs.annotation.ReadOnly;
import com.lacv.jmagrexs.annotation.Size;
import com.lacv.jmagrexs.annotation.TextField;
import com.lacv.jmagrexs.annotation.TypeFormField;
import com.lacv.jmagrexs.domain.BaseEntity;
import com.lacv.jmagrexs.enums.FieldType;
import com.lacv.jmagrexs.enums.HideView;
import java.util.Date;
import java.util.List;

/**
 *
 * @author lacastrillov
 */
public class WebEntityDto implements BaseEntity {

    private static final long serialVersionUID = 1L;
    
    @Order(1)
    @ReadOnly
    @ColumnWidth(100)
    private Long id;
    
    @Order(2)
    @Size(max = 255)
    @TextField("Nombre")
    private String name;
    
    @Order(3)
    @ReadOnly
    @Size(max = 255)
    @TextField("Ref Entidad")
    @HideField({HideView.FILTER, HideView.FORM})
    private String entityRef;
    
    @Order(4)
    @ReadOnly
    @Size(max = 100)
    @TextField("Nombre Entidad")
    @TypeFormField(value = FieldType.LIST, data = {"Categoria", "Comercio", "Producto", "Proveedor", "Usuario"})
    private String entityName;
    
    @Order(5)
    @Size(max = 100)
    @TextField("Id Entidad")
    private String entityId;
    
    @Order(6)
    @TextField("Orden Entidad")
    private Integer entityOrder;
    
    @Order(7)
    @TextField("Path")
    @ReadOnly
    @HideField({HideView.FILTER})
    private String path;
    
    @Order(8)
    @TextField("Ubicaci&oacute;n")
    @ReadOnly
    @HideField({HideView.FILTER})
    private String location;
    
    @Order(9)
    @ReadOnly
    @TextField("Fecha creaci&oacute;n")
    private Date creationDate;
    
    @Order(10)
    @TextField("Fecha modificaci&oacute;n")
    @ReadOnly
    private Date modificationDate;
    
    @Order(11)
    @ReadOnly
    @Size(max = 255)
    @TextField("Icono")
    @HideField({HideView.FILTER})
    private String icon;
    
    @Order(12)
    @Size(max = 45)
    @TextField("Estado")
    @TypeFormField(value = FieldType.MULTI_SELECT, data = {"Active", "Inactive", "Locked", "Deleted"})
    private String status;
    
    @Order(13)
    @Size(max = 255)
    @TextField("Autor")
    private String author;
    
    @TextField("Archivo padre")
    @ReadOnly
    @HideField({HideView.FILTER, HideView.GRID, HideView.FORM})
    private WebEntityDto webEntity;

    private List<WebEntityDto> webEntityList;
    
    
    public WebEntityDto() {
    }

    public WebEntityDto(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Object id) {
        this.id = (Long) id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEntityRef() {
        return entityRef;
    }

    public void setEntityRef(String entityRef) {
        this.entityRef = entityRef;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public Integer getEntityOrder() {
        return entityOrder;
    }

    public void setEntityOrder(Integer entityOrder) {
        this.entityOrder = entityOrder;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<WebEntityDto> getWebEntityList() {
        return webEntityList;
    }

    public void setWebEntityList(List<WebEntityDto> webEntityList) {
        this.webEntityList = webEntityList;
    }

    public WebEntityDto getWebEntity() {
        return webEntity;
    }

    public void setWebEntity(WebEntityDto webEntity) {
        this.webEntity = webEntity;
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
        if (!(object instanceof WebEntityDto)) {
            return false;
        }
        WebEntityDto other = (WebEntityDto) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.lacv.system.model.entities.WebEntity[ id=" + id + " ]";
    }
    
}
