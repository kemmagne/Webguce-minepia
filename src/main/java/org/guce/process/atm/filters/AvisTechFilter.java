package org.guce.process.atm.filters;

import java.util.Date;
import java.util.List;
import org.guce.core.services.SearchFilter;

public class AvisTechFilter extends SearchFilter {
    private List<String> registerNumber;

    private List<String> immatriculation;

    private List<String> technicalName;

    private List<String> deliveryDate;

    private Date expiryDateStartFilter;

    private Date expiryDateEndFilter;

    private List<String> status;

    public List<String> getRegisterNumber() {
        return registerNumber;
    }

    public void setRegisterNumber(List<String> registerNumber) {
        this.registerNumber = registerNumber;
    }

    public List<String> getImmatriculation() {
        return immatriculation;
    }

    public void setImmatriculation(List<String> immatriculation) {
        this.immatriculation = immatriculation;
    }

    public List<String> getTechnicalName() {
        return technicalName;
    }

    public void setTechnicalName(List<String> technicalName) {
        this.technicalName = technicalName;
    }

    public List<String> getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(List<String> deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Date getExpiryDateStartFilter() {
        return expiryDateStartFilter;
    }

    public Date getExpiryDateEndFilter() {
        return expiryDateEndFilter;
    }

    public void setExpiryDateStartFilter(Date expiryDateStartFilter) {
        this.expiryDateStartFilter = expiryDateStartFilter;
    }

    public void setExpiryDateEndFilter(Date expiryDateEndFilter) {
        this.expiryDateEndFilter = expiryDateEndFilter;
    }

    public List<String> getStatus() {
        return status;
    }

    public void setStatus(List<String> status) {
        this.status = status;
    }
}
