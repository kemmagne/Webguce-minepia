/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guce.web.process.vt2.util;

import hk.hku.cecid.ebms.pkg.MessageHeader;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPMessage;
import org.apache.commons.io.IOUtils;
import org.guce.core.documents.WebguceDocument;
import org.guce.core.ejb.facade.interfaces.CoreMessageFacadeLocal;
import org.guce.core.ejb.facade.interfaces.CoreProcessingFacadeLocal;
import org.guce.core.ejb.facade.interfaces.CoreRecordFacadeLocal;
import org.guce.core.entities.CoreAttachment;
import org.guce.core.entities.CoreAttachmenttype;
import org.guce.core.entities.CoreForm;
import org.guce.core.entities.CoreMessage;
import org.guce.core.entities.CoreMessageType;
import org.guce.core.entities.CorePartner;
import org.guce.core.entities.CoreProcessing;
import org.guce.core.entities.CoreProcessingtype;
import org.guce.core.entities.CoreRecord;
import org.guce.core.entities.CoreUser;
import org.guce.core.entities.util.CoreProcessingState;
import static org.guce.core.entities.util.CoreProcessingState.ATTENTE;
import static org.guce.core.entities.util.CoreProcessingState.TRAITER;
import org.guce.core.services.GuceCalendarUtil;
import org.guce.orchestra.core.OrchestraEbxmlMessage;
import org.guce.process.vt2.constants.Vt2Constant;
import org.guce.process.vt2.services.Vt2ServiceLocal;
import org.guce.process.ct.entities.CTGood;
import org.guce.process.ct.entities.VTMINEPDEDRegistration;
import org.guce.web.core.ebxml.EbxmlCreator;
import org.guce.web.core.mail.ServiceMailSenderLocal;
import org.guce.web.core.mail.util.AperakCreator;
import org.guce.web.core.transformation.DefaultTraitement;
import org.guce.web.core.transformation.ITraitement;
import org.guce.web.core.transformation.WebguceTransformer;

/**
 *
 * @author Ulrich ETEME
 */
@Stateless
public class Vt2Traitement extends DefaultTraitement implements ITraitement {

    private WebguceDocument<VTMINEPDEDRegistration> document;
    private VTMINEPDEDRegistration fiche;
    @EJB
    private Vt2ServiceLocal service;
    @EJB
    private CoreProcessingFacadeLocal processingFacade;
    private OrchestraEbxmlMessage ebxml;
    @EJB
    private CoreRecordFacadeLocal recordFacade;
    @EJB
    private CoreMessageFacadeLocal messageFacade;
    @EJB
    private ServiceMailSenderLocal serviceMail;

    private EbxmlCreator ebxmlCreator;
    private CoreMessage message;
    
    public static final String PRINCIPAL_ATTACHMENT_TYPE = "VT2";

    @Override
    public Integer processWait(CoreForm f, CoreUser user) {
        String pr = f.getFormid().trim();
        if (pr.equals(Vt2Constant.FORM_INITIATION)) {
            return ficheFacade.getRecordByProcessingType(Vt2Constant.PROCESSING_REJET, CoreProcessingState.ATTENTE, user.getPartnerid().getPartnerid()).intValue()
                    + ficheFacade.getRecordByProcessingType(Vt2Constant.PROCESSING_COMPLEMENT_INFORMATION, CoreProcessingState.ATTENTE, user.getPartnerid().getPartnerid()).intValue();
        }
        return ficheFacade.getRecordByProcessingType(pr, CoreProcessingState.ATTENTE, user.getPartnerid().getPartnerid()).intValue();
    }

