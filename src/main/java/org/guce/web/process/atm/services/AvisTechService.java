package org.guce.web.process.atm.services;

import java.util.List;
import javax.ejb.EJB;
import org.guce.core.ejb.facade.interfaces.CoreChargerFacadeLocal;
import org.guce.core.ejb.facade.interfaces.CoreStakeHolderFacadeLocal;
import org.guce.core.ejb.facade.interfaces.IdentifiantGeneratorLocal;
import org.guce.core.services.GuceCalendarUtil;
import org.guce.core.services.SearchFilter;
import org.guce.process.atm.entities.AvisTech;
import org.guce.process.atm.entities.AvisTechHistory;
import org.guce.process.atm.repositories.AvisTechHistoryRepository;
import org.guce.process.atm.repositories.impl.AvisTechRepositoryImpl;
import org.guce.web.process.atm.util.ATMUtils;

public class AvisTechService implements ICrudService<AvisTech> {
    @EJB
    protected AvisTechRepositoryImpl avisTechRepository;

    @EJB
    protected AvisTechHistoryRepository historyRepository;

    @EJB
    protected IdentifiantGeneratorLocal generator;

    @EJB
    protected CoreChargerFacadeLocal chargerFacade;

    @EJB
    protected CoreStakeHolderFacadeLocal stakeHolderFacade;

    @Override
    public int count() {
        return avisTechRepository.count();
    }

    @Override
    public List<AvisTech> findAll() {
        return avisTechRepository.findAll();
    }

    @Override
    public List<AvisTech> findAll(int[] range) {
        return avisTechRepository.findRange(range);
    }

    @Override
    public AvisTech findBy(String primaryKey) {
        if(primaryKey == null) {
            return null;
        }
        return avisTechRepository.find(primaryKey);
    }

    @Override
    public AvisTech save(AvisTech entity) {
        String opType = "edit";
        AvisTechHistory hist = new AvisTechHistory();
        hist.setModifyDate(GuceCalendarUtil.getCalendar().getTime());
        hist.setModifyUser(entity.getWriteUser());
        hist.setAfterModification(ATMUtils.objectToXml(entity));
        if(entity.getId() == null) {
            avisTechRepository.create(entity);
            opType = "create";
        } else {
            hist.setBeforeModification(ATMUtils.objectToXml(avisTechRepository.find(entity.getId())));
            avisTechRepository.edit(entity);
        }
        hist.setType(opType);
        hist.setItemId(entity);
        historyRepository.create(hist);
        return entity;
    }

    @Override
    public void remove(AvisTech entity) {
        entity.setDeleted(true);
        save(entity);
    }

    public List<AvisTech> findByDomain(String domain) {
        return avisTechRepository.findByDomain(domain);
    }

    @Override
    public List<AvisTech> search(SearchFilter filter, int start, int count, String orderField, String order) {
        return avisTechRepository.search(filter, start, count, orderField, order);
    }

    @Override
    public int searchCount(SearchFilter filter) {
        return avisTechRepository.searchCount(filter);
    }

    @Override
    public void remove(List<AvisTech> entities) {
        for (AvisTech item : entities) {
            remove(item);
        }
    }

    @Override
    public void save(List<AvisTech> entities) {
        for (AvisTech item : entities) {
            save(item);
        }
    }
}
