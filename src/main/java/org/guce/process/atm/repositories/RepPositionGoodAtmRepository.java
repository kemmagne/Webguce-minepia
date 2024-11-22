/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guce.process.atm.repositories;

import java.util.List;
import org.guce.process.atm.entities.AvisTech;
import org.guce.rep.entities.RepPositionTarifaire;

/**
 *
 * @author NGC
 */
public interface RepPositionGoodAtmRepository {
    public List<AvisTech> findActiveProduitsHalieutiques(String code);
    public List<AvisTech> findActiveIngredientsAdditifs(String code);
    public List<AvisTech> findActiveMaterialEquipment(String code);
}
