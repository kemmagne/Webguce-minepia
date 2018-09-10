/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guce.process.vt2.services;

import java.util.List;
import javax.ejb.Local;
import org.guce.process.ct.entities.VTMINEPDEDRegistration;
import org.guce.rep.entities.RepPositionTarifaire;


/**
 *
 * @author probook
 */
@Local
public interface Vt2ServiceLocal {
    public int save(VTMINEPDEDRegistration registration);
    public VTMINEPDEDRegistration findByRecordId(String recordId);
    public List<RepPositionTarifaire> verifySupportedNsh(VTMINEPDEDRegistration registration);
    
}
