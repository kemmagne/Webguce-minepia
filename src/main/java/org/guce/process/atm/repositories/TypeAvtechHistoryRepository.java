package org.guce.process.atm.repositories;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.guce.core.ejb.facade.interfaces.AbstractFacade;
import org.guce.process.atm.entities.TypeAvtechHistory;

@Stateless
@LocalBean
public class TypeAvtechHistoryRepository extends AbstractFacade<TypeAvtechHistory> {
    @PersistenceContext(
            unitName = "Partner-ejb-unity"
    )
    private EntityManager em;

    public TypeAvtechHistoryRepository() {
        super(TypeAvtechHistory.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
