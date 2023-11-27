package org.guce.web.process.dem.controllers;

import javax.annotation.PostConstruct;
import org.guce.core.entities.util.CoreProcessingState;
import org.guce.web.process.dem.controllers.impl.DEMControllerImpl;

public class DEMConsultationController extends DEMControllerImpl {
    @PostConstruct
    public void init() {
        checkProcessingAccessRight();
        if(processing.getProcState().equals(CoreProcessingState.ATTENTE)) {
            serviceMessage.updateProcessing(processing,userController.getUserConnecte(),CoreProcessingState.TRAITER);
        }
        prepareView();
    }
}
