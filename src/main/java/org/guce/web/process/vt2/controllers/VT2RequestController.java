package org.guce.web.process.vt2.controllers;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.guce.core.entities.CoreAttachment;
import org.guce.core.entities.CoreCAD;
import org.guce.core.entities.CoreCharger;
import org.guce.core.entities.CoreGood;
import org.guce.core.entities.CoreRecord;
import org.guce.process.vt2.entities.VT2Registration;
import org.guce.web.core.util.JsfUtil;
import org.primefaces.context.RequestContext;

public class VT2RequestController extends VT2Controller {
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
        current = new VT2Registration();
        current.setCoreAttachmentList(new ArrayList<CoreAttachment>());
        current.setGoodList(new ArrayList<CoreGood>());
        current.setRecordUserlogin(userController.getUserConnecte());
        current.setChargerid(new CoreCharger());
        current.setRecordProcess(getProcess());
        current.setCad(new CoreCAD());
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

    protected void prepareEdit() {
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

    protected void prepareView() {
    }

    protected void send() {
        serviceMessage.send(serviceMessage.sendRequest(current,userController.getUserConnecte()));
    }

    public void validateAndSaveAndSend() {
        if(!fileSent && checkRequestConformity()) {
            fileSent = true;
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
