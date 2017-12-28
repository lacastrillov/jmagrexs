package com.dot.gcpbasedot.reflection;

import com.dot.gcpbasedot.annotation.HideField;
import com.dot.gcpbasedot.domain.BaseEntity;
import com.dot.gcpbasedot.dto.GenericTableColumn;
import com.dot.gcpbasedot.enums.HideView;
import com.dot.gcpbasedot.util.Formats;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.BeanWrapperImpl;

/**
 * Clase utilitaria que permite inspeccionar metadata de las clase entidades.
 *
 * @author lacastrillov@gmail.com
 *
 */
public final class EntityReflection {

    private EntityReflection() {

    }

    /**
     * Retorna los tipos de los colaboradores {@link Entity} que tiene la clase.
     * Para los colaboradores que son listas parametrizadas con clase
     * {@link Entity}, utilizar el metodo
     * {@link EntityReflection#getListCollaborators(Class)}.
     *
     * @param entityClass
     * @return un mapa con el nombre del colaborador como clave y la clase como
     * valor
     */
    public static Map<String, Class<?>> getCollaboratorsType(Class<?> entityClass) {
        Map<String, Class<?>> collaborators = new HashMap<>();
        Class<?> lookClass = entityClass;
        Field[] fields;

        while (!lookClass.equals(Object.class)) {
            fields = lookClass.getDeclaredFields();
            for (Field field : fields) {
                Class<?> collabType = field.getType();
                if (collabType.getAnnotation(Entity.class) != null) {
                    collaborators.put(field.getName(), collabType);
                }
            }
            lookClass = lookClass.getSuperclass();
        }

        return collaborators;
    }

    /**
     * Retorna todos los valores posibles para los colaboradores enumerativos.
     *
     * @param entityClass
     * @return
     */
    public static Map<String, List<String>> getEnumCollaborators(Class<?> entityClass) {
        Map<String, List<String>> collaborators = new HashMap<>();
        Class<?> lookClass = entityClass;
        Field[] fields;

        while (!lookClass.equals(Object.class)) {
            fields = lookClass.getDeclaredFields();
            for (Field field : fields) {
                if (Enum.class.isAssignableFrom(field.getType())) {
                    Object[] values = field.getType().getEnumConstants();
                    List<String> enumValues = new ArrayList<>();
                    for (Object object : values) {
                        enumValues.add(((Enum) object).name());
                    }
                    collaborators.put(field.getName(), enumValues);
                }
            }
            lookClass = lookClass.getSuperclass();
        }

        return collaborators;
    }

    /**
     * Returns all collaborators of the entity that has a relationship resulting
     * in a list.
     *
     * @param entityClass the entity class.
     * @return a map with field name as key and field class a value.
     */
    public static Map<String, Class<?>> getListCollaborators(Class<?> entityClass) {
        final Map<String, Class<?>> collaborators = new HashMap<>();
        final Class<?>[] annotations = new Class<?>[]{OneToMany.class, ManyToMany.class};

        ReflectionUtils.doInHierarchy(entityClass, Object.class, new HierarchyVisitor() {
            @Override
            public Object doInClass(Class<?> hierarchyClass) {
                Field[] fields = hierarchyClass.getDeclaredFields();
                for (Field field : fields) {
                    field.getGenericType();
                    if (ReflectionUtils.hasAnyAnnotation(field, annotations)) {
                        Class<?> type = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                        if (type.getAnnotation(Entity.class) != null) {
                            collaborators.put(field.getName(), type);
                        }
                    }
                }
                return null;
            }
        });

        return collaborators;
    }

