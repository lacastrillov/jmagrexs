/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dot.gcpbasedot.mapper;

import com.dot.gcpbasedot.domain.BaseEntity;
import java.util.List;

/**
 *
 * @author lacastrillov
 * @param <T>
 * @param <F>
 */
public interface EntityMapper<T extends BaseEntity, F extends BaseEntity> {

    /**
     *
     * @param entity
     * @return
     */
    T entityToDto(F entity);

    /**
     *
     * @param entities
     * @return
     */
    List<T> listEntitiesToListDtos(List<F> entities);

}
