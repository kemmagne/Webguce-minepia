package org.guce.process.atm.filters;

import java.util.Date;
import java.util.List;
import org.guce.core.services.SearchFilter;
import org.guce.rep.entities.CorePays;
import org.guce.rep.entities.RepCustomsOffice;
import org.guce.rep.entities.RepTransportMode;

public class TransportFilter extends SearchFilter {
    private List<String> blNumber;

    private List<String> travelNumber;

    private Date leavingDateStartFilter;

    private Date leavingDateEndFilter;

    private Date arrivalDateStartFilter;

    private Date arrivalDateEndFilter;

    private List<CorePays> origin;

    private List<RepCustomsOffice> loadingPlace;

    private List<RepTransportMode> transportMode;

    private List<CorePays> provenance;

    private List<RepCustomsOffice> clearingPlace;

    public List<String> getBlNumber() {
        return blNumber;
    }

    public void setBlNumber(List<String> blNumber) {
        this.blNumber = blNumber;
    }

    public List<String> getTravelNumber() {
        return travelNumber;
    }

    public void setTravelNumber(List<String> travelNumber) {
        this.travelNumber = travelNumber;
    }

    public Date getLeavingDateStartFilter() {
        return leavingDateStartFilter;
    }

    public Date getLeavingDateEndFilter() {
        return leavingDateEndFilter;
    }

    public void setLeavingDateStartFilter(Date leavingDateStartFilter) {
        this.leavingDateStartFilter = leavingDateStartFilter;
    }

    public void setLeavingDateEndFilter(Date leavingDateEndFilter) {
        this.leavingDateEndFilter = leavingDateEndFilter;
    }

    public Date getArrivalDateStartFilter() {
        return arrivalDateStartFilter;
    }

    public Date getArrivalDateEndFilter() {
        return arrivalDateEndFilter;
    }

    public void setArrivalDateStartFilter(Date arrivalDateStartFilter) {
        this.arrivalDateStartFilter = arrivalDateStartFilter;
    }

    public void setArrivalDateEndFilter(Date arrivalDateEndFilter) {
        this.arrivalDateEndFilter = arrivalDateEndFilter;
    }

    public List<CorePays> getOrigin() {
        return origin;
    }

    public void setOrigin(List<CorePays> origin) {
        this.origin = origin;
    }

    public List<RepCustomsOffice> getLoadingPlace() {
        return loadingPlace;
    }

    public void setLoadingPlace(List<RepCustomsOffice> loadingPlace) {
        this.loadingPlace = loadingPlace;
    }

    public List<RepTransportMode> getTransportMode() {
        return transportMode;
    }

    public void setTransportMode(List<RepTransportMode> transportMode) {
        this.transportMode = transportMode;
    }

    public List<CorePays> getProvenance() {
        return provenance;
    }

    public void setProvenance(List<CorePays> provenance) {
        this.provenance = provenance;
    }

    public List<RepCustomsOffice> getClearingPlace() {
        return clearingPlace;
    }

    public void setClearingPlace(List<RepCustomsOffice> clearingPlace) {
        this.clearingPlace = clearingPlace;
    }
}
