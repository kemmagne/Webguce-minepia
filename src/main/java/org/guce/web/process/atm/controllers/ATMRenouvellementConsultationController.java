/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guce.web.process.atm.controllers;

import javax.annotation.PostConstruct;
import org.guce.core.entities.util.CoreProcessingState;
import org.guce.web.process.atm.controllers.impl.ATMControllerImpl;

/**
 *
 * @author LAROCHE
 */
public class ATMRenouvellementConsultationController extends ATMControllerImpl{
    
    @PostConstruct
    public void init() {
        checkProcessingAccessRight();
        if(processing.getProcState().equals(CoreProcessingState.ATTENTE)) {
            serviceMessage.updateProcessing(processing,userController.getUserConnecte(),CoreProcessingState.TRAITER);
        }
        prepareView();
    }
    
}
