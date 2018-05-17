/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.modules.security.dtos;

import com.lacv.jmagrexs.domain.BaseEntity;

/**
 *
 * @author grupot
 */
public class RoleAuthorizationDto implements BaseEntity {

    private static final long serialVersionUID = 1L;
    
    private Integer id;
    
    private RoleDto role;
    
    private AuthorizationDto authorization;

    public RoleAuthorizationDto() {
    }

    public RoleAuthorizationDto(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Object id) {
        this.id = (Integer) id;
    }

    public RoleDto getRole() {
        return role;
    }

    public void setRole(RoleDto role) {
        this.role = role;
    }

    public AuthorizationDto getAuthorization() {
        return authorization;
    }

    public void setAuthorization(AuthorizationDto authorization) {
        this.authorization = authorization;
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
        if (!(object instanceof RoleAuthorizationDto)) {
            return false;
        }
        RoleAuthorizationDto other = (RoleAuthorizationDto) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.lacv.marketplatform.entities.RoleAuthorizationDto[ id=" + id + " ]";
    }
    
}
