/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guce.process.atm.repositories.impl;

import java.util.Arrays;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.guce.process.atm.entities.AvisTech;
import org.guce.rep.ejb.facade.impl.RepPositionTarifaireFacade;
import org.guce.rep.entities.RepPositionTarifaire;
import org.guce.process.atm.repositories.RepPositionGoodAtmRepository;
/**
 *
 * @author NGC
 */
@Stateless
@LocalBean
public class RepPositionGoodAtmImpl extends RepPositionTarifaireFacade implements RepPositionGoodAtmRepository{

    Query q;
    
      @Override
      public List<AvisTech> findActiveProduitsHalieutiques(String code){
         String qb = "Select distinct t from AvisTech t where t.active = true and t.typeProduct.code='"+code+"'";
        try{
         TypedQuery<AvisTech> query = getEntityManager().createQuery(qb, AvisTech.class);
        return query.getResultList();
        }catch(NoResultException e){
          return null;
        }   
    } 
    
    @Override
      public List<AvisTech> findActiveIngredientsAdditifs(String code){
         String qb = "Select distinct t from AvisTech t where t.active = true and t.typeProduct.code='"+code+"'";
        try{
         TypedQuery<AvisTech> query = getEntityManager().createQuery(qb, AvisTech.class);
        return query.getResultList();
        }catch(NoResultException e){
          return null;
        }   
    } 
    
     @Override
      public List<AvisTech> findActiveMaterialEquipment(String code){
         String qb = "Select distinct t from AvisTech t where t.active = true and t.typeProduct.code='"+code+"'";
        try{
         TypedQuery<AvisTech> query = getEntityManager().createQuery(qb, AvisTech.class);
        return query.getResultList();
        }catch(NoResultException e){
          return null;
        }   
    } 
 
}
