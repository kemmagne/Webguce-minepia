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
import org.guce.process.atm.entities.TypeAvtech;
import org.guce.web.component.WebguceDefaultComponent;
import org.guce.web.core.util.DefaultLazyDataModel;
import org.guce.web.process.atm.services.impl.TypeAvtechServiceImpl;

@FacesComponent("org.guce.web.process.atm.jsf.components.TypeAvtechComponent")
public class TypeAvtechComponent extends WebguceDefaultComponent {
    protected DefaultLazyDataModel<TypeAvtech> options;

    protected List<TypeAvtech> optionsList;

    public TypeAvtechComponent() {
        super();
    }

    public List<TypeAvtech> getOptionsList() {
        System.out.println((String)getAttributes().get("domain"));
        optionsList = getOptionService().findByDomain((String)getAttributes().get("domain"));
        return optionsList;
    }

    public DefaultLazyDataModel<TypeAvtech> getOptions() {
        if (options == null) {
            options = new DefaultLazyDataModel<>(getOptionService().findByDomain((String)getAttributes().get("domain")));
        }
        return options;
    }

    public TypeAvtechServiceImpl getOptionService() {
        return (TypeAvtechServiceImpl)lookup("TypeAvtechServiceImpl");
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
        options = (DefaultLazyDataModel<TypeAvtech>) m.get("options");
        optionsList = (List<TypeAvtech>) m.get("optionsList");
        super.restoreState(context, m.get("object"));
    }
}
