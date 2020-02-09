/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.interfaces;

import com.lacv.jmagrexs.domain.BaseEntity;
import java.sql.Time;
import java.util.Date;

/**
 *
 * @author e11001a
 */
public interface DbOperationInterface extends BaseEntity {
    
    String getName();

    void setName(String name);

    String getType();

    void setType(String type);

    Date getRegistrationDate();

    void setRegistrationDate(Date registrationDate);

    Time getRecordTime();

    void setRecordTime(Time recordTime);

    String getDataNew();

    void setDataNew(String dataNew);

    String getEntityRef();

    void setEntityRef(String entityRef);

    String getEntityId();

    void setEntityId(String entityId);

    Boolean getSuccess();

    void setSuccess(Boolean success);

    String getMessage();

    void setMessage(String message);
    
    BaseEntity getUser();

    void setUser(BaseEntity user);

    MassiveOperationInterface getMassiveOperation();

    void setMassiveOperation(MassiveOperationInterface massiveOperation);
    
}
