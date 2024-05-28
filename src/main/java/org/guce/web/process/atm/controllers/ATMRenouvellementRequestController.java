/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guce.web.process.atm.controllers;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.guce.core.entities.CoreAttachment;
import org.guce.core.entities.CoreCharger;
import org.guce.core.entities.CoreGood;
import org.guce.core.entities.CoreRecord;
import org.guce.process.atm.entities.ATMRegistration;
import org.guce.web.core.util.JsfUtil;
import org.guce.web.process.atm.controllers.impl.ATMControllerImpl;
import org.primefaces.context.RequestContext;

/**
 *
 * @author NGC
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
     
        current.setCoreAttachmentList(new ArrayList<CoreAttachment>());
        current.setAtmExpiryDate(null);
        current.setAtmExpiryDate(null);
        current.setRecordId(null);
        prepareEdit();
        context.execute("PF('loadDialog').hide()");
        context.update("formContenu");
        context.update("dialogsForm");
    }


    
    // @Override
     public void validateAndSaveAndSend() {
        if(!fileSent && checkRequestConformity()) {
            fileSent = true;
            try {
                current.setIsrenewing(String.valueOf(Boolean.TRUE));
               this.send();
                JsfUtil.addSuccessMessage(bundle("RequestSend") + " " + current.getRecordId());
                goToPreviows();
            } catch(Exception ex) {
                fileSent = false;
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
                JsfUtil.addErrorMessage(bundle("CannotSendRecord") + " " + current.getRecordId());
            }
        }
    }
    

    protected boolean checkModificationRequestConformity() {
        return checkRequestConformity();
    }
    
     public void prepareSend() {
        if(checkRequestConformity()) {
          
        try {
            current = service.save(current);
           
        } catch(Exception ex) {
            JsfUtil.addErrorMessage(bundle("CannotSaveRecord") + " " + current.getRecordId());
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }
    
    
     protected void send() {
        beforeSend();
        serviceMessage.send(serviceMessage.sendRequest(current,userController.getUserConnecte()));
    }
}
