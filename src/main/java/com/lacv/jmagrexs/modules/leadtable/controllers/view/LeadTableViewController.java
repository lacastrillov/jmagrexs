/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lacv.jmagrexs.modules.leadtable.controllers.view;

import com.lacv.jmagrexs.modules.leadtable.dtos.LeadTableDto;
import com.lacv.jmagrexs.modules.leadtable.mappers.LeadTableMapper;
import com.lacv.jmagrexs.modules.leadtable.services.LeadTableService;
import com.lacv.jmagrexs.controller.view.ExtEntityController;
import com.lacv.jmagrexs.dto.MenuItem;
import com.lacv.jmagrexs.dto.config.EntityConfig;
import com.lacv.jmagrexs.modules.leadtable.entities.TableColumn;
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
@RequestMapping(value="/vista/leadTable")
public class LeadTableViewController extends ExtEntityController {
    
    @Autowired
    LeadTableService leadTableService;
    
    @Autowired
    LeadTableMapper leadTableMapper;
    
    @Autowired
    SecurityService securityService;
    
    
    @PostConstruct
    public void init(){
        EntityConfig view= new EntityConfig("leadTable", leadTableService, LeadTableDto.class);
        view.setSingularEntityTitle("Tabla Lead");
        view.setPluralEntityTitle("Tablas Lead");
        view.addChildExtView("tableColumn", TableColumn.class, EntityConfig.TCV_1_TO_N);
        super.addControlMapping(view);
        
        MenuItem menuParent= new MenuItem("Sistema");
        MenuItem menuParent1= new MenuItem("Tablas Lead");
        MenuItem menuItem= new MenuItem("leadTable", "Gestionar Tablas Lead");
        menuParent1.addSubMenu(menuItem);
        menuParent.addSubMenu(menuParent1);
        menuComponent.addItemMenu(menuParent);
    }
    
    @Override
    public List<MenuItem> configureVisibilityMenu(List<MenuItem> menuData){
        return securityService.configureVisibilityMenu(menuData);
    }
    
}
