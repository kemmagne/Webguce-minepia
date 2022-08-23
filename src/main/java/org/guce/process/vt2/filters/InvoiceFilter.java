package org.guce.process.vt2.filters;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.guce.core.services.SearchFilter;
import org.guce.rep.entities.RepCurrency;

public class InvoiceFilter extends SearchFilter {
    private List<String> invoiceNumber;

    private Date invoiceDateStartFilter;

    private Date invoiceDateEndFilter;

    private BigDecimal invoiceAmountStartFilter;

    private BigDecimal invoiceAmountEndFilter;

    private List<RepCurrency> currency;

    public List<String> getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(List<String> invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public Date getInvoiceDateStartFilter() {
        return invoiceDateStartFilter;
    }

    public Date getInvoiceDateEndFilter() {
        return invoiceDateEndFilter;
    }

    public void setInvoiceDateStartFilter(Date invoiceDateStartFilter) {
        this.invoiceDateStartFilter = invoiceDateStartFilter;
    }

    public void setInvoiceDateEndFilter(Date invoiceDateEndFilter) {
        this.invoiceDateEndFilter = invoiceDateEndFilter;
    }

    public BigDecimal getInvoiceAmountStartFilter() {
        return invoiceAmountStartFilter;
    }

    public BigDecimal getInvoiceAmountEndFilter() {
        return invoiceAmountEndFilter;
    }

    public void setInvoiceAmountStartFilter(BigDecimal invoiceAmountStartFilter) {
        this.invoiceAmountStartFilter = invoiceAmountStartFilter;
    }

    public void setInvoiceAmountEndFilter(BigDecimal invoiceAmountEndFilter) {
        this.invoiceAmountEndFilter = invoiceAmountEndFilter;
    }

    public List<RepCurrency> getCurrency() {
        return currency;
    }

    public void setCurrency(List<RepCurrency> currency) {
        this.currency = currency;
    }
}
