/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lacv.jmagrexs.modules.mail.controllers.view;

import com.lacv.jmagrexs.modules.mail.dtos.MailTemplateDto;
import com.lacv.jmagrexs.modules.mail.mappers.MailTemplateMapper;
import com.lacv.jmagrexs.modules.mail.services.MailTemplateService;
import com.lacv.jmagrexs.controller.view.ExtEntityController;
import com.lacv.jmagrexs.dto.MenuItem;
import com.lacv.jmagrexs.dto.config.EntityConfig;
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
@RequestMapping(value="/vista/mailTemplate")
public class MailTemplateViewController extends ExtEntityController {
    
    @Autowired
    MailTemplateService mailTemplateService;
    
    @Autowired
    MailTemplateMapper mailTemplateMapper;
    
    @Autowired
    SecurityService securityService;
    
    
    @PostConstruct
    public void init(){
        EntityConfig view= new EntityConfig("mailTemplate", mailTemplateService, MailTemplateDto.class);
        view.setSingularEntityTitle("Plantilla de Correo");
        view.setPluralEntityTitle("Plantillas de Correo");
        super.addControlMapping(view);
        
        MenuItem menuParent= new MenuItem("Sistema");
        MenuItem menuParent1= new MenuItem("Correos", 5);
        MenuItem menuItem= new MenuItem("mailTemplate", "Gestionar Plantillas de Correo");
        menuParent1.addSubMenu(menuItem);
        menuParent.addSubMenu(menuParent1);
        menuComponent.addItemMenu(menuParent);
    }
    
    @Override
    public List<MenuItem> configureVisibilityMenu(List<MenuItem> menuData){
        return securityService.configureVisibilityMenu(menuData);
    }
    
    
}
