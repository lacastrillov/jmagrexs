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
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.util.StringUtils;

/**
 *
 * @author grupot
 */
public class DtoGenerator extends ClassGenerator {
    
    public DtoGenerator(Class originClass, String groupId){
        super(originClass, groupId);
        packages.add("model");
        packages.add("dtos");
    }
    
    @Override
    public void generate(Class entityClass){
        if(BaseEntity.class.isAssignableFrom(entityClass)){
            String attributes="";
            String settersAndGetters="";
            String entityName= entityClass.getSimpleName();

            PropertyDescriptor[] propertyDescriptors = EntityReflection.getPropertyDescriptors(entityClass);
            HashSet<String> fieldsNN= getNotNullFields(entityClass);
            HashSet<String> fieldsRO= getReadOnlyFields(entityClass);
            HashMap<String, Integer[]> sizeColumnMap= getSizeColumnMap(entityClass);
            int index=1;
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                String type = propertyDescriptor.getPropertyType().getName();

                if(type.equals("java.lang.Class")==false){
                    String simpleType= propertyDescriptor.getPropertyType().getSimpleName();
                    String simpleTypeSet= simpleType;
                    String fieldName= propertyDescriptor.getName();
                    String fieldEntity= StringUtils.capitalize(fieldName);
                    String simpleListArgumentType= "";
                    if(type.equals("java.util.List")){
                        try {
                            Field listField = entityClass.getDeclaredField(fieldName);
                            ParameterizedType listType = (ParameterizedType) listField.getGenericType();
                            Class<?> listArgumentType = (Class<?>) listType.getActualTypeArguments()[0];
                            simpleListArgumentType= "<"+listArgumentType.getSimpleName()+"Dto>";
                        } catch (NoSuchFieldException | SecurityException ex) {
                            Logger.getLogger(DtoGenerator.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }else if(!Formats.TYPES_LIST.contains(type)){
                        simpleType= simpleType+"Dto";
                        simpleTypeSet= simpleTypeSet+"Dto";
                    }

                    int columnWidth= 200;
                    if(fieldName.equals("id") || type.equals("boolean") || type.equals("java.lang.Boolean")){
                        columnWidth= 100;
                    }
                    if(fieldName.equals("id")){
                        simpleTypeSet= "Object";
                    }

                    String sizeAnnotation="";
                    if(sizeColumnMap.containsKey(fieldName)){
                        Integer[] size= sizeColumnMap.get(fieldName);
                        sizeAnnotation= "    @Size("+((size[0]!=0)?"min="+size[0]+",":"")+"max="+size[1]+")\n";
                    }

                    attributes+="    \n";
                    if(!type.equals("java.util.List")){
                        attributes+=
                            "    @Order("+index+")\n" +
                            ((fieldsNN.contains(fieldName) && !fieldsRO.contains(fieldName))?"    @NotNull\n":"") +
                            ((fieldsRO.contains(fieldName))?"    @ReadOnly\n":"") +
                            sizeAnnotation +
                            "    @ColumnWidth("+columnWidth+")\n" +
                            "    @TextField(\""+fieldEntity+"\")\n";
                        index++;
                    }

                    attributes+=
                        "    private "+simpleType+simpleListArgumentType+" "+fieldName+";\n";

                    settersAndGetters+=
                        "\n" +
                        ((fieldName.equals("id"))?"    @Override\n":"") +
                        "    public "+simpleType+simpleListArgumentType+" get"+fieldEntity+"() {\n" +
                        "        return "+fieldName+";\n" +
                        "    }\n" +
                        "\n" +
                        ((fieldName.equals("id"))?"    @Override\n":"") +
                        "    public void set"+fieldEntity+"("+simpleTypeSet+simpleListArgumentType+" "+fieldName+") {\n" +
                        "        this."+fieldName+" = "+((fieldName.equals("id"))?"("+simpleType+") ":" ")+fieldName+";\n" +
                        "    }\n";
                }
            }

            String code= ""+
                    "/*\n" +
                    " * To change this license header, choose License Headers in Project Properties.\n" +
                    " * To change this template file, choose Tools | Templates\n" +
                    " * and open the template in the editor.\n" +
                    " */\n" +
                    "package "+groupId+".model.dtos;\n" +
                    "\n" +
                    "import com.lacv.jmagrexs.annotation.ColumnWidth;\n" +
                    "import com.lacv.jmagrexs.annotation.LabelField;\n" +
                    "import com.lacv.jmagrexs.annotation.NotNull;\n" +
                    "import com.lacv.jmagrexs.annotation.Order;\n" +
                    "import com.lacv.jmagrexs.annotation.ReadOnly;\n" +
                    "import com.lacv.jmagrexs.annotation.Size;\n" +
                    "import com.lacv.jmagrexs.annotation.TextField;\n" +
                    "import com.lacv.jmagrexs.domain.BaseDto;\n" +
                    "import java.sql.Time;\n" +
                    "import java.util.Date;\n" +
                    "import java.util.List;\n" +
                    "\n" +
                    "/**\n" +
                    " *\n" +
                    " * @author lcastrillo\n" +
                    " */\n" +
                    "@LabelField(\"id\")\n" +
                    "public class "+entityName+"Dto implements BaseDto {\n" +
                    "\n" +
                    "    private static final long serialVersionUID = 1L;\n" +

                    attributes +

                    "    \n" +
                    "\n" +
                    "    public "+entityName+"Dto() {\n" +
                    "    }\n" +

                    settersAndGetters +                

                    "\n" +
                    "    @Override\n" +
                    "    public int hashCode() {\n" +
                    "        int hash = 0;\n" +
                    "        hash += (id != null ? id.hashCode() : 0);\n" +
                    "        return hash;\n" +
                    "    }\n" +
                    "\n" +
                    "    @Override\n" +
                    "    public boolean equals(Object object) {\n" +
                    "        // TODO: Warning - this method won't work in the case the id fields are not set\n" +
                    "        if (!(object instanceof "+entityName+"Dto)) {\n" +
                    "            return false;\n" +
                    "        }\n" +
                    "        "+entityName+"Dto other = ("+entityName+"Dto) object;\n" +
                    "        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {\n" +
                    "            return false;\n" +
                    "        }\n" +
                    "        return true;\n" +
                    "    }\n" +
                    "\n" +
                    "    @Override\n" +
                    "    public String toString() {\n" +
                    "        return \""+groupId+".model.dtos."+entityName+"Dto[ id=\" + id + \" ]\";\n" +
                    "    }\n" +
                    "    \n" +
                    "}\n" +
                    "";

            createJavaFile(entityName+"Dto.java", code);
        }
    }
    
}
