package org.guce.web.process.atm.converters;

import javax.faces.convert.FacesConverter;
import org.guce.process.atm.entities.AvisTech;
import org.guce.web.core.converter.WebguceConverter;

@FacesConverter("avisTechConverter")
public class AvisTechConverter extends WebguceConverter<AvisTech> {
    public AvisTechConverter() {
        super(AvisTech.class);
    }

    @Override
    public String getId(Object value) {
        return ((AvisTech)value).getId();
    }
}
