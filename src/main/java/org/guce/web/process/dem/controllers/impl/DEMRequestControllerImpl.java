package org.guce.web.process.dem.controllers.impl;

import java.math.BigDecimal;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.guce.core.entities.CoreGood;
import org.guce.process.dem.DEMConstant;
import org.guce.process.dem.entities.PaymentDocument;
import org.guce.rep.entities.RepPositionTarifaire;
import org.guce.rep.entities.RepUnit;
import org.guce.web.process.dem.controllers.DEMRequestController;

@ManagedBean(
        name = "dEMRequestController"
)
@ViewScoped
public class DEMRequestControllerImpl extends DEMRequestController {
    
    
    protected void generatePaymentData() {

        current.setPaiement(new PaymentDocument.CONTENT.PAIEMENT());
        current.getPaiement().setCHARGEUR(new PaymentDocument.CONTENT.PAIEMENT.CHARGEUR());
        current.getPaiement().setBENEFICIAIRE(new PaymentDocument.CONTENT.PAIEMENT.BENEFICIAIRE());
        current.getPaiement().setFACTURE(new PaymentDocument.CONTENT.PAIEMENT.FACTURE());
        //  current.getPaiement().setREPARTITIONS(new PaymentDocument.CONTENT.PAIEMENT.REPARTITIONS());

        current.getPaiement().getCHARGEUR().setNUMEROCONTRIBUABLE(super.current.getChargerid().getNumeroContribuable());
        current.getPaiement().getCHARGEUR().setRAISONSOCIALE(current.getChargerid().getChargername());

        current.getPaiement().getBENEFICIAIRE().setCODE(getSpecificProcessParam(current.getRecordProcess(), DEMConstant.BILL_BENEFICIAIRE, "MINCOMMERCE"));
        current.getPaiement().getBENEFICIAIRE().setLIBELLE(getSpecificProcessParam(current.getRecordProcess(), DEMConstant.BILL_BENEFICIAIRE, "MINCOMMERCE"));

        current.getPaiement().getFACTURE().setMONTANTHT(DEMConstant.BILL_MONTANT_HT);
        current.getPaiement().getFACTURE().setREFERENCEFACTURE(current.getRecordId());
        current.getPaiement().getFACTURE().setTYPEFACTURE(getSpecificProcessParam(current.getRecordProcess(), DEMConstant.FORM_PAYMENT, "DEM602"));
        current.getPaiement().getFACTURE().setMONTANTTTC(DEMConstant.BILL_MONTANT_TTC);
        current.getPaiement().getFACTURE().setAUTREMONTANT("0");

    }

    protected void addMockGood() {
        CoreGood coreGood = new CoreGood();
        coreGood.setLineNumber(1);
        coreGood.setBrand(DEMConstant.PROCESS_CODE);
        coreGood.setQuantite(BigDecimal.ONE);
        coreGood.setWeight(BigDecimal.ZERO);
        RepPositionTarifaire hrCode = new RepPositionTarifaire(getSpecificProcessParam(current.getRecordProcess(), DEMConstant.DEFAULT_HS_CODE, "85369000000"));
        coreGood.sethSCode(hrCode);
        RepUnit unit = new RepUnit(getSpecificProcessParam(current.getRecordProcess(), DEMConstant.DEFAULT_UNIT, "KG"));
        coreGood.setRepUnit(unit);
        current.getGoodList().add(coreGood);
    }

    @Override
    public void prepareSend() {
        generatePaymentData();
        addMockGood();
        super.prepareSend();
    }
}
