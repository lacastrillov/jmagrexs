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
import javax.persistence.Embeddable;
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
            String settersED="";
            String settersDE="";
            String entityName= entityClass.getSimpleName();
            String entityVar= Character.toLowerCase(entityName.charAt(0)) + entityName.substring(1);
            String entityPackage= entityClass.getPackage().getName();
            boolean autowiredUp= false;
            boolean embeddableId= false;

            PropertyDescriptor[] propertyDescriptors = EntityReflection.getPropertyDescriptors(entityClass);
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                String type = propertyDescriptor.getPropertyType().getName();

                if(type.equals("java.lang.Class")==false && !type.equals("java.util.List")){
                    String fieldName= propertyDescriptor.getName();
                    String fieldEntity= StringUtils.capitalize(fieldName);
                    if(propertyDescriptor.getPropertyType().getAnnotation(Embeddable.class)!=null){
                        embeddableId= true;
                        mappers+=""+
                            "    \n" +
                            "    Gson gson= new Gson();\n";
                        
                        settersED+=
                            "            dto.set"+fieldEntity+"(gson.toJson(entity.get"+fieldEntity+"()));\n";
                        settersDE+=
                            "            entity.set"+fieldEntity+"(gson.fromJson(dto.get"+fieldEntity+"(),"+type+".class));\n";
                    }else if(!Formats.TYPES_LIST.contains(type)){
                        autowiredUp= true;
                        mappers+=""+
                            "    \n" +
                            "    @Autowired\n" +
                            "    "+fieldEntity+"Mapper "+fieldName+"Mapper;\n";
                        
                        settersED+=
                            "            dto.set"+fieldEntity+"("+fieldName+"Mapper.entityToDto(entity.get"+fieldEntity+"()));\n";
                        settersDE+=
                            "            entity.set"+fieldEntity+"("+fieldName+"Mapper.dtoToEntity(dto.get"+fieldEntity+"()));\n";
                        
                    }else if(!type.equals("java.util.List")){
                        settersED+=
                            "            dto.set"+fieldEntity+"(entity.get"+fieldEntity+"());\n";
                        settersDE+=
                            "            entity.set"+fieldEntity+"(dto.get"+fieldEntity+"());\n";
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
                    "import org.springframework.stereotype.Component;\n" +
                    ((autowiredUp)?"import org.springframework.beans.factory.annotation.Autowired;\n":"") +
                    ((embeddableId)?"import com.google.gson.Gson;\n":"") +
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
                    
                    settersED +
                    
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
                    "    @Override\n" +
                    "    public "+entityName+" dtoToEntity("+entityName+"Dto dto) {\n" +
                    "        "+entityName+" entity= new "+entityName+"();\n" +
                    "        if(dto!=null){\n" +
                    
                    settersDE +
                    
                    "        }\n" +
                    "        return entity;\n" +
                    "    }\n" +
                    "    \n" +
                    "    /**\n" +
                    "     *\n" +
                    "     * @return\n" +
                    "     */\n" +
                    "    @Override\n" +
                    "    public List<"+entityName+"> listDtosToListEntities(List<"+entityName+"Dto> dtos){\n" +
                    "        List<"+entityName+"> entities= new ArrayList<>();\n" +
                    "        if(entities!=null){\n" +
                    "            for("+entityName+"Dto dto: dtos){\n" +
                    "                entities.add(dtoToEntity(dto));\n" +
                    "            }\n" +
                    "        }\n" +
                    "        return entities;\n" +
                    "    }\n"+
                    "\n" +
                    "}\n" +
                    "";

            createJavaFile(entityName+"Mapper.java", code);
        }
    }
    
}
