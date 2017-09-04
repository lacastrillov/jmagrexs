package com.dot.gcpbasedot.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase utilitaria para operaciones con Reflection.
 *
 * @author lacastrillov@gmail.com
 *
 */
@SuppressWarnings("unchecked")
public class ReflectionUtils {

    /**
     * Retorna el tipo parametrizado en la clase pasado por parametro.
     *
     * @param clazz clase de la cual se quiere obtener la tipo parametrizado.
     * @return el tipo parametrizado
     */
    public static Class getParametrizedType(Class clazz) {
        ParameterizedType pType = (ParameterizedType) clazz.getGenericSuperclass();
        Type type = pType.getActualTypeArguments()[0];

        return (Class) type;
    }

    /**
     * Retorna el tipo parametrizado en la clase pasado por parametro.
     *
     * @param clazz clase de la cual se quiere obtener la tipo parametrizado.
     * @param index
     * @return el tipo parametrizado
     */
    public static Class getParametrizedType(Class clazz, int index) {
        ParameterizedType pType = (ParameterizedType) clazz.getGenericSuperclass();
        Type type = pType.getActualTypeArguments()[index];
        return (Class) type;
    }
    
    /**
     * Retorna el tipo parametrizado en la clase pasado por parametro.
     *
     * @param clazz clase de la cual se quiere obtener la tipo parametrizado.
     * @param listName
     * @return el tipo parametrizado
     */
    public static Class getParametrizedTypeList(Class clazz, String listName) {
        Field listField;
        try {
            listField = clazz.getDeclaredField(listName);
            ParameterizedType listType= (ParameterizedType) listField.getGenericType();
            Class type = (Class) listType.getActualTypeArguments()[0];
            return (Class) type;
        } catch (NoSuchFieldException | SecurityException ex) {
            Logger.getLogger(ReflectionUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Crea una instancia de la clase especificada.
     *
     * @param clazz clase a instanciar.
     * @return la instancia o lanza una {@link RuntimeException} en caso de
     * error.
     */
    public static Object instanciate(Class clazz) {
        Object instance;
        try {
            instance = clazz.newInstance();
            return instance;
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error al crear una instancia", e);
        } catch (InstantiationException e) {
            throw new RuntimeException("Error al crear una instancia", e);
        }
    }

    /**
     * Busca el campo anotado con la Annotation especificada, dentro de la clase
     * dada, y setea el valor en la instancia provista.
     *
     * @param clazz clase sobre la cual se busca el campo y que la instancia
     * @param instance instancia sobre la cual setear el campo, debe ser
     * casteable a
     * @param annotation clase de la annotation usada para anotar el campo.
     * @param value valor a setear en el campo.
     * @return si la operacion fue exitosa.
     */
    public static boolean setField(Class clazz, Object instance, Class annotation, Object value) {
        Field[] fields = clazz.getDeclaredFields();
        Field annotatedField = null;

        for (Field field : fields) {
            if (field.getAnnotation(annotation) != null) {
                annotatedField = field;
            }
        }
        if (annotatedField != null) {
            annotatedField.setAccessible(true);
            try {
                annotatedField.set(instance, value);
                return true;
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            }

        }
        return false;
    }

    /**
     * Setea un valor al campo anotado de la instancia.
     *
     * @param instance objeto sobre el cual asignar el valor.
     * @param annotationClass clase de la annotation.
     * @param value valor a asginar en el campo.
     *
     * @return si se pudo realizar el seteo.
     */
    public static boolean setField(Object instance, Class annotationClass, Object value) {
        return setField(instance.getClass(), instance, annotationClass, value);
    }

    /**
     * Setea un valor al campo anotado de la instancia. La busqueda de la
     * annotation es recursiva sobre la jerarquia de clases de la instancia.
     *
     * @param instance objeto sobre el cual asignar el valor.
     * @param annotation clase de la annotation.
     * @param value valor a asginar en el campo.
     *
     * @return si se pudo realizar el seteo.
     */
    public static boolean setFieldRecursively(Object instance, Class annotation, Object value) {
        Class<? extends Object> instanceClass = instance.getClass();
        boolean succed = false;
        while (!instanceClass.equals(Object.class) && !succed) {
            succed = setField(instanceClass, instance, annotation, value);
            instanceClass = instanceClass.getSuperclass();
        }

        return succed;
    }

    /**
     * Retorna el valor de un atributo de la annotation en la calse. Ejemplo:
     * para la annotation @Table(name="LaTabla") retorna LaTabla para el
     * atributo name.
     *
     * @param clazz clase anotada.
     * @param annotation clase de la annotation que tiene el atributo.
     * @param attribute nombre del atributo.
     * @return valor del atributo o null si no lo encuentra.
     */
    public static Object getAnnotationAttribute(Class clazz, Class annotation, String attribute) {
        Annotation annotationInstance = clazz.getAnnotation(annotation);
        if (annotationInstance != null) {
            try {
                Method method = annotation.getDeclaredMethod(attribute);
                if (method != null) {
                    Object[] args = new Object[]{};
                    return method.invoke(annotationInstance, args);
                }
                return null;
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (SecurityException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    /**
     * Busca el atributo de la anotation recusivamente en la jerarquia de
     * clases. Ejemplo: para la annotation @Table(name="LaTabla") retorna
     * LaTabla para el atributo name. En caso de que la annotation sea usada en
     * mas de una clase de la misma jerarquia, se queda con la definicion de la
     * clase mas generica.
     *
     * @param clazz clase a partir de la cual buscar la annotation.
     * @param annotation la clase de la anntoation a bucar
     * @param attribute el nombre del atributo en la annotation.
     * @return el valor o null si no se encontro.
     */
    public static Object getAnnotationAttributeRecursively(Class clazz, Class annotation, String attribute) {
        Class lookClass = clazz;
        Object attValue = null;
        Object parcialValue;
        while (!lookClass.equals(Object.class)) {
            parcialValue = getAnnotationAttribute(lookClass, annotation, attribute);
            if (parcialValue != null) {
                attValue = parcialValue;
            }
            lookClass = lookClass.getSuperclass();
        }
        return attValue;

    }

    /**
     * Busca el valor del campo anotado recursivamente en la jerarquia de clases
     * de la instancia.
     *
     * @param instance donde buscar el campo.
     * @param annotation clase de la annotation en el campo.
     * @return el valor o null si no se encuentra un campo anotado.
     */
    public static Object getFieldValueRecursively(Object instance, Class annotation) {
        Class lookClass = instance.getClass();
        while (!lookClass.equals(Object.class)) {
            Field[] fields = lookClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.getAnnotation(annotation) != null) {
                    try {
                        field.setAccessible(true);
                        return field.get(instance);
                    } catch (IllegalAccessException th) {
                        throw new RuntimeException(th);
                    } catch (IllegalArgumentException th) {
                        throw new RuntimeException(th);
                    } catch (SecurityException th) {
                        throw new RuntimeException(th);
                    }
                }
            }
            lookClass = lookClass.getSuperclass();
        }

        return null;
    }

    /**
     * Recorre la jerarquia de clases aplicando la accion en cada clase. Es una
     * implementacion del patron visitor, sobre la jerarquia de clases.
     *
     * @param start donde comenzar a aplicar la accion.
     * @param end hasta donde aplicar la accion.
     * @param visitor visitor particular.
     * @return el resultado de la accion.
     */
    public static Object doInHierarchy(Class start, Class end, HierarchyVisitor visitor) {
        Class lookClass = start;
        Object value = null;
        while (!lookClass.equals(end)) {
            value = visitor.doInClass(lookClass);
            lookClass = lookClass.getSuperclass();
        }
        return value;
    }

    /**
     * Returns the values of the specified fields seaching recusively.
     *
     * @param instance
     * @param fields fields names.
     * @return the values.
     */
    public static Object[] getFieldValues(final Object instance, final String[] fields) {
        final Object[] values = new Object[fields.length];
        doInHierarchy(instance.getClass(), Object.class, new HierarchyVisitor() {
            @Override
            public Object doInClass(Class<?> herarchyClass) {
                for (int i = 0; i < fields.length; i++) {
                    try {
                        Field f = herarchyClass.getDeclaredField(fields[i]);
                        f.setAccessible(true);
                        values[i] = f.get(instance);
                    } catch (NoSuchFieldException e) {

                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Error when accessing field " + fields[i], e);
                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException("Error when accessing field " + fields[i], e);
                    } catch (SecurityException e) {
                        throw new RuntimeException("Error when accessing field " + fields[i], e);
                    }

                }
                return null;
            }
        });

        return values;
    }

    public static Class<? extends Object> getAnnotatedClass(Object object, Annotation annotation) {
        Class lookClass = object.getClass();
        while (!lookClass.equals(Object.class)) {
            Annotation inClass = lookClass.getAnnotation(annotation.annotationType());
            if (inClass == annotation) {
                return lookClass;
            }
            lookClass = lookClass.getSuperclass();
        }

        return null;
    }

    /**
     * Determines if the field has at least one of the specified annotations.
     *
     * @param field where to search the annotations.
     * @param annotations the classes of the annotations.
     * @return
     */
    public static boolean hasAnyAnnotation(Field field, Class<?>[] annotations) {
        for (Class annClass : annotations) {
            if (field.getAnnotation(annClass) != null) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returs the value of the field in the class.
     *
     * @param object
     * @param field
     * @return
     */
    public static Object getFieldValue(Object object, String field) {
        try {
            Field f = object.getClass().getDeclaredField(field);
            if (f != null) {
                f.setAccessible(true);
                return f.get(object);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error when looking for field " + field);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error when looking for field " + field);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Error when looking for field " + field);
        } catch (SecurityException e) {
            throw new RuntimeException("Error when looking for field " + field);
        }

        return null;

    }

    /**
     * Returns the type of the field in the specified class. It follws any field
     * path defined with '_'. For example with name person_addres it will return
     * the type of address.
     *
     * @param clazz
     * @param name
     * @return
     */
    public static Class<?> getFieldType(Class<?> clazz, String name) {
        try {
            String[] path = name.split("_");
            Class<?> search = clazz;
            Field f;
            for (String fieldName : path) {
                f = getFieldInHerarchy(search, fieldName);
                search = f.getType();
            }
            return search;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Searches a field in the hierachy.
     *
     * @param clazz
     * @param fieldName
     * @return
     */
    public static Field getFieldInHerarchy(Class<?> clazz, String fieldName) {
        Class<?> search = clazz;
        Field f = null;
        while (!search.equals(Object.class) && f == null) {
            try {
                f = search.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                f = null;
            } catch (SecurityException e) {
                f = null;
            }
            search = search.getSuperclass();
        }

        return f;
    }

}
