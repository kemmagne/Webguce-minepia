/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guce.web.process.atm.controllers.impl;

import java.util.HashMap;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.apache.commons.lang.StringUtils;
import org.guce.process.atm.ATMConstant;
import org.guce.process.atm.entities.PaymentDocument;
import org.guce.web.process.atm.controllers.ATMRenouvellementRequestController;

/**
 *
 * @author NGC
 */
@ManagedBean(
        name = "aTMRenouvellementRequestController"
)
@ViewScoped
public class ATMRenouvellementRequestControllerImpl extends ATMRenouvellementRequestController {
 
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
     
     
       public String displayStampfees(){
      String stampFees = StringUtils.EMPTY;
      if(current != null){
          // stampFees = getSpecificProcessParam(current.getRecordProcess(), DEMConstant.BILL_MONTANT_HT, "0");
         stampFees = ATMConstant.BILL_MONTANT_HT;
      }
      return stampFees;
    }
     
     
  
    @Override
    public void prepareSend() {
        generatePaymentData();
        super.prepareSend();  
    }
     
    @Override 
    public void validateAndSaveAndSend(){
             current.setRecordId(null);
             current.setRecordSendate(null);
           prepareSend();
           super.validateAndSaveAndSend();
    }
}
