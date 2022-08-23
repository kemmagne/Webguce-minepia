package org.guce.web.process.vt2.services;

import java.util.List;
import javax.ejb.EJB;
import org.guce.core.ejb.facade.interfaces.CoreChargerFacadeLocal;
import org.guce.core.ejb.facade.interfaces.CoreStakeHolderFacadeLocal;
import org.guce.core.ejb.facade.interfaces.IdentifiantGeneratorLocal;
import org.guce.core.entities.CoreProcess;
import org.guce.core.entities.CoreRecord;
import org.guce.core.services.GuceCalendarUtil;
import org.guce.core.services.SearchFilter;
import org.guce.process.vt2.entities.VT2Registration;
import org.guce.process.vt2.repositories.impl.VT2RegistrationRepositoryImpl;
import org.guce.rep.entities.RepPositionTarifaire;

public class VT2RegistrationService implements ICrudService<VT2Registration> {
    @EJB
    protected VT2RegistrationRepositoryImpl vT2RegistrationRepository;

    @EJB
    protected IdentifiantGeneratorLocal generator;

    @EJB
    protected CoreChargerFacadeLocal chargerFacade;

    @EJB
    protected CoreStakeHolderFacadeLocal stakeHolderFacade;

    @Override
    public int count() {
        return vT2RegistrationRepository.count();
    }

    @Override
    public List<VT2Registration> findAll() {
        return vT2RegistrationRepository.findAll();
    }

    @Override
    public List<VT2Registration> findAll(int[] range) {
        return vT2RegistrationRepository.findRange(range);
    }

    @Override
    public VT2Registration findBy(String primaryKey) {
        if(primaryKey == null) {
            return null;
        }
        return vT2RegistrationRepository.find(primaryKey);
    }

    @Override
    public VT2Registration save(VT2Registration entity) {
        if(entity.getRecordId() == null) {
            entity.setRecordId(generator.getIdentifiant("VT2_SEQ", "VT2"));
            entity.setRecordCreatedate(GuceCalendarUtil.getCalendar().getTime());
            entity.setRecordState(CoreRecord.NO_START);
        }
        if(entity.getChargerid() != null && entity.getChargerid().getChargerid() == null) {
            chargerFacade.create(entity.getChargerid());
        }
        entity.setRecordLastCreateDate(GuceCalendarUtil.getCalendar().getTime());
        vT2RegistrationRepository.edit(entity);
        return entity;
    }

    @Override
    public void remove(VT2Registration entity) {
        vT2RegistrationRepository.remove(entity);
    }

    public List<RepPositionTarifaire> searchNshByProcess(CoreProcess process) {
        return vT2RegistrationRepository.searchNshByProcess(process);
    }

    @Override
    public List<VT2Registration> search(SearchFilter filter, int start, int count, String orderField, String order) {
        return vT2RegistrationRepository.search(filter, start, count, orderField, order);
    }

    @Override
    public int searchCount(SearchFilter filter) {
        return vT2RegistrationRepository.searchCount(filter);
    }

    @Override
    public void remove(List<VT2Registration> entities) {
        for (VT2Registration item : entities) {
            remove(item);
        }
    }

    @Override
    public void save(List<VT2Registration> entities) {
        for (VT2Registration item : entities) {
            save(item);
        }
    }
}
