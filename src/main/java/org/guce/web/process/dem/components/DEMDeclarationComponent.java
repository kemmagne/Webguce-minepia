package org.guce.web.process.dem.components;

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
import org.guce.process.dem.entities.DEMDeclaration;
import org.guce.web.component.WebguceDefaultComponent;
import org.guce.web.core.util.DefaultLazyDataModel;
import org.guce.web.process.dem.services.impl.DEMDeclarationServiceImpl;

@FacesComponent("org.guce.web.process.dem.jsf.components.DEMDeclarationComponent")
public class DEMDeclarationComponent extends WebguceDefaultComponent {
    protected DefaultLazyDataModel<DEMDeclaration> options;

    protected List<DEMDeclaration> optionsList;

    public DEMDeclarationComponent() {
        super();
    }

    public List<DEMDeclaration> getOptionsList() {
        System.out.println((String)getAttributes().get("domain"));
        optionsList = getOptionService().findByDomain((String)getAttributes().get("domain"));
        return optionsList;
    }

    public DefaultLazyDataModel<DEMDeclaration> getOptions() {
        if (options == null) {
            options = new DefaultLazyDataModel<>(getOptionService().findByDomain((String)getAttributes().get("domain")));
        }
        return options;
    }

    public DEMDeclarationServiceImpl getOptionService() {
        return (DEMDeclarationServiceImpl)lookup("DEMDeclarationServiceImpl");
    }

    public Object lookup(String service) {
        try {
            InitialContext c = new InitialContext();
            return c.lookup("java:global/" + ApplicationService.getContext() + "/webguce/" 
                                        + service + "!org.guce.web.process.dem.services.impl." + service);
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
        options = (DefaultLazyDataModel<DEMDeclaration>) m.get("options");
        optionsList = (List<DEMDeclaration>) m.get("optionsList");
        super.restoreState(context, m.get("object"));
    }
}