    @Override
    public void action(OrchestraEbxmlMessage object) throws Exception {
        ebxml = (OrchestraEbxmlMessage) object;
        JAXBContext context;
        if (ebxml.getAction().startsWith(CoreMessage.APERAK)) {
            message = ebxml.getAcknowledgment().getRefToMessageId() == null ? null : messageFacade.find(ebxml.getAcknowledgment().getRefToMessageId());
            if (message == null) {
                message = ebxml.getRefToMessageId() == null ? null : messageFacade.find(ebxml.getRefToMessageId());
            }
            if (message != null) {
                gererAperak();
            }
        } else {
            SOAPMessage soapMessage = ebxml.getSOAPMessage();
            Iterator iterator = soapMessage.getAttachments();
            InputStream dd = ((AttachmentPart) iterator.next()).getDataHandler().getInputStream();
            context = JAXBContext.newInstance(WebguceDocument.class, VTMINEPDEDRegistration.class, CTGood.class);
            Unmarshaller m = context.createUnmarshaller();
            document = (WebguceDocument) m.unmarshal(dd);
            if (document == null || document.getReference() == null || document.getReference().getNumeroDossier() == null) {
                Logger.getLogger(WebguceTransformer.class.getName()).log(Level.INFO, "Reference manquante  {0}", new Object[]{ebxml.getMessageId()});
                return;
            }
            fiche = service.findByRecordId(document.getReference().getNumeroDossier());

            if (fiche == null) {
                Logger.getLogger(WebguceTransformer.class.getName()).log(Level.INFO, "Numero fiche non trouve   {0}", new Object[]{document.getReference().getNumeroDossier()});
                return;
            }
            if (document.getContenu().getDecision() != null) {
                fiche.setDecision(document.getContenu().getDecision());
                fiche.setObservation(document.getContenu().getDecision().getObservation());
            }
            if (document.getContenu().getSignatory() != null) {
                fiche.setSignatory(document.getContenu().getSignatory());
            }
            if (document.getContenu().getCtDecision() != null) {
                fiche.setCtDecision(document.getContenu().getCtDecision());
            }
            fiche.setReocordConversationid(ebxml.getConversationId());
            service.save(fiche);
            Method method = this.getClass().getDeclaredMethod(CoreProcessingState.TRAITER + ebxml.getAction().toUpperCase());
            method.invoke(this);
            dd.close();
        }

    }

    public CoreProcessing createProcessing(String type, String state) {
        CoreProcessing processing = new CoreProcessing();
        processing.setProcState(state);
        processing.setProcstartdate(GuceCalendarUtil.getCalendar().getTime());
        processing.setProcessingtypeid(new CoreProcessingtype(type));
        processing.setRecordId(fiche);
        Iterator i = ebxml.getMessageHeader().getToPartyIds();
        String to = "";
        while (i.hasNext()) {
            MessageHeader.PartyId t = (MessageHeader.PartyId) i.next();
            to = t.getId();
        }
        processing.setProcPartner(new CorePartner(to));
        processingFacade.create(processing);
        return processing;
    }

    public CoreMessage createMessage(CoreProcessing processing) {
        CoreMessage mes = new CoreMessage();
        mes.setMessageId(ebxml.getMessageId());
        mes.setMessageContent(ebxml.getData());
        mes.setMessageProcessing(processing);
        mes.setMessageState(10);
        String from = "";
        String to = "";
        Iterator i = ebxml.getMessageHeader().getFromPartyIds();
        while (i.hasNext()) {
            MessageHeader.PartyId t = (MessageHeader.PartyId) i.next();
            from = t.getId();
        }
        i = ebxml.getMessageHeader().getToPartyIds();
        while (i.hasNext()) {
            MessageHeader.PartyId t = (MessageHeader.PartyId) i.next();
            to = t.getId();
        }
        mes.setMessageReceiver(new CorePartner(to));
        mes.setMessageSender(new CorePartner(from));
        mes.setMessageState(1);
        mes.setMessageTimestamp(GuceCalendarUtil.getCalendar().getTime());
        mes.setMessageType(new CoreMessageType(ebxml.getAction()));
        messageFacade.edit(mes);
        return mes;
    }

    public void udpdateFiche() {
        fiche.setReocordConversationid(ebxml.getConversationId());
        recordFacade.edit(fiche);
    }

    private void gererAperak() {
        if (ebxml.getAction().toUpperCase().startsWith(CoreMessage.APERAK) && message != null) {
            message.getMessageProcessing().setProcenddate(GuceCalendarUtil.getCalendar().getTime());
            message.getMessageProcessing().setProcState(TRAITER);
            processingFacade.edit(message.getMessageProcessing());
            if (ebxml.getAction().toUpperCase().equals("APERAK_D")) {
                if (processingFacade.findLastProcessing(fiche.getRecordId(), Vt2Constant.PROCESSING_VALIDATION, null) == null) {
                    createProcessing(Vt2Constant.PROCESSING_VALIDATION, ATTENTE);
                }
            }
        }
    }

    public void traiterVT201() {
        CoreProcessing p = createProcessing(Vt2Constant.PROCESSING_VALIDATION, ATTENTE);
        AperakCreator ac = new AperakCreator();
        org.guce.web.core.mail.util.Aperak a = ac.createAperak(createMessage(p), CoreMessage.APERAK_K);
        ebxmlCreator = new EbxmlCreator();
        ebxmlCreator.setRefMessageId(a.getNumeroMessage());
        CoreMessage mes = ebxmlCreator.generer(a, a.getTypeDocument(),
                ebxml.getService(),
                null, ebxml.getConversationId(),
                a.getEmetteur(),
                a.getDestinataire());
        mes.setMessageState(02);
        mes.setMessageProcessing(p);
        serviceMail.send(mes);
    }

