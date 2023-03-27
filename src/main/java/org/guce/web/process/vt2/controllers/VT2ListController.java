package org.guce.web.process.vt2.controllers;

import java.io.ByteArrayInputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import org.guce.core.entities.CoreRecord;
import org.guce.core.entities.util.CoreProcessingState;
import org.guce.core.services.CoreServiceLocal;
import org.guce.core.services.SearchFilter;
import org.guce.process.vt2.VT2Constant;
import org.guce.web.core.user.controller.SimpleController;
import org.guce.web.core.util.JsfUtil;
import org.guce.web.core.util.WebguceSimpleListModel;
import org.guce.web.process.vt2.util.VT2ExporterImpl;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

public class VT2ListController extends SimpleController {
    /**
     * service for fetch file list */
    @EJB
    private CoreServiceLocal service;

    /**
     * service for pdf/xlsx export */
    @EJB
    private VT2ExporterImpl exporter;

    @PostConstruct
    public void init() {
        if(isAjax()) {
            return;
        }
        initAbstract();
    }

    public void initAbstract() {
        filter = new SearchFilter();
        filter.setParthnerId(userController.getUserConnecte().getPartnerid().getPartnerid());
        otherFilter = new HashMap<>();
        if(formCode.equalsIgnoreCase(VT2Constant.FORM_REQUEST)) {
            if(page == null || page.trim().isEmpty() || page.equalsIgnoreCase(formCode)) {
                otherFilter.put("c.recordUserlogin.partnerid", userController.getUserConnecte().getPartnerid());
                otherFilter.put("c.recordState", CoreRecord.NO_START);
                filter.setOtherFilter(otherFilter);
            } else if(page.equalsIgnoreCase("sent")) {
                filter.setProcessingType(VT2Constant.PROCESSING_REQUEST);
            } else {
                goToPreviows();
            }
        } else if(formCode.equalsIgnoreCase(VT2Constant.FORM_RESPONSE_CI)) {
            if(page == null || page.trim().isEmpty() || page.equalsIgnoreCase(formCode)) {
                filter.setOtherFilter(otherFilter);
                filter.setProcessingType(VT2Constant.PROCESSING_CI);
                filter.setProcessingState(CoreProcessingState.ATTENTE);
            } else if(page.equalsIgnoreCase("sent")) {
                filter.setProcessingType(VT2Constant.PROCESSING_RESPONSE_CI);
            } else {
                goToPreviows();
            }
        } else if(formCode.equalsIgnoreCase(VT2Constant.FORM_CONSULTATION)) {
            filter.setOtherFilter(otherFilter);
            filter.setProcessingType(VT2Constant.PROCESSING_CONSULTATION);
        } else if(formCode.equalsIgnoreCase(VT2Constant.FORM_MODIFICATION_REQUEST)) {
            filter.setOtherFilter(otherFilter);
            filter.setProcessingType(VT2Constant.PROCESSING_MODIFICATION_REQUEST);
        } else if(formCode.equalsIgnoreCase(VT2Constant.FORM_MODIFICATION_CONSULTATION)) {
            if(page == null || page.trim().isEmpty() || page.equalsIgnoreCase(formCode)) {
                filter.setOtherFilter(otherFilter);
                filter.setProcessingType(VT2Constant.PROCESSING_CONSULTATION_MODIFICATION);
            } else if(page.equalsIgnoreCase("reject")) {
                filter.setProcessingType(VT2Constant.PROCESSING_MODIFICATION_REJECT);
            } else {
                goToPreviows();
            }
        } else if(formCode.equalsIgnoreCase(VT2Constant.FORM_INVOICE)) {
            if(page == null || page.trim().isEmpty() || page.equalsIgnoreCase(formCode)) {
                filter.setOtherFilter(otherFilter);
                filter.setProcessingType(VT2Constant.PROCESSING_PAYMENT);
                filter.setProcessingState(CoreProcessingState.ATTENTE);
            } else if(page.equalsIgnoreCase("paid")) {
                filter.setProcessingType(VT2Constant.PROCESSING_PAYMENT);
                filter.setProcessingState(CoreProcessingState.TRAITER);
            } else {
                goToPreviows();
            }
        }
        models = new WebguceSimpleListModel(service, filter, VT2Constant.REGISTRATION_RECORD_ENTITY);
    }

