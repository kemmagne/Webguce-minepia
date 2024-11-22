/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guce.process.atm.repositories.impl;

import org.guce.core.ejb.facade.impl.CoreProcessingFacade;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;
import org.guce.core.ejb.facade.impl.CoreProcessingFacade;
/**
 *
 * @author NGC
 */

@Stateless
@LocalBean
public class CoreProcessingFacadeATM extends CoreProcessingFacade implements CoreProcessingFacadeATMLocal{
    
    @Override
    public Number countCoreProcessingStartWithRecordIdAndProcessingType(String recordID, String type) {
        String sqlStrQuery = "Select COUNT(p) from CoreProcessing p where p.recordId.recordId like :recordID and p.processingtypeid.processingtypeid=:type";
        Query query = getEntityManager().createQuery(sqlStrQuery);
        query.setParameter("type", type);
        query.setParameter("recordID", recordID + "%");
        return (Number) query.getSingleResult();
    }
    
}
