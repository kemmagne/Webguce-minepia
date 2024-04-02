/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guce.web.process.atm.components;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.guce.core.ejb.facade.interfaces.CoreCADFacadeLocal;
import org.guce.core.entities.CoreCAD;
import org.guce.core.entities.CoreGood;
import org.guce.core.entities.CoreRecord;
import org.guce.core.entities.util.LookupUtil;
import org.guce.core.services.ApplicationService;
import org.guce.rep.ejb.facade.interfaces.RepAmmFacadeLocal;
import org.guce.rep.ejb.facade.interfaces.RepPositionTarifaireFacadeLocal;
import org.guce.rep.ejb.facade.interfaces.RepUnitsFacadeLocal;
import org.guce.rep.entities.RepAmm;
import org.guce.rep.entities.RepPositionTarifaire;
import org.guce.rep.entities.RepUnit;
import org.guce.rep.services.repServiceLocal;
import org.guce.web.component.WebguceDefaultComponent;
import org.guce.web.core.util.JsfUtil;
import org.guce.web.core.util.StringSimplifier;
import org.guce.web.process.atm.services.impl.PositionTarifaireGoodAtmServiceImpl;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author NGC
 */
@FacesComponent("org.guce.web.process.atm.components.Goods")
public class Goods extends WebguceDefaultComponent implements NamingContainer {

    public static NumberFormat format = new DecimalFormat("#");

    private CoreGood produit;

    private repServiceLocal repService;

    private final RepPositionTarifaireFacadeLocal pFacade;

    private final RepUnitsFacadeLocal unitFace;

    private CoreCADFacadeLocal cadFacadeLocal;

    private List<CoreCAD> cadList;

    private final StreamedContent file2;
    
    private Logger LOGGER = Logger.getLogger(Goods.class.getName());

    private int type;

    private final RepAmmFacadeLocal ammService;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Goods() {
        produit = new CoreGood();
        produit.setWeight(BigDecimal.ZERO);
        pFacade = (RepPositionTarifaireFacadeLocal) LookupUtil.RepLookup("RepPositionTarifaireFacade");
        unitFace = (RepUnitsFacadeLocal) LookupUtil.RepLookup("RepUnitsFacade");
        ammService = (RepAmmFacadeLocal) LookupUtil.RepLookup("RepAmmFacade");
        InputStream stream = getClass().getResourceAsStream("templates.xls");
        file2 = new DefaultStreamedContent(stream, "application/octet-stream", "templates.xls");
    }

    public StreamedContent getFile2() {
        return file2;
    }

    @Override
    public repServiceLocal getRepService() {
        if (repService == null) {
            repService = (repServiceLocal) LookupUtil.lookup("repService", "rep.services");
        }
        return repService;
    }

    public CoreGood getProduit() {
        if (produit == null) {
            newProduct();
        }
        return produit;
    }

    public void setProduit(CoreGood produit) {
        this.produit = produit;
    }

//    @Override
//    public List<RepPositionTarifaire> getPositionList() {
//        return getRepService().positionList();
//    }

    @Override
    public List<RepUnit> getUniteList() {
        return getRepService().unitList();
    }

    @Override
    public List<RepAmm> getAmmList() {
        return getRepService().ammList();
    }

    public void newProduct() {
        produit = new CoreGood();
        produit.setWeight(BigDecimal.ZERO);
        produit.setRecordId((CoreRecord) getAttributes().get("parentClass"));
    }

    public void suppProduit() {
        produit.setRecordId(null);
        List l = (List) getAttributes().get("value");
        l.remove(produit);
        for (int i = 1; i <= l.size(); i++) {
            ((CoreGood) l.get(i - 1)).setLineNumber(i);
        }
        RequestContext.getCurrentInstance().update(getClientId() + ":GoodList");
        invokeGoodChangeProvidedMethod();

    }