    public void search() {
        if(filter.getProcStartDateEnd() != null) {
            Date date = filter.getProcStartDateEnd();
            Calendar car = JsfUtil.getCalendar();
            car.setTime(date);
            car.set(Calendar.HOUR_OF_DAY, 23);
            filter.setProcStartDateEnd(car.getTime());
        }
        if(filter.getProcEndDateEnd() != null) {
            Date date = filter.getProcEndDateEnd();
            Calendar car = JsfUtil.getCalendar();
            car.setTime(date);
            car.set(Calendar.HOUR_OF_DAY, 23);
            filter.setProcEndDateEnd(car.getTime());
        }
        if(filter.getDateEnvoiEnd() != null) {
            Date date = filter.getDateEnvoiEnd();
            Calendar car = JsfUtil.getCalendar();
            car.setTime(date);
            car.set(Calendar.HOUR_OF_DAY, 23);
            filter.setDateEnvoiEnd(car.getTime());
        }
        models = new WebguceSimpleListModel(service, filter, VT2Constant.REGISTRATION_RECORD_ENTITY);
    }

    public void resetsearch() {
        initAbstract();
    }

    public int rejectNumber() {
        return processingWaitting(VT2Constant.PROCESSING_MODIFICATION_REJECT);
    }

    public int processingWaitting(String processing) {
        SearchFilter filter1 = new SearchFilter();
        filter1.setProcessingType(processing);
        filter1.setProcessingState(CoreProcessingState.ATTENTE);
        filter1.setParthnerId(userController.getUserConnecte().getPartnerid().getPartnerid());
        return service.searchCount(filter1);
    }

    public StreamedContent printExcel(String title) throws Exception {
        return new DefaultStreamedContent(new ByteArrayInputStream(exporter.createExcel(title,getSubTitle(),
                                service,filter)), null, "excel_" + getFormCode() + ".xlsx");
    }

    public StreamedContent printPdf(String title) throws Exception {
        return new DefaultStreamedContent(new ByteArrayInputStream(exporter.createPdf(title,getSubTitle(),
                                service,filter)), null, "pdf_" + getFormCode() + ".pdf");
    }

    public String getSubTitle() {
        StringBuilder subtitle = new StringBuilder();
        if(filter.getDateEnvoiStart() != null || filter.getDateEnvoiEnd() != null) {
            subtitle.append(bundle("DateEnvoi")).append(" ( ");
            subtitle.append(filter.getDateEnvoiStart() != null ? VT2ExporterImpl.DateFormat.format(filter.getDateEnvoiStart()):"?").append(" - ");
            subtitle.append(filter.getDateEnvoiEnd() != null ? VT2ExporterImpl.DateFormat.format(filter.getDateEnvoiEnd()):"?");
            subtitle.append(" ) ");
        }
        if(filter.getProStartDateStart() != null || filter.getProcStartDateEnd() != null) {
            subtitle.append(bundle("DateDebutActivite")).append(" ( ");
            subtitle.append(filter.getProStartDateStart() != null ? VT2ExporterImpl.DateFormat.format(filter.getProStartDateStart()):"?").append(" - ");
            subtitle.append(filter.getProcStartDateEnd() != null ? VT2ExporterImpl.DateFormat.format(filter.getProcStartDateEnd()):"?");
            subtitle.append(" ) ");
        }
        if(filter.getProcEndDateStart() != null || filter.getProcEndDateEnd() != null) {
            subtitle.append(bundle("DateFinActivite")).append(" ( ");
            subtitle.append(filter.getProcEndDateStart() != null ? VT2ExporterImpl.DateFormat.format(filter.getProcEndDateStart()):"?").append(" - ");
            subtitle.append(filter.getProcEndDateEnd() != null ? VT2ExporterImpl.DateFormat.format(filter.getProcEndDateEnd()):"?");
            subtitle.append(" ) ");
        }
        return subtitle.toString();
    }

    /**
     * check if request is ajax request */
    protected boolean isAjax() {
        return ((JsfUtil.getRequestParameter("javax.faces.partial.ajax") != null
                                && JsfUtil.getRequestParameter("javax.faces.partial.ajax").equals("true"))
                                || JsfUtil.getRequestParameter("back") != null);
    }
}
