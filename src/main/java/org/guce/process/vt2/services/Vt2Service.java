/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guce.process.vt2.services;

import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.guce.core.ejb.facade.interfaces.CoreChargerFacadeLocal;
import org.guce.core.ejb.facade.interfaces.CoreProcessingFacadeLocal;
import org.guce.core.ejb.facade.interfaces.IdentifiantGeneratorLocal;
import org.guce.core.entities.CoreGood;
import org.guce.core.entities.CoreProcess;
import org.guce.core.entities.CoreProcessing;
import org.guce.core.entities.CoreProcessingtype;
import org.guce.core.entities.CoreRecord;
import org.guce.core.services.GuceCalendarUtil;
import org.guce.core.services.UserSessionBeanLocal;
import org.guce.process.ct.ejb.interfaces.VTMINEPDEDRegistrationFacadeLocal;
import org.guce.process.ct.entities.VTMINEPDEDRegistration;
import org.guce.rep.entities.RepPositionTarifaire;

/**
 *
 * @author Koufana Crepin Sosthene
 * @author Steve Anyam
 * @author Ulrih ETEME
 */
@Stateless
public class Vt2Service implements Vt2ServiceLocal {

    @EJB
    private IdentifiantGeneratorLocal generator;
    @EJB
    private CoreChargerFacadeLocal chargerFacade;
    @EJB
    private VTMINEPDEDRegistrationFacadeLocal facade;
    @EJB
    private UserSessionBeanLocal userFacade;
    @EJB
    private CoreProcessingFacadeLocal processingFacade;
	
	
	
	@PersistenceContext(unitName = "Partner-ejb-unity")
    private EntityManager em;

    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public int save(VTMINEPDEDRegistration current) {
        current.setRecordLastCreateDate(GuceCalendarUtil.getCalendar().getTime());
        if (current.getRecordId() == null) {                            
            current.setRecordId(generator.getIdentifiant("VT2_SEQ", "VMD"));
            if (current.getChargerid().getChargerid() == null){
                chargerFacade.create(current.getChargerid());
            } else {
                chargerFacade.edit(current.getChargerid());
            }
            current.setRecordCreatedate(GuceCalendarUtil.getCalendar().getTime());
            facade.create(current);
            return 0;
        } else {
            chargerFacade.edit(current.getChargerid());
            facade.edit(current);            
            return 1;
        }
    }

    @Override
    public VTMINEPDEDRegistration findByRecordId(String recordId) {
        return (recordId == null) ? null : facade.find(recordId);
    }

    protected CoreProcessing createProcessing(String type, String state, CoreRecord current) {
        CoreProcessing processing = new CoreProcessing();
        processing.setProcState(state);
        processing.setProcstartdate(GuceCalendarUtil.getCalendar().getTime());
        processing.setProcessingtypeid(new CoreProcessingtype(type));
        processing.setRecordId(current);
        processing.setProcPartner(userFacade.getUserConnecte().getPartnerid());
        processingFacade.create(processing);
        return processing;
    }

    protected CoreProcessing createProcessing(String type, String state, int status, CoreRecord current) {
        CoreProcessing processing = new CoreProcessing();
        processing.setProcState(state);
        processing.setStatus(status);
        processing.setProcstartdate(GuceCalendarUtil.getCalendar().getTime());
        processing.setProcessingtypeid(new CoreProcessingtype(type));
        processing.setRecordId(current);
        processing.setProcPartner(userFacade.getUserConnecte().getPartnerid());
        processingFacade.create(processing);
        return processing;
    }
    
    public List<RepPositionTarifaire> verifySupportedNsh(VTMINEPDEDRegistration current) {
        CoreProcess process = current.getRecordProcess();
        List<RepPositionTarifaire> unsupportedNsh = new java.util.ArrayList<RepPositionTarifaire>();
        for (CoreGood good : current.getGoodList()) {
            java.util.List<CoreProcess> list = good.gethSCode().getProcessList();
            if (!list.contains(process)) {
                unsupportedNsh.add(good.gethSCode());
            }
        }
        return unsupportedNsh.isEmpty() ? null : unsupportedNsh;
    }
	
	@Override
	public List<RepPositionTarifaire> searchNshByProcess(CoreProcess process) {
		Query query = em.createQuery("SELECT nsh FROM RepPositionTarifaire nsh WHERE nsh.active = :active and :process MEMBER OF nsh.processList");
		query.setParameter("active", true);
		query.setParameter("process", process);
		return query.getResultList();
	}

}
