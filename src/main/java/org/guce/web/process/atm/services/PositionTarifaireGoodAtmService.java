/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guce.web.process.atm.services;

import java.util.List;
import javax.ejb.EJB;
import org.guce.process.atm.repositories.impl.RepPositionGoodAtmImpl;
import org.guce.rep.entities.RepPositionTarifaire;
/**
 *
 * @author NGC
 */
public class PositionTarifaireGoodAtmService {
    
    @EJB
    protected RepPositionGoodAtmImpl repPositionGoodAtmImpl;
    
    public List<RepPositionTarifaire> findActiveProduitsHalieutiques() {
      return repPositionGoodAtmImpl.findActiveProduitsHalieutiques();
    }
    
     public List<RepPositionTarifaire> findActiveIngredientsAdditifs() {
      return repPositionGoodAtmImpl.findActiveProduitsHalieutiques();
    }
     
    public List<RepPositionTarifaire> findActiveMaterialEquipment() {
      return repPositionGoodAtmImpl.findActiveMaterialEquipment();
    }
}
