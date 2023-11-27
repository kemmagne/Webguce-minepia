package org.guce.web.process.dem.services;

import java.util.List;
import javax.ejb.EJB;
import org.guce.core.ejb.facade.interfaces.CoreChargerFacadeLocal;
import org.guce.core.ejb.facade.interfaces.CoreStakeHolderFacadeLocal;
import org.guce.core.ejb.facade.interfaces.IdentifiantGeneratorLocal;
import org.guce.core.services.GuceCalendarUtil;
import org.guce.core.services.SearchFilter;
import org.guce.process.dem.entities.DEMDeclaration;
import org.guce.process.dem.entities.DEMDeclarationHistory;
import org.guce.process.dem.repositories.DEMDeclarationHistoryRepository;
import org.guce.process.dem.repositories.impl.DEMDeclarationRepositoryImpl;
import org.guce.web.process.dem.util.DEMUtils;

public class DEMDeclarationService implements ICrudService<DEMDeclaration> {
    @EJB
    protected DEMDeclarationRepositoryImpl dEMDeclarationRepository;

    @EJB
    protected DEMDeclarationHistoryRepository historyRepository;

    @EJB
    protected IdentifiantGeneratorLocal generator;

    @EJB
    protected CoreChargerFacadeLocal chargerFacade;

    @EJB
    protected CoreStakeHolderFacadeLocal stakeHolderFacade;

    @Override
    public int count() {
        return dEMDeclarationRepository.count();
    }

    @Override
    public List<DEMDeclaration> findAll() {
        return dEMDeclarationRepository.findAll();
    }

    @Override
    public List<DEMDeclaration> findAll(int[] range) {
        return dEMDeclarationRepository.findRange(range);
    }

    @Override
    public DEMDeclaration findBy(String primaryKey) {
        if(primaryKey == null) {
            return null;
        }
        return dEMDeclarationRepository.find(primaryKey);
    }

    @Override
    public DEMDeclaration save(DEMDeclaration entity) {
        String opType = "edit";
        DEMDeclarationHistory hist = new DEMDeclarationHistory();
        hist.setModifyDate(GuceCalendarUtil.getCalendar().getTime());
        hist.setModifyUser(entity.getWriteUser());
        hist.setAfterModification(DEMUtils.objectToXml(entity));
        if(entity.getId() == null) {
            dEMDeclarationRepository.create(entity);
            opType = "create";
        } else {
            hist.setBeforeModification(DEMUtils.objectToXml(dEMDeclarationRepository.find(entity.getId())));
            dEMDeclarationRepository.edit(entity);
        }
        hist.setType(opType);
        hist.setItemId(entity);
        historyRepository.create(hist);
        return entity;
    }

    @Override
    public void remove(DEMDeclaration entity) {
        entity.setDeleted(true);
        save(entity);
    }

    public List<DEMDeclaration> findByDomain(String domain) {
        return dEMDeclarationRepository.findByDomain(domain);
    }

    @Override
    public List<DEMDeclaration> search(SearchFilter filter, int start, int count, String orderField, String order) {
        return dEMDeclarationRepository.search(filter, start, count, orderField, order);
    }

    @Override
    public int searchCount(SearchFilter filter) {
        return dEMDeclarationRepository.searchCount(filter);
    }

    @Override
    public void remove(List<DEMDeclaration> entities) {
        for (DEMDeclaration item : entities) {
            remove(item);
        }
    }

    @Override
    public void save(List<DEMDeclaration> entities) {
        for (DEMDeclaration item : entities) {
            save(item);
        }
    }
}
