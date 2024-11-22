/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guce.web.process.atm.services;

import java.util.List;
import javax.ejb.EJB;
import org.guce.process.atm.ATMConstant;
import org.guce.process.atm.entities.AvisTech;
import org.guce.process.atm.repositories.impl.RepPositionGoodAtmImpl;
import org.guce.rep.entities.RepPositionTarifaire;
import org.guce.web.process.atm.controllers.CodeTarifaireMapper;
/**
 *
 * @author NGC
 */
public class PositionTarifaireGoodAtmService {
    
   
    
    @EJB
    protected RepPositionGoodAtmImpl repPositionGoodAtmImpl;
    
   // private List<RepPositionTarifaire> material;
    
    public List<AvisTech> findActiveProduitsHalieutiques(String code) {
      return repPositionGoodAtmImpl.findActiveProduitsHalieutiques(code);
    }
    
     public List<AvisTech> findActiveIngredientsAdditifs(String code) {
      return repPositionGoodAtmImpl.findActiveProduitsHalieutiques(code);
    }
     
    public List<AvisTech> findActiveMaterialEquipment(String code) {
      return repPositionGoodAtmImpl.findActiveMaterialEquipment(code);
    }
    
     public List<RepPositionTarifaire> findActiveProduitsHalieutiques() {
        List<RepPositionTarifaire> halieutiques = CodeTarifaireMapper.handleTypeAvisTechnique(findActiveMaterialEquipment(ATMConstant.TRAITEMENT_STOKAGE));
        return halieutiques;
    }
    
    
     public List<RepPositionTarifaire> findActiveIngredientsAdditifs() {
        List<RepPositionTarifaire> ingredients = CodeTarifaireMapper.handleTypeAvisTechnique(findActiveMaterialEquipment(ATMConstant.INGREDIENT_ADDITIFS));
        return ingredients;
    }
    

    public List<RepPositionTarifaire> getMateriealEtEquipement() {
        List<RepPositionTarifaire> material = CodeTarifaireMapper.handleTypeAvisTechnique(findActiveMaterialEquipment(ATMConstant.MATERIEL_EQUIPEMENT));
        return material;
    }
}
