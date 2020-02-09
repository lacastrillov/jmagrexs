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
public interface MassiveOperationInterface extends BaseEntity {
    
    String getName();

    void setName(String name);

    String getType();

    void setType(String type);

    Date getRegistrationDate();

    void setRegistrationDate(Date registrationDate);

    Time getRecordTime();

    void setRecordTime(Time recordTime);

    Integer getTotal();

    void setTotal(Integer total);

    Integer getProcessed();

    void setProcessed(Integer processed);

    Integer getPercentage();

    void setPercentage(Integer percentage);

    Integer getTotalSuccessful();

    void setTotalSuccessful(Integer totalSuccessful);

    Integer getTotalFailed();

    void setTotalFailed(Integer totalFailed);

    Integer getDuration();

    void setDuration(Integer duration);

    String getStatus();

    void setStatus(String status);

    String getMessage();

    void setMessage(String message);
    
    BaseEntity getUser();

    void setUser(BaseEntity user);
    
}
