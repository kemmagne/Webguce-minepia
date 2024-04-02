package org.guce.process.atm.filters;

import java.util.Date;
import java.util.List;
import org.guce.core.entities.CoreCharger;
import org.guce.core.services.SearchFilter;
import org.guce.process.atm.entities.AvisTech;
import org.guce.process.atm.entities.TypeAvtech;

public class ATMRegistrationFilter extends SearchFilter {
    private List<String> officeCode;

    private List<String> atmReference;

    private Date atmIssueDateStartFilter;

    private Date atmIssueDateEndFilter;

    private Date atmExpiryDateStartFilter;

    private Date atmExpiryDateEndFilter;

    private List<TypeAvtech> typeAtech;

    private List<CoreCharger> supplier;

    private TransportFilter transport;

    private InvoiceFilter invoice;

    private List<AvisTech> avisTech;

    public List<String> getOfficeCode() {
        return officeCode;
    }

    public void setOfficeCode(List<String> officeCode) {
        this.officeCode = officeCode;
    }

    public List<String> getAtmReference() {
        return atmReference;
    }

    public void setAtmReference(List<String> atmReference) {
        this.atmReference = atmReference;
    }

    public Date getAtmIssueDateStartFilter() {
        return atmIssueDateStartFilter;
    }

    public Date getAtmIssueDateEndFilter() {
        return atmIssueDateEndFilter;
    }

    public void setAtmIssueDateStartFilter(Date atmIssueDateStartFilter) {
        this.atmIssueDateStartFilter = atmIssueDateStartFilter;
    }

    public void setAtmIssueDateEndFilter(Date atmIssueDateEndFilter) {
        this.atmIssueDateEndFilter = atmIssueDateEndFilter;
    }

    public Date getAtmExpiryDateStartFilter() {
        return atmExpiryDateStartFilter;
    }

    public Date getAtmExpiryDateEndFilter() {
        return atmExpiryDateEndFilter;
    }

    public void setAtmExpiryDateStartFilter(Date atmExpiryDateStartFilter) {
        this.atmExpiryDateStartFilter = atmExpiryDateStartFilter;
    }

    public void setAtmExpiryDateEndFilter(Date atmExpiryDateEndFilter) {
        this.atmExpiryDateEndFilter = atmExpiryDateEndFilter;
    }

    public List<TypeAvtech> getTypeAtech() {
        return typeAtech;
    }

    public void setTypeAtech(List<TypeAvtech> typeAtech) {
        this.typeAtech = typeAtech;
    }

    public List<CoreCharger> getSupplier() {
        return supplier;
    }

    public void setSupplier(List<CoreCharger> supplier) {
        this.supplier = supplier;
    }

    public TransportFilter getTransport() {
        return transport;
    }

    public void setTransport(TransportFilter transport) {
        this.transport = transport;
    }

    public InvoiceFilter getInvoice() {
        return invoice;
    }

    public void setInvoice(InvoiceFilter invoice) {
        this.invoice = invoice;
    }

    public List<AvisTech> getAvisTech() {
        return avisTech;
    }

    public void setAvisTech(List<AvisTech> avisTech) {
        this.avisTech = avisTech;
    }
}
