package org.guce.process.dem.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.ManyToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.guce.core.documents.GuceDateAdapter;

@Embeddable
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@XmlType(
        propOrder = {"niu",
                "socialReson",
                "deliveryDate",
                "expirationDate",
                "tradeRegister",
                "adresse",
                "poxBose",
                "emailAdress",
                "town",
                "mobilePhone",
                "phoneNumber",
                "declaration"}
)
public class Information implements Serializable {
    @Column(
            name = "CAD_NIU"
    )
    private String niu;

    @Column(
            name = "CAD_NOM_DEPOSANT"
    )
    private String socialReson;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(
            name = "DE_DATE_DEL"
    )
    private Date deliveryDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(
            name = "DE_DATE_EXP"
    )
    private Date expirationDate;

    @Column(
            name = "DE_TRADE"
    )
    private String tradeRegister;

    @Column(
            name = "DE_ADRESS"
    )
    private String adresse;

    @Column(
            name = "DE_POX_BOX"
    )
    private String poxBose;

    @Column(
            name = "DE_EMAIL_ADRESSE"
    )
    private String emailAdress;

    @Column(
            name = "DE_TOWN"
    )
    private String town;

    @Column(
            name = "DE_MOBILE_PHONE"
    )
    private String mobilePhone;

    @Column(
            name = "DE_FIXE_PHONE"
    )
    private String phoneNumber;

    @ManyToMany(
            targetEntity = DEMDeclaration.class
    )
    private List<DEMDeclaration> declaration;

    @XmlElement(
            name = "NI_U"
    )
    public String getNiu() {
        return niu;
    }

    public void setNiu(String niu) {
        this.niu = niu;
    }

    @XmlElement(
            name = "SOCIAL_REASON"
    )
    public String getSocialReson() {
        return socialReson;
    }

    public void setSocialReson(String socialReson) {
        this.socialReson = socialReson;
    }

    @XmlElement(
            name = "DATE_DEL_DEM_MINCOMMERCE"
    )
    @XmlJavaTypeAdapter(GuceDateAdapter.class)
    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    @XmlElement(
            name = "DATE_EXP_DEM_MINCOMMERCE"
    )
    @XmlJavaTypeAdapter(GuceDateAdapter.class)
    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    @XmlElement(
            name = "TRADE_DEM_MINCOMMERCE"
    )
    public String getTradeRegister() {
        return tradeRegister;
    }

    public void setTradeRegister(String tradeRegister) {
        this.tradeRegister = tradeRegister;
    }

    @XmlElement(
            name = "ADRESSE_DEM_MINCOMMERCE"
    )
    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    @XmlElement(
            name = "BOX_DEM_MINCOMMERCE"
    )
    public String getPoxBose() {
        return poxBose;
    }

    public void setPoxBose(String poxBose) {
        this.poxBose = poxBose;
    }

    @XmlElement(
            name = "ADRESSE_EMAIL_DEM_MINCOMMERCE"
    )
    public String getEmailAdress() {
        return emailAdress;
    }

    public void setEmailAdress(String emailAdress) {
        this.emailAdress = emailAdress;
    }

    @XmlElement(
            name = "TOWN_DEM_MINCOMMERCE"
    )
    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    @XmlElement(
            name = "MOBILE_DEM_MINCOMMERCE"
    )
    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    @XmlElement(
            name = "FIX_PHONE_DEM_MINCOMMERCE"
    )
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @XmlElementWrapper(
            name = "DeclarationS"
    )
    @XmlElements({
                    @XmlElement(name = "Declaration")})
    public List<DEMDeclaration> getDeclaration() {
        return declaration;
    }

    public void setDeclaration(List<DEMDeclaration> declaration) {
        this.declaration = declaration;
    }
}
