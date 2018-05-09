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
public class UserByToken {
    
    private String username;
    
    private String password;
    
    private long creation;
    
    private long expiration;
    
    
    public UserByToken(){
        
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the creation
     */
    public long getCreation() {
        return creation;
    }

    /**
     * @param creation the creation to set
     */
    public void setCreation(long creation) {
        this.creation = creation;
    }

    /**
     * @return the expiration
     */
    public long getExpiration() {
        return expiration;
    }

    /**
     * @param expiration the expiration to set
     */
    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }
    
}