    public void addProduct() {
        if ((produit != null && produit.gethSCode() != null) && (!StringUtils.isBlank(produit.gethSCode().getCode()))) {
            RequestContext.getCurrentInstance().execute("PF('pDialog_" + getId() + "').hide()");
        } else {
            if (isRequired("CodeTarif",
                    getAttributes().get("requiredList") != null ? (String) getAttributes().get("requiredList") : "",
                    getAttributes().get("required") != null ? Boolean.valueOf((String) getAttributes().get("required")) : false)) {
                JsfUtil.addErrorMessage("Position obligatoire");
                RequestContext.getCurrentInstance().update("goodform:growl");
                return;
            } else {
                produit.sethSCode(null);
                RequestContext.getCurrentInstance().execute("PF('pDialog_" + getId() + "').hide()");
                RequestContext.getCurrentInstance().update(getClientId() + ":GoodList");
            }
        }
        List l = (List) getAttributes().get("value");
        produit.setDescription(StringSimplifier.getInstance().stripDiacritics(produit.getDescription()));
        if (produit.gethSCode() != null) {
            produit.gethSCode().setLibelle(StringSimplifier.getInstance().stripDiacritics(produit.gethSCode().getLibelle()));
        }
        if (!l.contains(produit)) {
            l.add(produit);
        }
        for (int i = 1; i <= l.size(); i++) {
            ((CoreGood) l.get(i - 1)).setLineNumber(i);
        }
        invokeGoodChangeProvidedMethod();
    }

    @Override
    public List<CoreCAD> getCadList() {
        if (cadList == null) {
            cadList = getCadFacadeLocal().findAll();
        }
        return cadList;
    }

    @Override
    public CoreCADFacadeLocal getCadFacadeLocal() {
        if (cadFacadeLocal == null) {
            cadFacadeLocal = (CoreCADFacadeLocal) LookupUtil.CoreLookup("CoreCADFacade");
        }
        return cadFacadeLocal;
    }

