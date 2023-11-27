package org.guce.web.process.dem.services;

import java.util.List;
import javax.ejb.EJB;
import org.guce.core.ejb.facade.interfaces.CoreChargerFacadeLocal;
import org.guce.core.ejb.facade.interfaces.CoreStakeHolderFacadeLocal;
import org.guce.core.ejb.facade.interfaces.IdentifiantGeneratorLocal;
import org.guce.core.entities.CoreProcess;
import org.guce.core.entities.CoreRecord;
import org.guce.core.services.GuceCalendarUtil;
import org.guce.core.services.SearchFilter;
import org.guce.process.dem.entities.DEMRegistration;
import org.guce.process.dem.repositories.impl.DEMRegistrationRepositoryImpl;
import org.guce.rep.entities.RepPositionTarifaire;

public class DEMRegistrationService implements ICrudService<DEMRegistration> {
    @EJB
    protected DEMRegistrationRepositoryImpl dEMRegistrationRepository;

    @EJB
    protected IdentifiantGeneratorLocal generator;

    @EJB
    protected CoreChargerFacadeLocal chargerFacade;

    @EJB
    protected CoreStakeHolderFacadeLocal stakeHolderFacade;

    @Override
    public int count() {
        return dEMRegistrationRepository.count();
    }

    @Override
    public List<DEMRegistration> findAll() {
        return dEMRegistrationRepository.findAll();
    }

    @Override
    public List<DEMRegistration> findAll(int[] range) {
        return dEMRegistrationRepository.findRange(range);
    }

    @Override
    public DEMRegistration findBy(String primaryKey) {
        if(primaryKey == null) {
            return null;
        }
        return dEMRegistrationRepository.find(primaryKey);
    }

    @Override
    public DEMRegistration save(DEMRegistration entity) {
        if(entity.getRecordId() == null) {
            entity.setRecordId(generator.getIdentifiant("DEM_SEQ", "DEM"));
            entity.setRecordCreatedate(GuceCalendarUtil.getCalendar().getTime());
            entity.setRecordState(CoreRecord.NO_START);
        }
        if(entity.getChargerid() != null && entity.getChargerid().getChargerid() == null) {
            
            chargerFacade.create(entity.getChargerid());
        }
        entity.setRecordLastCreateDate(GuceCalendarUtil.getCalendar().getTime());
        dEMRegistrationRepository.edit(entity);
        return entity;
    }

    @Override
    public void remove(DEMRegistration entity) {
        dEMRegistrationRepository.remove(entity);
    }

    public List<RepPositionTarifaire> searchNshByProcess(CoreProcess process) {
        return dEMRegistrationRepository.searchNshByProcess(process);
    }

    public List<DEMRegistration> findByDomain(String domain) {
        return dEMRegistrationRepository.findByDomain(domain);
    }

    @Override
    public List<DEMRegistration> search(SearchFilter filter, int start, int count, String orderField, String order) {
        return dEMRegistrationRepository.search(filter, start, count, orderField, order);
    }

    @Override
    public int searchCount(SearchFilter filter) {
        return dEMRegistrationRepository.searchCount(filter);
    }

    @Override
    public void remove(List<DEMRegistration> entities) {
        for (DEMRegistration item : entities) {
            remove(item);
        }
    }

    @Override
    public void save(List<DEMRegistration> entities) {
        for (DEMRegistration item : entities) {
            save(item);
        }
    }
}
