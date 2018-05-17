/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lacv.jmagrexs.modules.common.controllers.rest;


import com.lacv.jmagrexs.modules.common.mappers.LogProcessMapper;
import com.lacv.jmagrexs.modules.common.services.LogProcessService;
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
@RequestMapping(value="/rest/logProcess")
public class LogProcessRestController extends RestEntityController {
    
    @Autowired
    LogProcessService logProcessService;
    
    @Autowired
    LogProcessMapper logProcessMapper;
    
    
    @PostConstruct
    public void init(){
        super.addControlMapping("logProcess", logProcessService, logProcessMapper);
    }
    
}
