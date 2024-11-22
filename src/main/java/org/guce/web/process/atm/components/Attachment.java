/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guce.web.process.atm.components;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.security.MessageDigest;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.context.FacesContext;
import org.apache.commons.lang.StringUtils;
import org.guce.core.entities.CoreAttachment;
import org.guce.core.entities.CoreRecord;
import org.guce.core.entities.util.LookupUtil;
import org.guce.core.services.ApplicationServiceLocal;
import org.guce.core.services.GedService;
import org.guce.core.services.GedServiceLocal;
import org.guce.rep.services.repServiceLocal;
import org.guce.web.component.WebguceDefaultComponent;
import org.guce.web.core.user.controller.WebGuceDefaultController;
import org.guce.web.core.util.JsfUtil;
import org.guce.web.core.util.PieceJointeManager;
import org.guce.web.core.util.StringSimplifier;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Koufana Crepin Sosthene
 */
@FacesComponent("org.guce.web.process.atm.jsf.components.Attachment")
public class Attachment extends WebguceDefaultComponent implements NamingContainer {
    
    protected PieceJointeManager attachment;

    private repServiceLocal repService;
    private ApplicationServiceLocal appService;

    private static final CharsetEncoder ASCII_ENCODER = Charset.forName("US-ASCII").newEncoder(); // or "ISO-8859-1" for ISO Latin 1
    private static final Pattern PATTERN = Pattern.compile("(\\w|\\.|-)*", Pattern.CASE_INSENSITIVE);
    private static final int DEFAULT_MAX_FILE_SIZE = 1024 * 1024;
    private static final String PROCESS_MAX_FILE_SIZE_PARAM = "webguce.process.max.attachment.size";

    public static boolean isPureAscii(String v) {
        return PATTERN.matcher(v).matches();
    }
    private GedServiceLocal gedService;

    public Attachment() {
        attachment = new PieceJointeManager();
        attachment.setPiece(new CoreAttachment());
    }

    @Override
    public repServiceLocal getRepService() {
        if (repService == null) {
            repService = (repServiceLocal) LookupUtil.lookup("repService", "rep.services");
        }
        return repService;
    }

    public ApplicationServiceLocal getAppService() {
        if (appService == null) {
            appService = (ApplicationServiceLocal) LookupUtil.lookup("ApplicationService", "core.services");
        }
        return appService;
    }

    public GedServiceLocal getGedService() {
        if (gedService == null) {
            try {
                gedService = GedService.getInstance();
            } catch (Exception ex) {
                Logger.getLogger(getClass().getName()).log(Level.WARNING, null, ex);
                return (GedServiceLocal) LookupUtil.lookup("GedService", "core.services");
            }
        }
        return gedService;
    }

    public PieceJointeManager getAttachemnt() {
        return attachment;
    }

    public PieceJointeManager getAttachment() {
        return attachment;
    }

    public void setAttachment(PieceJointeManager attachment) {
        this.attachment = attachment;
    }

