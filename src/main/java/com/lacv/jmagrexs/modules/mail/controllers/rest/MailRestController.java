/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lacv.jmagrexs.modules.mail.controllers.rest;


import com.lacv.jmagrexs.modules.mail.mappers.MailMapper;
import com.lacv.jmagrexs.modules.mail.services.MailService;
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
@RequestMapping(value="/rest/mail")
public class MailRestController extends RestEntityController {
    
    @Autowired
    MailService mailService;
    
    @Autowired
    MailMapper mailMapper;
    
    
    @PostConstruct
    public void init(){
        super.addControlMapping("mail", mailService, mailMapper);
    }
    
    
}
