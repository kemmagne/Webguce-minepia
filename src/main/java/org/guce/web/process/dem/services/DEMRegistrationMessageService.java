package org.guce.web.process.dem.services;

import hk.hku.cecid.ebms.pkg.MessageHeader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPMessage;
import org.guce.core.documents.WebguceDocument;
import org.guce.core.ejb.facade.interfaces.CoreMessageFacadeLocal;
import org.guce.core.ejb.facade.interfaces.CorePaymentFacadeLocal;
import org.guce.core.ejb.facade.interfaces.CoreProcessFacadeLocal;
import org.guce.core.ejb.facade.interfaces.CoreProcessingFacadeLocal;
import org.guce.core.ejb.facade.interfaces.CoreTaxandinvoiceFacadeLocal;
import org.guce.core.entities.CoreDecisionType;
import org.guce.core.entities.CoreMessage;
import org.guce.core.entities.CoreMessageType;
import org.guce.core.entities.CorePartner;
import org.guce.core.entities.CorePayment;
import org.guce.core.entities.CoreProcessing;
import org.guce.core.entities.CoreProcessingtype;
import org.guce.core.entities.CoreRecord;
import org.guce.core.entities.CoreTaxandinvoice;
import org.guce.core.entities.CoreUser;
import org.guce.core.entities.util.CoreProcessingState;
import org.guce.core.services.GuceCalendarUtil;
import org.guce.orchestra.core.OrchestraEbxmlMessage;
import org.guce.process.dem.DEMConstant;
import org.guce.process.dem.entities.DEMRegistration;
import org.guce.process.dem.entities.DEMRegistrationHistory;
import org.guce.process.dem.repositories.DEMRegistrationHistoryRepository;
import org.guce.web.core.ebxml.EbxmlCreator;
import org.guce.web.core.mail.ServiceMailSenderLocal;
import org.guce.web.core.mail.util.Aperak;
import org.guce.web.core.mail.util.AperakCreator;
import org.guce.web.core.transformation.DefaultTraitement;
import org.guce.web.process.dem.services.impl.DEMRegistrationMessageServiceImpl;
import org.guce.web.process.dem.services.impl.DEMRegistrationServiceImpl;

public class DEMRegistrationMessageService extends DefaultTraitement implements Serializable {
    @EJB
    protected ServiceMailSenderLocal serviceMail;

    protected final EbxmlCreator ebxmlCreator = new EbxmlCreator();

    @EJB
    protected DEMRegistrationServiceImpl service;

    @EJB
    protected CoreMessageFacadeLocal messageFacade;

    @EJB
    protected CoreProcessingFacadeLocal processingFacade;

    @EJB
    protected DEMRegistrationHistoryRepository historyRepository;

    @EJB
    protected DEMRegistrationMessageServiceImpl serviceMessage;

    @EJB
    protected CoreProcessFacadeLocal processFacade;

    @EJB
    protected CoreTaxandinvoiceFacadeLocal taxandinvoiceFacade;

    @EJB
    protected CorePaymentFacadeLocal paymentFacade;

    public WebguceDocument<DEMRegistration> createMessage(DEMRegistration registration, CoreMessage message) {
        WebguceDocument<DEMRegistration> doc = new WebguceDocument<DEMRegistration>();
        doc.getMessage().setTypeMessage("XML_GUCE");
        doc.setTypeDocument(message.getMessageType().getCode());
        doc.getReference().setNumeroDossier(registration.getRecordId());
        doc.getReference().setReferenceGuce(registration.getReocordConversationid());
        doc.getReference().setNumeroDemande(registration.getRecordParent() != null ? registration.getRecordParent().getRecordId() : registration.getRecordId());
        doc.getReference().setService("dem");
        doc.getReference().setDateDossier(registration.getRecordCreatedate());
        doc.getMessage().setNumeroMessage(message.getMessageId());
        doc.getMessage().setDateEmission(message.getMessageTimestamp());
        doc.getMessage().setEtat("E");
        doc.getMessage().setMessageOrigine("");
        doc.getMessage().setTypeMessage(message.getMessageType().getCode());
        if (message.getMessageProcessing() != null) {
            if (message.getMessageSender() != null) {
                doc.getRoutage().setEmetteur(message.getMessageSender().getPartnerid());
            }
            doc.getRoutage().setRecepteur(message.getMessageReceiver() != null ? message.getMessageReceiver().getPartnerid() : "GUCE");
        }
        doc.setContenu(registration);
        return doc;
    }

