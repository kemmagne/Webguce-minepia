package org.guce.web.process.atm.controllers.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.guce.core.ejb.facade.interfaces.CorePaysFacadeLocal;
import org.guce.core.entities.CoreAttachment;
import org.guce.core.entities.CoreAttachmenttype;
import org.guce.core.entities.util.LookupUtil;
import org.guce.process.atm.ATMConstant;
import org.guce.process.atm.entities.PaymentDocument;
import org.guce.rep.entities.CorePays;
import org.guce.rep.entities.RepPositionTarifaire;
import org.guce.web.core.util.JsfUtil;
import org.guce.web.process.atm.controllers.ATMRequestController;
import org.primefaces.context.RequestContext;

@ManagedBean(
        name = "aTMRequestController"
)
@ViewScoped
public class ATMRequestControllerImpl extends ATMRequestController {
    
 private CorePaysFacadeLocal pFacade;

 
 @PostConstruct
 public void initQuery(){
   loadProductType();
   
    
   
   if( current != null  && current.getTransport() != null){
           current.getTransport().setDestination(getpFacade().find("CM"));
           
     String byCopy = application.getStringParam("type");
     if(byCopy.equals("copy")){
    
     if(byCopy != null &&  byCopy.equals("copy")){
         getCurrent().setCoreAttachmentList(new ArrayList<CoreAttachment>());
     }
     }
   }      
 }
 
 
 
   
    @Override
    public HashMap<String, String> getOfficeSelected() {
       return super.generateOffice(formCode);
    }
   
  
     protected void generatePaymentData() {

        current.setPaiement(new PaymentDocument.CONTENT.PAIEMENT());
        current.getPaiement().setCHARGEUR(new PaymentDocument.CONTENT.PAIEMENT.CHARGEUR());
        current.getPaiement().setBENEFICIAIRE(new PaymentDocument.CONTENT.PAIEMENT.BENEFICIAIRE());
        current.getPaiement().setFACTURE(new PaymentDocument.CONTENT.PAIEMENT.FACTURE());
        //  current.getPaiement().setREPARTITIONS(new PaymentDocument.CONTENT.PAIEMENT.REPARTITIONS());

        current.getPaiement().getCHARGEUR().setNUMEROCONTRIBUABLE(super.current.getChargerid().getNumeroContribuable());
        current.getPaiement().getCHARGEUR().setRAISONSOCIALE(current.getChargerid().getChargername());

        current.getPaiement().getBENEFICIAIRE().setCODE(getSpecificProcessParam(current.getRecordProcess(), ATMConstant.BILL_BENEFICIAIRE, "MINEPIA"));
        current.getPaiement().getBENEFICIAIRE().setLIBELLE(getSpecificProcessParam(current.getRecordProcess(), ATMConstant.BILL_BENEFICIAIRE, "MINEPIA"));

        current.getPaiement().getFACTURE().setMONTANTHT(ATMConstant.BILL_MONTANT_HT);
        current.getPaiement().getFACTURE().setREFERENCEFACTURE(current.getRecordId());
        current.getPaiement().getFACTURE().setTYPEFACTURE(getSpecificProcessParam(current.getRecordProcess(), ATMConstant.FORM_PAYMENT, "DEM602"));
        current.getPaiement().getFACTURE().setMONTANTTTC(ATMConstant.BILL_MONTANT_TTC);
        current.getPaiement().getFACTURE().setAUTREMONTANT("0");

    }
  
    @Override
    public void prepareSend() {
        generatePaymentData();
        super.prepareSend();  
    }
     
    public CorePaysFacadeLocal getpFacade() {
        if (pFacade == null) {
            pFacade = (CorePaysFacadeLocal) LookupUtil.CoreLookup("CorePaysFacade");
        }
        return pFacade;
    }
    
 
    
// @Override
// public void validateAndSave() {
//        Map<String , Boolean> fileMap = checkValidityOfAttachment(current.getCoreAttachmentList());
//        //checkRequestConformity();
//       if(fileMap == null ){
//            JsfUtil.addErrorMessage(bundle("EmptyFile")); return;
//       }else{
////        if(!fileMap.get(TypeAttachement.ATM01.name())) {
////            JsfUtil.addErrorMessage(bundle("EmptyProformat"));return;
////        }
////        if(selectedProductType.equals(ProductTypeCode.SEEDS_CODE) ) {
////                if( !fileMap.get(TypeAttachement.CERTIFICAT_ACTIVITY.name())) {
////                      JsfUtil.addErrorMessage(bundle("EmptySeedFile"));return;
////                  }   
////                  if(current.getSeed() == null){
////                 JsfUtil.addErrorMessage(bundle("seedNotFound"));return;
////                    }
////        }
//             super.validateAndSave();
//    }
//    }
}
