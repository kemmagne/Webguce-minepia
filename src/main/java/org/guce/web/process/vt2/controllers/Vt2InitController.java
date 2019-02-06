/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guce.web.process.vt2.controllers;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.apache.commons.lang.StringUtils;
import org.guce.core.documents.WebguceDocument;
import org.guce.core.ejb.facade.interfaces.CoreAttachmenttypeFacadeLocal;
import org.guce.core.ejb.facade.interfaces.CoreCADFacadeLocal;
import org.guce.core.entities.CoreAttachment;
import org.guce.core.entities.CoreAttachmenttype;
import org.guce.core.entities.CoreCharger;
import org.guce.core.entities.CoreGood;
import org.guce.core.entities.CoreMessage;
import org.guce.core.entities.CoreMessageType;
import org.guce.core.entities.CoreProcess;
import org.guce.core.entities.CoreProcessing;
import org.guce.core.entities.CoreRecord;
import org.guce.core.entities.CoreStakeHolder;
import org.guce.core.entities.CoreTaxandinvoice;
import org.guce.core.entities.util.CoreProcessingState;
import org.guce.core.services.GuceCalendarUtil;
import org.guce.process.ct.ejb.interfaces.VTMINEPDEDRegistrationFacadeLocal;
import org.guce.process.ct.entities.CTGood;
import org.guce.process.ct.entities.VTMINEPDEDRegistration;
import org.guce.process.vt2.constants.Vt2Constant;
import static org.guce.process.vt2.constants.Vt2Constant.FORM_CONSULTATION;
import static org.guce.process.vt2.constants.Vt2Constant.FORM_INITIATION;
import static org.guce.process.vt2.constants.Vt2Constant.PROCESSING_COMPLEMENT_INFORMATION;
import static org.guce.process.vt2.constants.Vt2Constant.PROCESSING_ENVOIE_COMPLEMENT_INFORMATION;
import static org.guce.process.vt2.constants.Vt2Constant.PROCESSING_INITIATION;
import static org.guce.process.vt2.constants.Vt2Constant.PROCESSING_REJET;
import org.guce.process.vt2.services.Vt2DocumentServiceLocal;
import org.guce.process.vt2.services.Vt2ServiceLocal;
import org.guce.rep.ejb.facade.interfaces.CarteContribuableFacadeLocal;
import org.guce.rep.entities.CarteContribuable;
import org.guce.rep.entities.RepPositionTarifaire;
import org.guce.web.core.ebxml.EbxmlCreator;
import org.guce.web.core.user.controller.WebGuceDefaultController;
import org.guce.web.core.util.DefaultLazyDataModel;
import org.guce.web.core.util.JsfUtil;
import org.guce.web.process.vt2.util.Vt2Traitement;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author Steve Anyam
 * @author Ulrich ETEME
 */
@ManagedBean
@ViewScoped
public class Vt2InitController extends WebGuceDefaultController {

    private VTMINEPDEDRegistration current;
    private CarteContribuable carte = new CarteContribuable();
    private String type;

    @EJB
    private Vt2ServiceLocal service;
    @EJB
    private Vt2DocumentServiceLocal documentService;
    @EJB
    private CarteContribuableFacadeLocal cFacade;
    @EJB
    private CoreAttachmenttypeFacadeLocal attachmenttypeFacade;
    @EJB
    private VTMINEPDEDRegistrationFacadeLocal aieRegistrationFacade;
    @EJB
    private CoreCADFacadeLocal cadFacade;
	private DefaultLazyDataModel<RepPositionTarifaire> hsCodeList;
    
    private boolean envoyer;
    private boolean complement;

    /**
     * Creates a new instance of Vt2InitController
     */
    public Vt2InitController() {
    }

