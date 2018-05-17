/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lacv.jmagrexs.modules.leadtable.controllers.view;

import com.lacv.jmagrexs.modules.leadtable.dtos.TableColumnDto;
import com.lacv.jmagrexs.modules.leadtable.mappers.TableColumnMapper;
import com.lacv.jmagrexs.modules.leadtable.services.TableColumnService;
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
@RequestMapping(value="/vista/tableColumn")
public class TableColumnViewController extends ExtEntityController {
    
    @Autowired
    TableColumnService tableColumnService;
    
    @Autowired
    TableColumnMapper tableColumnMapper;
    
    @Autowired
    SecurityService securityService;
    
    
    @PostConstruct
    public void init(){
        EntityConfig view= new EntityConfig("tableColumn", tableColumnService, TableColumnDto.class);
        view.setSingularEntityTitle("Columna de la tabla");
        view.setPluralEntityTitle("Columnas de la tabla");
        view.setDefaultOrder("columnOrder", "ASC");
        super.addControlMapping(view);
    }
    
    @Override
    public List<MenuItem> configureVisibilityMenu(List<MenuItem> menuData){
        return securityService.configureVisibilityMenu(menuData);
    }
    
}
