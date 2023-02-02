package org.guce.process.vt2.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.guce.core.documents.GuceDateAdapter;
import org.guce.core.entities.CoreAttachment;
import org.guce.core.entities.CoreCAD;
import org.guce.core.entities.CoreCharger;
import org.guce.core.entities.CoreDecisionType;
import org.guce.core.entities.CoreGood;
import org.guce.core.entities.CoreRecord;
import org.guce.core.entities.CoreSignatory;
import org.guce.rep.entities.RepProductCategory;

@DiscriminatorValue("VT_MINEPDED")
@SequenceGenerator(
        name = "VT2_SEQ",
        sequenceName = "VT2_SEQ",
        allocationSize = 1
)
@Entity
@Table(
        name = "VT2_MINEPDED_REGISTRATION"
)
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement(
        name = "CONTENT"
)
@XmlType(
        propOrder = {"officeCode",
                "vtReference",
                "vtDate",
                "chargerid",
                "supplier",
                "cad",
                "invoice",
                "transport",
                "goodList",
                "paiement",
                "signatory",
                "decision",
                "coreAttachmentList"}
)
public class VT2Registration extends CoreRecord implements Serializable {
    @Column(
            name = "OFFICE_CODE",
            length = 35
    )
    private String officeCode;

    @Column(
            name = "REFERENCE_VT",
            length = 35
    )
    private String vtReference;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(
            name = "VT_DATE"
    )
    private Date vtDate;

    @Transient
    private CoreDecisionType decision;

    @Transient
    private CoreSignatory signatory;

    @Transient
    public PaymentDocument.CONTENT.PAIEMENT paiement;

    @ManyToOne(
            targetEntity = CoreCAD.class
    )
    @JoinColumn(
            name = "CAD_ID"
    )
    private CoreCAD cad;

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
    
    @Column(name = "VT_MINEPDED_FEES_AMOUNT")
    private BigDecimal feesAmount;
    
    @Column(name = "VT_MINEPDED_TOTAL_FEES_AMOUNT")
    private BigDecimal totalFeesAmount;
    
    @JoinTable(name = "EXPEDITION_PRODUCT_CATEGORY",
            joinColumns = {
                @JoinColumn(name = "RECORD_ID", referencedColumnName = "RECORD_ID")},
            inverseJoinColumns = {
                @JoinColumn(name = "CAT_CODE", referencedColumnName = "CODE")
            })
    @ManyToMany
    private List<RepProductCategory> productCategoryList;

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
            name = "NUMERO_VT_MINEPDED"
    )
    public String getVtReference() {
        return vtReference;
    }

    public void setVtReference(String vtReference) {
        this.vtReference = vtReference;
    }

    @XmlElement(
            name = "DATE_VT_MINEPDED"
    )
    @XmlJavaTypeAdapter(GuceDateAdapter.class)
    public Date getVtDate() {
        return vtDate;
    }

    public void setVtDate(Date vtDate) {
        this.vtDate = vtDate;
    }

    @XmlElement(
            name = "TRANSITAIRE"
    )
    public CoreCAD getCad() {
        return cad;
    }

    public void setCad(CoreCAD cad) {
        this.cad = cad;
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

    @XmlTransient
    public BigDecimal getFeesAmount() {
        return feesAmount;
    }

    public void setFeesAmount(BigDecimal feesAmount) {
        this.feesAmount = feesAmount;
    }

    @XmlTransient
    public List<RepProductCategory> getProductCategoryList() {
        return productCategoryList;
    }

    public void setProductCategoryList(List<RepProductCategory> productCategoryList) {
        this.productCategoryList = productCategoryList;
    }

    @XmlTransient
    public BigDecimal getTotalFeesAmount() {
        return totalFeesAmount;
    }

    public void setTotalFeesAmount(BigDecimal totalFeesAmount) {
        this.totalFeesAmount = totalFeesAmount;
    }
}
