package org.guce.web.process.vt2.controllers;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.guce.web.core.util.JsfUtil;

public class VT2RequestCIController extends VT2Controller {
    @PostConstruct
    public void init() {
        checkProcessingAccessRight();
    }

    public void send() {
        if(!fileSent && checkRequestConformity()) {
            fileSent = true;
            try {
                serviceMessage.send(serviceMessage.sendCIResponse(current,userController.getUserConnecte(),processing));
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
