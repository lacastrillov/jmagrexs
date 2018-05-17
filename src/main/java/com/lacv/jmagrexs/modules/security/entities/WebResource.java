/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.modules.security.entities;

import com.lacv.jmagrexs.domain.BaseEntity;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author grupot
 */
@Entity
@Table(name = "web_resource")
@NamedQueries({
    @NamedQuery(name = "WebResource.findAll", query = "SELECT w FROM WebResource w")})
public class WebResource implements BaseEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 500)
    @Column(name = "path")
    private String path;
    @Size(max = 45)
    @Column(name = "type")
    private String type;
    @Column(name = "is_public")
    private Boolean isPublic;
    @Size(max = 100)
    @Column(name = "category")
    private String category;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "webResource")
    private List<WebresourceAuthorization> webresourceAuthorizationList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "webResource")
    private List<WebresourceRole> webresourceRoleList;

    public WebResource() {
    }

    public WebResource(Integer id) {
        this.id = id;
    }

    public WebResource(Integer id, String name, String pah) {
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

    public List<WebresourceAuthorization> getWebresourceAuthorizationList() {
        return webresourceAuthorizationList;
    }

    public void setWebresourceAuthorizationList(List<WebresourceAuthorization> webresourceAuthorizationList) {
        this.webresourceAuthorizationList = webresourceAuthorizationList;
    }

    public List<WebresourceRole> getWebresourceRoleList() {
        return webresourceRoleList;
    }

    public void setWebresourceRoleList(List<WebresourceRole> webresourceRoleList) {
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
        if (!(object instanceof WebResource)) {
            return false;
        }
        WebResource other = (WebResource) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.lacv.marketplatform.entities.WebResource[ id=" + id + " ]";
    }
    
}
