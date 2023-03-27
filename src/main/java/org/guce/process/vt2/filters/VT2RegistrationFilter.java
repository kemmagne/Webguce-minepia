package org.guce.process.vt2.filters;

import java.util.Date;
import java.util.List;
import org.guce.core.entities.CoreCAD;
import org.guce.core.entities.CoreCharger;
import org.guce.core.services.SearchFilter;

public class VT2RegistrationFilter extends SearchFilter {
    private List<String> officeCode;

    private List<String> vtReference;

    private Date vtDateStartFilter;

    private Date vtDateEndFilter;

    private List<CoreCAD> cad;

    private List<CoreCharger> supplier;

    private TransportFilter transport;

    private InvoiceFilter invoice;

    public List<String> getOfficeCode() {
        return officeCode;
    }

    public void setOfficeCode(List<String> officeCode) {
        this.officeCode = officeCode;
    }

    public List<String> getVtReference() {
        return vtReference;
    }

    public void setVtReference(List<String> vtReference) {
        this.vtReference = vtReference;
    }

    public Date getVtDateStartFilter() {
        return vtDateStartFilter;
    }

    public Date getVtDateEndFilter() {
        return vtDateEndFilter;
    }

    public void setVtDateStartFilter(Date vtDateStartFilter) {
        this.vtDateStartFilter = vtDateStartFilter;
    }

    public void setVtDateEndFilter(Date vtDateEndFilter) {
        this.vtDateEndFilter = vtDateEndFilter;
    }

    public List<CoreCAD> getCad() {
        return cad;
    }

    public void setCad(List<CoreCAD> cad) {
        this.cad = cad;
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
}
