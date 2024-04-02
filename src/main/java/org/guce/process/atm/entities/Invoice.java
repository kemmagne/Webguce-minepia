package org.guce.process.atm.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.guce.core.documents.GuceDateAdapter;
import org.guce.rep.entities.RepCurrency;

@Embeddable
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@XmlType(
        propOrder = {"invoiceNumber",
                "invoiceDate",
                "invoiceAmount",
                "currency"}
)
public class Invoice implements Serializable {
    @Column(
            name = "INVOICE_NUMBER",
            length = 150
    )
    private String invoiceNumber;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(
            name = "INVOICE_DATE"
    )
    private Date invoiceDate;

    @Column(
            name = "INVOICE_AMOUNT"
    )
    private BigDecimal invoiceAmount;

    @ManyToOne(
            targetEntity = RepCurrency.class
    )
    @JoinColumn(
            name = "CURRENCY_ID"
    )
    private RepCurrency currency;

    @XmlElement(
            name = "NUMERO_FACTURE"
    )
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    @XmlElement(
            name = "DATE_FACTURE"
    )
    @XmlJavaTypeAdapter(GuceDateAdapter.class)
    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    @XmlElement(
            name = "MONTANT_FACTURE"
    )
    public BigDecimal getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(BigDecimal invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    @XmlElement(
            name = "DEVISE_FACTURE"
    )
    public RepCurrency getCurrency() {
        return currency;
    }

    public void setCurrency(RepCurrency currency) {
        this.currency = currency;
    }
}
