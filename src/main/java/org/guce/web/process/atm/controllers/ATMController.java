package org.guce.web.process.atm.controllers;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import org.guce.core.ejb.facade.interfaces.CoreRecordFacadeLocal;
import org.guce.core.entities.CoreProcessing;
import org.guce.core.entities.CoreRecord;
import org.guce.process.atm.entities.ATMRegistration;
import org.guce.rep.entities.RepPositionTarifaire;
import org.guce.web.core.user.controller.WebGuceDefaultController;
import org.guce.web.core.util.DefaultLazyDataModel;
import org.guce.web.core.util.JsfUtil;
import org.guce.web.process.atm.services.impl.ATMRegistrationMessageServiceImpl;
import org.guce.web.process.atm.services.impl.ATMRegistrationServiceImpl;

public abstract class ATMController extends WebGuceDefaultController {
    protected ATMRegistration current;

    protected CoreProcessing processing;

    @EJB
    protected ATMRegistrationServiceImpl service;

    @EJB
    protected ATMRegistrationMessageServiceImpl serviceMessage;

    @EJB
    protected CoreRecordFacadeLocal recordFacade;

    protected String type;

    protected String parentId;

    protected boolean fileSent;

    protected DefaultLazyDataModel<RepPositionTarifaire> hsCodeList;

    @PostConstruct
    public void initCustomsParams() {
        type = application.getStringParam("type");
        parentId = application.getStringParam("parentId");
    }

    @Override
    public ATMRegistration getCurrent() {
        return current;
    }

    @Override
    public String getProcessingType() {
        return "ATM";
    }

    public void save() {
        try {
            current = service.save(current);
            JsfUtil.addSuccessMessage(bundle("RecordSaved") + " " + current.getRecordId());
        } catch(Exception ex) {
            JsfUtil.addErrorMessage(bundle("CannotSaveRecord") + " " + current.getRecordId());
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    protected boolean checkRecordAccessRight() {
        if (recordId == null) {
            JsfUtil.addErrorMessage(bundle("NumberNoFound"));
            goToPreviows();
            return false;
        }
        current = service.findBy(recordId);
        if (current == null) {
            JsfUtil.addErrorMessage(bundle("IncorrectNumber"));
            goToPreviows();
            return false;
        }
        if (!current.getRecordUserlogin().getPartnerid().equals(application.getUserConnecte().getPartnerid())) {
            JsfUtil.addErrorMessage(bundle("AccessDenied"));
            goToPreviows();
            return false;
        }
        return true;
    }

    protected boolean checkProcessingAccessRight() {
        if (processingid == null) {
            JsfUtil.addErrorMessage(bundle("TaskNoFound"));
            goToPreviows();
            return false;
        }
        processing = processingFacade.find(processingid);
        if (processing == null) {
            JsfUtil.addErrorMessage(bundle("IncorrectTaskNumber"));
            goToPreviows();
            return false;
        }
        if(!processing.getProcPartner().equals(application.getUserConnecte().getPartnerid())) {
            JsfUtil.addErrorMessage(bundle("AccessDenied"));
            goToPreviows();
            return false;
        }
        current = (ATMRegistration) processing.getRecordId();
        return true;
    }

    protected boolean checkRequestConformity() {
        if(current.getGoodList().isEmpty()) {
            JsfUtil.addErrorMessage(bundle("GoodListEmpty"));
            return false;
        }
        return true;
    }

    public CoreProcessing getProcessing() {
        return processing;
    }

    
     public boolean  disabledDownloadFileButton(ATMRegistration atmRegistration){
             atmRegistration.setRecordState(CoreRecord.CLOS);
              return !(atmRegistration != null &&  (atmRegistration.getRecordChildrens() == null  || atmRegistration.getRecordChildrens().isEmpty()));
              
     }
    
    public DefaultLazyDataModel<RepPositionTarifaire> getHsCodeList() {
        if (hsCodeList == null) {
            hsCodeList = new DefaultLazyDataModel<>(service.searchNshByProcess(getProcess()));
        }
        return hsCodeList;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    protected void prepareView() {
        fetchFromParent();
    }

    protected void prepareEdit() {
        fetchFromParent();
    }

    protected void beforeSend() {
        pushToParent();
    }

    public void fetchFromParent() {
    }

    public void pushToParent() {
    }

    public boolean compute_invoice() {
        return true;
    }

    public boolean compute_transport() {
        return true;
    }
}
