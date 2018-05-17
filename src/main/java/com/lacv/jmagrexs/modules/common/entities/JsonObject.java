/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.modules.common.entities;

import com.lacv.jmagrexs.interfaces.JsonObjectInterface;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author lacastrillov
 */
@Entity
@Table(name = "json_object")
@NamedQueries({
    @NamedQuery(name = "JsonObject.findAll", query = "SELECT c FROM JsonObject c")})
public class JsonObject implements JsonObjectInterface {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "type")
    private String type;
    @Size(max = 45)
    @Column(name = "related_entity")
    private String relatedEntity;
    @Column(name = "related_id")
    private Integer relatedId;
    @Lob
    @Size(max = 65535)
    @Column(name = "data")
    private String data;

    public JsonObject() {
    }

    public JsonObject(Integer id) {
        this.id = id;
    }

    public JsonObject(Integer id, String type) {
        this.id = id;
        this.type = type;
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
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getRelatedEntity() {
        return relatedEntity;
    }

    @Override
    public void setRelatedEntity(String relatedEntity) {
        this.relatedEntity = relatedEntity;
    }

    @Override
    public Integer getRelatedId() {
        return relatedId;
    }

    @Override
    public void setRelatedId(Integer relatedId) {
        this.relatedId = relatedId;
    }

    @Override
    public String getData() {
        return data;
    }

    @Override
    public void setData(String data) {
        this.data = data;
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
        if (!(object instanceof JsonObject)) {
            return false;
        }
        JsonObject other = (JsonObject) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.lacv.marketplatform.entities.JsonObject[ id=" + id + " ]";
    }
    
}
