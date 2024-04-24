package org.guce.web.process.atm.controllers.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.guce.core.ejb.facade.interfaces.CorePaysFacadeLocal;
import org.guce.core.entities.CoreAttachment;
import org.guce.core.entities.CoreAttachmenttype;
import org.guce.core.entities.util.LookupUtil;
import org.guce.process.atm.ATMConstant;
import org.guce.process.atm.TypeAttachement;
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

 private Logger LOGGER = Logger.getLogger(ATMControllerImpl.class.getName());
 
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
 
 
  protected List<String> requiredAttachmentCodes = Arrays.asList(
        new String[] {"DTarif", "CCI", "AT", "CMP", "LI", "TPV", "QPA"}
    );
  
  
  protected List<String> requiredAttachmentAtm02Codes = Arrays.asList(
        new String[] {"DT", "PDCE", "PDDE", "PPEC", "CRT", "LPI", "RAA", "PCCCEC"}
    );

  
   protected List<String> requiredAttachmentAtm03Codes = Arrays.asList(
        new String[] {"CDPP", "LMEI", "DT", "CRT", "RAA"}
    );
   
    protected List<String[]> requiredAttachments = new ArrayList<String[]>();
 
 
   
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
     

     
     
     protected boolean checkRequestConformityAttachment() {
        this.initRequiredAttachements();
        if(current != null) {
            List<CoreAttachment> attachments = current.getCoreAttachmentList();
            List<String> attachmentCodes = new ArrayList<String>();
            List<String> attachmentLabels = new ArrayList<String>();
            List<String> requiredAttachmentsLabel = new ArrayList<String>();
            LOGGER.log( Level.INFO, "ATMControllerImpl -- checkRequestConformityAttachment | requiredAttachments:: {0}", new Object[]{
                requiredAttachments,
            });
            
            LOGGER.log( Level.INFO, "ATMControllerImpl -- checkRequestConformityAttachment | attachments:: {0}", new Object[]{
                attachments,
            });
            if(attachments != null && attachments.size() > 0) {
                for(CoreAttachment attachment: attachments) {
                    if(attachment.getPjType() != null) {
                        attachmentCodes.add(attachment.getPjType().getAttachementtypeid());
                        attachmentLabels.add(attachment.getPjLibelle());
                    }
                }
                LOGGER.log( Level.INFO, "ATMControllerImpl -- checkRequestConformityAttachment | attachmentCodes:: {0}", new Object[]{
                    attachmentCodes,
                });

                for(String[] requiredAttachment: requiredAttachments) {
                    if(!attachmentCodes.contains(requiredAttachment[0])) {
                        requiredAttachmentsLabel.add(requiredAttachment[1]);
                    }
                }
                if(requiredAttachmentsLabel.size() > 0) {
                    
                    for(String requiredAttachment:requiredAttachmentsLabel){
                        String prefix = "";
                       JsfUtil.addErrorMessage(bundle("RequiredAttachmentsMessage"));
                        JsfUtil.addErrorMessage(prefix.concat(" ").concat(requiredAttachment.toLowerCase().concat(" ").concat(bundle("RequiredAttachmentsMessage"))));
                    }
                                    
//                  JsfUtil.addErrorMessage(bundle("RequiredAttachmentsMessage"));
                    return false;
                }
            } else {
                JsfUtil.addErrorMessage(bundle("AttachmentListEmpty"));
                return false;
            }
        }
      
        return true;
    }
     
     
     
     
    protected boolean checkRequestConformityofAttachment() {
      
        boolean checkRequestConformityAttachment = this.checkRequestConformityAttachment();
        if(!checkRequestConformityAttachment) {
            return checkRequestConformityAttachment;
        }
        return true;
    }
     
     
     
         private void initRequiredAttachements() {
        this.requiredAttachments = new ArrayList<String[]>();
        List<CoreAttachmenttype> avaibleAttachmentTypes = this.getAvaibleAttachmentType();
        for(CoreAttachmenttype avaibleAttachmentType: avaibleAttachmentTypes) {
          if(current != null && selectedTypeAvisTechnique.equals(ATMConstant.TRAITEMENT_STOKAGE)){
            if(this.requiredAttachmentCodes.contains(avaibleAttachmentType.getAttachementtypeid())) {
                this.requiredAttachments.add(new String[]{
                    avaibleAttachmentType.getAttachementtypeid(),
                    avaibleAttachmentType.getAttachementtypename(),
                });
            }
          }else if(current != null && selectedTypeAvisTechnique.equals(ATMConstant.INGREDIENT_ADDITIFS)){
            if(this.requiredAttachmentAtm02Codes.contains(avaibleAttachmentType.getAttachementtypeid())) {
                this.requiredAttachments.add(new String[]{
                    avaibleAttachmentType.getAttachementtypeid(),
                    avaibleAttachmentType.getAttachementtypename(),
                });
            }
          }else if(current != null && selectedTypeAvisTechnique.equals(ATMConstant.MATERIEL_EQUIPEMENT)){
            if(this.requiredAttachmentAtm03Codes.contains(avaibleAttachmentType.getAttachementtypeid())) {
                this.requiredAttachments.add(new String[]{
                    avaibleAttachmentType.getAttachementtypeid(),
                    avaibleAttachmentType.getAttachementtypename(),
                });
            }
          }
        }
    }
     
     
