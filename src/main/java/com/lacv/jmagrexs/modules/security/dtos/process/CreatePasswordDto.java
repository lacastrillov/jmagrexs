/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.modules.security.dtos.process;

import com.lacv.jmagrexs.annotation.HideField;
import com.lacv.jmagrexs.annotation.Order;
import com.lacv.jmagrexs.annotation.ReadOnly;
import com.lacv.jmagrexs.annotation.Size;
import com.lacv.jmagrexs.annotation.TextField;
import com.lacv.jmagrexs.annotation.TypeFormField;
import com.lacv.jmagrexs.enums.FieldType;
import com.lacv.jmagrexs.enums.HideView;

/**
 *
 * @author grupot
 */
public class CreatePasswordDto {
    
    @Size(max=100)
    @Order(1)
    @ReadOnly
    private String username;
    
    @Size(max=60)
    @TypeFormField(FieldType.PASSWORD)
    @HideField(HideView.LOG)
    @Order(2)
    private String password;
    
    @Order(3)
    @Size(max=60)
    @TypeFormField(FieldType.PASSWORD)
    @HideField(HideView.LOG)
    @TextField("Confirm Password")
    private String confirmPassword;

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
     * @return the confirmPassword
     */
    public String getConfirmPassword() {
        return confirmPassword;
    }

    /**
     * @param confirmPassword the confirmPassword to set
     */
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    
}
