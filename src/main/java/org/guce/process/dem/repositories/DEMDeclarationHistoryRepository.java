package org.guce.process.dem.repositories;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.guce.core.ejb.facade.interfaces.AbstractFacade;
import org.guce.process.dem.entities.DEMDeclarationHistory;

@Stateless
@LocalBean
public class DEMDeclarationHistoryRepository extends AbstractFacade<DEMDeclarationHistory> {
    @PersistenceContext(
            unitName = "Partner-ejb-unity"
    )
    private EntityManager em;

    public DEMDeclarationHistoryRepository() {
        super(DEMDeclarationHistory.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
