/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.mapper;

import com.lacv.jmagrexs.domain.BaseDto;
import com.lacv.jmagrexs.domain.BaseEntity;
import java.util.List;

/**
 *
 * @author lacastrillov
 * @param <T>
 * @param <F>
 */
public interface EntityMapper<T extends BaseEntity, F extends BaseDto> {

    /**
     * 
     * @return 
     */
    Class getEntityClass();
    
    /**
     * 
     * @return 
     */
    Class getDtoClass();
    /**
     *
     * @param entity
     * @return
     */
    F entityToDto(T entity);

    /**
     *
     * @param entities
     * @return
     */
    List<F> listEntitiesToListDtos(List<T> entities);
    
    /**
     *
     * @param dto
     * @return
     */
    T dtoToEntity(F dto);

    /**
     *
     * @param dtos
     * @return
     */
    List<T> listDtosToListEntities(List<F> dtos);

}
