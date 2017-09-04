package com.dot.gcpbasedot.domain;

import java.io.Serializable;

public interface BaseEntity extends Serializable {

    public Object getId();

    public void setId(Object id);

}
