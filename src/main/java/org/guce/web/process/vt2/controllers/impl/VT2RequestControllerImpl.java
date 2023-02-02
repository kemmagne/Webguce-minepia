package org.guce.web.process.vt2.controllers.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.apache.commons.collections.CollectionUtils;
import org.guce.core.entities.CoreGood;
import org.guce.core.entities.CoreRecord;
import org.guce.process.carg.CargConstants;
import org.guce.process.pred.entities.PredExpedition;
import org.guce.process.vt2.entities.PaymentDocument;
import org.guce.rep.entities.RepProductCategory;
import org.guce.web.core.util.JsfUtil;
import org.guce.web.process.vt2.controllers.VT2RequestController;

@ManagedBean(
        name = "vT2RequestController"
)
@ViewScoped
public class VT2RequestControllerImpl extends VT2RequestController {

    @Override
    protected boolean loadParentData(CoreRecord parent) {
        if (!(parent instanceof PredExpedition)) {
            JsfUtil.addErrorMessage(bundle("EnterEXPRegistrationFile"));
            return false;
        }
        PredExpedition exp = (PredExpedition) parent;
        current.setCad(exp.getCad());
        current.setSupplier(exp.getExportateur());
        current.getInvoice().setInvoiceNumber(exp.getTransaction().getNumeroFacture());
        current.getInvoice().setInvoiceDate(exp.getTransaction().getDateEmissionFacture());
        current.getInvoice().setCurrency(exp.getTransaction().getCurrency());
        current.getInvoice().setInvoiceAmount(exp.getTransaction().getMontantFacture());

        current.getTransport().setOrigin(exp.getRegistration().getOrigin());
        current.getTransport().setProvenance(exp.getRegistration().getProvenance());
        current.getTransport().setBlNumber(exp.getbLNumber());
        current.getTransport().setTravelNumber(exp.getTravelNumber());
        current.getTransport().setArrivalDate(exp.getVesselArrivalDate());
        current.getTransport().setLeavingDate(exp.getIssueDate());
        current.getTransport().setLoadingPlace(exp.getPlaceOfDepature());
        current.getTransport().setClearingPlace(exp.getPlaceOfClearing());
        current.getTransport().setTransportMode(exp.getTransportMode());

        current.setOfficeCode("BC-SNP");
        return super.loadParentData(parent);
    }

    @Override
    public void prepareSend() {
        if (checkRequestConformity()) {
            validateAndSave();
            generatePaymentData();
        }
    }
}
