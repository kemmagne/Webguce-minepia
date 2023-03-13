package org.guce.web.process.vt2.controllers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import org.apache.commons.collections.CollectionUtils;
import org.guce.core.ejb.facade.interfaces.CoreProcessFacadeLocal;
import org.guce.core.ejb.facade.interfaces.CoreRecordFacadeLocal;
import org.guce.core.entities.CoreGood;
import org.guce.core.entities.CoreProcess;
import org.guce.core.entities.CoreProcessing;
import org.guce.process.carg.CargConstants;
import org.guce.process.vt2.entities.PaymentDocument;
import org.guce.process.vt2.entities.VT2Registration;
import org.guce.rep.ejb.facade.interfaces.RepProductCategoryFacadeLocal;
import org.guce.rep.entities.RepPositionTarifaire;
import org.guce.rep.entities.RepProductCategory;
import org.guce.web.core.user.controller.WebGuceDefaultController;
import org.guce.web.core.util.DefaultLazyDataModel;
import org.guce.web.core.util.JsfUtil;
import org.guce.web.process.vt2.services.impl.VT2RegistrationMessageServiceImpl;
import org.guce.web.process.vt2.services.impl.VT2RegistrationServiceImpl;

public abstract class VT2Controller extends WebGuceDefaultController {
    protected VT2Registration current;

    protected CoreProcessing processing;

    @EJB
    protected VT2RegistrationServiceImpl service;

    @EJB
    protected VT2RegistrationMessageServiceImpl serviceMessage;

    @EJB
    protected CoreRecordFacadeLocal recordFacade;
    
    @EJB
    protected CoreProcessFacadeLocal processFacade;

    protected String type;

    protected String parentId;

    protected boolean fileSent;

    protected DefaultLazyDataModel<RepPositionTarifaire> hsCodeList;
    
    protected List<RepProductCategory> productCategoryList;
    protected List<RepProductCategory> selectedProductCategories;
    
    protected CoreProcess cargProcess;

    @EJB
    protected RepProductCategoryFacadeLocal repProductCategoryFacadeLocal;
    
    protected BigDecimal vtMinepdedAmountFees;

    @PostConstruct
    public void initCustomsParams() {
        type = application.getStringParam("type");
        parentId = application.getStringParam("parentId");
        cargProcess = processFacade.getProcessByAlias("carg");
        productCategoryList = repProductCategoryFacadeLocal.findAll();
    }

    @Override
    public VT2Registration getCurrent() {
        return current;
    }

    @Override
    public String getProcessingType() {
        return "VT2";
    }

