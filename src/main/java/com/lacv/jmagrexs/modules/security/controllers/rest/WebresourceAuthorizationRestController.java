/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lacv.jmagrexs.modules.security.controllers.rest;


import com.lacv.jmagrexs.modules.security.mappers.WebresourceAuthorizationMapper;
import com.lacv.jmagrexs.modules.security.services.WebresourceAuthorizationService;
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
@RequestMapping(value="/rest/webresourceAuthorization")
public class WebresourceAuthorizationRestController extends RestEntityController {
    
    @Autowired
    WebresourceAuthorizationService webresourceAuthorizationService;
    
    @Autowired
    WebresourceAuthorizationMapper webresourceAuthorizationMapper;
    
    
    @PostConstruct
    public void init(){
        super.addControlMapping("webresourceAuthorization", webresourceAuthorizationService, webresourceAuthorizationMapper);
    }
    
    
}