    public void importerFile(byte[] data) {
        RequestContext context = RequestContext.getCurrentInstance();
        try {
            Workbook my_xlsx_workbook;
            try {
                my_xlsx_workbook = new HSSFWorkbook(new ByteArrayInputStream(data));
            } catch (Exception e) {
                my_xlsx_workbook = new XSSFWorkbook(new ByteArrayInputStream(data));
            }
            FormulaEvaluator evaluator = my_xlsx_workbook.getCreationHelper().createFormulaEvaluator();
            Sheet my_worksheet = my_xlsx_workbook.getSheetAt(0);
            int rowCount = my_worksheet.getPhysicalNumberOfRows();
            List l = (List) getAttributes().get("value");
            for (int i = 2; i < rowCount; i++) {
                Row row = my_worksheet.getRow(i);
                if (row != null) {
                    CoreGood good = new CoreGood();
                    if (row.getCell(2) != null) {
                        String hsCode = getStringValueNoSpace(row.getCell(2), evaluator);
                        String hsc = hsCode == null ? "" : hsCode.replaceAll("\\.", "");
                        if (hsc.length() == 10) {
                            hsc = hsc + "0";
                        } else if (hsc.length() == 9) {
                            hsc = hsc + "00";
                        } else if (hsc.length() == 8) {
                            hsc = hsc + "000";
                        } else if (hsc.length() == 7) {
                            hsc = hsc + "0000";
                        } else if (hsc.length() == 6) {
                            hsc = hsc + "00000";
                        }
                        hsCode = hsc;
//                        System.out.println("hscode : " + hsCode);
                        if (!StringUtils.isEmpty(hsCode)) {
                            good.sethSCode(pFacade.find(hsCode));
                            if (good.gethSCode() == null) {
                                good.sethSCode(new RepPositionTarifaire(hsCode));
                            }
                        }
                        if (row.getCell(6) != null) {
                            good.setQuantiteBigDecimal(getNumericValue(row.getCell(6), evaluator));
                        }
                        if (row.getCell(7) != null) {
                            String unit = getStringValue(row.getCell(7), evaluator);
//                            good.setUnit(getStringValue(row.getCell(7), evaluator));
                            if (!StringUtils.isEmpty(unit)) {
                                RepUnit hh = unitFace.find(unit);
                                good.setRepUnit(hh);
                            }
                        }
                        if (row.getCell(9) != null) {
                            good.setFobValueInCurrency(getNumericValue(row.getCell(9), evaluator));
                        }
                        if (row.getCell(17) != null) {
                            String st = StringSimplifier.getInstance().stripDiacritics(getStringValue(row.getCell(17), evaluator));
                            st = st.substring(0, Math.min(st.length(), 1499));
                            good.setDescription(st);
                        }
                        if (row.getCell(18) != null) {
                            String amm = getStringValue(row.getCell(18), evaluator);
                            if (!StringUtils.isEmpty(amm)) {
                                RepAmm repAmm = ammService.find(amm);
                                good.setAmmCode(repAmm != null ? repAmm.getCode() : null);
                            }

                        }
                        good.setWeight(BigDecimal.ZERO);
                        produit.setRecordId((CoreRecord) getAttributes().get("parentClass"));
                        if (!l.contains(good)) {
                            l.add(good);
                            good.setLineNumber(l.size());
                        }
                    }
                }
            }
//            System.out.println(my_worksheet.getPhysicalNumberOfRows() + " - " + my_worksheet.getLastRowNum());
            JsfUtil.addSuccessMessage("Fichier importé avec succès");
            context.execute("PF('updloadDialog_" + getId() + "').hide()");
            context.update(getClientId() + ":GoodList");
            invokeGoodChangeProvidedMethod();

        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Impossible d'importer le fichier");
            JsfUtil.addErrorMessage(ex, ex.getMessage());
            Logger.getLogger(Goods.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }

    }

    private UploadedFile file;

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public void deleteGood() {
        RequestContext context = RequestContext.getCurrentInstance();
        List l = (List) getAttributes().get("value");
        for (Object o : getSelectedList()) {
            l.remove(o);
        }
        for (int i = 1; i <= l.size(); i++) {
            ((CoreGood) l.get(i - 1)).setLineNumber(i);
        }
        getSelectedList().clear();
        JsfUtil.addSuccessMessage("Elements supprimés");
        context.execute("PF('deleteConfirmDialog_" + getId() + "').hide()");
        context.update(getClientId() + ":GoodList");
        invokeGoodChangeProvidedMethod();
    }

    public void upload() {
//        System.out.println("file = " + file);
        if (file != null) {
            importerFile(file.getContents());
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        file = event.getFile();
    }

    @Override
    public String getFamily() {
        return ("javax.faces.NamingContainer");
    }

   public Object lookup(String service) {
     try {
            InitialContext c = new InitialContext();
            return c.lookup("java:global/" + ApplicationService.getContext() + "/webguce/" 
                                        + service + "!org.guce.web.process.atm.services.impl." + service);
        } catch (NamingException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }
    
     public PositionTarifaireGoodAtmServiceImpl getRepGoodAtm(){
        return (PositionTarifaireGoodAtmServiceImpl)lookup("PositionTarifaireGoodAtmServiceImpl");
    }
    
    @Override
    public List<RepPositionTarifaire> getPositionList() {
        String category = (String) getAttributes().get("category");
        if(Objects.nonNull(category)){
          LOGGER.log(Level.INFO, "Goods -- Category choosen " + category);
  
            // si on a categoriser le type de marchandise
            switch (category) {
                case "ATM01":
                    //Engrais
                    return  getRepGoodAtm().findActiveProduitsHalieutiques();
                case "ATM02":
                    //Pesticide
                    return  getRepGoodAtm().findActiveIngredientsAdditifs();
                default:
                    //Appareil Traitement
                    return  getRepGoodAtm().findActiveMaterialEquipment();
            }
        }else{
            //si non on retourne tous les marchandises disponible
        // return getRepService().positionList();
        return (List<RepPositionTarifaire>) new RepPositionTarifaire();
        } 
    }
    
    
    @Override
    public Object saveState(FacesContext context) {
        Map m = new HashMap();
        m.put("object", super.saveState(context));
        m.put("produit", produit);
        m.put("file", file);
        return m;
    }

    @Override
    public void restoreState(FacesContext context, Object state) {
        Map m = (Map) state;
        produit = (CoreGood) m.get("produit");
        if (produit == null) {
            produit = new CoreGood();
        }
        if (m.get("file") != null) {
            file = (UploadedFile) m.get("file");
        }
        super.restoreState(context, m.get("object"));
    }

    private String getStringValueNoSpace(Cell cell, FormulaEvaluator ev) {
        return getStringValue(cell, ev).trim().replaceAll("\\s", "");
    }

    private BigDecimal getNumericValue(Cell cell, FormulaEvaluator ev) {
//        System.out.println("getNumericValue type = " + cell.getCellType());
        BigDecimal rs = BigDecimal.ZERO;
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BLANK:
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                rs = cell.getBooleanCellValue() ? BigDecimal.ONE : BigDecimal.ZERO;
                break;
            case Cell.CELL_TYPE_ERROR:
                rs = BigDecimal.ZERO;
                break;
            case Cell.CELL_TYPE_FORMULA:
                CellValue value = ev.evaluate(cell);
                rs = BigDecimal.valueOf(value.getNumberValue()).setScale(2, RoundingMode.HALF_EVEN);
//                rs.round(MathContext.DECIMAL32)
                break;
            case Cell.CELL_TYPE_NUMERIC:
                rs = BigDecimal.valueOf(cell.getNumericCellValue()).setScale(2, RoundingMode.HALF_EVEN);
                break;
            case Cell.CELL_TYPE_STRING:
                rs = BigDecimal.valueOf(Double.parseDouble(cell.getStringCellValue().trim().replaceAll("\\s", ""))).setScale(2, RoundingMode.HALF_EVEN);
                break;
            default:
                break;
        }
        return rs;
    }

    private String getStringValue(Cell cell, FormulaEvaluator ev) {
        String rs = "";
//        System.out.println("StringValue type = " + cell.getCellType());
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BLANK:
                rs = "";
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                rs = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_ERROR:
                rs = "";
                break;
            case Cell.CELL_TYPE_FORMULA:
                break;
            case Cell.CELL_TYPE_NUMERIC:
                rs = format.format(cell.getNumericCellValue());
                break;
            case Cell.CELL_TYPE_STRING:
                rs = cell.getStringCellValue();
                break;
            default:
                rs = "";
                break;
        }
        return rs;
    }

    private void invokeGoodChangeProvidedMethod() {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            ELContext elContext = context.getELContext();
            if (this.getAttributes().containsKey("goodChangeListener") && this.getAttributes().get("goodChangeListener") != null && org.apache.commons.lang3.StringUtils.isNotEmpty(this.getAttributes().get("goodChangeListener").toString())) {
                MethodExpression me = (MethodExpression) this.getAttributes().get("goodChangeListener");
                if (me != null) {
                    me.invoke(elContext, null);
                }
                if (this.getAttributes().containsKey("updateList") && this.getAttributes().get("updateList") != null && org.apache.commons.lang3.StringUtils.isNotEmpty(this.getAttributes().get("updateList").toString())) {
                    String updateListId = this.getAttributes().get("updateList").toString();
                    RequestContext.getCurrentInstance().update(updateListId);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "", ex);
        }
    }

}


//import java.io.ByteArrayInputStream;
//import java.io.InputStream;
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.text.DecimalFormat;
//import java.text.NumberFormat;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import javax.ejb.EJB;
//import javax.el.ELContext;
//import javax.el.MethodExpression;
//import javax.faces.bean.ManagedProperty;
//import javax.faces.component.FacesComponent;
//import javax.faces.component.NamingContainer;
//import javax.faces.context.FacesContext;
//import javax.inject.Inject;
//import javax.naming.InitialContext;
//import javax.naming.NamingException;
//import org.apache.commons.lang.StringUtils;
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.ss.usermodel.Cell;
//import org.apache.poi.ss.usermodel.CellValue;
//import org.apache.poi.ss.usermodel.FormulaEvaluator;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.guce.core.ejb.facade.interfaces.CoreCADFacadeLocal;
//import org.guce.core.entities.CoreCAD;
//import org.guce.core.entities.CoreGood;
//import org.guce.core.entities.CoreRecord;
//import org.guce.core.entities.util.LookupUtil;
//import org.guce.core.services.ApplicationService;
//
//import org.guce.rep.ejb.facade.interfaces.RepAmmFacadeLocal;
//import org.guce.rep.ejb.facade.interfaces.RepPositionTarifaireFacadeLocal;
//import org.guce.rep.ejb.facade.interfaces.RepUnitsFacadeLocal;
//import org.guce.rep.entities.RepAmm;
//import org.guce.rep.entities.RepPositionTarifaire;
//import org.guce.rep.entities.RepUnit;
//import org.guce.rep.services.repServiceLocal;
//import org.guce.web.component.WebguceDefaultComponent;
//import org.guce.web.core.util.JsfUtil;
//import static org.guce.web.core.util.JsfUtil.lookup;
//import org.guce.web.core.util.StringSimplifier;
//import org.guce.web.process.atm.controllers.impl.ATMControllerImpl;
//import org.guce.web.process.atm.services.impl.ATMRegistrationMessageServiceImpl;
//import org.guce.web.process.atm.services.impl.PositionTarifaireGoodAtmServiceImpl;
//import org.primefaces.context.RequestContext;
//import org.primefaces.event.FileUploadEvent;
//import org.primefaces.model.DefaultStreamedContent;
//import org.primefaces.model.StreamedContent;
//import org.primefaces.model.UploadedFile;
///**
// *
// * @author NGC
// */
//@FacesComponent("org.guce.web.process.atm.components.Goods")
//public class Goods extends WebguceDefaultComponent implements NamingContainer{
//    
//    public static NumberFormat format = new DecimalFormat("#");
//
//    private CoreGood produit;
//
//    private repServiceLocal repService;
//
//    private final RepPositionTarifaireFacadeLocal pFacade;
//
//    private final RepUnitsFacadeLocal unitFace;
//    
//
//
//    private CoreCADFacadeLocal cadFacadeLocal;
//
//    private List<CoreCAD> cadList;
//
//    private final StreamedContent file2;
//
//    private int type;
//
//    private final RepAmmFacadeLocal ammService;
//    
//    private Logger LOGGER = Logger.getLogger(Goods.class.getName());
//
//    public int getType() {
//        return type;
//    }
//
//    public void setType(int type) {
//        this.type = type;
//    }
//    
//     public StreamedContent getFile2() {
//        return file2;
//    }
//     
//      private UploadedFile file;
//
//    public UploadedFile getFile() {
//        return file;
//    }
//
//    public void setFile(UploadedFile file) {
//        this.file = file;
//    } 
//     
//     
//      public Object lookup(String service) {
//        try {
//            InitialContext c = new InitialContext();
//            return c.lookup("java:global/" + ApplicationService.getContext() + "/webguce/" 
//                                        + service + "!org.guce.web.process.atm.services.impl." + service);
//        } catch (NamingException ex) {
//            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
//        }
//        return null;
//    }
//    
//     public PositionTarifaireGoodAtmServiceImpl getRepGoodAtm(){
//        return (PositionTarifaireGoodAtmServiceImpl)lookup("PositionTarifaireGoodAtmServiceImpl");
//    }
//
//    @Override
//    public repServiceLocal getRepService() {
//        if (repService == null) {
//            repService = (repServiceLocal) LookupUtil.lookup("repService", "rep.services");
//        }
//        return repService;
//    }
//
//        public Goods() {
//        produit = new CoreGood();
//        produit.setWeight(BigDecimal.ZERO);
//        pFacade = (RepPositionTarifaireFacadeLocal) LookupUtil.RepLookup("RepPositionTarifaireFacade");
//        unitFace = (RepUnitsFacadeLocal) LookupUtil.RepLookup("RepUnitsFacade");
//        ammService = (RepAmmFacadeLocal) LookupUtil.RepLookup("RepAmmFacade");
//        InputStream stream = getClass().getResourceAsStream("templates.xls");
//        file2 = new DefaultStreamedContent(stream, "application/octet-stream", "templates.xls");
//    }
//        
//      @Override
//    public List<RepPositionTarifaire> getPositionList() {
//        String category = (String) getAttributes().get("category");
//        if(Objects.nonNull(category)){
//          LOGGER.log(Level.INFO, "Goods -- Category choosen " + category);
//  
//            // si on a categoriser le type de marchandise
//            switch (category) {
//                case "ATM01":
//                    //Engrais
//                    return  getRepGoodAtm().findActiveProduitsHalieutiques();
//                case "ATM02":
//                    //Pesticide
//                    return  getRepGoodAtm().findActiveIngredientsAdditifs();
//                default:
//                    //Appareil Traitement
//                    return  getRepGoodAtm().findActiveMaterialEquipment();
//            }
//        }else{
//            //si non on retourne tous les marchandises disponible
//        // return getRepService().positionList();
//        return (List<RepPositionTarifaire>) new RepPositionTarifaire();
//        } 
//    } 
//    
//    public void addProduct() {
//        if ((produit != null && produit.gethSCode() != null) && (!StringUtils.isBlank(produit.gethSCode().getCode()))) {
//            RequestContext.getCurrentInstance().execute("PF('pDialog_" + getId() + "').hide()");
//        } else {
//            if (isRequired("CodeTarif",
//                    getAttributes().get("requiredList") != null ? (String) getAttributes().get("requiredList") : "",
//                    getAttributes().get("required") != null ? Boolean.valueOf((String) getAttributes().get("required")) : false)) {
//                JsfUtil.addErrorMessage("Position obligatoire");
//                RequestContext.getCurrentInstance().update("goodform:growl");
//                return;
//            } else {
//                produit.sethSCode(null);
//                RequestContext.getCurrentInstance().execute("PF('pDialog_" + getId() + "').hide()");
//                RequestContext.getCurrentInstance().update(getClientId() + ":GoodList");
//            }
//        }
//        if(produit.getQuantite() == 0)   {
//                JsfUtil.addErrorMessage("La quantité doit être superieure à 0"); return;
//            }
//        List l = (List) getAttributes().get("value");
//        produit.setDescription(StringSimplifier.getInstance().stripDiacritics(produit.getDescription()));
//        if (produit.gethSCode() != null) {
//            produit.gethSCode().setLibelle(StringSimplifier.getInstance().stripDiacritics(produit.gethSCode().getLibelle()));
//        }
//        if (!l.contains(produit)) {
//            l.add(produit);
//        }
//        for (int i = 1; i <= l.size(); i++) {
//            ((CoreGood) l.get(i - 1)).setLineNumber(i);
//        }
//        invokeGoodChangeProvidedMethod();
//    }
//
//    @Override
//    public List<CoreCAD> getCadList() {
//        if (cadList == null) {
//            cadList = getCadFacadeLocal().findAll();
//        }
//        return cadList;
//    }
//
//    @Override
//    public CoreCADFacadeLocal getCadFacadeLocal() {
//        if (cadFacadeLocal == null) {
//            cadFacadeLocal = (CoreCADFacadeLocal) LookupUtil.CoreLookup("CoreCADFacade");
//        }
//        return cadFacadeLocal;
//    }
//    
//    private void invokeGoodChangeProvidedMethod() {
//        try {
//            FacesContext context = FacesContext.getCurrentInstance();
//            ELContext elContext = context.getELContext();
//            if (this.getAttributes().containsKey("goodChangeListener") && this.getAttributes().get("goodChangeListener") != null && org.apache.commons.lang3.StringUtils.isNotEmpty(this.getAttributes().get("goodChangeListener").toString())) {
//                MethodExpression me = (MethodExpression) this.getAttributes().get("goodChangeListener");
//                if (me != null) {
//                    me.invoke(elContext, null);
//                }
//                if (this.getAttributes().containsKey("updateList") && this.getAttributes().get("updateList") != null && org.apache.commons.lang3.StringUtils.isNotEmpty(this.getAttributes().get("updateList").toString())) {
//                    String updateListId = this.getAttributes().get("updateList").toString();
//                    RequestContext.getCurrentInstance().update(updateListId);
//                }
//            }
//        } catch (Exception ex) {
//            Logger.getLogger(getClass().getName()).log(Level.WARNING, "", ex);
//        }
//    }
//    
//     @Override
//    public Object saveState(FacesContext context) {
//        Map m = new HashMap();
//        m.put("object", super.saveState(context));
//        m.put("produit", produit);
//       
//        m.put("file", file);
//        return m;
//    }
//
//    @Override
//    public void restoreState(FacesContext context, Object state) {
//        Map m = (Map) state;
//        produit = (CoreGood) m.get("produit");
//        if (produit == null) {
//            produit = new CoreGood();
//        }
//        if (m.get("file") != null) {
//            file = (UploadedFile) m.get("file");
//        }
//        super.restoreState(context, m.get("object"));
//    }
//
//      private String getStringValue(Cell cell, FormulaEvaluator ev) {
//        String rs = "";
////        System.out.println("StringValue type = " + cell.getCellType());
//        switch (cell.getCellType()) {
//            case Cell.CELL_TYPE_BLANK:
//                rs = "";
//                break;
//            case Cell.CELL_TYPE_BOOLEAN:
//                rs = String.valueOf(cell.getBooleanCellValue());
//                break;
//            case Cell.CELL_TYPE_ERROR:
//                rs = "";
//                break;
//            case Cell.CELL_TYPE_FORMULA:
//                break;
//            case Cell.CELL_TYPE_NUMERIC:
//                rs = format.format(cell.getNumericCellValue());
//                break;
//            case Cell.CELL_TYPE_STRING:
//                rs = cell.getStringCellValue();
//                break;
//            default:
//                rs = "";
//                break;
//        }
//        return rs;
//    }
//    
//    private String getStringValueNoSpace(Cell cell, FormulaEvaluator ev) {
//        return getStringValue(cell, ev).trim().replaceAll("\\s", "");
//    }
//    
//}
