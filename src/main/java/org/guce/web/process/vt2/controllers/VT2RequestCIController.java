package org.guce.web.process.vt2.controllers;

import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.guce.web.core.util.JsfUtil;

public class VT2RequestCIController extends VT2Controller {
    @PostConstruct
    public void init() {
        checkProcessingAccessRight();
        calculateVtMinepdedFees();
    }

    public void send() {
        if(!fileSent && checkRequestConformity()) {
            fileSent = true;
            try {
                serviceMessage.send(serviceMessage.sendCIResponse(current,userController.getUserConnecte(),processing));
                current.setFeesAmount(vtMinepdedAmountFees);
                if(vtMinepdedAmountFees.compareTo(BigDecimal.ZERO) > 0){
                    current.setTotalFeesAmount(current.getTotalFeesAmount() != null ? current.getTotalFeesAmount().add(vtMinepdedAmountFees) : vtMinepdedAmountFees);
                }
                current = service.save(current);
                JsfUtil.addSuccessMessage(bundle("CIResponseSend") + " " + current.getRecordId());
                goToPreviows();
            } catch(Exception ex) {
                fileSent = false;
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
                JsfUtil.addErrorMessage(bundle("CannotSendCIResponse") + " " + current.getRecordId());
            }
        }
    }
}