    protected String[] getTo(DEMRegistration registration) {
        return new String[]{"MINCOMMERCE"};
    }

    public CoreProcessing createProcessing(DEMRegistration registration, String type, String state) {
        return createProcessing(registration,type,state,registration.getRecordUserlogin().getPartnerid());
    }

    public CoreProcessing createProcessing(DEMRegistration registration, String type, String state, CorePartner partner) {
        CoreProcessing processing = new CoreProcessing();
        processing.setProcState(state);
        processing.setProcstartdate(GuceCalendarUtil.getCalendar().getTime());
        processing.setProcessingtypeid(new CoreProcessingtype(type));
        processing.setRecordId(registration);
        processing.setProcPartner(partner);
        if(CoreProcessingState.TRAITER.equals(state)) {
            processing.setProcenddate(GuceCalendarUtil.getCalendar().getTime());
        }
        return processing;
    }

    public CoreProcessing updateProcessing(CoreProcessing processing, CoreUser user, String state) {
        processing.setProcState(state);
        processing.setProcenddate(GuceCalendarUtil.getCalendar().getTime());
        processing.setUserLogin(user);
        processingFacade.edit(processing);
        return processing;
    }

    public CoreMessage createMessage(CoreProcessing processing, OrchestraEbxmlMessage ebxml) {
        CoreMessage mes = new CoreMessage();
        mes.setMessageId(ebxml.getMessageId());
        mes.setMessageContent(ebxml.getData());
        mes.setMessageTimestamp(GuceCalendarUtil.getCalendar().getTime());
        mes.setMessageProcessing(processing);
        mes.setMessageType(new CoreMessageType(ebxml.getAction()));
        mes.setMessageState(1);
        mes.setMessageSender(new CorePartner(ebxml.getMessageHeader().getFromPartyIds().hasNext() ? 
                                ((MessageHeader.PartyId)ebxml.getMessageHeader().getFromPartyIds().next()).getId() : ""));
        mes.setMessageReceiver(new CorePartner(ebxml.getMessageHeader().getToPartyIds().hasNext() ? 
                                ((MessageHeader.PartyId)ebxml.getMessageHeader().getToPartyIds().next()).getId() : ""));
        return mes;
    }

    public void sendAperak(CoreProcessing processing, OrchestraEbxmlMessage ebxml) {
        serviceMessage.send(serviceMessage.sendAperakMessage(processing, ebxml));
    }

