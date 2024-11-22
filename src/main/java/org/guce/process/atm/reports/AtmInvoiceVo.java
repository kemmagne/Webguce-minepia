/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guce.process.atm.reports;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.guce.core.entities.CorePayment;

/**
 *
 * @author NGC
 */

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.guce.core.entities.CorePayment;
import org.guce.process.atm.ATMConstant;
import org.guce.process.atm.entities.ATMRegistration;

/**
 *
 * @author NGC
 */
public class AtmInvoiceVo {
    
     private String chargerName;
    private String adresse;
    private String postalBox;
    private String telephone;
    private String contribNumber;
    private String fax;
    private String city;
    private   String codeRecette ; 
    private String datePaiement;
    private String dateValue;
    private String description;
    private  int  quantity;
    private BigDecimal price;
    private String priceInLetter;
    private String  paiementMode;
    private String invoiceNumber;
    private String invoiceReference;
    private String qrCode;

    public AtmInvoiceVo() {
        this.chargerName = StringUtils.EMPTY;
        this.adresse = StringUtils.EMPTY;
        this.postalBox = StringUtils.EMPTY;
        this.telephone = StringUtils.EMPTY;
        this.contribNumber = StringUtils.EMPTY;
        this.fax = StringUtils.EMPTY;
        this.city = StringUtils.EMPTY;
        this.datePaiement = StringUtils.EMPTY;
        this.dateValue = StringUtils.EMPTY;
        this.description = StringUtils.EMPTY;
        this.price = BigDecimal.ZERO;
        this.priceInLetter = StringUtils.EMPTY;
        this.paiementMode = StringUtils.EMPTY;
        this.qrCode = StringUtils.EMPTY;
        this.invoiceNumber =  StringUtils.EMPTY;
        this.invoiceReference =  StringUtils.EMPTY;
    }

    
    public AtmInvoiceVo(ATMRegistration  dem, String description) {
        if(dem.getInvoiceList() != null  & ! dem.getInvoiceList().isEmpty()){
            Collection<CorePayment> cpCollection = dem.getInvoiceList().get(0).getCorePaymentCollection();
            List<CorePayment>  cpList =  new ArrayList<>(cpCollection);
                CorePayment corePayment  = cpList.get(0);
                 this.invoiceNumber    =   corePayment.getReceiptNumber();
                 this.paiementMode    =   corePayment.getPaymentmodeid() != null ? corePayment.getPaymentmodeid().getPaymentmodename()  : StringUtils.EMPTY;
                 this.price = corePayment.getPaymentAmount() != null  ? corePayment.getPaymentAmount() : BigDecimal.ZERO;
                 this.datePaiement =  formatDateToStringDate(corePayment.getPaymentdate());
                 this.dateValue        =  addOneDayToDate(corePayment.getPaymentdate());
                 this.priceInLetter =  convertNumberToLetter(price.longValue());
                 this.invoiceReference = corePayment.getPaymentid();   //dem.getInvoice().getInvoiceNumber();

    }
         this.chargerName = dem.getChargerid() != null ? dem.getChargerid().getChargername().toUpperCase()  : StringUtils.EMPTY;
        //  this.chargerName = dem.getChargerid() != null ? dem.getOperatorName().toUpperCase()  : StringUtils.EMPTY;
        this.adresse = dem.getChargerid() != null ? dem.getChargerid().getChargeraddress1(): StringUtils.EMPTY;
        this.postalBox = dem.getChargerid() != null ? dem.getChargerid().getChargerpostofficebox(): StringUtils.EMPTY;
        this.telephone = dem.getChargerid() != null ? dem.getChargerid().getChargerphonemobile(): StringUtils.EMPTY;
        this.contribNumber = dem.getChargerid() != null ? dem.getChargerid().getNumeroContribuable(): StringUtils.EMPTY;
        this.fax = dem.getChargerid() != null ? dem.getChargerid().getChargerfax(): StringUtils.EMPTY;
        this.city = dem.getChargerid() != null ? dem.getChargerid().getChargercity(): StringUtils.EMPTY;
        this.description = description;
        this.codeRecette = ATMConstant.MINEPIA;
        this.quantity = 1;
    }
    

    public String getChargerName() {
        return chargerName;
    }

    public void setChargerName(String chargerName) {
        this.chargerName = chargerName;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getPostalBox() {
        return postalBox;
    }

    public void setPostalBox(String postalBox) {
        this.postalBox = postalBox;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getContribNumber() {
        return contribNumber;
    }

    public void setContribNumber(String contribNumber) {
        this.contribNumber = contribNumber;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCodeRecette() {
        return codeRecette;
    }

    public void setCodeRecette(String codeRecette) {
        this.codeRecette = codeRecette;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    

    public String getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(String datePaiement) {
        this.datePaiement = datePaiement;
    }

    public String getDateValue() {
        return dateValue;
    }

    public void setDateValue(String dateValue) {
        this.dateValue = dateValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getPriceInLetter() {
        return priceInLetter;
    }

    public void setPriceInLetter(String priceInLetter) {
        this.priceInLetter = priceInLetter;
    }

    public String getPaiementMode() {
        return paiementMode;
    }

    public void setPaiementMode(String paiementMode) {
        this.paiementMode = paiementMode;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getInvoiceReference() {
        return invoiceReference;
    }

    public void setInvoiceReference(String invoiceReference) {
        this.invoiceReference = invoiceReference;
    }
    
    /**
     * convertir ma date en String
     * @param date
     * @return 
     */
        private String formatDateToStringDate(Date date){
                return new SimpleDateFormat("dd-MM-yyyy").format(date);
        }
    
        /**
         * ajouter un jour a une date
         * @param date
         * @return 
         */
        private String  addOneDayToDate(Date date){
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date); 
                calendar.add(Calendar.DAY_OF_MONTH, 1); 
                return  formatDateToStringDate(calendar.getTime());
    }
        /**
         * Convertir un nombre entier en lettre
         * @return 
         */
    private String convertNumberToLetter(long nombre) {
        if (nombre == 0) {
            return "zéro";
        }
        if (nombre < 20) {
            return ATMConstant.UNITS[(int) nombre];
        } else if (nombre < 100) {
            int unité = (int) (nombre % 10);
            int dizaine = (int) (nombre / 10);
            return ATMConstant.TENS[dizaine] + (unité > 0 ? "-" + ATMConstant.UNITS[unité] : "");
        } else if (nombre < 1000) {
            int centaines = (int) (nombre / 100);
            int reste = (int) (nombre % 100);
            return (centaines == 1 ? "cent" : ATMConstant.UNITS[centaines] + " cent") + (reste > 0 ? " " + convertNumberToLetter(reste) : "");
        } else if (nombre < 1000000) {
            int milliers = (int) (nombre / 1000);
            int reste = (int) (nombre % 1000);
            return (milliers == 1 ? "mille" : convertNumberToLetter(milliers) + " mille") + (reste > 0 ? " " + convertNumberToLetter(reste) : "");
        } else if (nombre < 1000000000) {
            int millions = (int) (nombre / 1000000);
            int reste = (int) (nombre % 1000000);
            return (millions == 1 ? "un million" : convertNumberToLetter(millions) + " millions") + (reste > 0 ? " " + convertNumberToLetter(reste) : "");
        } else {
            return String.valueOf(nombre);
        }
    }

    
}