//    public boolean checkAttachmentConformity(){
//    
//        Map<String , Boolean> fileMap = checkValidityOfAttachment(current.getCoreAttachmentList());
//       if(fileMap == null && selectedTypeAvisTechnique.equals(ATMConstant.TRAITEMENT_STOKAGE)){
//         JsfUtil.addErrorMessage(bundle("EmptySeedFileToatal"));
//
//         return false;  
//          }else if(fileMap == null && selectedTypeAvisTechnique.equals(ATMConstant.INGREDIENT_ADDITIFS)){
//                JsfUtil.addErrorMessage(bundle("EmptySeedFileToata2"));
//                return false;
//          }else if(fileMap == null && selectedTypeAvisTechnique.equals(ATMConstant.MATERIEL_EQUIPEMENT)){
//                JsfUtil.addErrorMessage(bundle("EmptySeedFileToata3"));  
//                return false;
//          }else{
//               checkRequestConformityofAttachment();
//              
//              return true;
//              
//              
//              
//                  
//              
//              
//              
////              if(selectedTypeAvisTechnique.equals(ATMConstant.TRAITEMENT_STOKAGE)){
////              
////                if(!fileMap.get(TypeAttachement.DTarif.name())) {
////                  JsfUtil.addErrorMessage(bundle("EmptyProformat1"));return;
////                 }
////                if(!fileMap.get(TypeAttachement.CCI.name())) {
////                  JsfUtil.addErrorMessage(bundle("EmptyProformat2"));return;
////                 }
////                if(!fileMap.get(TypeAttachement.AT.name())) {
////                  JsfUtil.addErrorMessage(bundle("EmptyProformat3"));return;
////                 }
////                if(!fileMap.get(TypeAttachement.CMP.name())) {
////                  JsfUtil.addErrorMessage(bundle("EmptyProformat4"));return;
////                 }
////                if(!fileMap.get(TypeAttachement.LI.name())) {
////                  JsfUtil.addErrorMessage(bundle("EmptyProformat5"));return;
////                 }
////                if(!fileMap.get(TypeAttachement.TPV.name())) {
////                  JsfUtil.addErrorMessage(bundle("EmptyProformat6"));return;
////                 }
////                if(!fileMap.get(TypeAttachement.QPA.name())) {
////                  JsfUtil.addErrorMessage(bundle("EmptyProformat7"));return;
////                 }  
////              }
////              
////               if(selectedTypeAvisTechnique.equals(ATMConstant.INGREDIENT_ADDITIFS)){
////              
////                if(!fileMap.get(TypeAttachement.DT.name())) {
////                  JsfUtil.addErrorMessage(bundle("EmptyProformat1"));return;
////                 }
////                if(!fileMap.get(TypeAttachement.PDCE.name())) {
////                  JsfUtil.addErrorMessage(bundle("EmptyProformat2"));return;
////                 }
////                if(!fileMap.get(TypeAttachement.PDDE.name())) {
////                  JsfUtil.addErrorMessage(bundle("EmptyProformat3"));return;
////                 }
////                if(!fileMap.get(TypeAttachement.PPEC.name())) {
////                  JsfUtil.addErrorMessage(bundle("EmptyProformat4"));return;
////                 }
////                if(!fileMap.get(TypeAttachement.CRT.name())) {
////                  JsfUtil.addErrorMessage(bundle("EmptyProformat5"));return;
////                 }
////                if(!fileMap.get(TypeAttachement.LPI.name())) {
////                  JsfUtil.addErrorMessage(bundle("EmptyProformat6"));return;
////                 }
////                if(!fileMap.get(TypeAttachement.RAA.name())) {
////                  JsfUtil.addErrorMessage(bundle("EmptyProformat7"));return;
////                 }
////                 if(!fileMap.get(TypeAttachement.PCCCEC.name())) {
////                  JsfUtil.addErrorMessage(bundle("EmptyProformat7"));return;
////                 }
////              }
////               
////               if(selectedTypeAvisTechnique.equals(ATMConstant.MATERIEL_EQUIPEMENT)){
////              
////                if(!fileMap.get(TypeAttachement.CDPP.name())) {
////                  JsfUtil.addErrorMessage(bundle("EmptyProformat1"));return;
////                 }
////                if(!fileMap.get(TypeAttachement.LMEI.name())) {
////                  JsfUtil.addErrorMessage(bundle("EmptyProformat2"));return;
////                 }
////                if(!fileMap.get(TypeAttachement.DT.name())) {
////                  JsfUtil.addErrorMessage(bundle("EmptyProformat3"));return;
////                 }
////                if(!fileMap.get(TypeAttachement.CRT.name())) {
////                  JsfUtil.addErrorMessage(bundle("EmptyProformat4"));return;
////                 }
////                if(!fileMap.get(TypeAttachement.RAA.name())) {
////                  JsfUtil.addErrorMessage(bundle("EmptyProformat5"));return;
////                 }
//// 
////              }          
//    
//    
//              
//              
//          }
//    
//    }
 
    
    @Override
    public void prepareSend() {
        
       
        
        generatePaymentData();
        
         if(checkRequestConformityofAttachment()){
            super.prepareSend();
           RequestContext.getCurrentInstance().execute("PF('confirmSendDialog').show();");
         }  
    }

    public CorePaysFacadeLocal getpFacade() {
        if (pFacade == null) {
            pFacade = (CorePaysFacadeLocal) LookupUtil.CoreLookup("CorePaysFacade");
        }
        return pFacade;
    }
    
    
     public List<String> getRequiredAttachmentCodes() {
        return requiredAttachmentCodes;
    }

    public void setRequiredAttachmentCodes(List<String> requiredAttachmentCodes) {
        this.requiredAttachmentCodes = requiredAttachmentCodes;
    }

    public List<String[]> getRequiredAttachments() {
        return requiredAttachments;
    }

    public void setRequiredAttachments(List<String[]> requiredAttachments) {
        this.requiredAttachments = requiredAttachments;
    }
 
    
}
