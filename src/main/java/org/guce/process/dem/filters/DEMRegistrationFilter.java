package org.guce.process.dem.filters;

import java.util.Date;
import java.util.List;
import org.guce.core.services.SearchFilter;
import org.guce.process.dem.entities.DEMDeclaration;

public class DEMRegistrationFilter extends SearchFilter {
    private List<String> officeCode;

    private List<String> demReference;

    private Date demDateStartFilter;

    private Date demDateEndFilter;

    private List<DEMDeclaration> declarations;

    public List<String> getOfficeCode() {
        return officeCode;
    }

    public void setOfficeCode(List<String> officeCode) {
        this.officeCode = officeCode;
    }

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

    public List<DEMDeclaration> getDeclarations() {
        return declarations;
    }

    public void setDeclarations(List<DEMDeclaration> declarations) {
        this.declarations = declarations;
    }
}
