package com.lacv.jmagrexs.reflection;

/**
 * Interface que define el protocolo de un visitor de jerarquia de clases.
 *
 * @author lacastrillov@gmail.com
 *
 */
public interface HierarchyVisitor {

    /**
     * Accion a ejecutar sobre una clase de la jerarquia.
     *
     * @param hierarchyClass clase corriente en el recorrido.
     * @return resultado de la acccion o null si no genera ninguno.
     */
    Object doInClass(Class<?> hierarchyClass);

}
