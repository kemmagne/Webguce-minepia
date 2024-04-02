package org.guce.process.atm.repositories;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.guce.core.ejb.facade.interfaces.AbstractFacade;
import org.guce.process.atm.entities.ATMRegistrationHistory;

@Stateless
@LocalBean
public class ATMRegistrationHistoryRepository extends AbstractFacade<ATMRegistrationHistory> {
    @PersistenceContext(
            unitName = "Partner-ejb-unity"
    )
    private EntityManager em;

    public ATMRegistrationHistoryRepository() {
        super(ATMRegistrationHistory.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
