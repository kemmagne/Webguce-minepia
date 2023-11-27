package org.guce.web.process.dem.converters;

import javax.faces.convert.FacesConverter;
import org.guce.process.dem.entities.DEMDeclaration;
import org.guce.web.core.converter.WebguceConverter;

@FacesConverter("dEMDeclarationConverter")
public class DEMDeclarationConverter extends WebguceConverter<DEMDeclaration> {
    public DEMDeclarationConverter() {
        super(DEMDeclaration.class);
    }

    @Override
    public String getId(Object value) {
        return ((DEMDeclaration)value).getId();
    }
}