    /**
     * Updates the target entity with the values of the source HashMap, avoiding
     * null valued fields.
     *
     * @param jsonObject
     * @param target entity to update.
     * @return
     */
    public static String updateEntity(String jsonObject, Object target) {
        String result="";
        JSONObject source= new JSONObject(jsonObject);
        BeanWrapperImpl targetWrapper = new BeanWrapperImpl(target);
        PropertyDescriptor[] propertyDescriptors = targetWrapper.getPropertyDescriptors();

        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String value = null;
            if(source.has(propertyDescriptor.getName())){
                value= source.get(propertyDescriptor.getName()).toString();
            }
            Class<?> typeWrapper = propertyDescriptor.getPropertyType();
            if (value != null && targetWrapper.isWritableProperty(propertyDescriptor.getName())) {
                try{
                    Object parseValue = Formats.castParameter(typeWrapper.getName(), value);
                    if (parseValue != null) {
                        targetWrapper.setPropertyValue(propertyDescriptor.getName(), parseValue);
                        if(propertyDescriptor.getName().equals("id") && value.equals("0")){
                            targetWrapper.setPropertyValue(propertyDescriptor.getName(), null);
                        }
                    } else if(value.equals("0") || value.equals("")) {
                        targetWrapper.setPropertyValue(propertyDescriptor.getName(), null);
                    } else {
                        Object id= getParsedFieldValue(typeWrapper, "id", value);
                        BaseEntity childEntity = (BaseEntity) getObjectForClass(typeWrapper);
                        childEntity.setId(id);
                        targetWrapper.setPropertyValue(propertyDescriptor.getName(), childEntity);
                    }
                }catch(Exception e){
                    result+=e.getMessage()+". ";
                }
            }
        }
        return result;
    }
    
    /**
     * Updates the target entity with the values of the source HashMap, avoiding
     * null valued fields.
     *
     * @param jsonObject
     * @param target entity to update.
     * @param columns
     * @return
     */
    public static String updateEntity(String jsonObject, Map target, List<GenericTableColumn> columns) {
        String result="";
        JSONObject source= new JSONObject(jsonObject);

        for (GenericTableColumn column : columns) {
            String value = null;
            if(source.has(column.getColumnAlias())){
                value= source.get(column.getColumnAlias()).toString();
            }
            String propertyType = column.getDataType();
            if (value != null) {
                try{
                    Object parseValue = Formats.castParameter(propertyType, value);
                    target.put(column.getColumnAlias(), parseValue);
                    if(column.getColumnAlias().equals("id") && value.equals("0")){
                        target.put(column.getColumnAlias(), null);
                    }
                }catch(Exception e){
                    result+=e.getMessage()+". ";
                }
            }
        }
        return result;
    }
    
    /**
     * Updates the target entity with the values of the source JSONObject, avoiding
     * null valued fields.
     *
     * @param jsonObject
     * @param entityClass
     * @return
     */
    public static BaseEntity readEntity(String jsonObject, Class entityClass) {
        JSONObject source= new JSONObject(jsonObject);
        BaseEntity entity = (BaseEntity) EntityReflection.getObjectForClass(entityClass);
        BeanWrapperImpl targetWrapper = new BeanWrapperImpl(entity);
        PropertyDescriptor[] propertyDescriptors = targetWrapper.getPropertyDescriptors();

        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String value = null;
            if(source.has(propertyDescriptor.getName())){
                value= source.get(propertyDescriptor.getName()).toString();
            }
            Class<?> typeWrapper = propertyDescriptor.getPropertyType();
            if (value != null && targetWrapper.isWritableProperty(propertyDescriptor.getName())) {
                try{
                    Object parseValue = Formats.castParameter(typeWrapper.getName(), value);
                    if (parseValue != null) {
                        targetWrapper.setPropertyValue(propertyDescriptor.getName(), parseValue);
                        if(propertyDescriptor.getName().equals("id") && value.equals("0")){
                            targetWrapper.setPropertyValue(propertyDescriptor.getName(), null);
                        }
                    } else if(value.equals("0") || value.equals("")) {
                        targetWrapper.setPropertyValue(propertyDescriptor.getName(), null);
                    } else {
                        Object id= getParsedFieldValue(typeWrapper, "id", value);
                        BaseEntity childEntity = (BaseEntity) getObjectForClass(typeWrapper);
                        childEntity.setId(id);
                        targetWrapper.setPropertyValue(propertyDescriptor.getName(), childEntity);
                    }
                }catch(Exception e){
                    Logger.getLogger(EntityReflection.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }
        return entity;
    }
    
    /**
     * Updates the target entity with the values of the source JSONObject, avoiding
     * null valued fields.
     *
     * @param jsonObject
     * @param columns
     * @return
     */
    public static Map<String, Object> readEntity(String jsonObject, List<GenericTableColumn> columns) {
        JSONObject source= new JSONObject(jsonObject);
        Map<String, Object> entity = new HashMap<>();

        for (GenericTableColumn column : columns) {
            String value = null;
            if(source.has(column.getColumnAlias())){
                value= source.get(column.getColumnAlias()).toString();
            }
            String propertyType= column.getDataType();
            if (value != null) {
                try{
                    Object parseValue = Formats.castParameter(propertyType, value);
                    if (parseValue != null) {
                        entity.put(column.getColumnAlias(), parseValue);
                        if(column.getColumnAlias().equals("id") && value.equals("0")){
                            entity.put(column.getColumnAlias(), null);
                        }
                    }
                }catch(Exception e){
                    Logger.getLogger(EntityReflection.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }
        return entity;
    }
    
    /**
     * Updates the target entity with the values of the source JSONObject, avoiding
     * null valued fields.
     *
     * @param jsonObject
     * @param dtoClass
     * @return
     */
    public static Object jsonToObject(String jsonObject, Class dtoClass) {
        return jsonToObject(jsonObject, dtoClass, false);
    }
    
    /**
     * Updates the target entity with the values of the source JSONObject, avoiding
     * null valued fields.
     *
     * @param jsonObject
     * @param dtoClass
     * @param hideLogFields
     * @return
     */
    public static Object jsonToObject(String jsonObject, Class dtoClass, boolean hideLogFields) {
        JSONObject source= new JSONObject(jsonObject);
        Object entity = EntityReflection.getObjectForClass(dtoClass);
        BeanWrapperImpl targetWrapper = new BeanWrapperImpl(entity);
        Set<String> hiddenLogFields= new HashSet<>();
        if(hideLogFields){
            hiddenLogFields= getHiddenLogFields(dtoClass);
        }
        PropertyDescriptor[] propertyDescriptors = targetWrapper.getPropertyDescriptors();

        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String fieldName= propertyDescriptor.getName();
            String type = propertyDescriptor.getPropertyType().getName();
            
            if(type.equals("java.lang.Class")){
                continue;
            }
            if(type.equals("org.json.JSONObject")){
                if(source.has(fieldName)){
                    JSONObject value= source.getJSONObject(fieldName);
                    if (value != null && targetWrapper.isWritableProperty(fieldName)) {
                        targetWrapper.setPropertyValue(fieldName, value);
                    }
                }
            }else if(type.equals("org.json.JSONArray")){
                if(source.has(fieldName)){
                    JSONArray value= source.getJSONArray(fieldName);
                    if (value != null && targetWrapper.isWritableProperty(fieldName)) {
                        targetWrapper.setPropertyValue(fieldName, value);
                    }
                }
            }else if(type.equals("java.util.List")==false){
                String value = null;
                if(source.has(fieldName)){
                    value= source.get(fieldName).toString();
                }
                Class<?> typeWrapper = propertyDescriptor.getPropertyType();
                if (value != null && targetWrapper.isWritableProperty(fieldName)) {
                    try{
                        Object parseValue = Formats.castParameter(typeWrapper.getName(), value);
                        if (parseValue != null) {
                            if(!hiddenLogFields.contains(fieldName)){
                                targetWrapper.setPropertyValue(fieldName, parseValue);
                                if(fieldName.equals("id") && value.equals("0")){
                                    targetWrapper.setPropertyValue(fieldName, null);
                                }
                            }
                        } else if(value.equals("0") || value.equals("")) {
                            targetWrapper.setPropertyValue(fieldName, null);
                        } else {
                            Object childObject= jsonToObject(value, typeWrapper, hideLogFields);
                            targetWrapper.setPropertyValue(fieldName, childObject);
                        }
                    }catch(Exception e){
                        Logger.getLogger(EntityReflection.class.getName()).log(Level.SEVERE, null, e);
                    }
                }
            }else if(type.equals("java.util.List")){
                Class childClass = ReflectionUtils.getParametrizedTypeList(dtoClass, fieldName);
                if(source.has(fieldName)){
                    JSONArray itemList= source.getJSONArray(fieldName);
                    List itemsObject= new ArrayList();
                    for(int i=0; i<itemList.length(); i++){
                        if(!itemList.isNull(i)){
                            try {
                                Object parseValue = Formats.castParameter(childClass.getName(), itemList.get(i).toString());
                                if(parseValue!=null){
                                    itemsObject.add(parseValue);
                                }else{
                                    Object childObject= jsonToObject(itemList.get(i).toString(), childClass, hideLogFields);
                                    itemsObject.add(childObject);
                                }
                            } catch (ClassNotFoundException | NumberFormatException ex) {
                                Logger.getLogger(EntityReflection.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                    targetWrapper.setPropertyValue(fieldName, itemsObject);
                }
            }
        }
        return entity;
    }

    /**
     * Updates the target entity with the values of the source entity, avoiding
     * null valued fields.
     *
     * @param source entity with new values.
     * @param target entity to update.
     */
    public static void updateEntity(Object source, Object target) {
        BeanWrapperImpl sourceWrapper = new BeanWrapperImpl(source);
        BeanWrapperImpl targetWrapper = new BeanWrapperImpl(target);

        PropertyDescriptor[] propertyDescriptors = sourceWrapper.getPropertyDescriptors();

        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            Object value = sourceWrapper.getPropertyValue(propertyDescriptor.getName());
            String typeSource = propertyDescriptor.getPropertyType().getName();
            if (value != null && targetWrapper.isWritableProperty(propertyDescriptor.getName())) {
                String typeTarget = targetWrapper.getPropertyTypeDescriptor(propertyDescriptor.getName()).getName();
                if (typeSource.equals(typeTarget)) {
                    targetWrapper.setPropertyValue(propertyDescriptor.getName(), value);
                }
            }
        }
    }
    
    public static Set<String> getHiddenLogFields(Class dtoClass){
        Set<String> hiddenLogFields= new HashSet<>();
        List<Field> annotatedFields= getEntityAnnotatedFields(dtoClass, HideField.class);
        for(Field f: annotatedFields){
            HideField an= f.getAnnotation(HideField.class);
            HideView[] views= an.value();
            for(HideView v: views){
                if(v.equals(HideView.LOG)){
                    hiddenLogFields.add(f.getName());
                }
            }
        }
        
        return hiddenLogFields;
    }

    /**
     * Retorna el listado de propiedades de una clase.
     *
     * @param entityClass
     * @return
     */
    public static PropertyDescriptor[] getPropertyDescriptors(Class<?> entityClass) {
        Object object= getObjectForClass(entityClass);
        if(object!=null){
            BeanWrapperImpl targetWrapper = new BeanWrapperImpl(object);
            PropertyDescriptor[] propertyDescriptors = targetWrapper.getPropertyDescriptors();

            return propertyDescriptors;
        }else{
            return new PropertyDescriptor[0];
        }
    }
    
    /**
     * Retorna el valor parseado del un determinado atributo de la entidad.
     *
     * @param entityClass
     * @param fieldName
     * @param value
     * @return
     * @throws java.lang.ClassNotFoundException
     */
    public static Object getParsedFieldValue(Class entityClass, String fieldName, String value) throws ClassNotFoundException{
        PropertyDescriptor[] propertiesParam = getPropertyDescriptors(entityClass);
        Class typeParam = EntityReflection.getPropertyType(propertiesParam, fieldName);
        
        return Formats.castParameter(typeParam.getName(), value);
    }

    /**
     * Retorna el tipo de variable de un determinado atributo.
     *
     * @param properties
     * @param fieldName
     * @return
     */
    public static Class getPropertyType(PropertyDescriptor[] properties, String fieldName) {
        for (PropertyDescriptor property : properties) {
            if (property.getDisplayName().equals(fieldName)) {
                return property.getPropertyType();
            }
        }
        return null;
    }

    /**
     * Lists all entity fields that should be exposed in a form.
     *
     * @param entityClass the entity class
     * @return an array of fields.
     */
    public static List<Field> getEntityIdFields(Class<?> entityClass) {
        final List<Field> filtered = new ArrayList<>();

        ReflectionUtils.doInHierarchy(entityClass, Object.class, new HierarchyVisitor() {
            @Override
            public Object doInClass(Class<?> herarchyClass) {
                Field[] entityFields = herarchyClass.getDeclaredFields();
                for (Field field : entityFields) {
                    if (field.getAnnotation(Id.class) != null) {
                        filtered.add(field);
                    }
                }
                return null;
            }
        });

        return filtered;
    }
    
    /**
     * Lists all entity fields that should be exposed in a form.
     *
     * @param entityClass the entity class
     * @param annotationClass
     * @return an array of fields.
     */
    public static List<Field> getEntityAnnotatedFields(Class<?> entityClass, Class<?> annotationClass) {
        final List<Field> filtered = new ArrayList<>();
        
        Field[] entityFields = entityClass.getDeclaredFields();
        for (Field field : entityFields) {
            if (field.getAnnotation((Class) annotationClass) != null) {
                filtered.add(field);
            }
        }

        return filtered;
    }
    
    /**
     * Lists all entity fields that should be exposed in a form.
     *
     * @param entityClass the entity class
     * @param annotationClass
     * @return an array of fields.
     */
    public static List<Method> getClassAnnotatedMethods(Class<?> entityClass, Class<?> annotationClass) {
        final List<Method> filtered = new ArrayList<>();
        
        Method[] entityMethods = entityClass.getDeclaredMethods();
        for (Method method : entityMethods) {
            if (method.getAnnotation((Class) annotationClass) != null) {
                filtered.add(method);
            }
        }

        return filtered;
    }
    
    /**
     * Get Annotation from a Class of specific type.
     *
     * @param entityClass the entity class
     * @param annotationClass
     * @return an Annotation.
     */
    public static Annotation getClassAnnotation(Class<?> entityClass, Class<?> annotationClass) {
        Annotation ann= entityClass.getAnnotation((Class) annotationClass);
        if (entityClass.getAnnotation((Class) annotationClass) != null) {
            return ann;
        }
        return null;
    }

    /**
     * Clones the entity.
     *
     * @param entity the entity to clone.
     *
     * @return the clon.
     */
    public static Object clone(Object entity) {
        Object clon;
        try {
            clon = entity.getClass().newInstance();
            updateEntity(entity, clon);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return clon;
    }

    /**
     * Clones the entity.
     *
     * @param className
     *
     * @return the clon.
     */
    public static Object getObjectForClassName(String className) {
        try {
            Class objectClass = Class.forName(className);
            return getObjectForClass(objectClass);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(EntityReflection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Clones the entity.
     *
     * @param objectClass
     *
     * @return the clon.
     */
    public static Object getObjectForClass(Class objectClass) {
        try {
            Constructor<?> ctor = objectClass.getConstructor();
            Object object = ctor.newInstance();
            return object;
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(EntityReflection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Returns the entity id.
     *
     * @param entity
     * @return
     */
    public static Long getEntityId(Object entity) {
        return (Long) ReflectionUtils.getFieldValueRecursively(entity, Id.class);
    }

}
