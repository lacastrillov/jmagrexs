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
 * @author grupot
 */
public interface LogProcesInterface extends BaseEntity {

    public String getMainProcessRef();

    public void setMainProcessRef(String mainProcessRef);

    public String getProcessName();

    public void setProcessName(String processName);

    public String getDataIn();

    public void setDataIn(String dataIn);

    public String getDataOut();

    public void setDataOut(String dataOut);
    
    public String getOutputDataFormat();
    
    public void setOutputDataFormat(String outputDataFormat);

    public Date getRegistrationDate();

    public void setRegistrationDate(Date registrationDate);

    public Time getRecordTime();

    public void setRecordTime(Time recordTime);

    public Integer getDuration();

    public void setDuration(Integer duration);

    public Boolean getSuccess();

    public void setSuccess(Boolean success);

    public String getMessage();

    public void setMessage(String message);

    public String getClientId();

    public void setClientId(String clientId);
    
}
