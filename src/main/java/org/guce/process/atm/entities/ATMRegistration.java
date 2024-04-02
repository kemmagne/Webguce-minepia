package org.guce.process.atm.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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

@DiscriminatorValue("ATMRegistration")
@SequenceGenerator(
        name = "ATM_SEQ",
        sequenceName = "ATM_SEQ",
        allocationSize = 1
)
@Entity
@Table(
        name = "ATM_MINEPIA_REGISTRATION"
)
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement(
        name = "CONTENT"
)
@XmlType(
        propOrder = {"officeCode",
                "atmReference",
                "atmIssueDate",
                "atmExpiryDate",
                "avisTech",
                "typeAtech",
                "chargerid",
                "supplier",
                "invoice",
                "transport",
                "goodList",
                "paiement",
                "signatory",
                "decision",
                "coreAttachmentList"}
)
public class ATMRegistration extends CoreRecord implements Serializable {
    @Column(
            name = "OFFICE_CODE",
            length = 35
    )
    private String officeCode;

    @Column(
            name = "REFERENCE_AT",
            length = 35
    )
    private String atmReference;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(
            name = "ATM_DATE"
    )
    private Date atmIssueDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(
            name = "ATM_EXP_DATE"
    )
    private Date atmExpiryDate;

    @Transient
    private CoreDecisionType decision;

    @Transient
    private CoreSignatory signatory;

    @Transient
    public PaymentDocument.CONTENT.PAIEMENT paiement;

    @ManyToOne(
            targetEntity = TypeAvtech.class
    )
    @JoinColumn(
            name = "TYPEATECH_ID"
    )
    private TypeAvtech typeAtech;

    @ManyToOne(
            targetEntity = CoreCharger.class
    )
    @JoinColumn(
            name = "SUPPLIER_ID"
    )
    private CoreCharger supplier;

    @Embedded
    private Transport transport;

    @Embedded
    private Invoice invoice;

    @ManyToOne(
            targetEntity = AvisTech.class
    )
    @JoinColumn(
            name = "AVISTECH_ID"
    )
    private AvisTech avisTech;

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
            name = "NUMERO_VT_MINEPIA"
    )
    public String getAtmReference() {
        return atmReference;
    }

    public void setAtmReference(String atmReference) {
        this.atmReference = atmReference;
    }

    @XmlElement(
            name = "DATE_ATM_MINEPIA"
    )
    @XmlJavaTypeAdapter(GuceDateAdapter.class)
    public Date getAtmIssueDate() {
        return atmIssueDate;
    }

    public void setAtmIssueDate(Date atmIssueDate) {
        this.atmIssueDate = atmIssueDate;
    }

    @XmlElement(
            name = "DATE_ATM_EXP"
    )
    @XmlJavaTypeAdapter(GuceDateAdapter.class)
    public Date getAtmExpiryDate() {
        return atmExpiryDate;
    }

    public void setAtmExpiryDate(Date atmExpiryDate) {
        this.atmExpiryDate = atmExpiryDate;
    }

    @XmlElement(
            name = "TYPEAVIS"
    )
    public TypeAvtech getTypeAtech() {
        return typeAtech;
    }

    public void setTypeAtech(TypeAvtech typeAtech) {
        this.typeAtech = typeAtech;
    }

    @XmlElement(
            name = "FOURNISSEUR"
    )
    public CoreCharger getSupplier() {
        return supplier;
    }

    public void setSupplier(CoreCharger supplier) {
        this.supplier = supplier;
    }

    @XmlElement(
            name = "TRANSPORT"
    )
    public Transport getTransport() {
        if(this.transport == null) {
            this.transport = new Transport();
        }
        return transport;
    }

    public void setTransport(Transport transport) {
        this.transport = transport;
    }

    @XmlElement(
            name = "FACTURE"
    )
    public Invoice getInvoice() {
        if(this.invoice == null) {
            this.invoice = new Invoice();
        }
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    @XmlElement(
            name = "TECHNICALAVIS"
    )
    public AvisTech getAvisTech() {
        return avisTech;
    }

    public void setAvisTech(AvisTech avisTech) {
        this.avisTech = avisTech;
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
