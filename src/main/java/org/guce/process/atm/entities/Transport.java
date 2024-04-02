package org.guce.process.atm.entities;

import java.io.Serializable;
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
import org.guce.rep.entities.CorePays;
import org.guce.rep.entities.RepCustomsOffice;
import org.guce.rep.entities.RepTransportMode;

@Embeddable
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@XmlType(
        propOrder = {"blNumber",
                "travelNumber",
                "leavingDate",
                "arrivalDate",
                "provenance",
                "origin",
                "loadingPlace",
                "transportMode",
                "clearingPlace"}
)
public class Transport implements Serializable {
    @Column(
            name = "BL_NUMBER",
            length = 150
    )
    private String blNumber;

    @Column(
            name = "TRAVEL_NUMBER",
            length = 150
    )
    private String travelNumber;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(
            name = "LEAVING_DATE"
    )
    private Date leavingDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(
            name = "ARRIVAL_DATE"
    )
    private Date arrivalDate;

    @ManyToOne(
            targetEntity = CorePays.class
    )
    @JoinColumn(
            name = "ORIGIN_ID"
    )
    private CorePays origin;

    @ManyToOne(
            targetEntity = RepCustomsOffice.class
    )
    @JoinColumn(
            name = "LOADINGPLACE_ID"
    )
    private RepCustomsOffice loadingPlace;

    @ManyToOne(
            targetEntity = RepTransportMode.class
    )
    @JoinColumn(
            name = "TRANSPORTMODE_ID"
    )
    private RepTransportMode transportMode;

    @ManyToOne(
            targetEntity = CorePays.class
    )
    @JoinColumn(
            name = "PROVENANCE_ID"
    )
    private CorePays provenance;

    @ManyToOne(
            targetEntity = RepCustomsOffice.class
    )
    @JoinColumn(
            name = "CLEARINGPLACE_ID"
    )
    private RepCustomsOffice clearingPlace;

    @XmlElement(
            name = "NUMERO_BL"
    )
    public String getBlNumber() {
        return blNumber;
    }

    public void setBlNumber(String blNumber) {
        this.blNumber = blNumber;
    }

    @XmlElement(
            name = "NUMERO_VOYAGE"
    )
    public String getTravelNumber() {
        return travelNumber;
    }

    public void setTravelNumber(String travelNumber) {
        this.travelNumber = travelNumber;
    }

    @XmlElement(
            name = "DATE_DEPART"
    )
    @XmlJavaTypeAdapter(GuceDateAdapter.class)
    public Date getLeavingDate() {
        return leavingDate;
    }

    public void setLeavingDate(Date leavingDate) {
        this.leavingDate = leavingDate;
    }

    @XmlElement(
            name = "DATE_ARRIVEE"
    )
    @XmlJavaTypeAdapter(GuceDateAdapter.class)
    public Date getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(Date arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    @XmlElement(
            name = "PAYS_ORIGINE"
    )
    public CorePays getOrigin() {
        return origin;
    }

    public void setOrigin(CorePays origin) {
        this.origin = origin;
    }

    @XmlElement(
            name = "LIEU_CHARGEMENT"
    )
    public RepCustomsOffice getLoadingPlace() {
        return loadingPlace;
    }

    public void setLoadingPlace(RepCustomsOffice loadingPlace) {
        this.loadingPlace = loadingPlace;
    }

    @XmlElement(
            name = "MODE_TRANSPORT"
    )
    public RepTransportMode getTransportMode() {
        return transportMode;
    }

    public void setTransportMode(RepTransportMode transportMode) {
        this.transportMode = transportMode;
    }

    @XmlElement(
            name = "PAYS_PROVENANCE"
    )
    public CorePays getProvenance() {
        return provenance;
    }

    public void setProvenance(CorePays provenance) {
        this.provenance = provenance;
    }

    @XmlElement(
            name = "LIEU_DEDOUANEMENT"
    )
    public RepCustomsOffice getClearingPlace() {
        return clearingPlace;
    }

    public void setClearingPlace(RepCustomsOffice clearingPlace) {
        this.clearingPlace = clearingPlace;
    }
}
