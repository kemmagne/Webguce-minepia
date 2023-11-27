package org.guce.process.dem.repositories.impl;

import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import org.guce.core.entities.CoreCharger;
import org.guce.process.dem.entities.DEMRegistration;
import org.guce.process.dem.repositories.DEMRegistrationRepository;

@Stateless
@LocalBean
public class DEMRegistrationRepositoryImpl extends DEMRegistrationRepository {;
    public CoreCharger findCoreChargerByCarteContribNumber(String numero) {
        String qb = "Select distinct c from CoreCharger c where c.carte.numero='"+numero+"' " ;
        System.out.println(qb);
        TypedQuery<CoreCharger> query = getEntityManager().createQuery(qb, CoreCharger.class);
        return query.getSingleResult();
    }
}