    @PostConstruct
    public void init() {

        RequestContext context = RequestContext.getCurrentInstance();
        if ((context != null && context.isAjaxRequest() == true) || JsfUtil.getRequestParameter("back") != null) {
            return;
        }
        type = application.getStringParam("type");

        if (formCode != null && formCode.equalsIgnoreCase(FORM_INITIATION)) {
            if (page != null && page.equalsIgnoreCase("form")) {
                if (type != null && type.equalsIgnoreCase("copy")) {
                    current = service.findByRecordId(recordId);
                    prepareCopy();
                } else if (type != null && type.equalsIgnoreCase("edit")) {
                    current = service.findByRecordId(recordId);
                    if (current == null) {
                        JsfUtil.addErrorMessage(bundle("NumeroFicheIncorrect"));
                        goToPreviows();
                    }
                    if (!current.getRecordUserlogin().getPartnerid().equals(application.getUserConnecte().getPartnerid())) {
                        JsfUtil.addErrorMessage(bundle("AccesInterdit"));
                        goToPreviows();
                    }
                    prepareEdit();
                } else if (type != null && type.equalsIgnoreCase("comp")) {
                    prepareComplement();
                } else {
                    prepareInitForm();
                }
            } else if (page != null && page.equalsIgnoreCase("view")) {
                current = service.findByRecordId(recordId);
                if (current == null) {
                    JsfUtil.addErrorMessage(bundle("NumeroFicheIncorrect"));
                    goToPreviows();
                }
                if (!current.getRecordUserlogin().getPartnerid().equals(application.getUserConnecte().getPartnerid())) {
                    JsfUtil.addErrorMessage(bundle("AccesInterdit"));
                    goToPreviows();
                }
                if (type != null && type.equalsIgnoreCase("viewreject")) {
                    updateProcessing(PROCESSING_REJET, CoreProcessingState.TRAITER);
                    complement = true;
                }
                prepareView();
            }
        } else if (formCode != null && formCode.equalsIgnoreCase(FORM_CONSULTATION)) {
            if (type != null && type.equalsIgnoreCase("view")) {
                current = service.findByRecordId(recordId);
                if (current == null) {
                    JsfUtil.addErrorMessage(bundle("NumeroFicheIncorrect"));
                    goToPreviows();
                }
                if (!current.getRecordUserlogin().getPartnerid().equals(application.getUserConnecte().getPartnerid())) {
                    JsfUtil.addErrorMessage(bundle("AccesInterdit"));
                    goToPreviows();
                }
                if (processingid == null) {
                    JsfUtil.addErrorMessage(bundle("ProcessingIncorrect"));
                    goToPreviows();
                    return;
                }
                CoreProcessing pro = processingFacade.find(processingid);
                if (pro == null) {
                    JsfUtil.addErrorMessage(bundle("ProcessingNotFoud"));
                    goToPreviows();
                    return;
                }
                if (pro.getProcessingtypeid().getProcessingtypeid().equalsIgnoreCase(FORM_CONSULTATION)) {
                    pro.setUserLogin(application.getUserConnecte());
                    pro.setProcenddate(GuceCalendarUtil.getCalendar().getTime());
                    pro.setProcState(CoreProcessingState.TRAITER);
                    processingFacade.edit(pro);
                }
                prepareView();
            }
        }
    }

    @Override
    public VTMINEPDEDRegistration getCurrent() {
        return current;
    }

    @Override
    public String getProcessingType() {
        return "VT201";
    }
	
    private void prepareInitForm() {
        current = new VTMINEPDEDRegistration();
        current.setCoreAttachmentList(new ArrayList<CoreAttachment>());
        current.setGoodList(new ArrayList<CoreGood>());
        current.setChargerid(new CoreCharger());
        current.setTransitaire(null);
        current.setFournisseur(new CoreStakeHolder());
        current.setSignatory(null);
        current.setDecision(null);
        current.setInvoice(new CoreTaxandinvoice());
    }

    private void prepareCopy() {
        current.getChargerid().setChargerid(null);
        current.setReocordConversationid(null);
        current.setInvoiceList(null);
        current.setRecordId(null);
        current.setSignatory(null);
        current.setDecision(null);
        current.setCtDecision(null);

        if (current.getInvoice() != null) {
            current.getInvoice().setTaxandinvoiceid(null);
        } else {
            current.setInvoice(new CoreTaxandinvoice());
        }
        if (current.getBrandHolder() != null) {
            current.setBrandHolder(null);
        }
        if (current.getFournisseur() != null) {
            current.getFournisseur().setStakeholderId(null);
        } else {
            current.setFournisseur(new CoreStakeHolder());
        }
        current.setTransitaire(null);
        List<CoreGood> gds = current.getGoodList();
        if (gds != null && gds.size() > 0) {
            for (CoreGood gd : gds) {
                gd.setID(null);
            }
        } else {
            current.setGoodList(new ArrayList<CoreGood>());
        }

        List<CoreAttachment> atts = current.getCoreAttachmentList();
        List<CoreAttachment> removeAtts = new ArrayList<CoreAttachment>();

        if (atts != null && atts.size() > 0) {
            for (CoreAttachment att : atts) {
                att.setPjId(null);
                if (att.getPjType() != null && att.getPjType().getAttachementtypeid().equalsIgnoreCase(getProcess().getProcessAlias())) {
                    removeAtts.add(att);
                }
            }
            if (removeAtts.size() > 0) {
                atts.removeAll(removeAtts);
            }
        } else {
            current.setCoreAttachmentList(new ArrayList<CoreAttachment>());
        }
        prepareView();
    }

