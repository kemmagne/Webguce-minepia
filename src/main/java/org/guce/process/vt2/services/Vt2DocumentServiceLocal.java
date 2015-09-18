/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guce.process.vt2.services;

import javax.ejb.Local;
import org.guce.core.documents.WebguceDocument;
import org.guce.core.entities.CoreMessage;
import org.guce.process.ct.entities.VTMINEPDEDRegistration;


/**
 *
 * @author probook
 */
@Local
public interface Vt2DocumentServiceLocal {
    
     public WebguceDocument createVt2InitMessage(VTMINEPDEDRegistration registration,CoreMessage message);
     
}
