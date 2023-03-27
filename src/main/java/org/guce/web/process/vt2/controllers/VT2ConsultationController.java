package org.guce.web.process.vt2.controllers;

import javax.annotation.PostConstruct;
import org.guce.core.entities.util.CoreProcessingState;

public class VT2ConsultationController extends VT2Controller {
    @PostConstruct
    public void init() {
        checkProcessingAccessRight();
        if(processing.getProcState().equals(CoreProcessingState.ATTENTE)) {
            serviceMessage.updateProcessing(processing,userController.getUserConnecte(),CoreProcessingState.TRAITER);
        }
        prepareView();
    }

    protected void prepareView() {
    }
}