    public void save() {
        try {
            current = service.save(current);
            JsfUtil.addSuccessMessage(bundle("RecordSaved") + " " + current.getRecordId());
        } catch(Exception ex) {
            JsfUtil.addErrorMessage(bundle("CannotSaveRecord") + " " + current.getRecordId());
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    protected boolean checkRecordAccessRight() {
        if (recordId == null) {
            JsfUtil.addErrorMessage(bundle("NumberNoFound"));
            goToPreviows();
            return false;
        }
        current = service.findBy(recordId);
        if (current == null) {
            JsfUtil.addErrorMessage(bundle("IncorrectNumber"));
            goToPreviows();
            return false;
        }
        if (!current.getRecordUserlogin().getPartnerid().equals(application.getUserConnecte().getPartnerid())) {
            JsfUtil.addErrorMessage(bundle("AccessDenied"));
            goToPreviows();
            return false;
        }
        selectGoodProductCategories();
        return true;
    }

    protected boolean checkProcessingAccessRight() {
        if (processingid == null) {
            JsfUtil.addErrorMessage(bundle("TaskNoFound"));
            goToPreviows();
            return false;
        }
        processing = processingFacade.find(processingid);
        if (processing == null) {
            JsfUtil.addErrorMessage(bundle("IncorrectTaskNumber"));
            goToPreviows();
            return false;
        }
        if(!processing.getProcPartner().equals(application.getUserConnecte().getPartnerid())) {
            JsfUtil.addErrorMessage(bundle("AccessDenied"));
            goToPreviows();
            return false;
        }
        current = (VT2Registration) processing.getRecordId();
        return true;
    }

    protected boolean checkRequestConformity() {
        if(current.getGoodList().isEmpty()) {
            JsfUtil.addErrorMessage(bundle("GoodListEmpty"));
            return false;
        }
        restoreSelectedNshProductCategories();
        return true;
    }

    public CoreProcessing getProcessing() {
        return processing;
    }

    public DefaultLazyDataModel<RepPositionTarifaire> getHsCodeList() {
        if (hsCodeList == null) {
            hsCodeList = new DefaultLazyDataModel<>(service.searchNshByProcess(getProcess()));
        }
        return hsCodeList;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
    
    public List<RepProductCategory> getProductCategoryList() {
        return productCategoryList;
    }

    public void setProductCategoryList(List<RepProductCategory> productCategoryList) {
        this.productCategoryList = productCategoryList;
    }

    public List<RepProductCategory> getSelectedProductCategories() {
        return selectedProductCategories;
    }

    public void setSelectedProductCategories(List<RepProductCategory> selectedProductCategories) {
        this.selectedProductCategories = selectedProductCategories;
    }
    
    protected void restoreSelectedNshProductCategories(){
        if(selectedProductCategories != null && !selectedProductCategories.isEmpty()){
            for (RepProductCategory pc : selectedProductCategories) {
                if(!current.getProductCategoryList().contains(pc)){
                    current.getProductCategoryList().add(pc);
                }
            }
        }
    }
    
    protected void selectGoodProductCategories() {
        final List<CoreGood> goodList;
        goodList = current != null && current.getGoodList() != null ? current.getGoodList() : new ArrayList<CoreGood>();
        for (CoreGood g : goodList) {
            if (g.gethSCode() != null) {
                List<RepProductCategory> goodProductCategoryList = g.gethSCode().getProductCategoryList();
                if (current.getProductCategoryList() == null) {
                    current.setProductCategoryList(new ArrayList<RepProductCategory>());
                }
                if (selectedProductCategories == null) {
                    selectedProductCategories = new ArrayList<RepProductCategory>();
                }
                if (CollectionUtils.isNotEmpty(goodProductCategoryList)) {
                    for (RepProductCategory pc : goodProductCategoryList) {
                        if (!current.getProductCategoryList().contains(pc)) {
                            current.getProductCategoryList().add(pc);
                        }
                        if (!selectedProductCategories.contains(pc)) {
                            selectedProductCategories.add(pc);
                        }
                    }
                }
            }
        }
    }

    protected void calculateVtMinepdedFees() {
        BigDecimal vtMinepdedFeesAmountReference = new BigDecimal(getSpecificProcessParam(cargProcess, CargConstants.CARG_VT_MINEPDED_FEES_AMOUNT_REFERENCE, "50000"));
        vtMinepdedAmountFees = BigDecimal.ZERO;
        if (current.getProductCategoryList() != null && !current.getProductCategoryList().isEmpty()) {
            vtMinepdedAmountFees = vtMinepdedFeesAmountReference.multiply(new BigDecimal(current.getProductCategoryList().size()));
        }
        if (current.getTotalFeesAmount()!= null) {
            vtMinepdedAmountFees = vtMinepdedAmountFees.subtract(current.getTotalFeesAmount());
        }
    }
    
    protected void generatePaymentData() {
        BigDecimal vtMinepdedAmountFeesToSendInPayment = null;
        if (vtMinepdedAmountFees != null && vtMinepdedAmountFees.compareTo(BigDecimal.ZERO) > 0) {
            vtMinepdedAmountFeesToSendInPayment = current.getTotalFeesAmount() != null ? vtMinepdedAmountFees.add(current.getTotalFeesAmount()) : vtMinepdedAmountFees;
            current.setPaiement(new PaymentDocument.CONTENT.PAIEMENT());
            current.getPaiement().setCHARGEUR(new PaymentDocument.CONTENT.PAIEMENT.CHARGEUR());
            current.getPaiement().setBENEFICIAIRE(new PaymentDocument.CONTENT.PAIEMENT.BENEFICIAIRE());
            current.getPaiement().setFACTURE(new PaymentDocument.CONTENT.PAIEMENT.FACTURE());
            current.getPaiement().setREPARTITIONS(new PaymentDocument.CONTENT.PAIEMENT.REPARTITIONS());

            current.getPaiement().getCHARGEUR().setNUMEROCONTRIBUABLE(current.getChargerid().getNumeroContribuable());
            current.getPaiement().getCHARGEUR().setRAISONSOCIALE(current.getChargerid().getChargername());

            current.getPaiement().getBENEFICIAIRE().setCODE(getSpecificProcessParam(cargProcess, CargConstants.CARG_VT_MINEPDED_BENEFICIARE_MINEPDED_CODE, "MINEPDED"));
            current.getPaiement().getBENEFICIAIRE().setLIBELLE(getSpecificProcessParam(cargProcess, CargConstants.CARG_VT_MINEPDED_BENEFICIARE_MINEPDED_CODE, "MINEPDED"));

            current.getPaiement().getFACTURE().setMONTANTHT(vtMinepdedAmountFeesToSendInPayment.toPlainString());
            current.getPaiement().getFACTURE().setREFERENCEFACTURE(current.getRecordId());
            current.getPaiement().getFACTURE().setTYPEFACTURE(getSpecificProcessParam(cargProcess, CargConstants.CARG_VT_MINEPDED_FEES_BILL_TYPE, "VT2601"));
            current.getPaiement().getFACTURE().setMONTANTTTC(vtMinepdedAmountFeesToSendInPayment.toPlainString());
            current.getPaiement().getFACTURE().setAUTREMONTANT("0");
            BigDecimal vtMinepdedFeesAmountMinepdepRate = new BigDecimal(getSpecificProcessParam(cargProcess, CargConstants.CARG_VT_MINEPDED_FEES_AMOUNT_MINEPDED_PERCENT, "90")).divide(new BigDecimal(100));
            BigDecimal vtMinepdedFeesAmountGuceRate = new BigDecimal(getSpecificProcessParam(cargProcess, CargConstants.CARG_VT_MINEPDED_FEES_AMOUNT_GUCE_PERCENT, "10")).divide(new BigDecimal(100));

            BigDecimal vtMinepdedAmountFeesMinepdedPart = vtMinepdedAmountFeesToSendInPayment.multiply(vtMinepdedFeesAmountMinepdepRate);
            BigDecimal vtMinepdedFeesAmountGucePart = vtMinepdedAmountFeesToSendInPayment.multiply(vtMinepdedFeesAmountGuceRate);
            if (vtMinepdedAmountFeesMinepdedPart != null && vtMinepdedAmountFeesMinepdedPart.compareTo(BigDecimal.ZERO) > 0) {
                PaymentDocument.CONTENT.PAIEMENT.REPARTITIONS.REPARTITION minepdedRepartition = new PaymentDocument.CONTENT.PAIEMENT.REPARTITIONS.REPARTITION();
                minepdedRepartition.setCODEBENIF(getSpecificProcessParam(cargProcess, CargConstants.CARG_VT_MINEPDED_BENEFICIARE_MINEPDED_CODE, "MINEPDED"));
                minepdedRepartition.setTYPEDOSSIER(getSpecificProcessParam(cargProcess, CargConstants.CARG_VT_MINEPDED_FEES_BILL_TYPE, "VT2601"));
                minepdedRepartition.setMONTANTSTRING(vtMinepdedAmountFeesMinepdedPart.toPlainString());
                current.getPaiement().getREPARTITIONS().getREPARTITION().add(minepdedRepartition);
            }
            if (vtMinepdedFeesAmountGucePart != null && vtMinepdedFeesAmountGucePart.compareTo(BigDecimal.ZERO) > 0) {
                PaymentDocument.CONTENT.PAIEMENT.REPARTITIONS.REPARTITION minepdedRepartition = new PaymentDocument.CONTENT.PAIEMENT.REPARTITIONS.REPARTITION();
                minepdedRepartition.setCODEBENIF(getSpecificProcessParam(cargProcess, CargConstants.CARG_VT_MINEPDED_BENEFICIARE_GUCE_CODE, "GUCE"));
                minepdedRepartition.setTYPEDOSSIER(getSpecificProcessParam(cargProcess, CargConstants.CARG_VT_MINEPDED_GUCE_FEES_BILL_TYPE, "VT2601"));
                minepdedRepartition.setMONTANTSTRING(vtMinepdedFeesAmountGucePart.toPlainString());
                current.getPaiement().getREPARTITIONS().getREPARTITION().add(minepdedRepartition);
            }
        }
    }
    
    protected void prepareSend() {
        if (checkRequestConformity()) {
            calculateVtMinepdedFees();
            generatePaymentData();
        }
    }
    
}
