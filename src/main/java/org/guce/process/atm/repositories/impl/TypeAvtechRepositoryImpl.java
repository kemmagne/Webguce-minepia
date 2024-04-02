package org.guce.process.atm.repositories.impl;

import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import org.guce.process.atm.entities.TypeAvtech;
import org.guce.process.atm.repositories.TypeAvtechRepository;

@Stateless
@LocalBean
public class TypeAvtechRepositoryImpl extends TypeAvtechRepository {
    
      public List<TypeAvtech> findTypeAvtechOfATM(String key1, String key2, String Key3){
         String qb = "Select distinct t from TypeAvtech t where t.deleted=false and t.active = true  and (t.code='"+key1+"' or t.code='"+key2+"' or t.code='"+Key3+"') ";
        try{
            TypedQuery<TypeAvtech> query = getEntityManager().createQuery(qb, TypeAvtech.class);
        return query.getResultList();
        }catch(NoResultException e){
          return null;
        }   
    }
    
    public List<TypeAvtech> findTypeAvtechByCode(String code){
         String qb = "Select distinct t from TypeAvtech t where t.deleted=false and t.active = true  and t.code='"+code+"'";
        try{
            TypedQuery<TypeAvtech> query = getEntityManager().createQuery(qb, TypeAvtech.class);
        return query.getResultList();
        }catch(NoResultException e){
          return null;
        }   
    } 
}
