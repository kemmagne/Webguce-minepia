package org.guce.web.process.atm.controllers.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.guce.core.entities.CoreAttachment;
import org.guce.core.entities.CoreAttachmenttype;
import org.guce.process.atm.ATMConstant;
import org.guce.process.atm.entities.PaymentDocument;
import org.guce.rep.entities.CorePays;
import org.guce.web.process.atm.controllers.ATMRequestController;
import org.primefaces.context.RequestContext;

@ManagedBean(
        name = "aTMRequestController"
)
@ViewScoped
public class ATMRequestControllerImpl extends ATMRequestController {
    
 // private List<CoreAttachmenttype> attachmenttypes;
  
  private HashMap<String, String> officeSelected;

    @PostConstruct
    void inits(){
//        String byCopy = application.getStringParam("type");
//         currentForm     = application.getStringParam("formCode");
//        if(byCopy != null &&byCopy.equals("copy") ) current.setCoreAttachmentList(new ArrayList<CoreAttachment>());
//          selectedProductType = Objects.nonNull(current)  &&  Objects.nonNull(current.getTypeProduit() ) && !current.getTypeProduit().getCode().isEmpty() ? current.getTypeProduit().getCode() : application.getStringParam("typeProduit");
//           List<TypeProduit> t = typeProduitServiceImpl.findTypeProduitByCode(selectedProductType);
//           current.setTypeProduit(t != null && !t.isEmpty() ? t.get(0):null);
//           CorePays pays = new CorePays("CM");
//            pays.setLibellePays("CAMEROUN");
//            current.getTransport().setDestination(pays);
           selectedProductType = Objects.nonNull(current)  &&  Objects.nonNull(current.getTypeAtech()) && !current.getTypeAtech().getCode().isEmpty() ? current.getTypeAtech().getCode() : application.getStringParam("typeAtech");
           
//            List<TypeProduit> t = typeProduitServiceImpl.findTypeProduitByCode(selectedProductType);
//           current.setTypeProduit(t != null && !t.isEmpty() ? t.get(0):null);
           
          // this.initAttachement(selectedProductType);
           this.generateOffice(selectedProductType);
    }
//    
//  public void initAttachement(String productTypeCode){
//      
//        this.attachmenttypes = new ArrayList<>();
//        List<CoreAttachmenttype> attachment = this.getAvaibleAttachmentType();
//        if(productTypeCode != null){
//             for(CoreAttachmenttype coreAttachmenttype : attachment){
//            if (productTypeCode.equals(ATMConstant.TRAITEMENT_STOKAGE) && Arrays.asList("PCCCEC", "DTarif","AT").contains(coreAttachmenttype.getAttachementtypeid().trim())){
//                this.attachmenttypes.add(coreAttachmenttype);
//            }else if (productTypeCode.equals(ATMConstant.INGREDIENT_ADDITIFS) && Arrays.asList("PCCCEC").contains(coreAttachmenttype.getAttachementtypeid().trim())){    
//                    this.attachmenttypes.add(coreAttachmenttype);
//            } else if (productTypeCode.equals(ATMConstant.MATERIEL_EQUIPEMENT) && Arrays.asList("AT").contains(coreAttachmenttype.getAttachementtypeid().trim())){    
//                    this.attachmenttypes.add(coreAttachmenttype);
//                }
//        }
//        }
//  }
  
  
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
     
   public HashMap<String, String> generateOffice(String productTypeCode){
     this.officeSelected = new HashMap<String, String>();
     if(productTypeCode != null){
         if(productTypeCode.equals(ATMConstant.TRAITEMENT_STOKAGE)){
            this.officeSelected.put("DPAIH", "DPAIH");
         }else if (productTypeCode.equals(ATMConstant.INGREDIENT_ADDITIFS)){
        this.officeSelected.put("DPAAIE", "DPAAIE");
        }
     } 
     return this.officeSelected;
    }

//   public List<CoreAttachmenttype> getAttachmenttypes() {
//        return attachmenttypes;
//    }
//
//    public void setAttachmenttypes(List<CoreAttachmenttype> attachmenttypes) {
//        this.attachmenttypes = attachmenttypes;
//    }
    
    
   public HashMap<String, String> getOfficeSelected() {
       return officeSelected;
    }

   public void setOfficeSelected(HashMap<String, String> officeSelected) {
        this.officeSelected = officeSelected;
    }
}
