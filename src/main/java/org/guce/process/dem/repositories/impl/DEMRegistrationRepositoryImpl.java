package org.guce.process.dem.repositories.impl;

import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import org.guce.process.dem.entities.DEMRegistration;
import org.guce.process.dem.repositories.DEMRegistrationRepository;

@Stateless
@LocalBean
public class DEMRegistrationRepositoryImpl extends DEMRegistrationRepository {
    
    public DEMRegistration findDemRegistrationByRecordId(String id){
        
         String qb = "Select distinct t from DEMRegistration t where t.recordId='"+id+"'";
 
        try{
            TypedQuery<DEMRegistration> query = getEntityManager().createQuery(qb, DEMRegistration.class);
        return query.getSingleResult();
        }catch(NoResultException e){
          return null;
        }   
    }
}
