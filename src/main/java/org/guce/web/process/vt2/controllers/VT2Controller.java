package org.guce.web.process.vt2.controllers;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import org.guce.core.ejb.facade.interfaces.CoreRecordFacadeLocal;
import org.guce.core.entities.CoreProcessing;
import org.guce.process.vt2.entities.VT2Registration;
import org.guce.rep.entities.RepPositionTarifaire;
import org.guce.web.core.user.controller.WebGuceDefaultController;
import org.guce.web.core.util.DefaultLazyDataModel;
import org.guce.web.core.util.JsfUtil;
import org.guce.web.process.vt2.services.impl.VT2RegistrationMessageServiceImpl;
import org.guce.web.process.vt2.services.impl.VT2RegistrationServiceImpl;

public abstract class VT2Controller extends WebGuceDefaultController {
    protected VT2Registration current;

    protected CoreProcessing processing;

    @EJB
    protected VT2RegistrationServiceImpl service;

    @EJB
    protected VT2RegistrationMessageServiceImpl serviceMessage;

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
    public VT2Registration getCurrent() {
        return current;
    }

    @Override
    public String getProcessingType() {
        return "VT2";
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
        current = (VT2Registration) processing.getRecordId();
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
}