    public void traiterVT202() {
        CoreProcessing p = createProcessing(Vt2Constant.PROCESSING_COMPLEMENT_INFORMATION, ATTENTE);
        AperakCreator ac = new AperakCreator();
        org.guce.web.core.mail.util.Aperak a = ac.createAperak(createMessage(p), CoreMessage.APERAK_K);
        ebxmlCreator = new EbxmlCreator();
        ebxmlCreator.setRefMessageId(a.getNumeroMessage());
        CoreMessage mes = ebxmlCreator.generer(a, a.getTypeDocument(),
                ebxml.getService(),
                null, ebxml.getConversationId(),
                a.getEmetteur(),
                a.getDestinataire());
        mes.setMessageState(02);
        mes.setMessageProcessing(p);
        serviceMail.send(mes);
    }

    public void traiterVT203() {
        CoreProcessing p = createProcessing(Vt2Constant.PROCESSING_REJET, ATTENTE);
        fiche.setRecordState(CoreRecord.ERROR);
        fiche.setRecordEndDate(GuceCalendarUtil.getCalendar().getTime());
        ficheFacade.edit(fiche);
        AperakCreator ac = new AperakCreator();
        org.guce.web.core.mail.util.Aperak a = ac.createAperak(createMessage(p), CoreMessage.APERAK_K);
        ebxmlCreator = new EbxmlCreator();
        ebxmlCreator.setRefMessageId(a.getNumeroMessage());
        CoreMessage mes = ebxmlCreator.generer(a, a.getTypeDocument(),
                ebxml.getService(),
                null, ebxml.getConversationId(),
                a.getEmetteur(),
                a.getDestinataire());
        mes.setMessageState(02);
        mes.setMessageProcessing(p);
        serviceMail.send(mes);
    }

    public void traiterVT204() throws Exception {
        fiche.setRecordState(CoreRecord.CLOS);
        fiche.setRecordEndDate(GuceCalendarUtil.getCalendar().getTime());
        saveAttachments(ebxml, fiche);
        ficheFacade.edit(fiche);
        CoreProcessing waiting = processingFacade.findLastProcessing(fiche.getRecordId(), Vt2Constant.PROCESSING_VALIDATION, null);
        if (waiting == null) {
            waiting = createProcessing(Vt2Constant.PROCESSING_VALIDATION, TRAITER);
        } else {
            waiting.setProcState(CoreProcessingState.TRAITER);
        }
        waiting.setProcenddate(GuceCalendarUtil.getCalendar().getTime());
        processingFacade.edit(waiting);
        CoreProcessing p = processingFacade.findLastProcessing(fiche.getRecordId(), Vt2Constant.PROCESSING_CONSULTATION, null);
        if (p == null) {
            createProcessing(Vt2Constant.PROCESSING_CONSULTATION, ATTENTE);
        }
        AperakCreator ac = new AperakCreator();
        org.guce.web.core.mail.util.Aperak a = ac.createAperak(createMessage(waiting), CoreMessage.APERAK_K);
        ebxmlCreator = new EbxmlCreator();
        ebxmlCreator.setRefMessageId(a.getNumeroMessage());
        CoreMessage mes = ebxmlCreator.generer(a, a.getTypeDocument(),
                ebxml.getService(),
                null, ebxml.getConversationId(),
                a.getEmetteur(),
                a.getDestinataire());
        mes.setMessageState(02);
        mes.setMessageProcessing(waiting);

        serviceMail.send(mes);
    }
    
    @Override
    public void saveAttachments(OrchestraEbxmlMessage ebxml, CoreRecord record) throws Exception {
        Iterator iterator = ebxml.getSOAPMessage().getAttachments();
        if (iterator.hasNext()) {
            iterator.next();
        }
        while (iterator.hasNext()) {
            AttachmentPart attachmentPart = (AttachmentPart) iterator.next();
            InputStream pj = attachmentPart.getDataHandler().getInputStream();
            CoreAttachment attachment = new CoreAttachment();
            attachment.setPjFichier(IOUtils.toByteArray(pj));
            attachment.setPjRecord(record);
            attachment.setPjLibelle(record.getRecordId() + ".pdf");
            attachment.setPjIdentifiant(record.getRecordId() + ".pdf");
            String attachmentType = PRINCIPAL_ATTACHMENT_TYPE;
            CoreAttachmenttype attachtmentType = attachmentTypeFacade.find(attachmentType);
            if (attachtmentType == null) {
                attachtmentType = new CoreAttachmenttype(attachmentType);
                attachtmentType.setAttachementtypename(record.getRecordProcess().getProcessName().toUpperCase());
                attachmentTypeFacade.create(attachtmentType);
            }
            attachment.setPjType(attachtmentType);
            attachment.setPjCreatedate(java.util.Calendar.getInstance().getTime());
            attachmentFacade.create(attachment);
            break;
        }
    }
}
