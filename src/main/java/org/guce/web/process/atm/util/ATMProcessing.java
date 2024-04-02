package org.guce.web.process.atm.util;

import javax.ejb.EJB;
import org.guce.core.entities.CoreForm;
import org.guce.core.entities.CoreMessage;
import org.guce.core.entities.CoreUser;
import org.guce.core.entities.util.CoreProcessingState;
import org.guce.orchestra.core.OrchestraEbxmlMessage;
import org.guce.process.atm.ATMConstant;
import org.guce.web.core.transformation.DefaultTraitement;
import org.guce.web.core.transformation.ITraitement;
import org.guce.web.process.atm.services.impl.ATMRegistrationMessageServiceImpl;

public class ATMProcessing extends DefaultTraitement implements ITraitement {
    @EJB
    protected ATMRegistrationMessageServiceImpl serviceMessage;

    @Override
    public Integer processWait(CoreForm form, CoreUser user) {
        String codeForm = form.getFormid().trim();
        switch (codeForm) {
            case ATMConstant.FORM_REQUEST:return ficheFacade.getRecordByProcessingType(ATMConstant.PROCESSING_REQUEST, CoreProcessingState.ATTENTE, user.getPartnerid().getPartnerid()).intValue();
            case ATMConstant.FORM_RESPONSE_CI:return ficheFacade.getRecordByProcessingType(ATMConstant.PROCESSING_CI, CoreProcessingState.ATTENTE, user.getPartnerid().getPartnerid()).intValue();
            case ATMConstant.FORM_CONSULTATION:return ficheFacade.getRecordByProcessingType(ATMConstant.PROCESSING_CONSULTATION, CoreProcessingState.ATTENTE, user.getPartnerid().getPartnerid()).intValue();
            case ATMConstant.FORM_MODIFICATION_REQUEST:return ficheFacade.getRecordByProcessingType(ATMConstant.PROCESSING_MODIFICATION_REJECT, CoreProcessingState.ATTENTE, user.getPartnerid().getPartnerid()).intValue();
            case ATMConstant.FORM_RENOUVELLEMENT_REQUEST:return ficheFacade.getRecordByProcessingType(ATMConstant.PROCESSING_RENOUVELLEMENT_REJECT, CoreProcessingState.ATTENTE, user.getPartnerid().getPartnerid()).intValue();
            case ATMConstant.FORM_MODIFICATION_CONSULTATION:return ficheFacade.getRecordByProcessingType(ATMConstant.PROCESSING_CONSULTATION_MODIFICATION, CoreProcessingState.ATTENTE, user.getPartnerid().getPartnerid()).intValue();
            case ATMConstant.FORM_RENOUVELLEMENT_CONSULTATION:return ficheFacade.getRecordByProcessingType(ATMConstant.PROCESSING_CONSULTATION_RENOUVELLEMENT, CoreProcessingState.ATTENTE, user.getPartnerid().getPartnerid()).intValue();
            case ATMConstant.FORM_MODIFICATION_REJECT:return ficheFacade.getRecordByProcessingType(ATMConstant.PROCESSING_MODIFICATION_REJECT, CoreProcessingState.ATTENTE, user.getPartnerid().getPartnerid()).intValue();
            case ATMConstant.FORM_INVOICE:return ficheFacade.getRecordByProcessingType(ATMConstant.PROCESSING_PAYMENT, CoreProcessingState.ATTENTE, user.getPartnerid().getPartnerid()).intValue();
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