    public CoreMessage sendAperakMessage(CoreProcessing processing, OrchestraEbxmlMessage ebxml) {
        AperakCreator ac = new AperakCreator();
        Aperak a = ac.createAperak(createMessage(processing,ebxml), CoreMessage.APERAK_K);
        EbxmlCreator ebxmlCreator = new EbxmlCreator();
        ebxmlCreator.setRefMessageId(a.getNumeroMessage());
        CoreMessage mes = ebxmlCreator.generer(a, a.getTypeDocument(),
                                ebxml.getService(),
                                null, ebxml.getConversationId(),
                                a.getEmetteur(),
                                a.getDestinataire());
        mes.setMessageState(02);
        mes.setMessageProcessing(processing);
        messageFacade.create(mes);
        return messageFacade.find(mes.getId());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public CoreMessage sendCIResponse(DEMRegistration registration, CoreUser user, CoreProcessing processing) {
        updateProcessing(processing, user,CoreProcessingState.TRAITER);
        service.save(registration);
        registration.setDecision(new CoreDecisionType(processing.getSubject(), "Comlements d'informations"));
        registration.getDecision().setObservation(processing.getObservation());
        return send(registration, user,DEMConstant.PROCESSING_RESPONSE_CI,DEMConstant.PROCESSING_RESPONSE_CI,null);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public CoreMessage sendModificationRequest(DEMRegistration registration, CoreUser user) throws Exception {
        DEMRegistration before = service.findBy(registration.getRecordId());
        CoreProcessing processing = createProcessing(registration, DEMConstant.PROCESSING_MODIFICATION_REQUEST, CoreProcessingState.ATTENTE);
        processing.setUserLogin(user);
        processingFacade.create(processing);
        DEMRegistrationHistory event = new DEMRegistrationHistory();
        event.setRecordId(registration);
        event.setProcessingId(processing);
        event.setBeforeModification(objectToXml(before));
        event.setAfterModification(objectToXml(registration));
        historyRepository.create(event);
        before.setRecordState(CoreRecord.IN_PROCESS);
        service.save(before);
        registration.setReocordConversationid(null);
        return send(registration, user,DEMConstant.PROCESSING_MODIFICATION_REQUEST,DEMConstant.PROCESSING_MODIFICATION_REQUEST,processing);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public CoreMessage sendRequest(DEMRegistration registration, CoreUser user) {
        return send(registration, user,DEMConstant.PROCESSING_REQUEST,DEMConstant.PROCESSING_REQUEST,null);
    }

    public CoreMessage send(DEMRegistration registration, CoreUser user, String processingType, String messageType, CoreProcessing processing) {
        if(processing == null) {
            processing = createProcessing(registration,processingType, CoreProcessingState.ATTENTE);
            processing.setUserLogin(user);
            processingFacade.create(processing);
        }
        CoreMessage message = new CoreMessage();
        message.setMessageProcessing(processing);
        message.setMessageSender(user.getPartnerid());
        message.setMessageType(new CoreMessageType(messageType));
        WebguceDocument<DEMRegistration> docs = createMessage(registration, message);
        message = ebxmlCreator.genererto(docs, messageType,
                                DEMConstant.PROCESS_CODE.toLowerCase(),
                                registration.getCoreAttachmentList(), registration.getReocordConversationid(),
                                user.getPartnerid().getPartnerid(),
                                getTo(registration), docs.getClass(), registration.getClass());
        message.setMessageProcessing(processing);
        if(registration.getRecordSendate() == null) {
            registration.setRecordSendate(GuceCalendarUtil.getCalendar().getTime());
            registration.setRecordState(CoreRecord.IN_PROCESS);
            service.save(registration);
        }
        messageFacade.create(message);
        return messageFacade.find(message.getId());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void send(CoreMessage message) {
        Logger.getLogger(getClass().getName()).log(Level.INFO, "Send message , messageid =  {0}", new Object[]{message.getId()});
        serviceMail.send(message);
    }

    public String objectToXml(DEMRegistration registration) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JAXB.marshal(registration, out);
        return new String(out.toByteArray(),"UTF-8");
    }

    public DEMRegistration xmlToObject(String xml) throws Exception {
        return JAXB.unmarshal(new ByteArrayInputStream(xml.getBytes("UTF-8")), DEMRegistration.class);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public CoreMessage execute(OrchestraEbxmlMessage ebxml) throws Exception {
        Logger.getLogger(getClass().getName()).log(Level.INFO, "Traitement message in process , messageid =  {0}", new Object[]{ebxml.getMessageId()});
        DEMRegistration registration;
        if (ebxml.getAction().startsWith(CoreMessage.APERAK)) {
            CoreMessage message = messageFacade.find(ebxml.getAcknowledgment().getRefToMessageId());
            if (message == null && ebxml.getRefToMessageId() != null) {
                message = messageFacade.find(ebxml.getRefToMessageId());
            }
            if (message != null) {
                gererAperak(ebxml,message);
            }
        } else {
            SOAPMessage soapMessage = ebxml.getSOAPMessage();
            Iterator iterator = soapMessage.getAttachments();
            try (InputStream inputStream = ((AttachmentPart) iterator.next()).getDataHandler().getInputStream()) {
                JAXBContext context = JAXBContext.newInstance(WebguceDocument.class, DEMRegistration.class);
                WebguceDocument<DEMRegistration> document = (WebguceDocument) context.createUnmarshaller().unmarshal(inputStream);
                if (document == null || document.getReference() == null || document.getReference().getNumeroDossier() == null) {
                    Logger.getLogger(getClass().getName()).log(Level.INFO, "Reference manquante  {0}", new Object[]{ebxml.getMessageId()});
                    return null;
                }
                boolean createFromCircuit = false;
                registration = service.findBy(document.getReference().getNumeroDossier());
                if (registration == null  && document.getReference().getNumeroDemande() != null) {
                    registration = service.findBy(document.getReference().getNumeroDemande());
                }
                if (registration == null) {
                    CoreRecord r = ficheFacade.findByConversationId(ebxml.getConversationId());
                    if (r instanceof DEMRegistration) {
                        registration = (DEMRegistration) r;
                    }
                }
                if(registration == null) {
                    createFromCircuit = true;
                    registration = document.getContenu();
                    registration.setRecordId(document.getReference().getNumeroDossier());
                    registration.setRecordProcess(processFacade.getProcessByServiceName(ebxml.getService()));
                    registration.setRecordCreatedate(GuceCalendarUtil.getCalendar().getTime());
                    registration.setRecordCreatedate(GuceCalendarUtil.getCalendar().getTime());
                    registration.setRecordSendate(GuceCalendarUtil.getCalendar().getTime());
                    registration.setRecordState(CoreRecord.IN_PROCESS);
                    registration.setRecordLastCreateDate(GuceCalendarUtil.getCalendar().getTime());
                    if(document.getReference().getNumeroDemande() != null) {
                        CoreRecord circuit = ficheFacade.find(document.getReference().getNumeroDemande());
                        if(circuit != null) {
                            registration.setRecordUserlogin(circuit.getRecordUserlogin());
                            registration.setRecordParent(circuit);
                            registration.setChargerid(registration.getRecordParent().getChargerid());
                        }
                    }
                }
                registration.setReocordConversationid(ebxml.getConversationId());
                if(applyData(document, registration)) {
                    service.save(registration);
                    if(createFromCircuit) {
                        processingFacade.create(serviceMessage.createProcessing(registration, DEMConstant.PROCESSING_REQUEST, CoreProcessingState.TRAITER,
                                                                    registration.getRecordParent() != null ? registration.getRecordParent().getRecordUserlogin().getPartnerid() : null));
                    }
                }
                registration.setDecision(document.getContenu().getDecision());
                registration.setPaiement(document.getContenu().getPaiement());
                String action = ebxml.getAction().toUpperCase().equals(DEMConstant.PROCESSING_FOR_PAYMENT_REPONSE) ? DEMConstant.PROCESSING_PAYMENT : ebxml.getAction().toUpperCase();
                return (CoreMessage) getClass().getMethod(CoreProcessingState.TRAITER + action,OrchestraEbxmlMessage.class,DEMRegistration.class).invoke(this,ebxml,registration);            
            }
        }
        return null;
    }

    public boolean applyData(WebguceDocument<DEMRegistration> document, DEMRegistration registration) {
        if(document.getContenu().getDemDate() != null) {
            registration.setDemDate(document.getContenu().getDemDate());
        }
        if(document.getContenu().getDemReference() != null) {
            registration.setDemReference(document.getContenu().getDemReference());
        }
        return true;
    }

    public void gererAperak(OrchestraEbxmlMessage ebxml, CoreMessage message) {
        if (ebxml.getAction().toUpperCase().startsWith(CoreMessage.APERAK) && message != null) {
            message.getMessageProcessing().setProcenddate(GuceCalendarUtil.getCalendar().getTime());
            message.getMessageProcessing().setProcState(CoreProcessingState.TRAITER);
            processingFacade.edit(message.getMessageProcessing());
        }
    }

    public CoreMessage traiterDEM01(OrchestraEbxmlMessage ebxml, DEMRegistration registration) {
        return createProcessingAndSendAperak(ebxml, registration, DEMConstant.PROCESSING_VALIDATION);
    }

    public CoreMessage traiterDEM02(OrchestraEbxmlMessage ebxml, DEMRegistration registration) {
        return createProcessingAndSendAperak(ebxml, registration, DEMConstant.PROCESSING_CI, DEMConstant.PROCESSING_VALIDATION,DEMConstant.PROCESSING_INVOICE);
    }

    public CoreMessage traiterDEM11(OrchestraEbxmlMessage ebxml, DEMRegistration registration) {
        return createProcessingAndSendAperak(ebxml, registration, DEMConstant.PROCESSING_VALIDATION);
    }

    public CoreMessage traiterDEM04(OrchestraEbxmlMessage ebxml, DEMRegistration registration) throws Exception {
        saveAttachments(ebxml, registration);
        registration.setRecordEndDate(GuceCalendarUtil.getCalendar().getTime());
        registration.setRecordState(CoreRecord.CLOS);
        service.save(registration);
        return createProcessingAndSendAperak(ebxml, registration, DEMConstant.PROCESSING_CONSULTATION, DEMConstant.PROCESSING_VALIDATION);
    }

    public CoreMessage traiterDEM03(OrchestraEbxmlMessage ebxml, DEMRegistration registration) {
        registration.setRecordEndDate(GuceCalendarUtil.getCalendar().getTime());
        registration.setRecordState(CoreRecord.CLOS);
        service.save(registration);
        return createProcessingAndSendAperak(ebxml, registration, DEMConstant.PROCESSING_MODIFICATION_REJECT, DEMConstant.PROCESSING_MODIFICATION_VALIDATION);
    }

    public CoreMessage traiterDEM09(OrchestraEbxmlMessage ebxml, DEMRegistration registration) {
        return createProcessingAndSendAperak(ebxml, registration, DEMConstant.PROCESSING_MODIFICATION_VALIDATION);
    }

    public CoreMessage traiterDEM10(OrchestraEbxmlMessage ebxml, DEMRegistration registration) throws Exception {
        saveAttachments(ebxml, registration);
        registration.setRecordEndDate(GuceCalendarUtil.getCalendar().getTime());
        registration.setRecordState(CoreRecord.CLOS);
        service.save(registration);
        return createProcessingAndSendAperak(ebxml, registration, DEMConstant.PROCESSING_CONSULTATION_MODIFICATION,DEMConstant.PROCESSING_MODIFICATION_VALIDATION);
    }

    public CoreMessage traiterDEM601(OrchestraEbxmlMessage ebxml, DEMRegistration registration) {
        String processingType = processingFacade.findLastProcessing(registration.getRecordId(),  DEMConstant.PROCESSING_CONSULTATION) == null ?
                                DEMConstant.PROCESSING_VALIDATION : DEMConstant.PROCESSING_MODIFICATION_VALIDATION;
        String amount = (registration.getPaiement().getFACTURE().getMONTANTTTC() != null && 
                                !registration.getPaiement().getFACTURE().getMONTANTTTC().trim().isEmpty()) ? 
                                registration.getPaiement().getFACTURE().getMONTANTTTC() :
                                registration.getPaiement().getFACTURE().getMONTANTHT();
        String reference = registration.getPaiement().getFACTURE().getREFERENCEFACTURE() != null ? 
                                registration.getPaiement().getFACTURE().getREFERENCEFACTURE() :
                                registration.getRecordId();
        CoreTaxandinvoice invoice = new CoreTaxandinvoice();
        invoice.setRecordId(registration);
        invoice.setTaxReferenceNumber(reference);
        invoice.setTaxtotalamountBigDecimal(new BigDecimal(amount));
        invoice.setTaxobject("Frais DEM");
        invoice.setTaxrecipient(registration.getChargerid().getChargername());
        invoice.setTaxsenddate(GuceCalendarUtil.getCalendar().getTime());
        invoice.setTaxstate(Boolean.FALSE);
        taxandinvoiceFacade.create(invoice);
        return createProcessingAndSendAperak(ebxml, registration, DEMConstant.PROCESSING_PAYMENT);
    }

    public CoreMessage traiterDEM602(OrchestraEbxmlMessage ebxml, DEMRegistration registration) {
        String processingType = processingFacade.findLastProcessing(registration.getRecordId(),  DEMConstant.PROCESSING_CONSULTATION) == null ?
                                DEMConstant.PROCESSING_VALIDATION : DEMConstant.PROCESSING_MODIFICATION_VALIDATION;
        String reference = (registration.getPaiement().getFACTURE() != null) && (registration.getPaiement().getFACTURE().getREFERENCEFACTURE() != null) ? 
                                registration.getPaiement().getFACTURE().getREFERENCEFACTURE() :
                                registration.getRecordId();
        for(CoreTaxandinvoice invoice : registration.getInvoiceList()) {
            if(invoice.getTaxReferenceNumber().equals(reference) &&
                                !invoice.getTaxstate()) {
                CorePayment payment = new CorePayment();
                payment.setTaxandinvoiceid(invoice);
                payment.setPaymentAmount(new BigDecimal(registration.getPaiement().getENCAISSEMENT().getMONTANT()));
                payment.setPaymentdate(GuceCalendarUtil.getCalendar().getTime());
                payment.setReceiptNumber(registration.getPaiement().getENCAISSEMENT().getNUMERORECU());
                payment.setPaymentobservations(registration.getPaiement().getENCAISSEMENT().getOBSERVATIONS());
                payment.setPaymentobject(registration.getPaiement().getENCAISSEMENT().getNATURE());
                paymentFacade.create(payment);
            }
        }
        return createProcessingAndSendAperak(ebxml, registration, processingType, DEMConstant.PROCESSING_PAYMENT);
    }
    
    
       public CoreMessage createProcessingAndSendAperak(OrchestraEbxmlMessage ebxml, DEMRegistration registration, String processingType, String... processingTypeEnd) {
        List<CoreProcessing> listP = processingFacade.findLastProcessingList(registration.getRecordId(),
                                processingType,CoreProcessingState.ATTENTE);
        CoreMessage message = messageFacade.find(ebxml.getMessageId());
        
            if(processingTypeEnd != null  && processingTypeEnd.length > 0) {
                  CoreProcessing pEnd = null;
                  for (String pTEnd : processingTypeEnd) {
                      if (pEnd == null) {
                          pEnd = processingFacade.findLastProcessing(registration.getRecordId(),
                                  pTEnd, CoreProcessingState.ATTENTE);
                      }
                  }
        
        
        
            if(pEnd == null || !CoreProcessingState.ATTENTE.equalsIgnoreCase(pEnd.getProcState())) {
                return null;
            }
            serviceMessage.updateProcessing(pEnd, null, CoreProcessingState.TRAITER);
        }
        CoreProcessing p = serviceMessage.createProcessing(registration, processingType, CoreProcessingState.ATTENTE,new CorePartner(getToPartner(ebxml)));
        if(registration.getDecision() != null) {
            p.setObservation(registration.getDecision().getObservation());
            p.setSubject(registration.getDecision().getCode());
        }
        processingFacade.create(p);
        message.setMessageProcessing(p);
        messageFacade.edit(message);
        return serviceMessage.sendAperakMessage(p, ebxml);
    }

    
    

//    public CoreMessage createProcessingAndSendAperak(OrchestraEbxmlMessage ebxml, DEMRegistration registration, String processingType, String... processingTypeEnd) {
//        List<CoreProcessing> listP = processingFacade.findLastProcessingList(registration.getRecordId(),
//                                processingType,CoreProcessingState.ATTENTE);
//        CoreMessage message = messageFacade.find(ebxml.getMessageId());
//        if (listP != null && !listP.isEmpty() && CoreProcessingState.ATTENTE.equalsIgnoreCase(listP.get(0).getProcState())) {
//            if(!ebxml.getAction().toUpperCase().equals(DEMConstant.PROCESSING_PAYMENT)){
//                            return null;
//                 }        
//        }
//        if(processingTypeEnd != null  && processingTypeEnd.length > 0) {
//            CoreProcessing pEnd = null;
//            for (String pTEnd : processingTypeEnd) {
//                if (pEnd == null) {
//                    pEnd = processingFacade.findLastProcessing(registration.getRecordId(),
//                                            pTEnd, CoreProcessingState.ATTENTE);
//                }
//            }
//            if(pEnd == null || !CoreProcessingState.ATTENTE.equalsIgnoreCase(pEnd.getProcState())) {
//                return null;
//            }
//            serviceMessage.updateProcessing(pEnd, null, CoreProcessingState.TRAITER);
//        }
//        CoreProcessing p = serviceMessage.createProcessing(registration, processingType, CoreProcessingState.ATTENTE,new CorePartner(getToPartner(ebxml)));
//        if(registration.getDecision() != null) {
//            p.setObservation(registration.getDecision().getObservation());
//            p.setSubject(registration.getDecision().getCode());
//        }
//        processingFacade.create(p);
//        message.setMessageProcessing(p);
//        messageFacade.edit(message);
//        return serviceMessage.sendAperakMessage(p, ebxml);
//    }

    public String getToPartner(OrchestraEbxmlMessage ebxml) {
        String to = "GUCE";
        Iterator it = ebxml.getMessageHeader().getToPartyIds();
        while(it.hasNext()) {
            String t = ((MessageHeader.PartyId)it.next()).getId();
            if(!t.equals("GUCE") && !t.equals("WEBGUCE")) {
                to = t;
            }
        }
        return to;
    }
}
