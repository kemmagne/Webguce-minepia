/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guce.process.atm;

import java.util.ArrayList;
import java.util.List;
import org.guce.core.entities.CoreAttachment;
import org.guce.core.entities.CoreRecord;
import org.guce.core.services.GuceCalendarUtil;

/**
 *
 * @author NGC
 */
public class BeanMapper {
    
    public static List<CoreAttachment> changeCoreRecordAttachment(List<CoreAttachment> list,CoreRecord coreRecord){
         List<CoreAttachment> attachments = new ArrayList<>();
        for (CoreAttachment att : list) {
               CoreAttachment coreAttachment = new CoreAttachment();
                coreAttachment.setPjChecksum(att.getPjChecksum());
                coreAttachment.setPjCreatedate(GuceCalendarUtil.getCalendar().getTime());
                coreAttachment.setPjFichier(att.getPjFichier());
                coreAttachment.setPjIdentifiant(att.getPjIdentifiant());
                coreAttachment.setPjLibelle(att.getPjLibelle());
                coreAttachment.setPjReferenceFichierJoint(att.getPjReferenceFichierJoint());
                coreAttachment.setPjType(att.getPjType());
                coreAttachment.setPjType(att.getPjType()); 
                coreAttachment.setPjDocument(att.getPjDocument());        
                coreAttachment.setPjRecord(coreRecord);
                attachments.add(coreAttachment);        
        }
        return  attachments;
    }
    
}
