package org.guce.process.atm.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.guce.rep.entities.RepPositionTarifaire;

@Entity
@Table(
        name = "AVIS_TECH"
)
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@XmlRootElement(
        name = "AVIS_TECH"
)
@XmlType(
        propOrder = {
                "produit",
                "typeProduct"
        }
)
public class AvisTech extends AbstractEntity implements Serializable {
//    @Column(
//            name = "Label",
//            length = 155
//    )
//    private String intitule;

    @OneToMany(
            mappedBy = "itemId"
    )
    private List<AvisTechHistory> historyList;

    @ManyToOne(
            targetEntity = TypeAvtech.class
    )
    @JoinColumn(
            name = "TYPEPRODUCT_ID"
    )
    private TypeAvtech typeProduct;

    @ManyToOne(
            targetEntity = RepPositionTarifaire.class
    )
    @JoinColumn(
            name = "PRODUIT_ID"
    )
    private RepPositionTarifaire produit;

//    @XmlElement(
//            name = "Intitule"
//    )
//    public String getIntitule() {
//        return intitule;
//    }
//
//    public void setIntitule(String intitule) {
//        this.intitule = intitule;
//    }

    @XmlElement(
            name = "TYPEAVIS"
    )
    public TypeAvtech getTypeProduct() {
        return typeProduct;
    }

    public void setTypeProduct(TypeAvtech typeProduct) {
        this.typeProduct = typeProduct;
    }

    @XmlElement(
            name = "PRODUIT"
    )
    public RepPositionTarifaire getProduit() {
        return produit;
    }

    public void setProduit(RepPositionTarifaire produit) {
        this.produit = produit;
    }

//    @XmlTransient
//    public String getName() {
//        return this.getIntitule();
//    }

    @XmlTransient
    public List<AvisTechHistory> getHistoryList() {
        return historyList;
    }

    public void setHistoryList(List<AvisTechHistory> historyList) {
        this.historyList = historyList;
    }
}
