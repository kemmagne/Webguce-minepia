package org.guce.process.dem.repositories;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.guce.core.ejb.facade.interfaces.AbstractFacade;
import org.guce.process.dem.entities.DEMRegistrationHistory;

@Stateless
@LocalBean
public class DEMRegistrationHistoryRepository extends AbstractFacade<DEMRegistrationHistory> {
    @PersistenceContext(
            unitName = "Partner-ejb-unity"
    )
    private EntityManager em;

    public DEMRegistrationHistoryRepository() {
        super(DEMRegistrationHistory.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
