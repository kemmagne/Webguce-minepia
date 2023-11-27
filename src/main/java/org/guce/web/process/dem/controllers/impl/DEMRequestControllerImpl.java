package org.guce.web.process.dem.controllers.impl;

import java.util.Objects;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.guce.core.ejb.facade.interfaces.CoreChargerFacadeLocal;
import org.guce.core.entities.CoreCharger;
import org.guce.process.dem.DEMConstant;
import org.guce.process.dem.entities.PaymentDocument;
import org.guce.process.dem.repositories.impl.DEMRegistrationRepositoryImpl;
import org.guce.rep.ejb.facade.interfaces.CarteContribuableFacadeLocal;
import org.guce.rep.entities.CarteContribuable;
import org.guce.web.core.util.JsfUtil;
import org.guce.web.process.dem.controllers.DEMRequestController;

@ManagedBean(
        name = "dEMRequestController"
)
@ViewScoped
public class DEMRequestControllerImpl extends DEMRequestController {
    @EJB
    public CarteContribuableFacadeLocal carteContribuableFacadeLocal;
    
    @EJB
    public DEMRegistrationRepositoryImpl dEMRegistrationRepositoryImpl;
    
      public void findRaisonSocial(String enteredNui){
        
       
    
           if(!enteredNui.isEmpty()) {
                //CarteContribuable charger =  carteContribuableFacadeLocal.find(enteredNui);
                CoreCharger charger = dEMRegistrationRepositoryImpl.findCoreChargerByCarteContribNumber(enteredNui);
                if(charger == null){
                     JsfUtil.addErrorMessage(bundle("SocialResonNotFound"));
                }else{
                    if(charger.getCarte() != null){
                    current.getInformation().setSocialReson(charger.getCarte().getNom());
                    current.getInformation().setDeliveryDate(charger.getCarte().getDateDelivrance());
                    current.getInformation().setExpirationDate(charger.getCarte().getDateExpiration());
                    current.setChargerid(charger); 
                    }else{
                      JsfUtil.addErrorMessage(bundle("SocialResonNotFound"));
                    }
                }

           }
                      
    }
      
      
          protected void generatePaymentData() {
      
            current.setPaiement(new PaymentDocument.CONTENT.PAIEMENT());
            current.getPaiement().setCHARGEUR(new PaymentDocument.CONTENT.PAIEMENT.CHARGEUR());
            current.getPaiement().setBENEFICIAIRE(new PaymentDocument.CONTENT.PAIEMENT.BENEFICIAIRE());
            current.getPaiement().setFACTURE(new PaymentDocument.CONTENT.PAIEMENT.FACTURE());
          //  current.getPaiement().setREPARTITIONS(new PaymentDocument.CONTENT.PAIEMENT.REPARTITIONS());

            current.getPaiement().getCHARGEUR().setNUMEROCONTRIBUABLE(current.getChargerid().getNumeroContribuable());
            current.getPaiement().getCHARGEUR().setRAISONSOCIALE(current.getChargerid().getChargername());

            current.getPaiement().getBENEFICIAIRE().setCODE(getSpecificProcessParam(current.getRecordProcess(), DEMConstant.BILL_BENEFICIAIRE, "MINCOMMERCE"));
            current.getPaiement().getBENEFICIAIRE().setLIBELLE(getSpecificProcessParam(current.getRecordProcess(), DEMConstant.BILL_BENEFICIAIRE, "MINCOMMERCE"));

            current.getPaiement().getFACTURE().setMONTANTHT(DEMConstant.BILL_MONTANT_HT);
            current.getPaiement().getFACTURE().setREFERENCEFACTURE(current.getRecordId());
            current.getPaiement().getFACTURE().setTYPEFACTURE(getSpecificProcessParam(current.getRecordProcess(), DEMConstant.FORM_PAYMENT, "DEM602"));
            current.getPaiement().getFACTURE().setMONTANTTTC(DEMConstant.BILL_MONTANT_TTC);
            current.getPaiement().getFACTURE().setAUTREMONTANT("0");
           
        }
    
    @Override
     public void prepareSend() {
        if (checkRequestConformity()) {  
            generatePaymentData();
            super.prepareSend();
        }
     }
/**
     
     public CoreCharger mapToCharger(CarteContribuable carteContribuable){
        CoreCharger charger = new CoreCharger();
      if(Objects.nonNull(carteContribuable)){
          charger.setChargercni(carteContribuable.getCni());
          charger.setNumeroContribuable(carteContribuable.getNumero());
          charger.setChargername(parentId);
       
          
          return  charger;
      }
      return null;
     } **/
             
}
