package org.guce.web.process.dem.controllers;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.guce.web.core.util.JsfUtil;
import org.guce.web.process.dem.controllers.impl.DEMControllerImpl;

public class DEMRequestCIController extends DEMControllerImpl {
    @PostConstruct
    public void init() {
        if(checkProcessingAccessRight()) {
            prepareEdit();
        }
    }

    public void send() {
        if(!fileSent && checkRequestConformity()) {
            fileSent = true;
            try {
                beforeSend();
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
