/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.generator;

import com.lacv.jmagrexs.domain.BaseEntity;

/**
 *
 * @author grupot
 */
public class ViewControllerGenerator extends ClassGenerator {
    
    private boolean security;
    
    public ViewControllerGenerator(Class originClass, String groupId) {
        super(originClass, groupId);
        packages.add("controllers");
        packages.add("view");
        security=true;
    }
    
    @Override
    public void generate(Class entityClass){
        if(BaseEntity.class.isAssignableFrom(entityClass)){
            String entityName= entityClass.getSimpleName();
            String entityVar= Character.toLowerCase(entityName.charAt(0)) + entityName.substring(1);
            String entityTitle= splitClassName(entityName);

            String code= ""+
                    "/*\n" +
                    " * To change this license header, choose License Headers in Project Properties.\n" +
                    " * To change this template file, choose Tools | Templates\n" +
                    " * and open the template in the editor.\n" +
                    " */\n" +
                    "\n" +
                    "package "+groupId+".controllers.view;\n" +
                    "\n" +
                    "import "+groupId+".model.dtos."+entityName+"Dto;\n" +
                    "import "+groupId+".model.mappers."+entityName+"Mapper;\n" +
                    "import "+groupId+".services."+entityName+"Service;\n" +
                    "import com.lacv.jmagrexs.controller.view.ExtEntityController;\n" +
                    "import com.lacv.jmagrexs.dto.MenuItem;\n" +
                    "import com.lacv.jmagrexs.dto.config.EntityConfig;\n" +
                    ((!security)?"//":"")+"import com.lacv.jmagrexs.modules.security.services.bussiness.SecurityService;\n" +
                    "import java.util.List;\n" +
                    "import javax.annotation.PostConstruct;\n" +
                    "import org.springframework.beans.factory.annotation.Autowired;\n" +
                    "import org.springframework.stereotype.Controller;\n" +
                    "import org.springframework.web.bind.annotation.RequestMapping;\n" +
                    "\n" +
                    "/**\n" +
                    " *\n" +
                    " * @author lacastrillov\n" +
                    " */\n" +
                    "@Controller\n" +
                    "@RequestMapping(value=\"/vista/"+entityVar+"\")\n" +
                    "public class "+entityName+"ViewController extends ExtEntityController {\n" +
                    "    \n" +
                    "    @Autowired\n" +
                    "    "+entityName+"Service "+entityVar+"Service;\n" +
                    "    \n" +
                    "    @Autowired\n" +
                    "    "+entityName+"Mapper "+entityVar+"Mapper;\n" +
                    "    \n" +
                    "    "+((!security)?"//":"")+"@Autowired\n" +
                    "    "+((!security)?"//":"")+"SecurityService securityService;\n" +
                    "    \n" +
                    "    \n" +
                    "    @PostConstruct\n" +
                    "    public void init(){\n" +
                    "        EntityConfig view= new EntityConfig(\""+entityVar+"\", "+entityVar+"Service, "+entityName+"Dto.class);\n" +
                    "        view.setSingularEntityTitle(\""+entityTitle+"\");\n" +
                    "        view.setPluralEntityTitle(\""+entityTitle+"s\");\n" +
                    "        view.setMultipartFormData(false);\n" +
                    "        view.setVisibleSeeAllButton(false);\n" +
                    "        view.setDefaultOrder(\"id\", \"DESC\");\n" +
                    "        super.addControlMapping(view);\n" +
                    "        \n" +
                    "        MenuItem menuItem= new MenuItem(\""+entityVar+"\", \"Gestionar "+entityTitle+"s\", 1);\n" +
                    "        \n" +
                    "        MenuItem menuParent= new MenuItem(\"Entidades\", 1);\n" +
                    "        menuParent.addSubMenu(menuItem);\n" +
                    "        \n" +
                    "        menuComponent.addItemMenu(menuParent);\n" +
                    "    }\n" +
                    "    \n" +
                    "    @Override\n" +
                    "    public List<MenuItem> configureVisibilityMenu(List<MenuItem> menuData){\n" +
                    "        "+((!security)?"return menuData;//":"")+"return securityService.configureVisibilityMenu(menuData);"+"\n" +
                    "    }\n" +
                    "    \n" +
                    "}" +
                    "";

            createJavaFile(entityName+"ViewController.java", code);
        }
    }

    /**
     * @param security the security to set
     */
    public void setSecurity(boolean security) {
        this.security = security;
    }
    
    
}
