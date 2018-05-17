/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.modules.common.entities;

import com.lacv.jmagrexs.interfaces.LogProcesInterface;
import java.sql.Time;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author grupot
 */
@Entity
@Table(name = "log_process")
@NamedQueries({
    @NamedQuery(name = "LogProcess.findAll", query = "SELECT l FROM LogProcess l")})
public class LogProcess implements LogProcesInterface {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "main_process_ref")
    private String mainProcessRef;
    @Basic(optional = false)
    @Column(name = "process_name")
    private String processName;
    @Lob
    @Column(name = "data_in")
    private String dataIn;
    @Lob
    @Column(name = "data_out")
    private String dataOut;
    @Column(name = "output_data_format")
    private String outputDataFormat;
    @Column(name = "registration_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date registrationDate;
    @Column(name = "record_time")
    private Time recordTime;
    @Column(name = "duration")
    private Integer duration;
    @Column(name = "success")
    private Boolean success;
    @Column(name = "message")
    private String message;
    @Column(name = "client_id")
    private String clientId;
    @Transient
    protected Object[] jdoDetachedState;

    public LogProcess() {
    }

    public LogProcess(Integer id) {
        this.id = id;
    }

    public LogProcess(Integer id, String mainProcessRef, String processName) {
        this.id = id;
        this.mainProcessRef = mainProcessRef;
        this.processName = processName;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Object id) {
        this.id = (Integer) id;
    }

    @Override
    public String getMainProcessRef() {
        return mainProcessRef;
    }

    @Override
    public void setMainProcessRef(String mainProcessRef) {
        this.mainProcessRef = mainProcessRef;
    }

    @Override
    public String getProcessName() {
        return processName;
    }

    @Override
    public void setProcessName(String processName) {
        this.processName = processName;
    }

    @Override
    public String getDataIn() {
        return dataIn;
    }

    @Override
    public void setDataIn(String dataIn) {
        this.dataIn = dataIn;
    }

    @Override
    public String getDataOut() {
        return dataOut;
    }

    @Override
    public void setDataOut(String dataOut) {
        this.dataOut = dataOut;
    }

    @Override
    public String getOutputDataFormat() {
        return outputDataFormat;
    }

    @Override
    public void setOutputDataFormat(String outputDataFormat) {
        this.outputDataFormat = outputDataFormat;
    }

    @Override
    public Date getRegistrationDate() {
        return registrationDate;
    }

    @Override
    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    @Override
    public Time getRecordTime() {
        return recordTime;
    }

    @Override
    public void setRecordTime(Time recordTime) {
        this.recordTime = recordTime;
    }

    @Override
    public Integer getDuration() {
        return duration;
    }

    @Override
    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    @Override
    public Boolean getSuccess() {
        return success;
    }

    @Override
    public void setSuccess(Boolean success) {
        this.success = success;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LogProcess)) {
            return false;
        }
        LogProcess other = (LogProcess) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.lacv.marketplatform.entities.LogProcess[ id=" + id + " ]";
    }
    
}
