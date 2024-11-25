package org.guce.web.process.atm.controllers.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.guce.core.ejb.facade.interfaces.CorePaysFacadeLocal;
import org.guce.core.entities.CoreAttachment;
import org.guce.core.entities.CoreAttachmenttype;
import org.guce.core.entities.CoreRecord;
import org.guce.core.entities.util.LookupUtil;
import org.guce.process.atm.ATMConstant;
import org.guce.process.atm.TypeAttachement;
import org.guce.process.atm.entities.ATMRegistration;
import org.guce.process.atm.entities.PaymentDocument;
import org.guce.process.atm.reports.AtmInvoiceVo;
import org.guce.process.atm.reports.QRCodeGenerator;
import org.guce.rep.entities.CorePays;
import org.guce.rep.entities.RepPositionTarifaire;
import org.guce.web.core.util.JsfUtil;
import org.guce.web.process.atm.controllers.ATMRequestController;
import org.primefaces.context.RequestContext;
import org.guce.web.process.atm.util.ATMAddArticleToAttachment;
import static org.guce.web.process.atm.util.ATMAddArticleToAttachment.addArticleToAttachment;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@ManagedBean(
        name = "aTMRequestController"
)
@ViewScoped
public class ATMRequestControllerImpl extends ATMRequestController {
    
 private CorePaysFacadeLocal pFacade;

 private Logger LOGGER = Logger.getLogger(ATMControllerImpl.class.getName());
 
 @PostConstruct
 public void initQuery(){
   loadProductType();
   
    
   
   if( current != null  && current.getTransport() != null){
           current.getTransport().setDestination(getpFacade().find("CM"));
           
     String byCopy = application.getStringParam("type");
     if(byCopy.equals("copy")){
    
     if(byCopy != null &&  byCopy.equals("copy")){
         getCurrent().setCoreAttachmentList(new ArrayList<CoreAttachment>());
     }
     }
   }      
 }
 
 
  protected List<String> requiredAttachmentCodes = Arrays.asList(
        new String[] {"DTarif", "CCI", "AT", "CMP", "LI", "TPV", "QPA"}
    );
  
  
  protected List<String> requiredAttachmentAtm02Codes = Arrays.asList(
        new String[] {"DT", "PDCE", "PDDE", "PPEC", "CRT", "LPI", "RAA", "PCCCEC"}
    );

  
   protected List<String> requiredAttachmentAtm03Codes = Arrays.asList(
        new String[] {"CDPP", "LMEI", "DT", "CRT", "RAA"}
    );
   
    protected List<String[]> requiredAttachments = new ArrayList<String[]>();
 
 
   
    @Override
    public HashMap<String, String> getOfficeSelected() {
       return super.generateOffice(formCode);
    }
   
  
     protected void generatePaymentData() {

        current.setPaiement(new PaymentDocument.CONTENT.PAIEMENT());
        current.getPaiement().setCHARGEUR(new PaymentDocument.CONTENT.PAIEMENT.CHARGEUR());
        current.getPaiement().setBENEFICIAIRE(new PaymentDocument.CONTENT.PAIEMENT.BENEFICIAIRE());
        current.getPaiement().setFACTURE(new PaymentDocument.CONTENT.PAIEMENT.FACTURE());
        //  current.getPaiement().setREPARTITIONS(new PaymentDocument.CONTENT.PAIEMENT.REPARTITIONS());

        current.getPaiement().getCHARGEUR().setNUMEROCONTRIBUABLE(super.current.getChargerid().getNumeroContribuable());
        current.getPaiement().getCHARGEUR().setRAISONSOCIALE(current.getChargerid().getChargername());

        current.getPaiement().getBENEFICIAIRE().setCODE(getSpecificProcessParam(current.getRecordProcess(), ATMConstant.BILL_BENEFICIAIRE, "MINEPIA"));
        current.getPaiement().getBENEFICIAIRE().setLIBELLE(getSpecificProcessParam(current.getRecordProcess(), ATMConstant.BILL_BENEFICIAIRE, "MINEPIA"));

        current.getPaiement().getFACTURE().setMONTANTHT(ATMConstant.BILL_MONTANT_HT);
        current.getPaiement().getFACTURE().setREFERENCEFACTURE(current.getRecordId());
        current.getPaiement().getFACTURE().setTYPEFACTURE(getSpecificProcessParam(current.getRecordProcess(), ATMConstant.FORM_PAYMENT, "DEM602"));
        current.getPaiement().getFACTURE().setMONTANTTTC(ATMConstant.BILL_MONTANT_TTC);
        current.getPaiement().getFACTURE().setAUTREMONTANT("0");

    }
     

     
     