    private void prepareView() {

        if (current.getChargerid().getNumeroContribuable() != null) {
            carte = cFacade.find(current.getChargerid().getNumeroContribuable());
            if (carte == null) {
                carte = new CarteContribuable();
            }
        }
        if (current.getCoreAttachmentList() != null && current.getCoreAttachmentList().size() > -1) {
            current.setCoreAttachmentList(new ArrayList<CoreAttachment>(current.getCoreAttachmentList()));
        }
    }

    private void prepareComplement() {
        current = aieRegistrationFacade.find(recordId);
        if (current == null) {
            JsfUtil.addErrorMessage(bundle("NumeroFicheIncorrect"));
            goToPreviows();
        }
        if (!current.getRecordUserlogin().getPartnerid().equals(application.getUserConnecte().getPartnerid())) {
            JsfUtil.addErrorMessage(bundle("AccesInterdit"));
            goToPreviows();
        }
        prepareView();
        complement = true;
    }

    private void prepareEdit() {
        prepareView();
    }

    public boolean validRequiredAttachements(String paramRequiredAttachement) {
        String[] requiredAttachements = StringUtils.split(paramRequiredAttachement, ",");
        Map<String, String> availableRequiredAttachements = new HashMap<String, String>();
        String attachementsNotAdded = "";
        String attachementsNotFound = "";
        boolean error;
        for (String id : requiredAttachements) {
            error = true;
            for (CoreAttachmenttype attType : this.getAvaibleAttachmentType()) {
                if (attType.getAttachementtypeid().equals(id)) {
                    availableRequiredAttachements.put(id, attType.getAttachementtypename());
                    error = false;
                    break;
                }
            }
            if (error) {
                attachementsNotFound = attachementsNotFound.equals("") ? id : attachementsNotFound + ", " + id;
            }
        }
        if (!attachementsNotFound.equals("")) {
            JsfUtil.addErrorMessage(bundle("NotFoundAttachements") + ": " + attachementsNotFound);
            return false;
        }
        for (CoreAttachment attachement : current.getCoreAttachmentList()) {
            if (availableRequiredAttachements.containsKey(attachement.getPjType().getAttachementtypeid())) {
                availableRequiredAttachements.remove(attachement.getPjType().getAttachementtypeid());
            }
        }
        for (String absentAttachement : availableRequiredAttachements.values()) {
            attachementsNotAdded = attachementsNotAdded.equals("") ? absentAttachement : attachementsNotAdded + ", " + absentAttachement;
        }
        if (attachementsNotAdded.equals("")) {
            return true;
        } else {
            JsfUtil.addErrorMessage(bundle("AddAttachements") + ": " + attachementsNotAdded);
            return false;
        }

    }

