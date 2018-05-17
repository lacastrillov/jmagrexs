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
public class ServiceGenerator extends ClassGenerator {
    
    public ServiceGenerator(Class originClass, String groupId) {
        super(originClass, groupId);
        packages.add("services");
    }
    
    @Override
    public void generate(Class entityClass){
        if(BaseEntity.class.isAssignableFrom(entityClass)){
            String entityName= entityClass.getSimpleName();
            String entityPackage= entityClass.getPackage().getName();

            String code= ""+
                    "/*\n" +
                    " * To change this license header, choose License Headers in Project Properties.\n" +
                    " * To change this template file, choose Tools | Templates\n" +
                    " * and open the template in the editor.\n" +
                    " */\n" +
                    "\n" +
                    "package "+groupId+".services;\n" +
                    "\n" +
                    "import "+entityPackage+"."+entityName+";\n" +
                    "import com.lacv.jmagrexs.service.EntityService;\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "/**\n" +
                    " *\n" +
                    " * @author lacastrillov\n" +
                    " */\n" +
                    "public interface "+entityName+"Service extends EntityService<"+entityName+"> {\n" +
                    "    \n" +
                    "    \n" +
                    "}" +
                    "";

            createJavaFile(entityName+"Service.java", code);
        }
    }
    
}
