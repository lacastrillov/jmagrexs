/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.modules.fileexplorer.dtos;

import com.lacv.jmagrexs.annotation.ColumnWidth;
import com.lacv.jmagrexs.annotation.HideField;
import com.lacv.jmagrexs.annotation.LabelField;
import com.lacv.jmagrexs.annotation.Order;
import com.lacv.jmagrexs.annotation.ReadOnly;
import com.lacv.jmagrexs.annotation.TextField;
import com.lacv.jmagrexs.annotation.TypeFormField;
import com.lacv.jmagrexs.domain.BaseEntity;
import com.lacv.jmagrexs.enums.FieldType;
import com.lacv.jmagrexs.enums.HideView;
import java.util.Date;
import java.util.List;

/**
 *
 * @author grupot
 */
@LabelField("name")
public class WebFileDto implements BaseEntity {

    private static final long serialVersionUID = 1L;
    
    @Order(1)
    @ColumnWidth(100)
    @ReadOnly
    private Long id;
    
    @Order(2)
    @TextField("Nombre")
    private String name;
    
    @Order(3)
    @TextField("Tipo")
    @ReadOnly
    private String type;
    
    @Order(4)
    @TypeFormField(FieldType.MULTI_FILE_TYPE)
    @TextField("Ubicaci&oacute;n")
    @ReadOnly
    private String location;
    
    @Order(5)
    @TextField("Path")
    @ReadOnly
    private String path;
    
    @Order(6)
    @TextField("Tama&ntilde;o")
    @ReadOnly
    private Integer size;
    
    @Order(7)
    @TextField("Icono")
    @ReadOnly
    private String icon;
    
    @Order(8)
    @TextField("Fecha creaci&oacute;n")
    @ReadOnly
    private Date creationDate;
    
    @Order(9)
    @TextField("Fecha modificaci&oacute;n")
    @ReadOnly
    private Date modificationDate;
    
    @Order(10)
    @TextField("Acceso")
    private Integer access;
    
    @Order(11)
    @TextField("Autor")
    private String author;
    
    @TextField("Archivo padre")
    @ReadOnly
    @HideField({HideView.FILTER, HideView.GRID, HideView.FORM})
    private WebFileDto webFile;
    
    private List<WebFileDto> webFileList;
    

    public WebFileDto() {
    }

    public WebFileDto(Long id) {
        this.id = id;
    }

    public WebFileDto(Long id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
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

    public Integer getAccess() {
        return access;
    }

    public void setAccess(Integer access) {
        this.access = access;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<WebFileDto> getWebFileList() {
        return webFileList;
    }

    public void setWebFileList(List<WebFileDto> webFileList) {
        this.webFileList = webFileList;
    }

    public WebFileDto getWebFile() {
        return webFile;
    }

    public void setWebFile(WebFileDto webFile) {
        this.webFile = webFile;
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
        if (!(object instanceof WebFileDto)) {
            return false;
        }
        WebFileDto other = (WebFileDto) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.lacv.marketplatform.entities.WebFileDto[ id=" + id + " ]";
    }
    
}
