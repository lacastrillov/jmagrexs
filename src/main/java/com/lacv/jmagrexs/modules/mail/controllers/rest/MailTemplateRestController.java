/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lacv.jmagrexs.modules.mail.controllers.rest;


import com.lacv.jmagrexs.modules.mail.mappers.MailTemplateMapper;
import com.lacv.jmagrexs.modules.mail.services.MailTemplateService;
import com.lacv.jmagrexs.controller.rest.RestEntityController;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author lcastrillo
 */
@Controller
@RequestMapping(value="/rest/mailTemplate")
public class MailTemplateRestController extends RestEntityController {
    
    @Autowired
    MailTemplateService mailTemplateService;
    
    @Autowired
    MailTemplateMapper mailTemplateMapper;
    
    
    @PostConstruct
    public void init(){
        super.addControlMapping("mailTemplate", mailTemplateService, mailTemplateMapper);
    }
    
    
}
