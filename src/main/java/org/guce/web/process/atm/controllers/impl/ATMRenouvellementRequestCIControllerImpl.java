package org.guce.web.process.atm.controllers.impl;

import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.guce.core.ejb.facade.interfaces.CorePaysFacadeLocal;
import org.guce.core.entities.CoreAttachment;
import org.guce.core.entities.util.LookupUtil;
import org.guce.web.process.atm.controllers.ATMRequestCIController;

@ManagedBean(
        name = "aTMRenouvellementRequestCIController"
)
@ViewScoped
public class ATMRenouvellementRequestCIControllerImpl extends ATMRequestCIController {
    private CorePaysFacadeLocal pFacade;
    
    
 @PostConstruct
 public void initCIQuery(){

   if( current != null  && current.getTransport() != null){
           current.getTransport().setDestination(getpFacade().find("CM"));
   }      
 }
    
    
     public CorePaysFacadeLocal getpFacade() {
        if (pFacade == null) {
            pFacade = (CorePaysFacadeLocal) LookupUtil.CoreLookup("CorePaysFacade");
        }
        return pFacade;
    }
}
