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
            "0301.10.00","0301.91.00","0301.92.00","0301.93.00","0301.94.00","0301.95.00","0302.11.00",
            "0302.12.00","0302.19.00","0302.21.00","0302.22.00","0302.23.00","0302.29.00","0302.22.00",
            "0302.23.00","0302.31.00","0302.32.00","0302.33.00","0302.34.00","0302.35.00","0302.36.00"
        );
        
      q = getEntityManager().createQuery("Select p from RepPositionTarifaire p where p.active = :active and p.code in :list");  
      q.setParameter("active", true);
      q.setParameter("list",list);
      return q.getResultList();   
    }

    @Override
    public List<RepPositionTarifaire> findActiveIngredientsAdditifs() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<RepPositionTarifaire> findActiveMaterialEquipment() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
 
}