    public void saveProduit() {
        if (((List) getAttributes().get("value")) != null && ((List) getAttributes().get("value")).size() > -1
                && attachment != null && attachment.getFile() != null && attachment.getFile().getContents() != null) {
            attachment.getPiece().setPjLibelle(attachment.getPiece().getPjType().getAttachementtypename());
            attachment.getPiece().setPjRecord((CoreRecord) getAttributes().get("parentClass"));
//			attachment.getPiece().setPjChecksum(generateChecksum(attachment.getPiece()));
            ((List) getAttributes().get("value")).add(attachment.getPiece());
            attachment = new PieceJointeManager();
            attachment.setPiece(new CoreAttachment());
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        StringSimplifier sm = StringSimplifier.getInstance();
        UploadedFile file = event.getFile();
        String name = file.getFileName();
        if (isPureAscii(name)) {
            String[] s = name.replace("\\", "/").split("[/]+");
            if (s != null && s.length > 0) {
                name = s[s.length - 1];
            }

            int maxFileSize = DEFAULT_MAX_FILE_SIZE;

            if (event.getFile().getContents().length > maxFileSize) {
                String msg = MessageFormat.format(JsfUtil.getLocaleValue("org/guce/web/component/Bundle", "InvalidSizeAttachment"), JsfUtil.humanReadableByteCount(event.getFile().getContents().length, false), JsfUtil.humanReadableByteCount(maxFileSize, false));
                FacesContext.getCurrentInstance().addMessage("formContenu:allcontent:att:file", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", msg));
            } else if (event.getFile().getContents().length == 0) {
                String msg = JsfUtil.getLocaleValue("org/guce/web/component/Bundle", "InvalidSizeAttachmentSizeZero");
                FacesContext.getCurrentInstance().addMessage("formContenu:allcontent:att:file", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", msg));
            } else {
                List<CoreAttachment> attachements = (List) getAttributes().get("value");
                int items = 0;
                String fileNameId = sm.simplifiedString(file.getFileName());
                int currentCount = 1;
                if (attachements != null && !attachements.isEmpty()) {
                    for (int i = 0; i < attachements.size(); i++) {
                        String baseName = fileNameId.substring(0, fileNameId.lastIndexOf("."));
                        if (attachements.get(i).getPjIdentifiant().contains(baseName)) {
                            items++;
                            if (attachements.get(i).getPjIdentifiant().contains("_")) {
                                String atId = attachements.get(i).getPjIdentifiant();
                                String digit = atId.substring(atId.lastIndexOf("_") + 1, atId.lastIndexOf(".")).trim();
                                int val = Integer.parseInt(digit);
                                if (val > currentCount) {
                                    currentCount = val;
                                }
                            }
                        }
                    }
                }
                if (items > 0 && fileNameId.contains(".")) {
                    String sep = "" + fileNameId.charAt(fileNameId.lastIndexOf("."));
                    String[] parts = fileNameId.split("\\.");

                    String part1 = parts[0];
                    String ext = parts[1];

                    fileNameId = part1 + "_" + ++currentCount + "." + ext;
                }

                attachment.setFile(file);
                attachment.setName(fileNameId);
                attachment.getPiece().setPjFichier(file.getContents());
                attachment.getPiece().setPjIdentifiant(fileNameId);
            }
        } else {
            FacesContext.getCurrentInstance().addMessage("formContenu:allcontent:att:file", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", JsfUtil.getLocaleValue("org/guce/web/component/Bundle", "InvalidNameAttachment") + " - " + name));
        }

    }

    public void removeAtt(CoreAttachment att) {
        if (((List) getAttributes().get("value")) != null && ((List) getAttributes().get("value")).size() > -1) {
            ((List) getAttributes().get("value")).remove(att);
        }
    }

    public StreamedContent file(CoreAttachment a) {
        try (ByteArrayInputStream ints = new ByteArrayInputStream(a.getPjFichier())) {
            return new DefaultStreamedContent(ints, null, hasExtension(a.getPjIdentifiant()) ? a.getPjIdentifiant() : a.getPjLibelle());
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public boolean hasExtension(String pjName) {
        if (pjName != null) {
            pjName = pjName.toLowerCase();
            return pjName.endsWith(".pdf") || pjName.endsWith(".png") || pjName.endsWith(".gif") || pjName.endsWith(".jpg") || pjName.endsWith(".jpeg");
        }
        return false;
    }

    @Override
    public String getFamily() {
        return ("javax.faces.NamingContainer");
    }

    @Override
    public Object saveState(FacesContext context) {
        Map m = new HashMap();
        m.put("object", super.saveState(context));
        m.put("attachment", attachment);
        return m;
    }

    @Override
    public void restoreState(FacesContext context, Object state) {
        Map m = (Map) state;
        attachment = (PieceJointeManager) m.get("attachment");
        if (attachment == null) {
            attachment = new PieceJointeManager();
            attachment.setPiece(new CoreAttachment());
        }
        super.restoreState(context, m.get("object"));
    }

    public boolean isGedAvailable() {
        GedServiceLocal service = getGedService();
        boolean ok = service != null && service.isAvailable();
        return ok;
    }

    public boolean isGedEnable() {
        return GedService.isGedEnable();
    }

    public String generateChecksum(CoreAttachment attachment) {
        try {
            if (getAppService() != null) {
                String checkumType = getAppService().getProperties().getProperty("checksum.type", "md5");
                if (!StringUtils.isEmpty(checkumType)) {
                    MessageDigest md = MessageDigest.getInstance(checkumType.toUpperCase());
                    md.update(attachment.getPjFichier());
                    byte[] byteData = md.digest();
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < byteData.length; ++i) {
                        sb.append(Integer.toHexString((byteData[i] & 0xFF) | 0x100).substring(1, 3));
                    }
                    return sb.toString().toUpperCase();
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(WebGuceDefaultController.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
        return null;
    }
    
    public boolean isModifiedFile(CoreAttachment att){
         String name = hasExtension(att.getPjIdentifiant()) ? att.getPjIdentifiant() : (hasExtension(att.getPjLibelle()) ? att.getPjLibelle() : att.getPjIdentifiant());
         CoreRecord coreRecord = att.getPjRecord();
         if(coreRecord.getRecordParent()==null){// si ca n as pas de parent alors c est une demande initiale
           return true;
         }else{
          // on verifie si ca appartient a liste des coreAttchment parent
          List<CoreAttachment> list = coreRecord.getRecordParent().getCoreAttachmentList();
          boolean isPresent = true;
          for(CoreAttachment elt : list){
            if(elt.getPjIdentifiant().equals(att.getPjIdentifiant())){
                isPresent = false;
            }
          }
          return  isPresent;
         }    
    }
}