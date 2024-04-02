/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guce.web.process.atm.controllers;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.guce.core.entities.CoreRecord;
import org.guce.web.core.util.JsfUtil;
import org.guce.web.process.atm.controllers.impl.ATMControllerImpl;
import org.primefaces.context.RequestContext;

/**
 *
 * @author LAROCHE
 */
public class ATMRenouvellementRequestController extends ATMControllerImpl{
    
     @PostConstruct
    public void init() {
        if ("form".equals(type) && recordId != null && checkRecordAccessRight()) {
            prepareEdit();
        } else if ("view".equals(type) && checkRecordAccessRight()) {
            prepareView();
        }
    }

    public void loadRecord() {
        RequestContext context = RequestContext.getCurrentInstance();
        current = service.findBy(recordId);
        if (current == null) {
            JsfUtil.addErrorMessage(bundle("IncorrectNumber"));
            return;
        }
        if (!current.getRecordUserlogin().getPartnerid().equals(application.getUserConnecte().getPartnerid())) {
            JsfUtil.addErrorMessage(bundle("AccessDenied"));
            current = null;
            return;
        }
        if(current.getRecordState().equals(CoreRecord.IN_PROCESS)) {
            JsfUtil.addErrorMessage(bundle("fileInProcessing"));
            current = null;
            return;
        }
        prepareEdit();
        context.execute("PF('loadDialog').hide()");
        context.update("formContenu");
        context.update("dialogsForm");
    }

    public void validateAndSaveAndSend() {
        if(!fileSent && checkRenouvellementRequestConformity()) {
            fileSent = false;
            try {
                beforeSend();
                serviceMessage.send(serviceMessage.sendRenouvellementRequest(current, userController.getUserConnecte()));
                JsfUtil.addSuccessMessage(bundle("RenouvelementRequestSend") + " " + current.getRecordId());
                goToPreviows();
            } catch(Exception ex) {
                fileSent = true;
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
                JsfUtil.addErrorMessage(bundle("CannotSendRenouvellementRequestSend") + " " + current.getRecordId());
            }
        }
    }

    protected boolean checkRenouvellementRequestConformity() {
        return checkRequestConformity();
    }
}
