/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lacv.jmagrexs.modules.security.controllers.view;

import com.lacv.jmagrexs.modules.security.dtos.UserDto;
import com.lacv.jmagrexs.modules.security.entities.UserRole;
import com.lacv.jmagrexs.modules.security.mappers.UserMapper;
import com.lacv.jmagrexs.modules.security.services.UserService;
import com.lacv.jmagrexs.controller.view.ExtEntityController;
import com.lacv.jmagrexs.dto.MenuItem;
import com.lacv.jmagrexs.dto.ProcessButton;
import com.lacv.jmagrexs.dto.config.EntityConfig;
import com.lacv.jmagrexs.modules.security.dtos.process.CreatePasswordDto;
import com.lacv.jmagrexs.modules.security.services.SecurityService;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author lacastrillov
 */
@Controller
@RequestMapping(value="/vista/user")
public class UserViewController extends ExtEntityController {
    
    @Autowired
    UserService userService;
    
    @Autowired
    UserMapper userMapper;
    
    @Autowired
    SecurityService securityService;
    
    
    @PostConstruct
    public void init(){
        EntityConfig view= new EntityConfig("user", userService, UserDto.class);
        view.setSingularEntityTitle("Usuario");
        view.setPluralEntityTitle("Usuarios");
        view.addChildExtView("userRole", UserRole.class, EntityConfig.TCV_N_TO_N);
        view.setMultipartFormData(true);
        view.setVisibleSeeAllButton(true);
        
        ProcessButton setPasswordButton= new ProcessButton();
        setPasswordButton.setMainProcessRef("processUser");
        setPasswordButton.setProcessName("createPassword");
        setPasswordButton.setProcessTitle("Crear Password");
        setPasswordButton.addSourceByDestinationField("username", "username");
        setPasswordButton.setDtoClass(CreatePasswordDto.class);
        setPasswordButton.setIconUrl("/img/process_icons/password.png");
        view.addProcessButton(setPasswordButton);
        
        super.addControlMapping(view);
        
        MenuItem menuParent= new MenuItem("Sistema", 1);
        MenuItem menuParent1= new MenuItem("Seguridad");
        MenuItem menuItem= new MenuItem("user", "Gestionar Usuarios", 2);
        menuParent1.addSubMenu(menuItem);
        menuParent.addSubMenu(menuParent1);
        menuComponent.addItemMenu(menuParent);
    }
    
    @Override
    public List<MenuItem> configureVisibilityMenu(List<MenuItem> menuData){
        return securityService.configureVisibilityMenu(menuData);
    }
    
}
