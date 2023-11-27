package org.guce.process.dem.filters;

import java.util.Date;
import java.util.List;
import org.guce.core.services.SearchFilter;

public class DEMRegistrationFilter extends SearchFilter {
    private List<String> demReference;

    private Date demDateStartFilter;

    private Date demDateEndFilter;

    private InformationFilter information;

    public List<String> getDemReference() {
        return demReference;
    }

    public void setDemReference(List<String> demReference) {
        this.demReference = demReference;
    }

    public Date getDemDateStartFilter() {
        return demDateStartFilter;
    }

    public Date getDemDateEndFilter() {
        return demDateEndFilter;
    }

    public void setDemDateStartFilter(Date demDateStartFilter) {
        this.demDateStartFilter = demDateStartFilter;
    }

    public void setDemDateEndFilter(Date demDateEndFilter) {
        this.demDateEndFilter = demDateEndFilter;
    }

    public InformationFilter getInformation() {
        return information;
    }

    public void setInformation(InformationFilter information) {
        this.information = information;
    }
}
