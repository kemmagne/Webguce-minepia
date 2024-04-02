/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guce.process.atm.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.xml.bind.annotation.XmlTransient;
import org.guce.core.entities.CoreUser;

/**
 *
 * @author NGC
 */
@MappedSuperclass
@XmlTransient
public class AbstractEntity implements Serializable{
    
    @Id
    @SequenceGenerator(
            name = "ATM_CRUD_SEQ",
            sequenceName = "ATM_CRUD_SEQ",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "ATM_CRUD_SEQ"
    )
    @Column(name = "ID")
    private String id;
    
    @Column(name = "CREATE_DATE")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date createDate;
    
    @Column(name = "WRITE_DATE")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date writeDate;
    
    @ManyToOne
    @JoinColumn(name = "CREATE_USER")
    private CoreUser createUser;
    
    @ManyToOne
    @JoinColumn(name = "WRITE_USER")
    private CoreUser writeUser;
    
    @Column(name = "ENTITY_SEQUENCE")
    private Long sequence;
    
    @Column(name = "ACTIVE")
    private Boolean active;
    
    private boolean deleted = false;


    public AbstractEntity() {
    }

    @XmlTransient
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlTransient
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @XmlTransient
    public Date getWriteDate() {
        return writeDate;
    }

    public void setWriteDate(Date writeDate) {
        this.writeDate = writeDate;
    }

    @XmlTransient
    public CoreUser getCreateUser() {
        return createUser;
    }

    public void setCreateUser(CoreUser createUser) {
        this.createUser = createUser;
    }

    @XmlTransient
    public CoreUser getWriteUser() {
        return writeUser;
    }

    public void setWriteUser(CoreUser writeUser) {
        this.writeUser = writeUser;
    }

    @XmlTransient
    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }

    @XmlTransient
    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
    @XmlTransient
    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
    
    
    @Override
    public boolean equals(Object obj) {
            if(obj == null)
                    return false;
            if(!(obj.getClass() == getClass()))
                    return false;
            return ((AbstractEntity)obj).getId().equals(this.id);
    }
    
    @Override
    public int hashCode() {
        int hash = 1;
        return hash * 31 + id.hashCode();
    }
    
}
