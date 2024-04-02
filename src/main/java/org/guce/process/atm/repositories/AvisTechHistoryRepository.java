package org.guce.process.atm.repositories;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.guce.core.ejb.facade.interfaces.AbstractFacade;
import org.guce.process.atm.entities.AvisTechHistory;

@Stateless
@LocalBean
public class AvisTechHistoryRepository extends AbstractFacade<AvisTechHistory> {
    @PersistenceContext(
            unitName = "Partner-ejb-unity"
    )
    private EntityManager em;

    public AvisTechHistoryRepository() {
        super(AvisTechHistory.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
