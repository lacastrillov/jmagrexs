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
    
    public ViewControllerGenerator(Class originClass, String groupId) {
        super(originClass, groupId);
        packages.add("controllers");
        packages.add("view");
    }
    
    @Override
    public void generate(Class entityClass){
        if(BaseEntity.class.isAssignableFrom(entityClass)){
            String entityName= entityClass.getSimpleName();
            String entityVar= Character.toLowerCase(entityName.charAt(0)) + entityName.substring(1);

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
                    "import com.lacv.jmagrexs.modules.security.services.SecurityService;\n" +
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
                    "    @Autowired\n" +
                    "    SecurityService securityService;\n" +
                    "    \n" +
                    "    \n" +
                    "    @PostConstruct\n" +
                    "    public void init(){\n" +
                    "        EntityConfig view= new EntityConfig(\""+entityVar+"\", "+entityVar+"Service, "+entityName+"Dto.class);\n" +
                    "        view.setSingularEntityTitle(\""+entityName+"\");\n" +
                    "        view.setPluralEntityTitle(\""+entityName+"s\");\n" +
                    "        view.setMultipartFormData(false);\n" +
                    "        view.setVisibleSeeAllButton(false);\n" +
                    "        view.setDefaultOrder(\"id\", \"DESC\");\n" +
                    "        super.addControlMapping(view);\n" +
                    "        \n" +
                    "        MenuItem menuParent= new MenuItem(\"Entidades\", 1);\n" +
                    "        MenuItem menuItem= new MenuItem(\""+entityVar+"\", \"Gestionar "+entityName+"s\", 1);\n" +
                    "        menuParent.addSubMenu(menuItem);\n" +
                    "        menuComponent.addItemMenu(menuParent);\n" +
                    "    }\n" +
                    "    \n" +
                    "    @Override\n" +
                    "    public List<MenuItem> configureVisibilityMenu(List<MenuItem> menuData){\n" +
                    "        return securityService.configureVisibilityMenu(menuData);\n" +
                    "    }\n" +
                    "    \n" +
                    "}" +
                    "";

            createJavaFile(entityName+"ViewController.java", code);
        }
    }
    
}
