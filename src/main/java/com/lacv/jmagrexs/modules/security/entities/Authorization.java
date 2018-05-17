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
@Table(name = "authorization")
@NamedQueries({
    @NamedQuery(name = "Authorization.findAll", query = "SELECT a FROM Authorization a")})
public class Authorization implements BaseEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "name")
    private String name;
    @Size(max = 255)
    @Column(name = "description")
    private String description;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "authorization")
    private List<RoleAuthorization> roleAuthorizationList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "authorization")
    private List<WebresourceAuthorization> webresourceAuthorizationList;

    public Authorization() {
    }

    public Authorization(Integer id) {
        this.id = id;
    }

    public Authorization(Integer id, String name) {
        this.id = id;
        this.name = name;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<RoleAuthorization> getRoleAuthorizationList() {
        return roleAuthorizationList;
    }

    public void setRoleAuthorizationList(List<RoleAuthorization> roleAuthorizationList) {
        this.roleAuthorizationList = roleAuthorizationList;
    }

    public List<WebresourceAuthorization> getWebresourceAuthorizationList() {
        return webresourceAuthorizationList;
    }

    public void setWebresourceAuthorizationList(List<WebresourceAuthorization> webresourceAuthorizationList) {
        this.webresourceAuthorizationList = webresourceAuthorizationList;
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
        if (!(object instanceof Authorization)) {
            return false;
        }
        Authorization other = (Authorization) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.lacv.marketplatform.entities.Authorization[ id=" + id + " ]";
    }
    
}
