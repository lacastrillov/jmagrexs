/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dot.gcpbasedot.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author lacastrillov
 */
@XmlRootElement
public class OperationCallback {

    private boolean success;

    private String message;

    private Object data;

    /**
     * @return the success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @param success the success to set
     */
    @XmlElement
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    @XmlElement
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the data
     */
    public Object getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    @XmlElement
    public void setData(Object data) {
        this.data = data;
    }

}
