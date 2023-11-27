package org.guce.process.dem.filters;

import java.util.List;
import org.guce.core.services.SearchFilter;

public class DEMDeclarationFilter extends SearchFilter {
    private List<String> declaration;

    public List<String> getDeclaration() {
        return declaration;
    }

    public void setDeclaration(List<String> declaration) {
        this.declaration = declaration;
    }
}
