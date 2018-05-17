/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.interfaces;

import com.lacv.jmagrexs.domain.BaseEntity;

/**
 *
 * @author lacastrillov
 */
public interface JsonObjectInterface extends BaseEntity {

    String getType();

    void setType(String type);

    String getRelatedEntity();

    void setRelatedEntity(String relatedEntity);

    Integer getRelatedId();

    void setRelatedId(Integer relatedId);

    String getData();

    void setData(String data);
    
}
