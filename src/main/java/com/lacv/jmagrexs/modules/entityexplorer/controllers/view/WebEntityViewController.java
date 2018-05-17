/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lacv.jmagrexs.modules.entityexplorer.controllers.view;

import com.lacv.jmagrexs.modules.entityexplorer.dtos.WebEntityDto;
import com.lacv.jmagrexs.modules.entityexplorer.mappers.WebEntityMapper;
import com.lacv.jmagrexs.controller.view.ExtEntityExplorerController;
import com.lacv.jmagrexs.dto.GridTemplate;
import com.lacv.jmagrexs.dto.MenuItem;
import com.lacv.jmagrexs.dto.config.EntityExplorerConfig;
import com.lacv.jmagrexs.enums.PageType;
import com.lacv.jmagrexs.modules.security.services.SecurityService;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.lacv.jmagrexs.modules.entityexplorer.services.WebEntityService;

/**
 *
 * @author lacastrillov
 */
@Controller
@RequestMapping(value="/vista/webEntity")
public class WebEntityViewController extends ExtEntityExplorerController {
    
    @Autowired
    WebEntityService webEntityService;
    
    @Autowired
    WebEntityMapper webEntityMapper;
    
    @Autowired
    SecurityService securityService;
    
    
    @PostConstruct
    public void init(){
        EntityExplorerConfig view= new EntityExplorerConfig("webEntity", webEntityService, WebEntityDto.class);
        view.setSingularEntityTitle("Entidad Web");
        view.setPluralEntityTitle("Entidades Web");
        view.addEntityRef("category", "Categoria");
        view.addEntityRef("commerce", "Comercio");
        view.addEntityRef("product", "Producto");
        view.addEntityRef("supplier", "Proveedor");
        view.addEntityRef("user", "Usuario");
        view.setDefaultOrder("entityRef", "ASC");
        
        GridTemplate gridTemplate= new GridTemplate("webEntity.vm");
        gridTemplate.setNumColumns(6);
        view.setGridTemplate(gridTemplate);
        view.setActiveGridTemplateAsParent(true);
        
        super.addControlMapping(view);
        
        MenuItem menuParent= new MenuItem("Gestor de Contenidos", 3);
        MenuItem menuItem= new MenuItem("webEntity", "Explorador de Entidades");
        menuItem.setPageType(PageType.ENTITY_EXPLORER);
        menuParent.addSubMenu(menuItem);
        menuComponent.addItemMenu(menuParent);
    }
    
    @Override
    public List<MenuItem> configureVisibilityMenu(List<MenuItem> menuData){
        return securityService.configureVisibilityMenu(menuData);
    }
    
}
