/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guce.process.vt2.services;

import java.util.ArrayList;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.guce.core.ejb.facade.interfaces.CoreChargerFacadeLocal;
import org.guce.core.ejb.facade.interfaces.CoreProcessingFacadeLocal;
import org.guce.core.ejb.facade.interfaces.CoreSignatoryFacadeLocal;
import org.guce.core.ejb.facade.interfaces.CoreStakeHolderFacadeLocal;
import org.guce.core.ejb.facade.interfaces.IdentifiantGeneratorLocal;
import org.guce.core.entities.CoreProcessing;
import org.guce.core.entities.CoreProcessingtype;
import org.guce.core.entities.CoreRecord;
import org.guce.core.services.GuceCalendarUtil;
import org.guce.core.services.UserSessionBeanLocal;
import org.guce.process.ct.ejb.interfaces.CTGoodFacadeLocal;
import org.guce.process.ct.ejb.interfaces.VTMINEPDEDRegistrationFacadeLocal;
import org.guce.process.ct.entities.CTGood;
import org.guce.process.ct.entities.VTMINEPDEDRegistration;

/**
 *
 * @author Koufana Crepin Sosthene
 * @author Steve Anyam
 */
@Stateless
public class Vt2Service implements Vt2ServiceLocal {

    @EJB
    private IdentifiantGeneratorLocal generator;
    @EJB
    private CoreChargerFacadeLocal chargerFacade;
    @EJB
    private CoreStakeHolderFacadeLocal stakeHolderFacade;
    @EJB
    private CTGoodFacadeLocal aieGoodFacade;
    @EJB
    private CoreSignatoryFacadeLocal signatoryFacade;
    @EJB
    private VTMINEPDEDRegistrationFacadeLocal facade;
    @EJB
    private UserSessionBeanLocal userFacade;
    @EJB
    private CoreProcessingFacadeLocal processingFacade;

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
            /*
            if (current.getAieGoods().size() > 0) {
                for (int i = 0; i < current.getAieGoods().size(); i++) {
                    try {
                        current.getAieGoods().get(i).setRegistration(current);
                        if (current.getAieGoods().get(i).getsFCode().getId() == null) {
                            current.getAieGoods().get(i).setsFCode(null);
                        }
                    } catch (Exception ex) {

                    }
                }
            }*/
            current.setRecordCreatedate(GuceCalendarUtil.getCalendar().getTime());
            facade.create(current);
            return 0;
        } else {

            chargerFacade.edit(current.getChargerid());
            /*stakeHolderFacade.edit(current.getSponsor());
            stakeHolderFacade.edit(current.getFormulator());
            stakeHolderFacade.edit(current.getLocalRepresentative());
            stakeHolderFacade.edit(current.getMaterialManufacturer());
            signatoryFacade.edit(current.getSignatory());            */
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

}
