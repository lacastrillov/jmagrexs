/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.interfaces;

import java.util.Date;

/**
 *
 * @author lacastrillov
 */

public interface WebEntityInterface {

    String getAuthor();

    void setAuthor(String author);

    Date getCreationDate();

    void setCreationDate(Date creationDate);

    Date getModificationDate();

    void setModificationDate(Date modificationDate);

    String getIcon();

    void setIcon(String icon);

    String getName();

    void setName(String name);

    String getEntityRef();

    void setEntityRef(String entityRef);

    String getEntityName();

    void setEntityName(String entityName);

    String getEntityId();

    void setEntityId(String entityId);

    Integer getEntityOrder();

    void setEntityOrder(Integer entityOrder);

    String getStatus();

    void setStatus(String status);
    
    String getLocation();
    
    String getPath();
    
}