    public synchronized void saveAndSend() {
        try {
            if (current.getGoodList().isEmpty()) {
                JsfUtil.addErrorMessage(bundle("PleaseAddOneGood"));
                return;
            }
            if (!validRequiredAttachements(getProcessParam(Vt2Constant.PROCESS_PARAM_REQUIRED_ATTACHMENT, "FACTURE_PROFORMA,DEMANDE_TIMBRE,ATTESTATION_INSCRIPTION_RC,NOTE_TECHNIQUE,QUITTANCE"))) {
                return;
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage(bundle("UnableToSendFile"));
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return;
        }
        if (envoyer) {
            return;
        }
        try {
            int res = save();
            if (res == 0) {
                send();
                goToPreviows();
                envoyer = true;
            }
        } catch (Exception ex) {
            envoyer = false;
            JsfUtil.addErrorMessage(bundle("UnableToSendFile"));
            Logger.getLogger(Vt2InitController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public synchronized void saveFile() {
        try {
            save();
        } catch (Exception ex) {
            JsfUtil.addErrorMessage(bundle("UnableToSaveFile"));
        }
    }

    public synchronized void saveFileExit() {
        saveFile();
        goToPreviows();
    }

    private int save() {
        current.setRecordUserlogin(userController.getUserConnecte());
        current.setRecordProcess(getProcess());
        current.setRecordState(CoreRecord.NO_START);
        List<RepPositionTarifaire> unsupportedNsh = service.verifySupportedNsh(current);
        if (unsupportedNsh != null) {
            List<String> elts = new java.util.ArrayList<String>();
            for (RepPositionTarifaire nsh : unsupportedNsh) {
                elts.add(nsh.getCode());
            }
            JsfUtil.addErrorMessage(bundle("PositionsTarifairesNonSupportees") + StringUtils.join(elts, ", "));
            return 1;
        } else {
            if (service.save(current) == 1) {
                JsfUtil.addSuccessMessage(bundle("DOSSIERMODIFIE") + "/ N° DOSSIER : " + current.getRecordId());
            } else {
                JsfUtil.addSuccessMessage(bundle("DOSSIERSAUVEGARDE") + "/ N° DOSSIER : " + current.getRecordId());
            }
        }
        return 0;
    }
    
    private void send() {
        CoreMessage message = new CoreMessage();
        message.setMessageSender(userController.getUserConnecte().getPartnerid());
        String action = complement ? PROCESSING_ENVOIE_COMPLEMENT_INFORMATION : PROCESSING_INITIATION;
        message.setMessageType(new CoreMessageType(action));
        WebguceDocument docs = documentService.createVt2InitMessage(current, message);
        EbxmlCreator ebxmlCreator = new EbxmlCreator();
        message = ebxmlCreator.generer(
                docs,
                action,
                getProcess().getProcessServicename(),
                current.getCoreAttachmentList(),
                current.getReocordConversationid(),
                userController.getUserConnecte().getPartnerid().getPartnerid(),
                "GUCE",
                docs.getClass(),
                current.getClass(), CTGood.class);
        CoreProcessing processing = null;
        if (complement) {
            processing = createProcessing(action, CoreProcessingState.ATTENTE, 1);
            message.setMessageProcessing(processing);
            CoreProcessing pro = getProcessing(PROCESSING_COMPLEMENT_INFORMATION, CoreProcessingState.ATTENTE);
            if (pro != null) {
                pro.setStatus(1);
                pro.setProcState(CoreProcessingState.TRAITER);
                pro.setUserLogin(application.getUserConnecte());
                pro.setProcenddate(GuceCalendarUtil.getCalendar().getTime());
                processingFacade.edit(pro);
                JsfUtil.addSuccessMessage(bundle("ComplementVt2Envoye"));
            }
        } else {
            processing = createProcessing(PROCESSING_INITIATION, CoreProcessingState.ATTENTE);
            message.setMessageProcessing(processing);
            JsfUtil.addSuccessMessage(bundle("DemandeVt2Envoye"));
        }
        current.setRecordSendate(GuceCalendarUtil.getCalendar().getTime());
        current.setRecordState(CoreRecord.IN_PROCESS);
        service.save(current);
        serviceMail.send(message);
    }
    
    public boolean isRequestForm() {
        return (page.equalsIgnoreCase("form") && formCode.equals(FORM_INITIATION)) || page.equals("forms");
    }
    
    public boolean isViewForm() {
        return (page.equalsIgnoreCase("view") && formCode.equals(FORM_CONSULTATION));
    }

    public List<CoreAttachmenttype> getAttachmentTypeList() {
        try {
            return attachmenttypeFacade.findAll();
        } catch (Exception ex) {
            return null;
        }

    }

    public boolean isComplement() {
        return complement;
    }

    public void setComplement(boolean complement) {
        this.complement = complement;
    }
    
    public void goodCurrencyChanged() {
    }
    
    public StreamedContent downloadReport() {
        byte[] bytes = null;
        String attachmentType = Vt2Traitement.PRINCIPAL_ATTACHMENT_TYPE;
        final String name = current.getRecordId() + ".PDF";
        for (CoreAttachment attachment : current.getCoreAttachmentList()) {
            if (attachment.getPjType().getAttachementtypeid().equals(attachmentType)) {
                bytes = attachment.getPjFichier();
                break;
            }
        }
        DefaultStreamedContent sc = new DefaultStreamedContent(new ByteArrayInputStream(bytes), "application/pdf", name);
        return sc;
    }
	
	public DefaultLazyDataModel<RepPositionTarifaire> getHsCodeList() {
        if (hsCodeList == null) {
				hsCodeList = new DefaultLazyDataModel<RepPositionTarifaire>(service.searchNshByProcess(getProcess()));
			} 
        return hsCodeList;
    }
}
