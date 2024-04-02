package org.guce.web.process.atm.controllers;

import javax.annotation.PostConstruct;
import org.guce.core.entities.util.CoreProcessingState;
import org.guce.web.process.atm.controllers.impl.ATMControllerImpl;

public class ATMConsultationController extends ATMControllerImpl {
    @PostConstruct
    public void init() {
        checkProcessingAccessRight();
        if(processing.getProcState().equals(CoreProcessingState.ATTENTE)) {
            serviceMessage.updateProcessing(processing,userController.getUserConnecte(),CoreProcessingState.TRAITER);
        }
        prepareView();
    }
}
