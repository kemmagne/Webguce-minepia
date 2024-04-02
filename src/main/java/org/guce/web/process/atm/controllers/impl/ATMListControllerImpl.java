package org.guce.web.process.atm.controllers.impl;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.guce.process.atm.entities.TypeAvtech;
import org.guce.web.process.atm.controllers.ATMListController;
import org.guce.web.process.atm.services.impl.TypeAvtechServiceImpl;

@ManagedBean(
        name = "aTMListController"
)
@ViewScoped
public class ATMListControllerImpl extends ATMListController {
    
    @EJB
    private TypeAvtechServiceImpl typeAvtechServiceImpl;
    
    private  List<TypeAvtech> typeTypeAvtechList;
    
    
    @PostConstruct
    void inits(){
        typeTypeAvtechList = typeAvtechServiceImpl.findTypeTypeAvtechs("ATM01", "ATM02", "ATM03");
     }

    public TypeAvtechServiceImpl getTypeAvtechServiceImpl() {
        return typeAvtechServiceImpl;
    }

    public void setTypeAvtechServiceImpl(TypeAvtechServiceImpl typeAvtechServiceImpl) {
        this.typeAvtechServiceImpl = typeAvtechServiceImpl;
    }

    public List<TypeAvtech> getTypeTypeAvtechList() {
        return typeTypeAvtechList;
    }

    public void setTypeTypeAvtechList(List<TypeAvtech> typeTypeAvtechList) {
        this.typeTypeAvtechList = typeTypeAvtechList;
    }
    
}
