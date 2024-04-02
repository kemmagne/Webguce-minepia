package org.guce.web.process.atm.services;

import java.util.List;
import javax.ejb.EJB;
import org.guce.core.ejb.facade.interfaces.CoreChargerFacadeLocal;
import org.guce.core.ejb.facade.interfaces.CoreStakeHolderFacadeLocal;
import org.guce.core.ejb.facade.interfaces.IdentifiantGeneratorLocal;
import org.guce.core.entities.CoreProcess;
import org.guce.core.entities.CoreRecord;
import org.guce.core.services.GuceCalendarUtil;
import org.guce.core.services.SearchFilter;
import org.guce.process.atm.entities.ATMRegistration;
import org.guce.process.atm.repositories.impl.ATMRegistrationRepositoryImpl;
import org.guce.rep.entities.RepPositionTarifaire;

public class ATMRegistrationService implements ICrudService<ATMRegistration> {
    @EJB
    protected ATMRegistrationRepositoryImpl aTMRegistrationRepository;

    @EJB
    protected IdentifiantGeneratorLocal generator;

    @EJB
    protected CoreChargerFacadeLocal chargerFacade;

    @EJB
    protected CoreStakeHolderFacadeLocal stakeHolderFacade;

    @Override
    public int count() {
        return aTMRegistrationRepository.count();
    }

    @Override
    public List<ATMRegistration> findAll() {
        return aTMRegistrationRepository.findAll();
    }

    @Override
    public List<ATMRegistration> findAll(int[] range) {
        return aTMRegistrationRepository.findRange(range);
    }

    @Override
    public ATMRegistration findBy(String primaryKey) {
        if(primaryKey == null) {
            return null;
        }
        return aTMRegistrationRepository.find(primaryKey);
    }

    @Override
    public ATMRegistration save(ATMRegistration entity) {
        if(entity.getRecordId() == null) {
            entity.setRecordId(generator.getIdentifiant("ATM_SEQ", "ATM"));
            entity.setRecordCreatedate(GuceCalendarUtil.getCalendar().getTime());
            entity.setRecordState(CoreRecord.NO_START);
        }
        if(entity.getChargerid() != null && entity.getChargerid().getChargerid() == null) {
            chargerFacade.create(entity.getChargerid());
        }
        entity.setRecordLastCreateDate(GuceCalendarUtil.getCalendar().getTime());
        aTMRegistrationRepository.edit(entity);
        return entity;
    }

    @Override
    public void remove(ATMRegistration entity) {
        aTMRegistrationRepository.remove(entity);
    }

    public List<RepPositionTarifaire> searchNshByProcess(CoreProcess process) {
        return aTMRegistrationRepository.searchNshByProcess(process);
    }

    public List<ATMRegistration> findByDomain(String domain) {
        return aTMRegistrationRepository.findByDomain(domain);
    }

    @Override
    public List<ATMRegistration> search(SearchFilter filter, int start, int count, String orderField, String order) {
        return aTMRegistrationRepository.search(filter, start, count, orderField, order);
    }

    @Override
    public int searchCount(SearchFilter filter) {
        return aTMRegistrationRepository.searchCount(filter);
    }

    @Override
    public void remove(List<ATMRegistration> entities) {
        for (ATMRegistration item : entities) {
            remove(item);
        }
    }

    @Override
    public void save(List<ATMRegistration> entities) {
        for (ATMRegistration item : entities) {
            save(item);
        }
    }
}
