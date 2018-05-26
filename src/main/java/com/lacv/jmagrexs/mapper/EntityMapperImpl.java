/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.mapper;

import com.lacv.jmagrexs.domain.BaseDto;
import com.lacv.jmagrexs.domain.BaseEntity;
import com.lacv.jmagrexs.reflection.ReflectionUtils;


/**
 *
 * @author lacastrillov
 * @param <T>
 * @param <F>
 */
public abstract class EntityMapperImpl<T extends BaseEntity, F extends BaseDto> {

    private final Class<T> entityClass;

    private final Class<F> dtoClass;
    
    
    public EntityMapperImpl() {
        entityClass = ReflectionUtils.getParametrizedType(this.getClass(), 0);
        dtoClass = ReflectionUtils.getParametrizedType(this.getClass(),1);
    }
    
    public Class getEntityClass(){
        return entityClass;
    }
    
    public Class getDtoClass(){
        return dtoClass;
    }

    /**
     *
     * @param baseEntityF
     * @return
     *
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
    }*/

    /**
     *
     * @param entities
     * @return
     *
    public List<T> listEntitiesToListDtos(List entities) {
        ArrayList<T> dtos = new ArrayList<>();
        if (entities != null) {
            for (Object entity : entities) {
                dtos.add((T) entityToDto((BaseEntity) entity));
            }
        }
        return dtos;
    }*/

}
