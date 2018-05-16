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
public class ServiceImplGenerator extends ClassGenerator {
    
    private String entityServiceImpl;
    
    public ServiceImplGenerator(Class originClass, String groupId) {
        super(originClass, groupId);
        packages.add("services");
        packages.add("impl");
        this.entityServiceImpl= "EntityServiceImpl";
    }
    
    @Override
    public void generate(Class entityClass){
        if(BaseEntity.class.isAssignableFrom(entityClass)){
            String entityName= entityClass.getSimpleName();
            String entityVar= Character.toLowerCase(entityName.charAt(0)) + entityName.substring(1);
            String entityPackage= entityClass.getPackage().getName();

            String code= ""+
                    "/*\n" +
                    " * To change this license header, choose License Headers in Project Properties.\n" +
                    " * To change this template file, choose Tools | Templates\n" +
                    " * and open the template in the editor.\n" +
                    " */\n" +
                    "\n" +
                    "package "+groupId+".services.impl;\n" +
                    "\n" +
                    "\n" +
                    "import "+groupId+".daos."+entityName+"Jpa;\n" +
                    "import "+entityPackage+"."+entityName+";\n" +
                    "import "+groupId+".model.mappers."+entityName+"Mapper;\n" +
                    "import "+groupId+".services."+entityName+"Service;\n" +
                    "import com.dot.gcpbasedot.dao.GenericDao;\n" +
                    "import com.dot.gcpbasedot.service.EntityServiceImpl1;\n" +
                    "import org.springframework.beans.factory.annotation.Autowired;\n" +
                    "import org.springframework.stereotype.Service;\n" +
                    "\n" +
                    "/**\n" +
                    " *\n" +
                    " * @author lcastrillo\n" +
                    " */\n" +
                    "@Service(\""+entityVar+"Service\")\n" +
                    "public class "+entityName+"ServiceImpl extends "+this.getEntityServiceImpl()+"<"+entityName+"> implements "+entityName+"Service {\n" +
                    "    \n" +
                    "    @Autowired\n" +
                    "    public "+entityName+"Jpa "+entityVar+"Jpa;\n" +
                    "    \n" +
                    "    @Autowired\n" +
                    "    public "+entityName+"Mapper "+entityVar+"Mapper;\n" +
                    "    \n" +
                    "    @Override\n" +
                    "    public GenericDao getGenericDao(){\n" +
                    "        return "+entityVar+"Jpa;\n" +
                    "    }\n" +
                    "    \n" +
                    "}" +
                    "";

            createJavaFile(entityName+"ServiceImpl.java", code);
        }
    }

    /**
     * @return the entityServiceImpl
     */
    public String getEntityServiceImpl() {
        return entityServiceImpl;
    }

    /**
     * @param entityServiceImpl the entityServiceImpl to set
     */
    public void setEntityServiceImpl(String entityServiceImpl) {
        this.entityServiceImpl = entityServiceImpl;
    }
    
}
