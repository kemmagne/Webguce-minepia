package org.guce.process.vt2.services;

import javax.ejb.Stateless;
import org.guce.core.documents.WebguceDocument;
import org.guce.core.entities.CoreGood;
import org.guce.core.entities.CoreMessage;
import org.guce.process.ct.entities.CTGood;
import org.guce.process.ct.entities.VTMINEPDEDRegistration;

/**
 *
 * @author Koufana Crepin Sosthene
 */
@Stateless
public class Vt2DocumentService implements Vt2DocumentServiceLocal {

    @Override
    public WebguceDocument createVt2InitMessage(VTMINEPDEDRegistration registration, CoreMessage message) {

        registration.getChargerid().getAddress().setAddress3(null);
        registration.getChargerid().getAddress().setAddress4(null);
        registration.getChargerid().getAddress().setAddress5(null);

        WebguceDocument doc = new WebguceDocument();
        if (message.getMessageType() != null) {
            doc.getMessage().setTypeMessage("SANCRT");//UNCEFACT
            doc.setTypeDocument(message.getMessageType().getCode());
            doc.getReference().setService("vt2");
            doc.getReference().setNumeroDossier(registration.getRecordId());
            doc.getReference().setReferenceGuce(registration.getReocordConversationid());
            doc.getReference().setNumeroCircuit(registration.getRecordId());
        }
        if (message.getMessageProcessing() != null) {
            if (message.getMessageSender() != null) {
                doc.getRoutage().setEmetteur(message.getMessageSender().getPartnerid());
            }
            if (message.getMessageReceiver() != null) {
                doc.getRoutage().setRecepteur(message.getMessageReceiver().getPartnerid());
            }
        }
        if(registration.getDecision()!=null){
            registration.getDecision().setObservation(registration.getObservation());
        }
        
        doc.setContenu(registration);
        return doc;
    }
}
