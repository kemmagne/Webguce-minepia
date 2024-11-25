/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guce.web.process.atm.controllers.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.apache.commons.lang.StringUtils;
import org.guce.core.entities.CoreAttachment;
import org.guce.core.entities.CoreAttachmenttype;
import org.guce.core.entities.CoreGood;
import org.guce.core.entities.CoreAttachment;
import org.guce.core.entities.CoreAttachmenttype;
import org.guce.core.entities.CoreGood;
import org.guce.process.atm.ATMConstant;
import org.guce.process.atm.entities.PaymentDocument;
import org.guce.web.core.util.JsfUtil;
import org.guce.web.process.atm.controllers.ATMRenouvellementRequestController;
import static org.guce.web.process.atm.util.ATMAddArticleToAttachment.addArticleToAttachment;
import org.primefaces.context.RequestContext;

import static org.guce.web.process.atm.util.ATMAddArticleToAttachment.addArticleToAttachment;
import org.primefaces.context.RequestContext;


/**
 *
 * @author NGC
 */
@ManagedBean(
        name = "aTMRenouvellementRequestController"
)
@ViewScoped
public class ATMRenouvellementRequestControllerImpl extends ATMRenouvellementRequestController {
    
    @PostConstruct
    public void initAttachment(){
      checkRenouvellementRequestConformity();
    }
    
    
    private Logger LOGGER = Logger.getLogger(ATMControllerImpl.class.getName());
    protected List<CoreAttachmenttype> attachmentRenouvellementtypes;    
    
    protected List<String[]> requiredAttachments = new ArrayList<String[]>();
    
    
     @Override
    public HashMap<String, String> getOfficeSelected() {
       return super.generateOffice(formCode);
    }
    
    
    protected List<String> requiredAttachmentCodes = Arrays.asList(
        new String[] {"PATEN","QUIT","DEMANDE","COPIE","RAPPORT","CONFORMITE","ORIGINE","DECLARATION","PREVISION","CERTIFICAT"}
    );
   
    @Override
   public void loadRecord() {
        super.loadRecord();
        checkValidAttachmentType(); 
          
   }
   
     

    protected boolean checkRenouvellementRequestConformity() {
      
        boolean checkRequestConformityAttachment = this.checkRequestConformityAttachment();
        if(!checkRequestConformityAttachment) {
            return checkRequestConformityAttachment;
        }
        return true;
    }
    
  
    
    
    protected boolean checkRequestConformityAttachment() {
        this.initRequiredAttachements();
        if(current != null) {
            List<CoreAttachment> attachments = current.getCoreAttachmentList();
            List<String> attachmentCodes = new ArrayList<String>();
            List<String> attachmentLabels = new ArrayList<String>();
            List<String> requiredAttachmentsLabel = new ArrayList<String>();
            LOGGER.log( Level.INFO, "ATMRenouvellementRequestController -- checkRequestConformityAttachment | requiredAttachments:: {0}", new Object[]{
                requiredAttachments,
            });
            
            LOGGER.log( Level.INFO, "ATMRenouvellementRequestController -- checkRequestConformityAttachment | attachments:: {0}", new Object[]{
                attachments,
            });
            if(attachments != null && attachments.size() > 0) {
                for(CoreAttachment attachment: attachments) {
                    if(attachment.getPjType() != null) {
                        attachmentCodes.add(attachment.getPjType().getAttachementtypeid());
                        attachmentLabels.add(attachment.getPjLibelle());
                    }
                }
                LOGGER.log( Level.INFO, "ATMRenouvellementRequestController -- checkRequestConformityAttachment | attachmentCodes:: {0}", new Object[]{
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
                        JsfUtil.addErrorMessage(prefix.concat(" ").concat(requiredAttachment.toLowerCase().concat(" ").concat(bundle("RequiredAttachmentsMessage"))));
                    }
                                    
//                    JsfUtil.addErrorMessage(bundle("RequiredAttachmentsMessage") + String.format(" (%s)", String.join(", ", requiredAttachmentsLabel)));
                    return false;
                }
            } else {
                JsfUtil.addErrorMessage(bundle("AttachmentListEmpty"));
                return false;
            }
        }
      
        return true;
    }
    private void initRequiredAttachements() {
        this.requiredAttachments = new ArrayList<String[]>();
        List<CoreAttachmenttype> avaibleAttachmentTypes = this.getAvaibleAttachmentType();
        for(CoreAttachmenttype avaibleAttachmentType: avaibleAttachmentTypes) {
            if(this.requiredAttachmentCodes.contains(avaibleAttachmentType.getAttachementtypeid())) {
                this.requiredAttachments.add(new String[]{
                    avaibleAttachmentType.getAttachementtypeid(),
                    addArticleToAttachment(avaibleAttachmentType.getAttachementtypename()),
                });
            }
        }
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
     
     
       public String displayStampfees(){
      String stampFees = StringUtils.EMPTY;
      if(current != null){
          // stampFees = getSpecificProcessParam(current.getRecordProcess(), DEMConstant.BILL_MONTANT_HT, "0");
         stampFees = ATMConstant.BILL_MONTANT_HT;
      }
      return stampFees;
    }
   
   
       
   public void checkValidAttachmentType(){
      if(current != null){
      this.attachmentRenouvellementtypes = new ArrayList<>();
       List<CoreAttachmenttype> attachment = this.getAvaibleAttachmentType();
          for(CoreAttachmenttype coreAttachmenttype : attachment){
              if(Arrays.asList("AUTRE","PATEN","QUIT","DEMANDE","COPIE","RAPPORT","CONFORMITE","ORIGINE","DECLARATION","PREVISION","CERTIFICAT").contains(coreAttachmenttype.getAttachementtypeid().trim())){
                 this.attachmentRenouvellementtypes.add(coreAttachmenttype);
            
          }
      }
   }
  }
   
   
    
    @Override
    public void prepareSend() {
        
        
        generatePaymentData();
        super.prepareSend(); 
        RequestContext.getCurrentInstance().execute("PF('confirmSendDialog').show();");

        super.prepareSend(); 
        RequestContext.getCurrentInstance().execute("PF('confirmSendDialog').show();");

    }
     
    @Override 
    public void validateAndSaveAndSend(){
             current.setRecordId(null);
             current.setRecordSendate(null);
        if(checkRenouvellementRequestConformity()){
        if(checkRenouvellementRequestConformity()){
           prepareSend();
           super.validateAndSaveAndSend();
        } 
    }
    } 

     
//     protected void send() {
//        beforeSend();
//        serviceMessage.send(serviceMessage.sendRequest(current,userController.getUserConnecte()));
//    }

    public List<CoreAttachmenttype> getAttachmentRenouvellementtypes() {
        return attachmentRenouvellementtypes;
    }

    public void setAttachmentRenouvellementtypes(List<CoreAttachmenttype> attachmentRenouvellementtypes) {
        this.attachmentRenouvellementtypes = attachmentRenouvellementtypes;
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
