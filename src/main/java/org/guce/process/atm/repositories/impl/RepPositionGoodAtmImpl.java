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
import javax.persistence.Query;
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
    public List<RepPositionTarifaire> findActiveProduitsHalieutiques() {
       List<String> list = Arrays.asList(
            "03029000000","03043100000","03043200000","03043300000",
            "03043900000","03044100000","03044200000","03044300000",
            "03044400000","03044500000","03044500000","03044600000",
            "03044600000","03038200000","03038300000","03038400000","03038900000"
            ,"03039000000","03046100000","03046200000","03046200000"
            ,"03072900000","03073100000","03073900000","03074100000"
            ,"03074900000","03075100000","03075900000","03077100000"
            ,"03077900000","03078900000","03079100000","03079900000"

            ,"03081100000","03081900000","03082100000","03082900000"
            ,"03011900000","03019100000","03019200000","03019300000"
            ,"03019400000","03019500000","03019900000","16030000000"
        );
        
      q = getEntityManager().createQuery("Select p from RepPositionTarifaire p where p.active = :active and p.code in :list");  
      q.setParameter("active", true);
      q.setParameter("list",list);
      return q.getResultList();   
    }

    @Override
    public List<RepPositionTarifaire> findActiveIngredientsAdditifs() {
        Query q;
        q = getEntityManager().createQuery("Select p from RepPositionTarifaire p where p.active = :active and p.code in ('23080000000','23099010000','23099090100','23099090900', '23011000000','11052000000','05119100000','23091000000','05080000000','12091000000','12092100000','12092200000','12092900000')");
        q.setParameter("active", true);
        return q.getResultList(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<RepPositionTarifaire> findActiveMaterialEquipment() {
         Query q;
        q = getEntityManager().createQuery("Select p from RepPositionTarifaire p where p.active = :active and p.code in ('84361000000','84362100000','84362900000','84369100000', '84334000000','84335100000','84335200000','84332000000','84362900000','84368000000','84369100000', '84371010000','84369900000','84371090000')");
        q.setParameter("active", true);
        return q.getResultList(); //To change body of generated methods, choose Tools | Templates.
    }
    
 
}
