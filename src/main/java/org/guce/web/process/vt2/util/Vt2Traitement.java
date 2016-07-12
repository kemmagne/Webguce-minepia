/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guce.web.process.vt2.util;

import hk.hku.cecid.ebms.pkg.MessageHeader;
import java.io.IOException;
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
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import org.apache.commons.io.IOUtils;
import org.guce.core.documents.WebguceDocument;
import org.guce.core.ejb.facade.interfaces.CoreAttachmentFacadeLocal;
import org.guce.core.ejb.facade.interfaces.CoreAttachmenttypeFacadeLocal;
import org.guce.core.ejb.facade.interfaces.CoreChargerFacadeLocal;
import org.guce.core.ejb.facade.interfaces.CoreMessageFacadeLocal;
import org.guce.core.ejb.facade.interfaces.CorePaysFacadeLocal;
import org.guce.core.ejb.facade.interfaces.CoreProcessFacadeLocal;
import org.guce.core.ejb.facade.interfaces.CoreProcessingFacadeLocal;
import org.guce.core.ejb.facade.interfaces.CoreRecordFacadeLocal;
import org.guce.core.ejb.facade.interfaces.CoreTaxandinvoiceFacadeLocal;
import org.guce.core.ejb.facade.interfaces.CoreUserFacadeLocal;
import org.guce.core.ejb.facade.interfaces.DocumentUtilityLocal;
import org.guce.core.entities.CoreAttachment;
import org.guce.core.entities.CoreAttachmenttype;
import org.guce.core.entities.CoreForm;
import org.guce.core.entities.CoreMessage;
import org.guce.core.entities.CoreMessageType;
import org.guce.core.entities.CorePartner;
import org.guce.core.entities.CoreProcessing;
import org.guce.core.entities.CoreProcessingtype;
import org.guce.core.entities.CoreRecord;
import org.guce.core.entities.CoreSignatory;
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
import org.guce.rep.ejb.facade.interfaces.RepCustomsOfficeFacadeLocal;
import org.guce.rep.entities.RepDomiciliation;
import org.guce.web.core.ebxml.EbxmlCreator;
import org.guce.web.core.mail.ServiceMailSenderLocal;
import org.guce.web.core.mail.util.AperakCreator;
import org.guce.web.core.transformation.DefaultTraitement;
import org.guce.web.core.transformation.ITraitement;
import org.guce.web.core.transformation.WebguceTransformer;
import org.milyn.edi.unedifact.d95b.APERAK.Aperak;

/**
 *
 * @author Administrateur
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
    @EJB
    private CoreUserFacadeLocal userFacadeLocal;

    private EbxmlCreator ebxmlCreator;
    @EJB
    private CoreChargerFacadeLocal chargerFacade;
    private CoreMessage message;
    @EJB
    private CoreProcessFacadeLocal processFacade;
    @EJB
    private CorePaysFacadeLocal paysFacade;
    @EJB
    private RepCustomsOfficeFacadeLocal customFacade;
    @EJB
    private DocumentUtilityLocal doc;
    @EJB
    private CoreTaxandinvoiceFacadeLocal tFacade;
    @EJB
    private CoreAttachmentFacadeLocal attachmentFacade;
    @EJB
    private CoreAttachmenttypeFacadeLocal attachmentTypeFacade;
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
            message = messageFacade.find(ebxml.getAcknowledgment().getRefToMessageId());
            if (message == null) {
                message = messageFacade.find(ebxml.getRefToMessageId());
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
//                     
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
            fiche.setReocordConversationid(ebxml.getConversationId()); //Code du WF provenant d'orchestra
            service.save(fiche);
            saveAttachments(ebxml, fiche);           
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
        if (to != null && !to.equalsIgnoreCase("GUCE")) {
            processing.setProcPartner(new CorePartner(to));
        }
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
        if (!to.equalsIgnoreCase("GUCE")) {
            mes.setMessageReceiver(new CorePartner(to));
        }
        if (!from.equalsIgnoreCase("GUCE")) {
            mes.setMessageSender(new CorePartner(from));
        }
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
        fiche.setRecordState(CoreRecord.CLOS);
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

    public void traiterVT204() {
        System.out.println("TRAITER VT204----------------------------->");
        fiche.setRecordState(CoreRecord.CLOS);//Cloture du dossier
        fiche.setRecordEndDate(GuceCalendarUtil.getCalendar().getTime());//Date de cloture       
        ficheFacade.edit(fiche);
        CoreProcessing p = createProcessing(Vt2Constant.PROCESSING_CONSULTATION, ATTENTE);//Création d'un processing de consultation pour permettre l'affichage du resultat.
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
}
