package org.guce.process.vt2.repositories;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.guce.core.ejb.facade.interfaces.AbstractFacade;
import org.guce.process.vt2.entities.VT2RegistrationHistory;

@Stateless
@LocalBean
public class VT2RegistrationHistoryRepository extends AbstractFacade<VT2RegistrationHistory> {
    @PersistenceContext(
            unitName = "Partner-ejb-unity"
    )
    private EntityManager em;

    public VT2RegistrationHistoryRepository() {
        super(VT2RegistrationHistory.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
