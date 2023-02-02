package org.guce.web.process.vt2.controllers;

import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.guce.core.entities.CoreRecord;
import org.guce.web.core.util.JsfUtil;
import org.primefaces.context.RequestContext;

public class VT2ModificationRequestController extends VT2Controller {
    @PostConstruct
    public void init() {
        if ("form".equals(type) && recordId != null && checkRecordAccessRight()) {
            prepareEdit();
        } else if ("view".equals(type) && checkRecordAccessRight()) {
            prepareView();
        }
    }

    protected void prepareEdit() {
    }

    protected void prepareView() {
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
        selectGoodProductCategories();
        context.execute("PF('loadDialog').hide()");
        context.update("formContenu");
        context.update("dialogsForm");
    }

    public void validateAndSaveAndSend() {
        if(!fileSent && checkModificationRequestConformity()) {
            fileSent = false;
            try {
                serviceMessage.send(serviceMessage.sendModificationRequest(current, userController.getUserConnecte()));
                current.setFeesAmount(vtMinepdedAmountFees);
                if(vtMinepdedAmountFees.compareTo(BigDecimal.ZERO) > 0){
                    current.setTotalFeesAmount(current.getTotalFeesAmount() != null ? current.getTotalFeesAmount().add(vtMinepdedAmountFees) : vtMinepdedAmountFees);
                }
                current = service.save(current);
                JsfUtil.addSuccessMessage(bundle("ModificationRequestSend") + " " + current.getRecordId());
                goToPreviows();
            } catch(Exception ex) {
                fileSent = true;
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
                JsfUtil.addErrorMessage(bundle("CannotSendModificationRequestSend") + " " + current.getRecordId());
            }
        }
    }
    
    protected boolean checkModificationRequestConformity() {
        return checkRequestConformity();
    }
}
