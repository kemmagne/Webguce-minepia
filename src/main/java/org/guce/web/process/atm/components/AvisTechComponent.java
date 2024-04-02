package org.guce.web.process.atm.components;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.FacesComponent;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.guce.core.services.ApplicationService;
import org.guce.process.atm.entities.AvisTech;
import org.guce.web.component.WebguceDefaultComponent;
import org.guce.web.core.util.DefaultLazyDataModel;
import org.guce.web.process.atm.services.impl.AvisTechServiceImpl;

@FacesComponent("org.guce.web.process.atm.jsf.components.AvisTechComponent")
public class AvisTechComponent extends WebguceDefaultComponent {
    protected DefaultLazyDataModel<AvisTech> options;

    protected List<AvisTech> optionsList;

    public AvisTechComponent() {
        super();
    }

    public List<AvisTech> getOptionsList() {
        System.out.println((String)getAttributes().get("domain"));
        optionsList = getOptionService().findByDomain((String)getAttributes().get("domain"));
        return optionsList;
    }

    public DefaultLazyDataModel<AvisTech> getOptions() {
        if (options == null) {
            options = new DefaultLazyDataModel<>(getOptionService().findByDomain((String)getAttributes().get("domain")));
        }
        return options;
    }

    public AvisTechServiceImpl getOptionService() {
        return (AvisTechServiceImpl)lookup("AvisTechServiceImpl");
    }

    public Object lookup(String service) {
        try {
            InitialContext c = new InitialContext();
            return c.lookup("java:global/" + ApplicationService.getContext() + "/webguce/" 
                                        + service + "!org.guce.web.process.atm.services.impl." + service);
        } catch (NamingException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public Object saveState(FacesContext context) {
        Map m = new HashMap();
        m.put("object", super.saveState(context));
        m.put("options", options);
        m.put("optionsList", optionsList);
        return m;
    }

    @Override
    public void restoreState(FacesContext context, Object state) {
        Map m = (Map) state;
        options = (DefaultLazyDataModel<AvisTech>) m.get("options");
        optionsList = (List<AvisTech>) m.get("optionsList");
        super.restoreState(context, m.get("object"));
    }
}