     protected boolean checkRequestConformityAttachment() {
        this.initRequiredAttachements();
        if(current != null) {
            List<CoreAttachment> attachments = current.getCoreAttachmentList();
            List<String> attachmentCodes = new ArrayList<String>();
            List<String> attachmentLabels = new ArrayList<String>();
            List<String> requiredAttachmentsLabel = new ArrayList<String>();
            LOGGER.log( Level.INFO, "ATMControllerImpl -- checkRequestConformityAttachment | requiredAttachments:: {0}", new Object[]{
                requiredAttachments,
            });
            
            LOGGER.log( Level.INFO, "ATMControllerImpl -- checkRequestConformityAttachment | attachments:: {0}", new Object[]{
                attachments,
            });
            if(attachments != null && attachments.size() > 0) {
                for(CoreAttachment attachment: attachments) {
                    if(attachment.getPjType() != null) {
                        attachmentCodes.add(attachment.getPjType().getAttachementtypeid());
                        attachmentLabels.add(attachment.getPjLibelle());
                    }
                }
                LOGGER.log( Level.INFO, "ATMControllerImpl -- checkRequestConformityAttachment | attachmentCodes:: {0}", new Object[]{
                    attachmentCodes,
                });

                for(String[] requiredAttachment: requiredAttachments) {
                    if(!attachmentCodes.contains(requiredAttachment[0])) {
                        requiredAttachmentsLabel.add(requiredAttachment[1]);
                    }
                }
                if(requiredAttachmentsLabel.size() > 0) {
                    
                    for(String requiredAttachment:requiredAttachmentsLabel){
                        String prefix = "";
                       //JsfUtil.addErrorMessage(bundle("RequiredAttachmentsMessage"));
                        JsfUtil.addErrorMessage(prefix.concat(" ").concat(requiredAttachment.toLowerCase().concat(" ").concat(bundle("RequiredAttachmentsMessage"))));
                    }
                                    
//                  JsfUtil.addErrorMessage(bundle("RequiredAttachmentsMessage"));
                    return false;
                }
            } else {
                JsfUtil.addErrorMessage(bundle("AttachmentListEmpty"));
                return false;
            }
        }
      
        return true;
    }
     
     
     
     
    protected boolean checkRequestConformityofAttachment() {
      
        boolean checkRequestConformityAttachment = this.checkRequestConformityAttachment();
        if(!checkRequestConformityAttachment) {
            return checkRequestConformityAttachment;
        }
        return true;
    }
     
     
     
         private void initRequiredAttachements() {
        this.requiredAttachments = new ArrayList<String[]>();
        List<CoreAttachmenttype> avaibleAttachmentTypes = this.getAvaibleAttachmentType();
        for(CoreAttachmenttype avaibleAttachmentType: avaibleAttachmentTypes) {
          if(current != null && selectedTypeAvisTechnique.equals(ATMConstant.TRAITEMENT_STOKAGE)){
            if(this.requiredAttachmentCodes.contains(avaibleAttachmentType.getAttachementtypeid())) {
                this.requiredAttachments.add(new String[]{
                    avaibleAttachmentType.getAttachementtypeid(),
                    addArticleToAttachment(avaibleAttachmentType.getAttachementtypename()),
                });
            }
          }else if(current != null && selectedTypeAvisTechnique.equals(ATMConstant.INGREDIENT_ADDITIFS)){
            if(this.requiredAttachmentAtm02Codes.contains(avaibleAttachmentType.getAttachementtypeid())) {
                this.requiredAttachments.add(new String[]{
                    avaibleAttachmentType.getAttachementtypeid(),
                    addArticleToAttachment(avaibleAttachmentType.getAttachementtypename()),
                });
            }
          }else if(current != null && selectedTypeAvisTechnique.equals(ATMConstant.MATERIEL_EQUIPEMENT)){
            if(this.requiredAttachmentAtm03Codes.contains(avaibleAttachmentType.getAttachementtypeid())) {
                this.requiredAttachments.add(new String[]{
                    avaibleAttachmentType.getAttachementtypeid(),
                    addArticleToAttachment(avaibleAttachmentType.getAttachementtypename()),
                });
            }
          }
        }
    }
     

         
     public String displayStampfees(){
      String stampFees = StringUtils.EMPTY;
 
      if(current != null){
         stampFees = getSpecificProcessParam(current.getRecordProcess(), ATMConstant.BILL_MONTANT_HT, "1500");
       //   stampFees = DEMConstant.BILL_MONTANT_HT;
      }
      return stampFees;
    }
 
    
    @Override
    public void prepareSend() {
        
       
        
        generatePaymentData();
        
         if(checkRequestConformityofAttachment()){
            super.prepareSend();
           RequestContext.getCurrentInstance().execute("PF('confirmSendDialog').show();");
         }  
    }

