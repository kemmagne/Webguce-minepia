/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guce.web.process.vt2.controllers;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.guce.core.entities.util.CoreProcessingState;
import org.guce.core.services.CoreServiceLocal;
import org.guce.core.services.SearchFilter;
import org.guce.process.vt2.constants.Vt2Constant;
import org.guce.web.core.user.controller.SimpleController;
import org.guce.web.core.util.JsfUtil;
import org.guce.web.core.util.WebguceSimpleListModel;

/**
 *
 * @author koufana crepin
 */
@ManagedBean
@ViewScoped
public class Vt2ControllerList extends SimpleController implements Serializable {

    @EJB
    private CoreServiceLocal service;

    private WebguceSimpleListModel models;

    private SearchFilter filter;

    public Vt2ControllerList() {

    }

    @PostConstruct
    public void init() {
        if ((JsfUtil.getRequestParameter("javax.faces.partial.ajax") != null
                && JsfUtil.getRequestParameter("javax.faces.partial.ajax").equals("true"))
                || JsfUtil.getRequestParameter("back") != null) {
            return;
        }
        if (page == null || page.trim().isEmpty() || page.equalsIgnoreCase(Vt2Constant.PROCESSING_INITIATION)) {
            filter = new SearchFilter();
            filter.setProcessingType(getProcessingType());
            filter.setProcessingState(CoreProcessingState.ATTENTE);
            filter.setParthnerId(userController.getUserConnecte().getPartnerid().getPartnerid());
            models = new WebguceSimpleListModel(service, filter, Vt2Constant.ENTITIES_RECORD_NAME);
        }
        if (getFormCode().equals(Vt2Constant.FORM_VALIDATION)) {
            filter = new SearchFilter();
            filter.setProcessingType(Vt2Constant.PROCESSING_VALIDATION);
            filter.setProcessingState(CoreProcessingState.ATTENTE);
            if (page != null && page.equalsIgnoreCase("stat")) {
                filter.setProcessingState(CoreProcessingState.TRAITER);
                filter.setProcStatus(0);
            }
            filter.setParthnerId(userController.getUserConnecte().getPartnerid().getPartnerid());
            models = new WebguceSimpleListModel(service, filter, Vt2Constant.ENTITIES_RECORD_NAME);
        } else if (getFormCode().equals(Vt2Constant.FORM_CONSULTATION)) {
            if (getUserController().getUserConnecte().getPartnerid().isUsager()) {
                filter = new SearchFilter();
                filter.setProcessingType(Vt2Constant.PROCESSING_CONSULTATION);
                filter.setProcessingState(null);
                filter.setNumeroContribuable(getUserController().getUserConnecte().getPartnerid().getPartnerNiu());
                models = new WebguceSimpleListModel(service, filter, Vt2Constant.ENTITIES_RECORD_NAME);
            } else {
                filter = new SearchFilter();
                filter.setProcessingType(Vt2Constant.PROCESSING_CONSULTATION);
                filter.setProcessingState(null);
                filter.setParthnerId(userController.getUserConnecte().getPartnerid().getPartnerid());
                models = new WebguceSimpleListModel(service, filter,Vt2Constant.ENTITIES_RECORD_NAME);
            }

        } else if (page != null && page.equalsIgnoreCase(getPage())) {
            filter = new SearchFilter();
            filter.setProcessingType(Vt2Constant.PROCESSING_INITIATION);
            filter.setProcessingState(null);
            filter.setProcStatus(0);
            filter.setParthnerId(userController.getUserConnecte().getPartnerid().getPartnerid());
            models = new WebguceSimpleListModel(service, filter,Vt2Constant.ENTITIES_RECORD_NAME);
        } else if (page != null && page.equalsIgnoreCase("reject")) {
            filter = new SearchFilter();
            filter.setProcessingType(Vt2Constant.PROCESSING_REJET);
            filter.setProcessingState(null);
            filter.setParthnerId(userController.getUserConnecte().getPartnerid().getPartnerid());
            models = new WebguceSimpleListModel(service, filter, Vt2Constant.ENTITIES_RECORD_NAME);
        } else if (page != null && page.equalsIgnoreCase("C")) {

        }

    }

    public SearchFilter getFilter() {
        return filter;
    }

    public void setFilter(SearchFilter filter) {
        this.filter = filter;
    }

    public void search() {
        if (filter.getProcStartDateEnd() != null) {
            Date date = filter.getProcStartDateEnd();
            Calendar car = JsfUtil.getCalendar();
            car.setTime(date);
            car.set(Calendar.HOUR_OF_DAY, 23);
            filter.setProcStartDateEnd(car.getTime());
        }
        if (filter.getProcEndDateEnd() != null) {
            Date date = filter.getProcEndDateEnd();
            Calendar car = JsfUtil.getCalendar();
            car.setTime(date);
            car.set(Calendar.HOUR_OF_DAY, 23);
            filter.setProcEndDateEnd(car.getTime());
        }
        if (filter.getDateEnvoiEnd() != null) {
            Date date = filter.getDateEnvoiEnd();
            Calendar car = JsfUtil.getCalendar();
            car.setTime(date);
            car.set(Calendar.HOUR_OF_DAY, 23);
            filter.setDateEnvoiEnd(car.getTime());
        }

        models = new WebguceSimpleListModel(service, filter, Vt2Constant.ENTITIES_RECORD_NAME);
    }

    public void resetsearch() {
        init();
    }

    @Override
    public String getFormCode() {
        return formCode;
    }

    public String getPage() {
        return "comp";
    }

    private String getProcessingType() {
        return Vt2Constant.PROCESSING_COMPLEMENT_INFORMATION;
    }

    public WebguceSimpleListModel getModels() {
        return models;
    }

    public void setModels(WebguceSimpleListModel models) {
        this.models = models;
    }

    public int rejectNumber() {
        SearchFilter filter1 = new SearchFilter();
        filter1.setProcessingType(Vt2Constant.PROCESSING_REJET);
        filter1.setProcessingState(CoreProcessingState.ATTENTE);
        filter1.setParthnerId(userController.getUserConnecte().getPartnerid().getPartnerid());
        return service.searchCount(filter1);
    }
}
