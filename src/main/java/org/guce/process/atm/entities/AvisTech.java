package org.guce.process.atm.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@Entity
@Table(
        name = "AVISTECH"
)
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@XmlRootElement(
        name = "AVISTECH"
)
@XmlType(
        propOrder = {}
)
public class AvisTech extends AbstractEntity implements Serializable {
    @Column(
            name = "REGISTERNUMBER",
            unique = true,
            length = 35
    )
    private String registerNumber;

    @Column(
            name = "IMMATRICULATION",
            length = 35
    )
    private String immatriculation;

    @Column(
            name = "TechName",
            length = 100
    )
    private String technicalName;

    @Column(
            name = "DELIVERY_DATE"
    )
    private String deliveryDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(
            name = "EXPIRATION_DATE"
    )
    private Date expiryDate;

    @Column(
            name = "STATUS",
            length = 100
    )
    private String status;

    @OneToMany(
            mappedBy = "itemId"
    )
    private List<AvisTechHistory> historyList;

    @XmlTransient
    public String getRegisterNumber() {
        return registerNumber;
    }

    public void setRegisterNumber(String registerNumber) {
        this.registerNumber = registerNumber;
    }

    @XmlTransient
    public String getImmatriculation() {
        return immatriculation;
    }

    public void setImmatriculation(String immatriculation) {
        this.immatriculation = immatriculation;
    }

    @XmlTransient
    public String getTechnicalName() {
        return technicalName;
    }

    public void setTechnicalName(String technicalName) {
        this.technicalName = technicalName;
    }

    @XmlTransient
    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    @XmlTransient
    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    @XmlTransient
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @XmlTransient
    public String getName() {
        return this.getRegisterNumber();
    }

    @XmlTransient
    public List<AvisTechHistory> getHistoryList() {
        return historyList;
    }

    public void setHistoryList(List<AvisTechHistory> historyList) {
        this.historyList = historyList;
    }
}
