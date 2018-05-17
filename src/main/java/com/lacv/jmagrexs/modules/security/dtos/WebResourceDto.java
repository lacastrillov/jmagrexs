/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.modules.security.dtos;

import com.lacv.jmagrexs.annotation.ColumnWidth;
import com.lacv.jmagrexs.annotation.LabelField;
import com.lacv.jmagrexs.annotation.NotNull;
import com.lacv.jmagrexs.annotation.Order;
import com.lacv.jmagrexs.annotation.ReadOnly;
import com.lacv.jmagrexs.annotation.TextField;
import com.lacv.jmagrexs.annotation.TypeFormField;
import com.lacv.jmagrexs.domain.BaseEntity;
import com.lacv.jmagrexs.enums.FieldType;
import java.util.List;

/**
 *
 * @author grupot
 */
@LabelField("name")
public class WebResourceDto implements BaseEntity {

    private static final long serialVersionUID = 1L;
    
    @ColumnWidth(100)
    @Order(1)
    @ReadOnly
    private Integer id;
    
    @Order(2)
    @TextField("Categor&iacute;a")
    private String category;
    
    @Order(3)
    @NotNull
    @TextField("Nombre")
    private String name;
    
    @Order(4)
    @NotNull
    @ColumnWidth(300)
    @TextField("Path")
    private String path;
    
    @Order(5)
    @NotNull
    @TextField("Tipo")
    @TypeFormField(value = FieldType.LIST, data = {"general", "specific"})
    private String type;
    
    @Order(6)
    @TextField("Es p&uacute;blico")
    private Boolean isPublic;
    
    private List<WebresourceAuthorizationDto> webresourceAuthorizationList;
    
    private List<WebresourceRoleDto> webresourceRoleList;

    public WebResourceDto() {
    }

    public WebResourceDto(Integer id) {
        this.id = id;
    }

    public WebResourceDto(Integer id, String name, String pah) {
        this.id = id;
        this.name = name;
        this.path = pah;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }
    
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<WebresourceAuthorizationDto> getWebresourceAuthorizationList() {
        return webresourceAuthorizationList;
    }

    public void setWebresourceAuthorizationList(List<WebresourceAuthorizationDto> webresourceAuthorizationList) {
        this.webresourceAuthorizationList = webresourceAuthorizationList;
    }

    public List<WebresourceRoleDto> getWebresourceRoleList() {
        return webresourceRoleList;
    }

    public void setWebresourceRoleList(List<WebresourceRoleDto> webresourceRoleList) {
        this.webresourceRoleList = webresourceRoleList;
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
        if (!(object instanceof WebResourceDto)) {
            return false;
        }
        WebResourceDto other = (WebResourceDto) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.lacv.marketplatform.entities.WebResourceDto[ id=" + id + " ]";
    }
    
}
