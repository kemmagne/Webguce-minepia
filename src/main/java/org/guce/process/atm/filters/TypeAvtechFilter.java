package org.guce.process.atm.filters;

import java.util.List;
import org.guce.core.services.SearchFilter;

public class TypeAvtechFilter extends SearchFilter {
    private List<String> label;

    private List<String> code;

    public List<String> getLabel() {
        return label;
    }

    public void setLabel(List<String> label) {
        this.label = label;
    }

    public List<String> getCode() {
        return code;
    }

    public void setCode(List<String> code) {
        this.code = code;
    }
}
