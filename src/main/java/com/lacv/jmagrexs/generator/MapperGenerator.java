/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.generator;

import com.lacv.jmagrexs.domain.BaseEntity;
import com.lacv.jmagrexs.reflection.EntityReflection;
import com.lacv.jmagrexs.util.Formats;
import java.beans.PropertyDescriptor;
import org.springframework.util.StringUtils;

/**
 *
 * @author grupot
 */
public class MapperGenerator extends ClassGenerator {
    
    public MapperGenerator(Class originClass, String groupId) {
        super(originClass, groupId);
        packages.add("model");
        packages.add("mappers");
    }
    
    @Override
    public void generate(Class entityClass){
        if(BaseEntity.class.isAssignableFrom(entityClass)){
            String mappers="";
            String setters="";
            String entityName= entityClass.getSimpleName();
            String entityVar= Character.toLowerCase(entityName.charAt(0)) + entityName.substring(1);
            String entityPackage= entityClass.getPackage().getName();

            PropertyDescriptor[] propertyDescriptors = EntityReflection.getPropertyDescriptors(entityClass);
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                String type = propertyDescriptor.getPropertyType().getName();

                if(type.equals("java.lang.Class")==false && !type.equals("java.util.List")){
                    String fieldName= propertyDescriptor.getName();
                    String fieldEntity= StringUtils.capitalize(fieldName);
                    if(!Formats.TYPES_LIST.contains(type)){
                        mappers+=""+
                            "    \n" +
                            "    @Autowired\n" +
                            "    "+fieldEntity+"Mapper "+fieldName+"Mapper;\n";
                        
                        setters+=
                            "            dto.set"+fieldEntity+"("+fieldName+"Mapper.entityToDto(entity.get"+fieldEntity+"()));\n";
                        
                    }else if(!type.equals("java.util.List")){
                        setters+=
                            "            dto.set"+fieldEntity+"(entity.get"+fieldEntity+"());\n";
                    }
                }
            }

            String code= ""+
                    "/*\n" +
                    " * To change this template, choose Tools | Templates\n" +
                    " * and open the template in the editor.\n" +
                    " */\n" +
                    "package "+groupId+".model.mappers;\n" +
                    "\n" +
                    "import com.lacv.jmagrexs.mapper.EntityMapper;\n" +
                    "import com.lacv.jmagrexs.mapper.EntityMapperImpl;\n" +
                    "import "+groupId+".model.dtos."+entityName+"Dto;\n" +
                    "import "+entityPackage+"."+entityName+";\n" +
                    "import java.util.ArrayList;\n" +
                    "import java.util.List;\n" +
                    "import org.springframework.beans.factory.annotation.Autowired;\n" +
                    "import org.springframework.stereotype.Component;\n" +
                    "\n" +
                    "/**\n" +
                    " *\n" +
                    " * @author lcastrillo\n" +
                    " */\n" +
                    "@Component(\""+entityVar+"Mapper\")\n" +
                    "public class "+entityName+"Mapper extends EntityMapperImpl<"+entityName+", "+entityName+"Dto> implements EntityMapper<"+entityName+", "+entityName+"Dto> {\n" +
                    
                    mappers +
                    
                    "\n" +
                    "    \n" +
                    "    @Override\n" +
                    "    public "+entityName+"Dto entityToDto("+entityName+" entity) {\n" +
                    "        "+entityName+"Dto dto= new "+entityName+"Dto();\n" +
                    "        if(entity!=null){\n" +
                    
                    setters +
                    
                    "        }\n" +
                    "        return dto;\n" +
                    "    }\n" +
                    "    \n" +
                    "    /**\n" +
                    "     *\n" +
                    "     * @param entities\n" +
                    "     * @return\n" +
                    "     */\n" +
                    "    @Override\n" +
                    "    public List<"+entityName+"Dto> listEntitiesToListDtos(List<"+entityName+"> entities){\n" +
                    "        List<"+entityName+"Dto> dtos= new ArrayList<>();\n" +
                    "        if(entities!=null){\n" +
                    "            for("+entityName+" entity: entities){\n" +
                    "                dtos.add(entityToDto(entity));\n" +
                    "            }\n" +
                    "        }\n" +
                    "        return dtos;\n" +
                    "    }\n" +
                    "    \n" +
                    "}\n" +
                    ""+
                    "";

            createJavaFile(entityName+"Mapper.java", code);
        }
    }
    
}
