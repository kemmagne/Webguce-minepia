package org.guce.web.process.atm.services;

import java.util.List;
import javax.ejb.EJB;
import org.guce.core.ejb.facade.interfaces.CoreChargerFacadeLocal;
import org.guce.core.ejb.facade.interfaces.CoreStakeHolderFacadeLocal;
import org.guce.core.ejb.facade.interfaces.IdentifiantGeneratorLocal;
import org.guce.core.services.GuceCalendarUtil;
import org.guce.core.services.SearchFilter;
import org.guce.process.atm.entities.TypeAvtech;
import org.guce.process.atm.entities.TypeAvtechHistory;
import org.guce.process.atm.repositories.TypeAvtechHistoryRepository;
import org.guce.process.atm.repositories.impl.TypeAvtechRepositoryImpl;
import org.guce.web.process.atm.util.ATMUtils;

public class TypeAvtechService implements ICrudService<TypeAvtech> {
    @EJB
    protected TypeAvtechRepositoryImpl typeAvtechRepository;

    @EJB
    protected TypeAvtechHistoryRepository historyRepository;

    @EJB
    protected IdentifiantGeneratorLocal generator;

    @EJB
    protected CoreChargerFacadeLocal chargerFacade;

    @EJB
    protected CoreStakeHolderFacadeLocal stakeHolderFacade;

    @Override
    public int count() {
        return typeAvtechRepository.count();
    }

    @Override
    public List<TypeAvtech> findAll() {
        return typeAvtechRepository.findAll();
    }

    @Override
    public List<TypeAvtech> findAll(int[] range) {
        return typeAvtechRepository.findRange(range);
    }

    @Override
    public TypeAvtech findBy(String primaryKey) {
        if(primaryKey == null) {
            return null;
        }
        return typeAvtechRepository.find(primaryKey);
    }

    @Override
    public TypeAvtech save(TypeAvtech entity) {
        String opType = "edit";
        TypeAvtechHistory hist = new TypeAvtechHistory();
        hist.setModifyDate(GuceCalendarUtil.getCalendar().getTime());
        hist.setModifyUser(entity.getWriteUser());
        hist.setAfterModification(ATMUtils.objectToXml(entity));
        if(entity.getId() == null) {
            typeAvtechRepository.create(entity);
            opType = "create";
        } else {
            hist.setBeforeModification(ATMUtils.objectToXml(typeAvtechRepository.find(entity.getId())));
            typeAvtechRepository.edit(entity);
        }
        hist.setType(opType);
        hist.setItemId(entity);
        historyRepository.create(hist);
        return entity;
    }

    @Override
    public void remove(TypeAvtech entity) {
        entity.setDeleted(true);
        save(entity);
    }

    public List<TypeAvtech> findByDomain(String domain) {
        return typeAvtechRepository.findByDomain(domain);
    }

    @Override
    public List<TypeAvtech> search(SearchFilter filter, int start, int count, String orderField, String order) {
        return typeAvtechRepository.search(filter, start, count, orderField, order);
    }

    @Override
    public int searchCount(SearchFilter filter) {
        return typeAvtechRepository.searchCount(filter);
    }

    @Override
    public void remove(List<TypeAvtech> entities) {
        for (TypeAvtech item : entities) {
            remove(item);
        }
    }

    @Override
    public void save(List<TypeAvtech> entities) {
        for (TypeAvtech item : entities) {
            save(item);
        }
    }
}
