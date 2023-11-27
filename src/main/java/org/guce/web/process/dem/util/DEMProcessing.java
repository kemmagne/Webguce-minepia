package org.guce.web.process.dem.util;

import javax.ejb.EJB;
import org.guce.core.entities.CoreForm;
import org.guce.core.entities.CoreMessage;
import org.guce.core.entities.CoreUser;
import org.guce.core.entities.util.CoreProcessingState;
import org.guce.orchestra.core.OrchestraEbxmlMessage;
import org.guce.process.dem.DEMConstant;
import org.guce.web.core.transformation.DefaultTraitement;
import org.guce.web.core.transformation.ITraitement;
import org.guce.web.process.dem.services.impl.DEMRegistrationMessageServiceImpl;

public class DEMProcessing extends DefaultTraitement implements ITraitement {
    @EJB
    protected DEMRegistrationMessageServiceImpl serviceMessage;

    @Override
    public Integer processWait(CoreForm form, CoreUser user) {
        String codeForm = form.getFormid().trim();
        switch (codeForm) {
            case DEMConstant.FORM_REQUEST:return ficheFacade.getRecordByProcessingType(DEMConstant.PROCESSING_REQUEST, CoreProcessingState.ATTENTE, user.getPartnerid().getPartnerid()).intValue();
            case DEMConstant.FORM_RESPONSE_CI:return ficheFacade.getRecordByProcessingType(DEMConstant.PROCESSING_CI, CoreProcessingState.ATTENTE, user.getPartnerid().getPartnerid()).intValue();
            case DEMConstant.FORM_CONSULTATION:return ficheFacade.getRecordByProcessingType(DEMConstant.PROCESSING_CONSULTATION, CoreProcessingState.ATTENTE, user.getPartnerid().getPartnerid()).intValue();
            case DEMConstant.FORM_MODIFICATION_REQUEST:return ficheFacade.getRecordByProcessingType(DEMConstant.PROCESSING_MODIFICATION_REJECT, CoreProcessingState.ATTENTE, user.getPartnerid().getPartnerid()).intValue();
            case DEMConstant.FORM_MODIFICATION_CONSULTATION:return ficheFacade.getRecordByProcessingType(DEMConstant.PROCESSING_CONSULTATION_MODIFICATION, CoreProcessingState.ATTENTE, user.getPartnerid().getPartnerid()).intValue();
            case DEMConstant.FORM_MODIFICATION_REJECT:return ficheFacade.getRecordByProcessingType(DEMConstant.PROCESSING_MODIFICATION_REJECT, CoreProcessingState.ATTENTE, user.getPartnerid().getPartnerid()).intValue();
            case DEMConstant.FORM_INVOICE:return ficheFacade.getRecordByProcessingType(DEMConstant.PROCESSING_PAYMENT, CoreProcessingState.ATTENTE, user.getPartnerid().getPartnerid()).intValue();
            default:break;
            }
            return ficheFacade.getRecordByProcessingType(codeForm, CoreProcessingState.ATTENTE, user.getPartnerid().getPartnerid()).intValue();
        }

        @Override
        public void action(OrchestraEbxmlMessage ebxml) throws Exception {
            CoreMessage m = serviceMessage.execute(ebxml);
            if(m != null) {
                serviceMessage.send(m);
            }
        }
    }
