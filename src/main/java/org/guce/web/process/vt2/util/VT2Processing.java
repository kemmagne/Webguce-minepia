package org.guce.web.process.vt2.util;

import javax.ejb.EJB;
import org.guce.core.entities.CoreForm;
import org.guce.core.entities.CoreMessage;
import org.guce.core.entities.CoreUser;
import org.guce.core.entities.util.CoreProcessingState;
import org.guce.orchestra.core.OrchestraEbxmlMessage;
import org.guce.process.vt2.VT2Constant;
import org.guce.web.core.transformation.DefaultTraitement;
import org.guce.web.core.transformation.ITraitement;
import org.guce.web.process.vt2.services.impl.VT2RegistrationMessageServiceImpl;

public class VT2Processing extends DefaultTraitement implements ITraitement {
    @EJB
    protected VT2RegistrationMessageServiceImpl serviceMessage;

    @Override
    public Integer processWait(CoreForm form, CoreUser user) {
        String codeForm = form.getFormid().trim();
        switch (codeForm) {
            case VT2Constant.FORM_REQUEST:return ficheFacade.getRecordByProcessingType(VT2Constant.PROCESSING_REQUEST, CoreProcessingState.ATTENTE, user.getPartnerid().getPartnerid()).intValue();
            case VT2Constant.FORM_RESPONSE_CI:return ficheFacade.getRecordByProcessingType(VT2Constant.PROCESSING_CI, CoreProcessingState.ATTENTE, user.getPartnerid().getPartnerid()).intValue();
            case VT2Constant.FORM_CONSULTATION:return ficheFacade.getRecordByProcessingType(VT2Constant.PROCESSING_CONSULTATION, CoreProcessingState.ATTENTE, user.getPartnerid().getPartnerid()).intValue();
            case VT2Constant.FORM_MODIFICATION_REQUEST:return ficheFacade.getRecordByProcessingType(VT2Constant.PROCESSING_MODIFICATION_REJECT, CoreProcessingState.ATTENTE, user.getPartnerid().getPartnerid()).intValue();
            case VT2Constant.FORM_MODIFICATION_CONSULTATION:return ficheFacade.getRecordByProcessingType(VT2Constant.PROCESSING_CONSULTATION_MODIFICATION, CoreProcessingState.ATTENTE, user.getPartnerid().getPartnerid()).intValue();
            case VT2Constant.FORM_MODIFICATION_REJECT:return ficheFacade.getRecordByProcessingType(VT2Constant.PROCESSING_MODIFICATION_REJECT, CoreProcessingState.ATTENTE, user.getPartnerid().getPartnerid()).intValue();
            case VT2Constant.FORM_INVOICE:return ficheFacade.getRecordByProcessingType(VT2Constant.PROCESSING_PAYMENT, CoreProcessingState.ATTENTE, user.getPartnerid().getPartnerid()).intValue();
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
