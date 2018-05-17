/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lacv.jmagrexs.modules.security.controllers.rest;


import com.lacv.jmagrexs.annotation.DoProcess;
import com.lacv.jmagrexs.controller.rest.RestProcessController;
import com.lacv.jmagrexs.util.AESEncrypt;
import com.lacv.jmagrexs.modules.common.dtos.BasicResultDto;
import com.lacv.jmagrexs.modules.security.dtos.process.CreatePasswordDto;
import com.lacv.jmagrexs.modules.common.entities.LogProcess;
import com.lacv.jmagrexs.modules.security.entities.User;
import com.lacv.jmagrexs.modules.common.services.LogProcessService;
import com.lacv.jmagrexs.modules.security.services.UserService;
import com.lacv.jmagrexs.modules.security.services.SecurityService;
import com.lacv.jmagrexs.modules.common.constants.SystemConstants;
import com.lacv.jmagrexs.modules.common.services.PropertyService;
import com.lacv.jmagrexs.modules.security.services.RoleService;
import com.lacv.jmagrexs.modules.security.services.UserRoleService;
import com.lacv.jmagrexs.modules.mail.services.MailingService;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author lcastrillo
 */
@Controller
@RequestMapping(value="/rest/processUser")
public class UserProcessRestController extends RestProcessController {
    
    @Autowired
    UserService userService;
    
    @Autowired
    RoleService roleService;
    
    @Autowired
    UserRoleService userRoleService;
    
    @Autowired
    LogProcessService logProcessService;
    
    @Autowired
    SecurityService securityService;
    
    @Autowired
    MailingService mailingService;
    
    @Autowired
    PropertyService propertyService;
    
    AESEncrypt myInstance= AESEncrypt.getDefault(SystemConstants.SECURITY_SALT);
    
    @PostConstruct
    public void init(){
        super.addControlProcess("processUser", LogProcess.class, logProcessService);
    }
    
    @Override
    public String getClientId(){
        User user= securityService.getCurrentUser();
        return user.getUsername();
    }
    
    @DoProcess
    public BasicResultDto createPassword(CreatePasswordDto createPassword){
        BasicResultDto result= new BasicResultDto();
        result.setUsername(createPassword.getUsername());
        
        User user= userService.loadByParameter("username", createPassword.getUsername());
        
        result.setSuccess(false);
        if(user!=null){
            if(createPassword.getPassword().equals(createPassword.getConfirmPassword())){
                user.setPassword(myInstance.encrypt(createPassword.getPassword(), SystemConstants.SECURITY_SEED_PASSW));
                userService.update(user);
                result.setMessage("La contraseña se ha creado correctamente");
                result.setSuccess(true);
            }else{
                result.setMessage("Las contraseñas no coinciden");
            }
        }else{
            result.setMessage("No se encontro el usuario");
        }
        
        return result;
    }
    
}
