/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.modules.mail.dtos;

import com.lacv.jmagrexs.annotation.ColumnWidth;
import com.lacv.jmagrexs.annotation.HideField;
import com.lacv.jmagrexs.annotation.Order;
import com.lacv.jmagrexs.annotation.ReadOnly;
import com.lacv.jmagrexs.annotation.TextField;
import com.lacv.jmagrexs.annotation.TypeFormField;
import com.lacv.jmagrexs.domain.BaseEntity;
import com.lacv.jmagrexs.enums.FieldType;
import com.lacv.jmagrexs.enums.HideView;
import java.util.Date;

/**
 *
 * @author lacastrillov
 */
public class MailDto implements BaseEntity {

    private static final long serialVersionUID = 1L;
    
    @Order(1)
    @ColumnWidth(100)
    @ReadOnly
    private Integer id;
    
    @Order(2)
    @TextField("De")
    private String mailFrom;
    
    @Order(3)
    @TextField("Para")
    private String mailTo;
    
    @Order(4)
    @TextField("Asunto")
    private String subject;
    
    @Order(5)
    @TextField("Mensaje")
    @TypeFormField(FieldType.HTML_EDITOR)
    @HideField({HideView.FILTER})
    @ColumnWidth(500)
    private String message;
    
    @Order(6)
    @TextField("Fecha Env&iacute;o")
    private Date sendDate;
    
    @Order(7)
    @TextField("Resultado")
    @TypeFormField(value = FieldType.LIST, data = {"Pendiente", "Enviado", "Rechazado", "Error"})
    private String result;
    
    private MailTemplateDto mailTemplate;

    public MailDto() {
    }

    public MailDto(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Object id) {
        this.id = (Integer) id;
    }

    public String getMailFrom() {
        return mailFrom;
    }

    public void setMailFrom(String mailFrom) {
        this.mailFrom = mailFrom;
    }

    public String getMailTo() {
        return mailTo;
    }

    public void setMailTo(String mailTo) {
        this.mailTo = mailTo;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public MailTemplateDto getMailTemplate() {
        return mailTemplate;
    }

    public void setMailTemplate(MailTemplateDto mailTemplate) {
        this.mailTemplate = mailTemplate;
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
        if (!(object instanceof MailDto)) {
            return false;
        }
        MailDto other = (MailDto) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.lacv.marketplatform.entities.MailDto[ id=" + id + " ]";
    }
    
}
