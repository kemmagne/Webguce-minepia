package org.guce.web.process.atm.services;

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
import org.guce.process.atm.ATMConstant;
import org.guce.process.atm.entities.ATMRegistration;
import org.guce.process.atm.entities.ATMRegistrationHistory;
import org.guce.process.atm.repositories.ATMRegistrationHistoryRepository;
import org.guce.web.core.ebxml.EbxmlCreator;
import org.guce.web.core.mail.ServiceMailSenderLocal;
import org.guce.web.core.mail.util.Aperak;
import org.guce.web.core.mail.util.AperakCreator;
import org.guce.web.core.transformation.DefaultTraitement;
import org.guce.web.process.atm.services.impl.ATMRegistrationMessageServiceImpl;
import org.guce.web.process.atm.services.impl.ATMRegistrationServiceImpl;

public class ATMRegistrationMessageService extends DefaultTraitement implements Serializable {
    @EJB
    protected ServiceMailSenderLocal serviceMail;

    protected final EbxmlCreator ebxmlCreator = new EbxmlCreator();

    @EJB
    protected ATMRegistrationServiceImpl service;

    @EJB
    protected CoreMessageFacadeLocal messageFacade;

    @EJB
    protected CoreProcessingFacadeLocal processingFacade;

    @EJB
    protected ATMRegistrationHistoryRepository historyRepository;

    @EJB
    protected ATMRegistrationMessageServiceImpl serviceMessage;

    @EJB
    protected CoreProcessFacadeLocal processFacade;

    @EJB
    protected CoreTaxandinvoiceFacadeLocal taxandinvoiceFacade;

    @EJB
    protected CorePaymentFacadeLocal paymentFacade;

