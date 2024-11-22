package org.guce.web.process.atm.controllers;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import org.guce.core.entities.CoreAttachment;
import org.guce.core.entities.CoreRecord;
import org.guce.process.atm.ATMConstant;
import org.guce.process.atm.BeanMapper;
import org.guce.web.core.util.JsfUtil;
import org.guce.process.atm.entities.ATMRegistration;
import org.guce.process.atm.repositories.impl.CoreProcessingFacadeATM;
import org.guce.web.process.atm.controllers.impl.ATMControllerImpl;
import org.primefaces.context.RequestContext;

public class ATMModificationRequestController extends ATMControllerImpl {
    
    @EJB
    protected CoreProcessingFacadeATM coreProcessingFacadeAtm;
    
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
        if(!fileSent && checkModificationRequestConformity()) {
            fileSent = false;
            try {
                beforeSend();
                
               ATMRegistration atMRegistration = service.findBy(current.getRecordId());
                while(atMRegistration.getRecordParent() != null){   
                     atMRegistration = service.findBy(atMRegistration.getRecordParent().getRecordId());
                }
                int count = coreProcessingFacadeAtm.countCoreProcessingStartWithRecordIdAndProcessingType(atMRegistration.getRecordId(),ATMConstant.PROCESSING_MODIFICATION_REQUEST).intValue();
                ATMRegistration atmRegistration =  new ATMRegistration(current,count,atMRegistration.getRecordId()); 
                //
                if(current.getIsStorage().equalsIgnoreCase("TRUE")){
                        atmRegistration.setIsStorage(String.valueOf(Boolean.TRUE));
                   }
                ATMRegistration atMModificaRegistration = service.save(atmRegistration);
                List<CoreAttachment> attachments = BeanMapper.changeCoreRecordAttachment(current.getCoreAttachmentList(),atMModificaRegistration);
                atMModificaRegistration.setCoreAttachmentList(attachments);
                service.save(atMModificaRegistration);
                
                serviceMessage.send(serviceMessage.sendModificationRequest(atMModificaRegistration, userController.getUserConnecte()));
                JsfUtil.addSuccessMessage(bundle("ModificationRequestSend") + " " + atMModificaRegistration.getRecordId());
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
