/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.generator;

import com.lacv.jmagrexs.reflection.EntityReflection;
import com.lacv.jmagrexs.util.FileService;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EmbeddedId;
import javax.persistence.GeneratedValue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author grupot
 */
public class ClassGenerator {

    protected String classPath;

    protected final String groupId;

    protected final List<String> packages;

    public ClassGenerator(Class originClass, String groupId) {
        classPath= originClass.getProtectionDomain().getCodeSource().getLocation().getPath();
        classPath= classPath.split("/target/")[0];
        classPath+= "/src/main/java/";
        this.groupId = groupId;
        packages = new ArrayList<>();
        packages.addAll(Arrays.asList(groupId.split("\\.")));
    }

    public void generate(String entitiesPackage) {
        try {
            Class[] classes= getClasses(entitiesPackage);
            for(Class entityClass: classes){
                generate(entityClass);
            }
        } catch (ClassNotFoundException | IOException ex) {
            Logger.getLogger(ClassGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void generate(Class entityClass) {
    }
    
    protected void createJavaFile(String fileName, String code){
        String pathFile= classPath;
        for(String folder: packages){
            pathFile+=folder+"/";
            if(!FileService.existsFile(pathFile)){
                FileService.createFolder(pathFile);
            }
        }
        try {
            pathFile+= fileName;
            FileService.setTextFile(code, pathFile);
            System.out.println(pathFile.replace(classPath, "")+" generated...");
        } catch (IOException ex) {
            Logger.getLogger(DtoGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    protected void orderPropertyDescriptor(PropertyDescriptor[] propertyDescriptors){
        String[] defaultOrder= {"id", "nombre", "name", "razonsocial", "titulo", "title", "alias"};
        PropertyDescriptor aux;
        int i=0;
        for(String fieldName: defaultOrder){
            for(int j=0; j<propertyDescriptors.length; j++){
                if(i!=j && fieldName.equals(propertyDescriptors[j].getName())){
                    if(fieldName.equals(propertyDescriptors[j].getName())){
                        aux= propertyDescriptors[i];
                        propertyDescriptors[i]= propertyDescriptors[j];
                        propertyDescriptors[j]= aux;
                        i++;
                        break;
                    }
                }
            }
        }
    }

    protected HashSet<String> getNotNullFields(Class entityClass) {
        HashSet<String> fieldsNN = new HashSet<>();
        List<Field> fieldsNotNull = EntityReflection.getEntityAnnotatedFields(entityClass, NotNull.class);
        for (Field f : fieldsNotNull) {
            fieldsNN.add(f.getName());
        }

        return fieldsNN;
    }
    
    protected HashSet<String> getReadOnlyFields(Class entityClass) {
        HashSet<String> fieldsRO = new HashSet<>();
        List<Field> fieldsReadOnly = EntityReflection.getEntityAnnotatedFields(entityClass, GeneratedValue.class);
        for (Field f : fieldsReadOnly) {
            fieldsRO.add(f.getName());
        }
        List<Field> fieldsEmbeddedId = EntityReflection.getEntityAnnotatedFields(entityClass, EmbeddedId.class);
        for (Field f : fieldsEmbeddedId) {
            fieldsRO.add(f.getName());
        }

        return fieldsRO;
    }

    protected HashMap<String, Integer[]> getSizeColumnMap(Class entityClass) {
        HashMap<String, Integer[]> map = new HashMap<>();

        List<Field> fieldsSize = EntityReflection.getEntityAnnotatedFields(entityClass, Size.class);
        for (Field f : fieldsSize) {
            Size annotation = f.getAnnotation(Size.class);
            Integer[] size = {annotation.min(), annotation.max()};
            map.put(f.getName(), size);
        }

        return map;
    }
    
    public static Class[] getClasses(String packageName) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    public static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }
    
    public static String splitClassName(String className){
        StringBuilder resultName=new StringBuilder(2 * className.length());
        boolean blockUP= false;
        for(int i=0; i<className.length(); i++){
            char c0= (i>0)?className.charAt(i-1):'|';
            char c1= className.charAt(i);
            char c2= (i<className.length()-1)?className.charAt(i+1):'|';
            
            if(!Character.isLowerCase(c1)){
                if((c0!='|' && Character.isLowerCase(c0) && !blockUP) || (i!=0 && c2!='|' &&  Character.isLowerCase(c2))){
                    resultName.append(' ');
                }
                blockUP= true;
            }else{
                blockUP= false;
            }
            resultName.append(c1);
        }
        return resultName.toString();
    }

}
