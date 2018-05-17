/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.modules.mail.dtos;

import com.lacv.jmagrexs.annotation.ColumnWidth;
import com.lacv.jmagrexs.annotation.HideField;
import com.lacv.jmagrexs.annotation.LabelField;
import com.lacv.jmagrexs.annotation.Order;
import com.lacv.jmagrexs.annotation.ReadOnly;
import com.lacv.jmagrexs.annotation.TextField;
import com.lacv.jmagrexs.annotation.TypeFormField;
import com.lacv.jmagrexs.domain.BaseEntity;
import com.lacv.jmagrexs.enums.FieldType;
import com.lacv.jmagrexs.enums.HideView;
import java.util.List;

/**
 *
 * @author lacastrillov
 */
@LabelField("name")
public class MailTemplateDto implements BaseEntity {

    private static final long serialVersionUID = 1L;
    
    @Order(1)
    @ColumnWidth(100)
    @ReadOnly
    private Integer id;
    
    @Order(2)
    @TextField("Nombre")
    private String name;
    
    @Order(3)
    @TextField("Alias")
    private String alias;
    
    @Order(4)
    @TextField("Contenido")
    @TypeFormField(FieldType.HTML_EDITOR)
    @HideField({HideView.FILTER})
    @ColumnWidth(500)
    private String content;
    
    @Order(5)
    @TextField("Estado")
    @TypeFormField(value = FieldType.LIST, data = {"Active", "Inactive", "Locked", "Deleted"})
    private String status;
    
    @Order(6)
    @TextField("Total Enviados")
    @ReadOnly
    private Integer totalSent;
    
    private List<MailDto> mailList;

    public MailTemplateDto() {
    }

    public MailTemplateDto(Integer id) {
        this.id = id;
    }

    public MailTemplateDto(Integer id, String alias) {
        this.id = id;
        this.alias = alias;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Object id) {
        this.id = (Integer) id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public Integer getTotalSent() {
        return totalSent;
    }

    public void setTotalSent(Integer totalSent) {
        this.totalSent = totalSent;
    }

    public List<MailDto> getMailList() {
        return mailList;
    }

    public void setMailList(List<MailDto> mailList) {
        this.mailList = mailList;
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
        if (!(object instanceof MailTemplateDto)) {
            return false;
        }
        MailTemplateDto other = (MailTemplateDto) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.lacv.marketplatform.entities.MailTemplateDto[ id=" + id + " ]";
    }
    
}
