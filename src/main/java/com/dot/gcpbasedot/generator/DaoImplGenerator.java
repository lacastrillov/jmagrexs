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
public class DaoImplGenerator extends ClassGenerator {
    
    private String dataSource;
    
    private String unitName;
    
    public DaoImplGenerator(Class originClass, String groupId) {
        super(originClass, groupId);
        packages.add("daos");
        packages.add("impl");
        this.dataSource= "dataSource";
        this.unitName= "";
    }
    
    @Override
    public void generate(Class entityClass){
        if(BaseEntity.class.isAssignableFrom(entityClass)){
            String entityName= entityClass.getSimpleName();
            String persistenceContext= "@PersistenceContext";
            String entityPackage= entityClass.getPackage().getName();
            if(!this.unitName.equals("")){
                persistenceContext+= "(unitName =\""+this.unitName+"\")";
            }
            
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
                    "import com.dot.gcpbasedot.dao.JPAAbstractDao;\n" +
                    "import javax.persistence.EntityManager;\n" +
                    "import javax.persistence.PersistenceContext;\n" +
                    "import javax.sql.DataSource;\n" +
                    "import org.springframework.beans.factory.annotation.Autowired;\n" +
                    "import org.springframework.stereotype.Repository;\n" +
                    "\n" +
                    "/**\n" +
                    " *\n" +
                    " * @author lacastrillov\n" +
                    " */\n" +
                    "@Repository\n" +
                    "public class "+entityName+"JpaController extends JPAAbstractDao<"+entityName+"> implements "+entityName+"Jpa {\n" +
                    "\n" +
                    "    @Autowired\n" +
                    "    public void init(DataSource "+dataSource+"){\n" +
                    "        super.setDataSource("+dataSource+");\n" +
                    "    }\n" +
                    "    \n" +
                    "    @Override\n" +
                    "    "+persistenceContext+"\n" +
                    "    public void setEntityManager(EntityManager entityManager) {\n" +
                    "        this.entityManager= entityManager;\n" +
                    "    }\n" +
                    "    \n" +
                    "}" +
                    "";

            createJavaFile(entityName+"JpaController.java", code);
        }
    }

    /**
     * @return the dataSource
     */
    public String getDataSource() {
        return dataSource;
    }

    /**
     * @param dataSource the dataSource to set
     */
    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * @return the unitName
     */
    public String getUnitName() {
        return unitName;
    }

    /**
     * @param unitName the unitName to set
     */
    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }
    
}