    public WebguceDocument<ATMRegistration> createMessage(ATMRegistration registration, CoreMessage message) {
        WebguceDocument<ATMRegistration> doc = new WebguceDocument<ATMRegistration>();
        doc.getMessage().setTypeMessage("XML_GUCE");
        doc.setTypeDocument(message.getMessageType().getCode());
        doc.getReference().setNumeroDossier(registration.getRecordId());
        doc.getReference().setReferenceGuce(registration.getReocordConversationid());
        doc.getReference().setNumeroDemande(registration.getRecordParent() != null ? registration.getRecordParent().getRecordId() : registration.getRecordId());
        doc.getReference().setService("atm");
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

    protected String[] getTo(ATMRegistration registration) {
        return new String[]{"MINEPIA"};
    }

    public CoreProcessing createProcessing(ATMRegistration registration, String type, String state) {
        return createProcessing(registration,type,state,registration.getRecordUserlogin().getPartnerid());
    }

    public CoreProcessing createProcessing(ATMRegistration registration, String type, String state, CorePartner partner) {
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
    public CoreMessage sendCIResponse(ATMRegistration registration, CoreUser user, CoreProcessing processing) {
        updateProcessing(processing, user,CoreProcessingState.TRAITER);
        service.save(registration);
        registration.setDecision(new CoreDecisionType(processing.getSubject(), "Comlements d'informations"));
        registration.getDecision().setObservation(processing.getObservation());
        return send(registration, user,ATMConstant.PROCESSING_RESPONSE_CI,ATMConstant.PROCESSING_RESPONSE_CI,null);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public CoreMessage sendModificationRequest(ATMRegistration registration, CoreUser user) throws Exception {
        ATMRegistration before = service.findBy(registration.getRecordId());
        CoreProcessing processing = createProcessing(registration, ATMConstant.PROCESSING_MODIFICATION_REQUEST, CoreProcessingState.ATTENTE);
        processing.setUserLogin(user);
        processingFacade.create(processing);
        ATMRegistrationHistory event = new ATMRegistrationHistory();
        event.setRecordId(registration);
        event.setProcessingId(processing);
        event.setBeforeModification(objectToXml(before));
        event.setAfterModification(objectToXml(registration));
        historyRepository.create(event);
        before.setRecordState(CoreRecord.IN_PROCESS);
        service.save(before);
        registration.setReocordConversationid(null);
        return send(registration, user,ATMConstant.PROCESSING_MODIFICATION_REQUEST,ATMConstant.PROCESSING_MODIFICATION_REQUEST,processing);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public CoreMessage sendRenouvellementRequestBackUp(ATMRegistration registration, CoreUser user) throws Exception {
        ATMRegistration before = service.findBy(registration.getRecordId());
        CoreProcessing processing = createProcessing(registration, ATMConstant.PROCESSING_RENOUVELLEMENT_REQUEST, CoreProcessingState.ATTENTE);
        processing.setUserLogin(user);
        processingFacade.create(processing);
        ATMRegistrationHistory event = new ATMRegistrationHistory();
        event.setRecordId(registration);
        event.setProcessingId(processing);
        event.setBeforeModification(objectToXml(before));
        event.setAfterModification(objectToXml(registration));
        historyRepository.create(event);
        before.setRecordState(CoreRecord.IN_PROCESS);
        service.save(before);
        registration.setReocordConversationid(null);
        return send(registration, user,ATMConstant.PROCESSING_RENOUVELLEMENT_REQUEST,ATMConstant.PROCESSING_RENOUVELLEMENT_REQUEST,processing);
    }
    
     @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public CoreMessage sendRequest(ATMRegistration registration, CoreUser user) {
        return send(registration, user,ATMConstant.PROCESSING_REQUEST,ATMConstant.PROCESSING_REQUEST,null);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public CoreMessage sendRenouvellementRequest(ATMRegistration registration, CoreUser user) { 
        return send(registration, user,ATMConstant.PROCESSING_RENOUVELLEMENT_REQUEST,ATMConstant.PROCESSING_RENOUVELLEMENT_REQUEST,null);
    }

    public CoreMessage send(ATMRegistration registration, CoreUser user, String processingType, String messageType, CoreProcessing processing) {
        if(processing == null) {
            processing = createProcessing(registration,processingType, CoreProcessingState.ATTENTE);
            processing.setUserLogin(user);
            processingFacade.create(processing);
        }
        CoreMessage message = new CoreMessage();
        message.setMessageProcessing(processing);
        message.setMessageSender(user.getPartnerid());
        message.setMessageType(new CoreMessageType(messageType));
        WebguceDocument<ATMRegistration> docs = createMessage(registration, message);
        message = ebxmlCreator.genererto(docs, messageType,
                                ATMConstant.PROCESS_CODE.toLowerCase(),
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

    public String objectToXml(ATMRegistration registration) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JAXB.marshal(registration, out);
        return new String(out.toByteArray(),"UTF-8");
    }

    public ATMRegistration xmlToObject(String xml) throws Exception {
        return JAXB.unmarshal(new ByteArrayInputStream(xml.getBytes("UTF-8")), ATMRegistration.class);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public CoreMessage execute(OrchestraEbxmlMessage ebxml) throws Exception {
        Logger.getLogger(getClass().getName()).log(Level.INFO, "Traitement message in process , messageid =  {0}", new Object[]{ebxml.getMessageId()});
        ATMRegistration registration;
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
                JAXBContext context = JAXBContext.newInstance(WebguceDocument.class, ATMRegistration.class);
                WebguceDocument<ATMRegistration> document = (WebguceDocument) context.createUnmarshaller().unmarshal(inputStream);
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
                    if (r instanceof ATMRegistration) {
                        registration = (ATMRegistration) r;
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
                        processingFacade.create(serviceMessage.createProcessing(registration, ATMConstant.PROCESSING_REQUEST, CoreProcessingState.TRAITER,
                                                                    registration.getRecordParent() != null ? registration.getRecordParent().getRecordUserlogin().getPartnerid() : null));
                    }
                }
                registration.setDecision(document.getContenu().getDecision());
                registration.setPaiement(document.getContenu().getPaiement());
                return (CoreMessage) getClass().getMethod(CoreProcessingState.TRAITER + ebxml.getAction().toUpperCase(),OrchestraEbxmlMessage.class,ATMRegistration.class).invoke(this,ebxml,registration);
            }
        }
        return null;
    }

    public boolean applyData(WebguceDocument<ATMRegistration> document, ATMRegistration registration) {
        if(document.getContenu().getAtmReference() != null) {
            registration.setAtmReference(document.getContenu().getAtmReference());
        }
        if(document.getContenu().getAtmIssueDate() != null) {
            registration.setAtmIssueDate(document.getContenu().getAtmIssueDate());
        }
        if(document.getContenu().getAtmExpiryDate() != null) {
            registration.setAtmExpiryDate(document.getContenu().getAtmExpiryDate());
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

    public CoreMessage traiterATM01(OrchestraEbxmlMessage ebxml, ATMRegistration registration) {
        return createProcessingAndSendAperakATM602(ebxml, registration, ATMConstant.PROCESSING_VALIDATION);
    }
    
     public CoreMessage traiterATM01R(OrchestraEbxmlMessage ebxml, ATMRegistration registration) {
        return createProcessingAndSendAperakATM602(ebxml, registration, ATMConstant.PROCESSING_VALIDATION);
    }

    public CoreMessage traiterATM02(OrchestraEbxmlMessage ebxml, ATMRegistration registration) {
        return createProcessingAndSendAperakATM602(ebxml, registration, ATMConstant.PROCESSING_CI, ATMConstant.PROCESSING_VALIDATION,ATMConstant.PROCESSING_INVOICE);
    }

    public CoreMessage traiterATM11(OrchestraEbxmlMessage ebxml, ATMRegistration registration) {
        return createProcessingAndSendAperakATM602(ebxml, registration, ATMConstant.PROCESSING_VALIDATION);
    }

    public CoreMessage traiterATM04(OrchestraEbxmlMessage ebxml, ATMRegistration registration) throws Exception {
        saveAttachments(ebxml, registration);
        registration.setRecordEndDate(GuceCalendarUtil.getCalendar().getTime());
        registration.setRecordState(CoreRecord.CLOS);
        service.save(registration);
        return createProcessingAndSendAperakATM602(ebxml, registration, ATMConstant.PROCESSING_CONSULTATION, ATMConstant.PROCESSING_VALIDATION);
    }

    public CoreMessage traiterATM03(OrchestraEbxmlMessage ebxml, ATMRegistration registration) {
        registration.setRecordEndDate(GuceCalendarUtil.getCalendar().getTime());
        registration.setRecordState(CoreRecord.CLOS);
        service.save(registration);
        return createProcessingAndSendAperakATM602(ebxml, registration, ATMConstant.PROCESSING_MODIFICATION_REJECT, ATMConstant.PROCESSING_MODIFICATION_VALIDATION);
    }
    
    public CoreMessage traiterATM03R(OrchestraEbxmlMessage ebxml, ATMRegistration registration) {
        registration.setRecordEndDate(GuceCalendarUtil.getCalendar().getTime());
        registration.setRecordState(CoreRecord.CLOS);
        service.save(registration);
        return createProcessingAndSendAperakATM602(ebxml, registration, ATMConstant.PROCESSING_RENOUVELLEMENT_REJECT, ATMConstant.PROCESSING_RENOUVELLEMENT_VALIDATION);
    }

    public CoreMessage traiterATM09(OrchestraEbxmlMessage ebxml, ATMRegistration registration) {
        return createProcessingAndSendAperakATM602(ebxml, registration, ATMConstant.PROCESSING_MODIFICATION_VALIDATION);
    }
    
    public CoreMessage traiterATM09R(OrchestraEbxmlMessage ebxml, ATMRegistration registration) {
        return createProcessingAndSendAperakATM602(ebxml, registration, ATMConstant.PROCESSING_RENOUVELLEMENT_VALIDATION);
    }

    public CoreMessage traiterATM10(OrchestraEbxmlMessage ebxml, ATMRegistration registration) throws Exception {
        saveAttachments(ebxml, registration);
        registration.setRecordEndDate(GuceCalendarUtil.getCalendar().getTime());
        registration.setRecordState(CoreRecord.CLOS);
        service.save(registration);
        return createProcessingAndSendAperakATM602(ebxml, registration, ATMConstant.PROCESSING_CONSULTATION_MODIFICATION,ATMConstant.PROCESSING_MODIFICATION_VALIDATION);
    }
    
     public CoreMessage traiterATM10R(OrchestraEbxmlMessage ebxml, ATMRegistration registration) throws Exception {
        saveAttachments(ebxml, registration);
        registration.setRecordEndDate(GuceCalendarUtil.getCalendar().getTime());
        registration.setRecordState(CoreRecord.CLOS);
        service.save(registration);
        return createProcessingAndSendAperakATM602(ebxml, registration, ATMConstant.PROCESSING_CONSULTATION_RENOUVELLEMENT,ATMConstant.PROCESSING_RENOUVELLEMENT_VALIDATION);
    }

    public CoreMessage traiterATM601(OrchestraEbxmlMessage ebxml, ATMRegistration registration) {
        String processingType = processingFacade.findLastProcessing(registration.getRecordId(),  ATMConstant.PROCESSING_CONSULTATION) == null ?
                                ATMConstant.PROCESSING_VALIDATION : ATMConstant.PROCESSING_MODIFICATION_VALIDATION;
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
        invoice.setTaxobject("Frais ATM");
        invoice.setTaxrecipient(registration.getChargerid().getChargername());
        invoice.setTaxsenddate(GuceCalendarUtil.getCalendar().getTime());
        invoice.setTaxstate(Boolean.FALSE);
        taxandinvoiceFacade.create(invoice);
        return createProcessingAndSendAperakATM602(ebxml, registration, ATMConstant.PROCESSING_PAYMENT);
    }
    
    public CoreMessage traiterATM602(OrchestraEbxmlMessage ebxml, ATMRegistration registration) {
        String processingType = processingFacade.findLastProcessing(registration.getRecordId(),  ATMConstant.PROCESSING_CONSULTATION) == null ?
                                ATMConstant.PROCESSING_VALIDATION : ATMConstant.PROCESSING_MODIFICATION_VALIDATION;
        String reference = (registration.getPaiement().getFACTURE() != null)&&(registration.getPaiement().getFACTURE().getREFERENCEFACTURE() != null) ? 
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
        return createProcessingAndSendAperakATM602(ebxml, registration, processingType, ATMConstant.PROCESSING_PAYMENT);
    }
    
    
    public CoreMessage createProcessingAndSendAperakATM602(OrchestraEbxmlMessage ebxml, ATMRegistration registration, String processingType, String... processingTypeEnd) {
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
    

//    public CoreMessage traiterATM602(OrchestraEbxmlMessage ebxml, ATMRegistration registration) {
//        String processingType = processingFacade.findLastProcessing(registration.getRecordId(),  ATMConstant.PROCESSING_CONSULTATION) == null ?
//                                ATMConstant.PROCESSING_VALIDATION : ATMConstant.PROCESSING_MODIFICATION_VALIDATION;
//        String reference = registration.getPaiement().getFACTURE().getREFERENCEFACTURE() != null ? 
//                                registration.getPaiement().getFACTURE().getREFERENCEFACTURE() :
//                                registration.getRecordId();
//        for(CoreTaxandinvoice invoice : registration.getInvoiceList()) {
//            if(invoice.getTaxReferenceNumber().equals(reference) &&
//                                !invoice.getTaxstate()) {
//                CorePayment payment = new CorePayment();
//                payment.setTaxandinvoiceid(invoice);
//                payment.setPaymentAmount(new BigDecimal(registration.getPaiement().getENCAISSEMENT().getMONTANT()));
//                payment.setPaymentdate(GuceCalendarUtil.getCalendar().getTime());
//                payment.setReceiptNumber(registration.getPaiement().getENCAISSEMENT().getNUMERORECU());
//                payment.setPaymentobservations(registration.getPaiement().getENCAISSEMENT().getOBSERVATIONS());
//                payment.setPaymentobject(registration.getPaiement().getENCAISSEMENT().getNATURE());
//                paymentFacade.create(payment);
//            }
//        }
//        return createProcessingAndSendAperakATM602(ebxml, registration, processingType, ATMConstant.PROCESSING_PAYMENT);
//    }

//    public CoreMessage createProcessingAndSendAperakATM602(OrchestraEbxmlMessage ebxml, ATMRegistration registration, String processingType, String... processingTypeEnd) {
//        List<CoreProcessing> listP = processingFacade.findLastProcessingList(registration.getRecordId(),
//                                processingType,CoreProcessingState.ATTENTE);
//        CoreMessage message = messageFacade.find(ebxml.getMessageId());
//        if (listP != null && !listP.isEmpty() && CoreProcessingState.ATTENTE.equalsIgnoreCase(listP.get(0).getProcState())) {
//            return null;
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
