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
public class RestControllerGenerator extends ClassGenerator {
    
    public RestControllerGenerator(Class originClass, String groupId) {
        super(originClass, groupId);
        packages.add("controllers");
        packages.add("rest");
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
                    "package "+groupId+".controllers.rest;\n" +
                    "\n" +
                    "\n" +
                    "import "+groupId+".model.mappers."+entityName+"Mapper;\n" +
                    "import "+groupId+".services."+entityName+"Service;\n" +
                    "import com.dot.gcpbasedot.controller.RestEntityController;\n" +
                    "import java.io.InputStream;\n" +
                    "import javax.annotation.PostConstruct;\n" +
                    "import org.springframework.beans.factory.annotation.Autowired;\n" +
                    "import org.springframework.stereotype.Controller;\n" +
                    "import org.springframework.web.bind.annotation.RequestMapping;\n" +
                    "\n" +
                    "/**\n" +
                    " *\n" +
                    " * @author lcastrillo\n" +
                    " */\n" +
                    "@Controller\n" +
                    "@RequestMapping(value=\"/rest/"+entityVar+"\")\n" +
                    "public class "+entityName+"RestController extends RestEntityController {\n" +
                    "    \n" +
                    "    @Autowired\n" +
                    "    "+entityName+"Service "+entityVar+"Service;\n" +
                    "    \n" +
                    "    @Autowired\n" +
                    "    "+entityName+"Mapper "+entityVar+"Mapper;\n" +
                    "    \n" +
                    "    \n" +
                    "    @PostConstruct\n" +
                    "    public void init(){\n" +
                    "        super.addControlMapping(\""+entityVar+"\", "+entityVar+"Service, "+entityVar+"Mapper);\n" +
                    "    }\n" +
                    "    \n" +
                    "    @Override\n" +
                    "    public String saveFilePart(int slice, String fieldName, String fileName, String fileType, int fileSize, InputStream is, Object idParent){\n" +
                    "        return \"Almacenamiento de archivo no implementado!!\";\n" +
                    "    }" +
                    "    \n" +
                    "}" +
                    "";

            createJavaFile(entityName+"RestController.java", code);
        }
    }
    
}
