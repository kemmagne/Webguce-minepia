package org.guce.web.process.atm.controllers.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.guce.core.entities.CoreAttachment;
import org.guce.core.entities.CoreAttachmenttype;
import org.guce.process.atm.ATMConstant;
import org.guce.process.atm.entities.PaymentDocument;
import org.guce.process.atm.entities.TypeAvtech;
import org.guce.rep.entities.CorePays;
import org.guce.rep.entities.RepPositionTarifaire;
import org.guce.web.core.util.JsfUtil;
import org.guce.web.process.atm.controllers.ATMRequestController;
import org.guce.web.process.atm.services.impl.ATMRegistrationServiceImpl;
import org.guce.web.process.atm.services.impl.TypeAvtechServiceImpl;
import org.primefaces.context.RequestContext;

@ManagedBean(
        name = "aTMRequestController"
)
@ViewScoped
public class ATMRequestControllerImpl extends ATMRequestController {

  private Logger LOGGER = Logger.getLogger(ATMRequestControllerImpl.class.getName());    
  
      @EJB
    private ATMRegistrationServiceImpl atMRegistrationServiceImpl;
    
    private List<CoreAttachmenttype> attachmenttypes ;

    @EJB
    private TypeAvtechServiceImpl typeAvtechServiceImpl;
 
 private String selectedTypeAvisTechnique = "";   
    
 
 @PostConstruct
 public void initQuery(){
  
     
     
//      if(application.getStringParam("typeAtech")==null){
//         selectedTypeAvisTechnique = (current != null)?current.getTypeAtech().getLabel():"Not Found";
//       }else{
//         selectedTypeAvisTechnique = application.getStringParam("typeAtech");
//       }
//     


//     selectedTypeAvisTechnique = Objects.nonNull(current)  &&  Objects.nonNull(current.getTypeAtech() ) && !current.getTypeAtech().getCode().isEmpty() ? current.getTypeAtech().getCode() : application.getStringParam("typeAtech");
//     List<TypeAvtech> t = typeAvtechServiceImpl.findTypeTypeAvtechByCode(selectedTypeAvisTechnique);
//     TypeAvtech t1 = t.get(0);
//     current.setTypeAtech(t != null && !t.isEmpty() ? t.get(0):null);
//
//   
//    CorePays pays = new CorePays("CM");
//    pays.setLibellePays("CAMEROUN");
//    current.getTransport().setDestination(pays);  
//     
//    String byCopy = application.getStringParam("type");
//     if(byCopy.equals("copy")){
//    
//      if(byCopy != null &&  byCopy.equals("copy")){
//         getCurrent().setCoreAttachmentList(new ArrayList<CoreAttachment>());
//      }
//    }
      
     
     loadProductType();
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
     
  
    
//    
//    
//    
//    protected List<String> requiredAttachmentCodes = Arrays.asList(
//               
//        new String[] {"DTarif", "CCI", "AT", "CMP", "LI", "TPV", "QPA"}
//    );
//    
//      protected List<String> requiredAttachmentCodesATM02 = Arrays.asList(
//               
//        new String[] {"DT", "PDCE", "PDDE", "PPEC", "CRT", "LPI", "RAA", "PCCCEC"}
//    );
//      
//      
//        protected List<String> requiredAttachmentCodesATM03 = Arrays.asList(
//               
//        new String[] {"CDPP", "LMEI", "DT", "CRT", "RAA"}
//    );
//    
//    protected List<String[]> requiredAttachments = new ArrayList<String[]>();
//    
//   
//     
//    @Override
//    protected boolean checkRequestConformity() {
//      
//        boolean checkRequestConformityAttachment = this.checkRequestConformityAttachment();
//        if(!checkRequestConformityAttachment) {
//            return checkRequestConformityAttachment;
//        }
//        return true;
//    }
//    
//  
//    
//    
//    protected boolean checkRequestConformityAttachment() {
//        this.initRequiredAttachements();
//        if(current != null) {
//            List<CoreAttachment> attachments = current.getCoreAttachmentList();
//            List<String> attachmentCodes = new ArrayList<String>();
//            List<String> attachmentLabels = new ArrayList<String>();
//            List<String> requiredAttachmentsLabel = new ArrayList<String>();
//            LOGGER.log( Level.INFO, "DEMControllerImpl -- checkRequestConformityAttachment | requiredAttachments:: {0}", new Object[]{
//                requiredAttachments,
//            });
//            
//            LOGGER.log( Level.INFO, "ATMControllerImpl -- checkRequestConformityAttachment | attachments:: {0}", new Object[]{
//                attachments,
//            });
//            if(attachments != null && attachments.size() > 0) {
//                for(CoreAttachment attachment: attachments) {
//                    if(attachment.getPjType() != null) {
//                        attachmentCodes.add(attachment.getPjType().getAttachementtypeid());
//                        attachmentLabels.add(attachment.getPjLibelle());
//                    }
//                }
//                LOGGER.log( Level.INFO, "ATMControllerImpl -- checkRequestConformityAttachment | attachmentCodes:: {0}", new Object[]{
//                    attachmentCodes,
//                });
//
//                for(String[] requiredAttachment: requiredAttachments) {
//                    if(!attachmentCodes.contains(requiredAttachment[0])) {
//                        requiredAttachmentsLabel.add(requiredAttachment[1]);
//                    }
//                }
//                if(requiredAttachmentsLabel.size() > 0) {
//                    
//                    for(String requiredAttachment:requiredAttachmentsLabel){
//                        String prefix = "L' ";
//                        JsfUtil.addErrorMessage(prefix.concat(" ").concat(requiredAttachment.toLowerCase().concat(" ").concat(bundle("RequiredAttachmentsMessage"))));
//                    }
//                                    
////                    JsfUtil.addErrorMessage(bundle("RequiredAttachmentsMessage") + String.format(" (%s)", String.join(", ", requiredAttachmentsLabel)));
//                    return false;
//                }
//            } else {
//                JsfUtil.addErrorMessage(bundle("AttachmentListEmpty"));
//                return false;
//            }
//        }
//      
//        return true;
//    }
//    private void initRequiredAttachements() {
//        this.requiredAttachments = new ArrayList<String[]>();
//        List<CoreAttachmenttype> avaibleAttachmentTypes = this.getAvaibleAttachmentType();
//        for(CoreAttachmenttype avaibleAttachmentType: avaibleAttachmentTypes) {
//            if(this.requiredAttachmentCodes.contains(avaibleAttachmentType.getAttachementtypeid())) {
//                this.requiredAttachments.add(new String[]{
//                    avaibleAttachmentType.getAttachementtypeid(),
//                    avaibleAttachmentType.getAttachementtypename(),
//                });
//            }else if(this.requiredAttachmentCodesATM02.contains(avaibleAttachmentType.getAttachementtypeid())) {
//                this.requiredAttachments.add(new String[]{
//                    avaibleAttachmentType.getAttachementtypeid(),
//                    avaibleAttachmentType.getAttachementtypename(),
//                });
//            }else if(this.requiredAttachmentCodesATM03.contains(avaibleAttachmentType.getAttachementtypeid())){
//                     this.requiredAttachments.add(new String[]{
//                    avaibleAttachmentType.getAttachementtypeid(),
//                    avaibleAttachmentType.getAttachementtypename(),
//                     });
//          }
//        }
//    }
    
}
