package org.guce.web.process.atm.controllers;

import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.guce.core.entities.CoreAttachment;
import org.guce.core.entities.CoreCharger;
import org.guce.core.entities.CoreGood;
import org.guce.core.entities.CoreRecord;
import org.guce.process.atm.ATMConstant;
import org.guce.process.atm.entities.ATMRegistration;
import org.guce.web.core.util.JsfUtil;
import org.guce.web.process.atm.controllers.impl.ATMControllerImpl;
import org.primefaces.context.RequestContext;

public class ATMRequestController extends ATMControllerImpl {
    @PostConstruct
    public void init() {
        if ("form".equals(type) && recordId == null) {
            prepareInitForm();
        } else if ("form".equals(type) && recordId != null && checkRecordAccessRight()) {
            prepareEdit();
        } else if ("copy".equals(type) && checkRecordAccessRight()) {
            prepareCopy();
        } else if ("view".equals(type) && checkRecordAccessRight()) {
            prepareView();
        }
    }

    protected void prepareInitForm() {
        current = new ATMRegistration();
        current.setCoreAttachmentList(new ArrayList<CoreAttachment>());
        current.setGoodList(new ArrayList<CoreGood>());
        current.setRecordUserlogin(userController.getUserConnecte());
        current.setChargerid(new CoreCharger());
        current.setRecordProcess(getProcess());
        current.setSupplier(new CoreCharger());
    }

    public void loadRecord() {
        RequestContext context = RequestContext.getCurrentInstance();
        CoreRecord parent = recordFacade.find(parentId);
        if (parent == null) {
            JsfUtil.addErrorMessage(bundle("IncorrectNumber"));
            return;
        }
        if (!parent.getRecordUserlogin().getPartnerid().equals(application.getUserConnecte().getPartnerid())) {
            JsfUtil.addErrorMessage(bundle("AccessDenied"));
            return;
        }
        if(loadParentData(parent)) {
            current.setRecordParent(parent);
            prepareEdit();
            context.execute("PF('loadDialog').hide()");
            context.update("formContenu");
            context.update("dialogsForm");
        }
    }

    public void prepareSend() {
        if(checkRequestConformity()) {
            save();
        }
    }

    protected void prepareCopy() {
        current.setRecordId(null);
        current.getChargerid().setChargerid(null);
        current.setReocordConversationid(null);
        current.setRecordSendate(null);
        current.setRecordCreatedate(null);
        current.setRecordState(null);
        current.setReocordConversationid(null);
        current.getChargerid().setChargerid(null);
        if(current.getGoodList() != null) {
            for(CoreGood g : current.getGoodList()) {
                g.setID(null);
            }
        }
        if(current.getCoreAttachmentList() != null) {
            for(CoreAttachment a : current.getCoreAttachmentList()) {
                a.setPjId(null);
            }
        }
        current.getSupplier().setChargerid(null);
    }

    protected void send() {
        beforeSend();
        serviceMessage.send(serviceMessage.sendRequest(current,userController.getUserConnecte()));
    }

    public void validateAndSaveAndSend() {
        if(!fileSent && checkRequestConformity()) {
            fileSent = true;
            
             if(selectedTypeAvisTechnique != null && selectedTypeAvisTechnique.equals(ATMConstant.TRAITEMENT_STOKAGE)){  
             current.setIsStorage(String.valueOf(Boolean.TRUE));  
          }
          current.getTransport().getDestination().setCodePays("CM.");
         
//          if(selectedTypeAvisTechnique!=null){
//          current.setTypeAtech(typeAvtechServiceImpl.findTypeAvTechBySingleCode(selectedTypeAvisTechnique));}
          //current.setTypeAtech();
           Logger.getLogger("This is the new country code"+ current.getTransport().getDestination().getCodePays());
            
            try {
                send();
                JsfUtil.addSuccessMessage(bundle("RequestSend") + " " + current.getRecordId());
                goToPreviows();
            } catch(Exception ex) {
                fileSent = false;
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
                JsfUtil.addErrorMessage(bundle("CannotSendRecord") + " " + current.getRecordId());
            }
        }
    }

    public void validateAndSave() {
        if(checkRequestConformity()) {
            save();
        }
    }

    protected boolean loadParentData(CoreRecord parent) {
        current.setChargerid(parent.getChargerid());
        if(parent.getGoodList() != null) {
            for(CoreGood g : parent.getGoodList()) {
                g.setID(null);
                current.getGoodList().add(g);
            }
        }
        return true;
    }
}
