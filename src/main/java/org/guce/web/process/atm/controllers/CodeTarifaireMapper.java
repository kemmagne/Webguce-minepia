/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guce.web.process.atm.controllers;

import java.util.ArrayList;
import java.util.List;
import org.guce.process.atm.entities.AvisTech;
import org.guce.rep.entities.RepPositionTarifaire;

/**
 *
 * @author NGC
 */
public class CodeTarifaireMapper {
    
    public static List<RepPositionTarifaire> handleTypeAvisTechnique(List<AvisTech> avisTech){
        List<RepPositionTarifaire> repPositionTarifaire = new ArrayList<>();
    
        if(avisTech != null){
          
          for(AvisTech dec: avisTech){
              repPositionTarifaire.add(dec.getProduit());
          }
    }
        return repPositionTarifaire;
}

}