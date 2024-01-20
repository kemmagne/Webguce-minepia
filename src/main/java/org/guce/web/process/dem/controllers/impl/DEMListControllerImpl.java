package org.guce.web.process.dem.controllers.impl;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.guce.process.dem.entities.DEMDeclaration;
import org.guce.process.dem.entities.DEMRegistration;
import org.guce.web.process.dem.controllers.DEMListController;

import org.guce.process.dem.repositories.impl.DEMRegistrationRepositoryImpl;


@ManagedBean(
        name = "dEMListController"
)
@ViewScoped
public class DEMListControllerImpl extends DEMListController {

    @EJB
    protected DEMRegistrationRepositoryImpl demRegistrationRepositoryImpl;
    
    
    public String activityFormat(String  recordId){
        String listDeclaration = "";
        DEMRegistration demRegistration = demRegistrationRepositoryImpl.findDemRegistrationByRecordId(recordId);
        if(demRegistration != null){
          List<DEMDeclaration> declarations = demRegistration.getDeclarations();
          if(!declarations.isEmpty()){
          for(DEMDeclaration current: declarations){
              listDeclaration += current.getDeclaration()+"\n";
          }
        }
      }
        return  listDeclaration;     
    }

}
