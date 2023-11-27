package org.guce.process.dem.filters;

import java.util.Date;
import java.util.List;
import org.guce.core.services.SearchFilter;
import org.guce.process.dem.entities.DEMDeclaration;

public class InformationFilter extends SearchFilter {
    private List<String> niu;

    private List<String> socialReson;

    private Date deliveryDateStartFilter;

    private Date deliveryDateEndFilter;

    private Date expirationDateStartFilter;

    private Date expirationDateEndFilter;

    private List<String> tradeRegister;

    private List<String> adresse;

    private List<String> poxBose;

    private List<String> emailAdress;

    private List<String> town;

    private List<String> mobilePhone;

    private List<String> phoneNumber;

    private List<DEMDeclaration> declaration;

    public List<String> getNiu() {
        return niu;
    }

    public void setNiu(List<String> niu) {
        this.niu = niu;
    }

    public List<String> getSocialReson() {
        return socialReson;
    }

    public void setSocialReson(List<String> socialReson) {
        this.socialReson = socialReson;
    }

    public Date getDeliveryDateStartFilter() {
        return deliveryDateStartFilter;
    }

    public Date getDeliveryDateEndFilter() {
        return deliveryDateEndFilter;
    }

    public void setDeliveryDateStartFilter(Date deliveryDateStartFilter) {
        this.deliveryDateStartFilter = deliveryDateStartFilter;
    }

    public void setDeliveryDateEndFilter(Date deliveryDateEndFilter) {
        this.deliveryDateEndFilter = deliveryDateEndFilter;
    }

    public Date getExpirationDateStartFilter() {
        return expirationDateStartFilter;
    }

    public Date getExpirationDateEndFilter() {
        return expirationDateEndFilter;
    }

    public void setExpirationDateStartFilter(Date expirationDateStartFilter) {
        this.expirationDateStartFilter = expirationDateStartFilter;
    }

    public void setExpirationDateEndFilter(Date expirationDateEndFilter) {
        this.expirationDateEndFilter = expirationDateEndFilter;
    }

    public List<String> getTradeRegister() {
        return tradeRegister;
    }

    public void setTradeRegister(List<String> tradeRegister) {
        this.tradeRegister = tradeRegister;
    }

    public List<String> getAdresse() {
        return adresse;
    }

    public void setAdresse(List<String> adresse) {
        this.adresse = adresse;
    }

    public List<String> getPoxBose() {
        return poxBose;
    }

    public void setPoxBose(List<String> poxBose) {
        this.poxBose = poxBose;
    }

    public List<String> getEmailAdress() {
        return emailAdress;
    }

    public void setEmailAdress(List<String> emailAdress) {
        this.emailAdress = emailAdress;
    }

    public List<String> getTown() {
        return town;
    }

    public void setTown(List<String> town) {
        this.town = town;
    }

    public List<String> getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(List<String> mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public List<String> getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(List<String> phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<DEMDeclaration> getDeclaration() {
        return declaration;
    }

    public void setDeclaration(List<DEMDeclaration> declaration) {
        this.declaration = declaration;
    }
}
