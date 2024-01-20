package org.guce.process.dem.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.guce.core.documents.GuceDateAdapter;
import org.guce.core.entities.CoreAttachment;
import org.guce.core.entities.CoreCharger;
import org.guce.core.entities.CoreDecisionType;
import org.guce.core.entities.CoreGood;
import org.guce.core.entities.CoreRecord;
import org.guce.core.entities.CoreSignatory;

@DiscriminatorValue("DEMRegistration")
@SequenceGenerator(
        name = "DEM_SEQ",
        sequenceName = "DEM_SEQ",
        allocationSize = 1
)
@Entity
@Table(
        name = "DEM_MIN_REG"
)
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement(
        name = "CONTENT"
)
@XmlType(
        propOrder = {"officeCode",
                "demReference",
                "demDate",
                "expDate",
                "operatorName",
                "declarations",
                "chargerid",
                "goodList",
                "paiement",
                "signatory",
                "decision",
                "coreAttachmentList"}
)
public class DEMRegistration extends CoreRecord implements Serializable {
    @Column(
            name = "OFFICE_CODE",
            length = 35
    )
    private String officeCode;

    @Column(
            name = "REFERENCE_DEM",
            length = 35
    )
    private String demReference;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(
            name = "DEM_DATE"
    )
    private Date demDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(
            name = "EXP_DATE"
    )
    private Date expDate;
    
    
    @Column(
       name = "OPERATOR_NAME",
       length = 35
    )
    private String operatorName;

    

    @Transient
    private CoreDecisionType decision;

    @Transient
    private CoreSignatory signatory;

    @Transient
    public PaymentDocument.CONTENT.PAIEMENT paiement;

    @ManyToMany(
            targetEntity = DEMDeclaration.class
    )
    private List<DEMDeclaration> declarations;

    @XmlElement(
            name = "CODE_BUREAU"
    )
    public String getOfficeCode() {
        return officeCode;
    }

    public void setOfficeCode(String officeCode) {
        this.officeCode = officeCode;
    }

    @XmlElement(
            name = "NUMERO_DEM_MINCOMMERCE"
    )
    public String getDemReference() {
        return demReference;
    }

    public void setDemReference(String demReference) {
        this.demReference = demReference;
    }

    @XmlElement(
            name = "DATE_DEM_MINCOMMERCE"
    )
    @XmlJavaTypeAdapter(GuceDateAdapter.class)
    public Date getDemDate() {
        return demDate;
    }

    public void setDemDate(Date demDate) {
        this.demDate = demDate;
    }

    @XmlElement(
            name = "EXP_DATE_MIN"
    )
    @XmlJavaTypeAdapter(GuceDateAdapter.class)
    public Date getExpDate() {
        return expDate;
    }
    
    
    
   @XmlElement(
       name = "OPERATOR_NAME"
    )
    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }
    
    
    
    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    @XmlElementWrapper(
            name = "DECLARATIONS"
    )
    @XmlElements({
                    @XmlElement(name = "DECLARATION")})
    public List<DEMDeclaration> getDeclarations() {
        return declarations;
    }

    public void setDeclarations(List<DEMDeclaration> declarations) {
        this.declarations = declarations;
    }

    @XmlElement(
            name = "DECISION_ORGANISME"
    )
    public CoreDecisionType getDecision() {
        if(this.decision == null) {
            this.decision = new CoreDecisionType();
        }
        return decision;
    }

    public void setDecision(CoreDecisionType decision) {
        this.decision = decision;
    }

    @XmlElement(
            name = "SIGNATAIRE"
    )
    public CoreSignatory getSignatory() {
        if(this.signatory == null) {
            this.signatory = new CoreSignatory();
        }
        return signatory;
    }

    public void setSignatory(CoreSignatory signatory) {
        this.signatory = signatory;
    }

    @XmlElement(
            name = "PAIEMENT"
    )
    public PaymentDocument.CONTENT.PAIEMENT getPaiement() {
        return paiement;
    }

    public void setPaiement(PaymentDocument.CONTENT.PAIEMENT paiement) {
        this.paiement = paiement;
    }

    @Override
    @XmlElement(
            name = "CLIENT"
    )
    public CoreCharger getChargerid() {
        return super.getChargerid();
    }

    @Override
    public void setChargerid(CoreCharger chargerid) {
        super.setChargerid(chargerid);
    }

    @Override
    @XmlElementWrapper(
            name = "PIECES_JOINTES"
    )
    @XmlElements(@XmlElement(name = "PIECE_JOINTE", type = CoreAttachment.class))
    public List<CoreAttachment> getCoreAttachmentList() {
        return super.getCoreAttachmentList();
    }

    @Override
    public void setCoreAttachmentList(List<CoreAttachment> coreAttachmentList) {
        super.setCoreAttachmentList(coreAttachmentList);
    }

    @Override
    @XmlElementWrapper(
            name = "MARCHANDISES"
    )
    @XmlElements({@XmlElement(name = "MARCHANDISE", type = CoreGood.class)})
    public List<CoreGood> getGoodList() {
        return super.getGoodList();
    }

    @Override
    public void setGoodList(List<CoreGood> goodList) {
        super.setGoodList(goodList);
    }
}