    public CorePaysFacadeLocal getpFacade() {
        if (pFacade == null) {
            pFacade = (CorePaysFacadeLocal) LookupUtil.CoreLookup("CorePaysFacade");
        }
        return pFacade;
    }
    
    
     public List<String> getRequiredAttachmentCodes() {
        return requiredAttachmentCodes;
    }

    public void setRequiredAttachmentCodes(List<String> requiredAttachmentCodes) {
        this.requiredAttachmentCodes = requiredAttachmentCodes;
    }

    public List<String[]> getRequiredAttachments() {
        return requiredAttachments;
    }

    public void setRequiredAttachments(List<String[]> requiredAttachments) {
        this.requiredAttachments = requiredAttachments;
    }
    
    
 
    
    public StreamedContent printInvoice() throws JRException, IOException, Exception {
        byte[] bytes = null;
        if (bytes == null) {
            Locale locale = new Locale("FR");
            ExternalContext extContext = FacesContext.getCurrentInstance().getExternalContext();
            String pathReport = extContext.getRealPath("user/proccess/atm/print/ATM_INVOICE.jasper");
            Map params = new HashMap();
            ResourceBundle reportBundle = ResourceBundle.getBundle("org/guce/web/process/atm/util/Bundle", locale);
            params.put(JRParameter.REPORT_RESOURCE_BUNDLE, reportBundle);
            params.put(JRParameter.REPORT_LOCALE, locale);
            /*
            set content object to print 
            */
            AtmInvoiceVo printObject    = new AtmInvoiceVo(current ,  StringUtils.EMPTY );   //current.getTypeProduit().getLabel().toLowerCase(locale)
            String invoiceDescription = bundle("description_invoice").replace("PRODUIT", current.getRecordId()).replace("DOSSIER", current.getRecordId()).replace("INVOICEDATE", current.getRecordId());
            printObject.setDescription(invoiceDescription);                          
            String qrCodeContent =  current.getRecordId().concat(", ").concat(printObject.getDatePaiement().concat(", ").concat(printObject.getPrice().toString()));
            /**
             * generer le qr code
             */
            putQrCode(qrCodeContent, printObject);
            List<AtmInvoiceVo> origine = new ArrayList<>();
            origine.add(printObject);
            JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(origine);
            JasperPrint jasperPrint = JasperFillManager.fillReport(pathReport, params, beanCollectionDataSource);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
            outputStream.close();
            bytes = outputStream.toByteArray();
        }
        DefaultStreamedContent sc = new DefaultStreamedContent(new ByteArrayInputStream(bytes), "application/pdf", "ATM_INVOICE");
        return sc;
    }
 
     
       private void putQrCode(String qrCodeContent , AtmInvoiceVo demInvoiceVo) {
        try {
            int qrCodeImageSize = 512;
            InputStream qrCodeStream = new ByteArrayInputStream(new QRCodeGenerator().generateQR(qrCodeContent, qrCodeImageSize));
            byte[] qrCodeBytes = IOUtils.toByteArray(qrCodeStream);
            java.io.File qrCodeTempFile = Files.createTempFile(current.getRecordId().concat("qr_code"), ".png").toFile();
            FileUtils.writeByteArrayToFile(qrCodeTempFile, qrCodeBytes);
            demInvoiceVo.setQrCode(qrCodeTempFile.getPath());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
     
       public boolean isFileSolded(){
           return  current.getInvoiceList() != null  && ! current.getInvoiceList().isEmpty() &&!current.getInvoiceList().get(0).getCorePaymentCollection().isEmpty();
       }   
    
}
