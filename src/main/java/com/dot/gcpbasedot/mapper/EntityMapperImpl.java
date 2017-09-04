/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dot.gcpbasedot.mapper;

import com.dot.gcpbasedot.domain.BaseEntity;
import com.dot.gcpbasedot.reflection.EntityReflection;
import com.dot.gcpbasedot.reflection.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lacastrillov
 * @param <T>
 * @param <F>
 */
public abstract class EntityMapperImpl<T extends BaseEntity, F extends BaseEntity> implements EntityMapper {

    private final Class<T> classT;

    // private final Class<F> classF;
    public EntityMapperImpl() {
        classT = ReflectionUtils.getParametrizedType(this.getClass(), 0);
        // classF= ReflectionUtils.getParametrizedType(this.getClass(),1);
    }

    /**
     *
     * @param baseEntityF
     * @return
     */
    @Override
    public T entityToDto(BaseEntity baseEntityF) {
        Constructor<?> ctor;
        try {
            ctor = classT.getConstructor();
            T entityT = (T) ctor.newInstance();
            F entityF = (F) baseEntityF;
            EntityReflection.updateEntity(entityF, entityT);

            return (T) entityT;
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(EntityMapperImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     *
     * @param entities
     * @return
     */
    @Override
    public List<T> listEntitiesToListDtos(List entities) {
        ArrayList<T> dtos = new ArrayList<>();
        if (entities != null) {
            for (Object entity : entities) {
                dtos.add((T) entityToDto((BaseEntity) entity));
            }
        }
        return dtos;
    }

}
