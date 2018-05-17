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
public class DaoImplGenerator extends ClassGenerator {
    
    private String indexDataSource;
    
    public DaoImplGenerator(Class originClass, String groupId) {
        super(originClass, groupId);
        packages.add("daos");
        packages.add("impl");
        this.indexDataSource= "";
    }
    
    @Override
    public void generate(Class entityClass){
        if(BaseEntity.class.isAssignableFrom(entityClass)){
            String entityName= entityClass.getSimpleName();
            String entityPackage= entityClass.getPackage().getName();
            
            String code= ""+
                    "/*\n" +
                    " * To change this template, choose Tools | Templates\n" +
                    " * and open the template in the editor.\n" +
                    " */\n" +
                    "package "+groupId+".daos.impl;\n" +
                    "\n" +
                    "\n" +
                    "import "+groupId+".daos."+entityName+"Jpa;\n" +
                    "import "+entityPackage+"."+entityName+";\n" +
                    "import com.lacv.jmagrexs.dao.JPAGenericDao;\n" +
                    "import org.springframework.stereotype.Repository;\n" +
                    "\n" +
                    "/**\n" +
                    " *\n" +
                    " * @author lacastrillov\n" +
                    " */\n" +
                    "@Repository\n" +
                    "public class "+entityName+"JpaController extends JPAGenericDao"+this.indexDataSource+"<"+entityName+"> implements "+entityName+"Jpa {\n" +
                    "\n" +
                    "}" +
                    "";

            createJavaFile(entityName+"JpaController.java", code);
        }
    }

    /**
     * @param indexDataSource the indexDataSource to set
     */
    public void setIndexDataSource(String indexDataSource) {
        this.indexDataSource = indexDataSource;
    }
    
}
