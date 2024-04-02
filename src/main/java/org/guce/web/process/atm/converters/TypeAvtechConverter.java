package org.guce.web.process.atm.converters;

import javax.faces.convert.FacesConverter;
import org.guce.process.atm.entities.TypeAvtech;
import org.guce.web.core.converter.WebguceConverter;

@FacesConverter("typeAvtechConverter")
public class TypeAvtechConverter extends WebguceConverter<TypeAvtech> {
    public TypeAvtechConverter() {
        super(TypeAvtech.class);
    }

    @Override
    public String getId(Object value) {
        return ((TypeAvtech)value).getId();
    }
}
