/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.modules.security.dtos.security;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author lacastrillov
 */
public class WebResourceAuthorities {
    
    private final String webResource;
    
    private boolean isPublic;
    
    private final Set<String> roles;
            
    private final Set<String> authorizations;
    
    
    public WebResourceAuthorities(String webResource){
        this.webResource= webResource;
        roles= new HashSet<>();
        authorizations= new HashSet<>();
    }

    public String getWebResource() {
        return webResource;
    }

    public boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    /**
     * @return the roles
     */
    public Set<String> getRoles() {
        return roles;
    }

    /**
     * @param role
     */
    public void addRole(String role) {
        roles.add(role);
    }

    /**
     * @return the authorizations
     */
    public Set<String> getAuthorizations() {
        return authorizations;
    }

    /**
     * @param authorization
     */
    public void addAuthorization(String authorization) {
        authorizations.add(authorization);
    }
    
}
