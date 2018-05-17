/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.modules.fileexplorer.entities;

import com.lacv.jmagrexs.components.ServerDomain;
import com.lacv.jmagrexs.domain.BaseEntity;
import com.lacv.jmagrexs.modules.common.constants.SystemConstants;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

/**
 *
 * @author grupot
 */
@Entity
@Table(name = "web_file")
@NamedQueries({
    @NamedQuery(name = "WebFile.findAll", query = "SELECT w FROM WebFile w")})
public class WebFile implements BaseEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @Column(name = "type")
    private String type;
    @Column(name = "size")
    private Integer size;
    @Column(name = "icon")
    private String icon;
    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    @Column(name = "modification_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modificationDate;
    @Column(name = "access")
    private Integer access;
    @Column(name = "author")
    private String author;
    @OneToMany(mappedBy = "webFile", cascade = CascadeType.ALL)
    private List<WebFile> webFileList;
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    @ManyToOne
    private WebFile webFile;
    @Transient
    protected Object[] jdoDetachedState;

    public WebFile() {
    }

    public WebFile(Long id) {
        this.id = id;
    }

    public WebFile(Long id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Object id) {
        this.id = (Long) id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
    }

    public Integer getAccess() {
        return access;
    }

    public void setAccess(Integer access) {
        this.access = access;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<WebFile> getWebFileList() {
        return webFileList;
    }

    public void setWebFileList(List<WebFile> webFileList) {
        this.webFileList = webFileList;
    }

    public WebFile getWebFile() {
        return webFile;
    }

    public void setWebFile(WebFile webFile) {
        this.webFile = webFile;
    }
    
    public String getLocation() {
        ApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
        ServerDomain serverDomain= (ServerDomain) ctx.getBean("serverDomain");
        return serverDomain.getDomainWithPort() + "/" + SystemConstants.ROOT_FOLDER + getPath() + getName();
    }
    
    public String getPath(){
        String path="";
        WebFile parent= getWebFile();
        while(parent!=null){
            path= parent.getName()+"/"+path;
            parent= parent.getWebFile();
        }
        
        return path;
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
        if (!(object instanceof WebFile)) {
            return false;
        }
        WebFile other = (WebFile) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.lacv.marketplatform.entities.WebFile[ id=" + id + " ]";
    }
    
}
