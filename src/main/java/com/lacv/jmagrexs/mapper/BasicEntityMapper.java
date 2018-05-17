/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.mapper;

import com.lacv.jmagrexs.domain.BaseEntity;
import java.util.List;

/**
 *
 * @author lacastrillov
 */
public interface BasicEntityMapper {
    
    /**
     *
     * @param entity
     * @return
     */
    BaseEntity entityToDto(BaseEntity entity);

    /**
     *
     * @param entities
     * @return
     */
    List<? extends BaseEntity> listEntitiesToListDtos(List<? extends BaseEntity> entities);
    
}
