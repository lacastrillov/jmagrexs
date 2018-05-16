/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dot.gcpbasedot.generator;

import com.dot.gcpbasedot.domain.BaseEntity;

/**
 *
 * @author grupot
 */
public class DaoGenerator extends ClassGenerator {
    
    public DaoGenerator(Class originClass, String groupId) {
        super(originClass, groupId);
        packages.add("daos");
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
                    "package "+groupId+".daos;\n" +
                    "\n" +
                    "import "+entityPackage+"."+entityName+";\n" +
                    "import com.dot.gcpbasedot.dao.GenericDao;\n" +
                    "\n" +
                    "/**\n" +
                    " *\n" +
                    " * @author lcastrillo\n" +
                    " */\n" +
                    "public interface "+entityName+"Jpa extends GenericDao<"+entityName+"> {\n" +
                    "\n" +
                    "    \n" +
                    "}"+
                    "";

            createJavaFile(entityName+"Jpa.java", code);
        }
    }
    
}
