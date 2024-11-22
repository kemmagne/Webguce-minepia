package org.guce.web.process.atm.controllers.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.apache.commons.lang.StringUtils;
import org.guce.core.entities.CoreAttachment;
import org.guce.core.entities.CoreAttachmenttype;
import org.guce.process.atm.ATMConstant;
import org.guce.process.atm.TypeAttachement;
import org.guce.web.process.atm.controllers.ATMController;


public abstract class ATMControllerImpl extends ATMController {
    
    protected String selectedTypeAvisTechnique = "";

   

    protected HashMap<String, String> officeSelected;

    
   protected List<CoreAttachmenttype> attachmenttypes; 
   
   private Logger LOGGER = Logger.getLogger(ATMControllerImpl.class.getName());

   
   protected void loadProductType(){
  
       
        selectedTypeAvisTechnique = Objects.nonNull(current)  &&  Objects.nonNull(current.getTypeAtech() ) && !current.getTypeAtech().getCode().isEmpty() ? current.getTypeAtech().getCode() : application.getStringParam("typeAtech");

       
 
      if(selectedTypeAvisTechnique!=null){this.initAttachement(selectedTypeAvisTechnique);}  
    
   }  
    
      public void initAttachement(String productTypeCode){
      
        this.attachmenttypes = new ArrayList<>();
        List<CoreAttachmenttype> attachment = this.getAvaibleAttachmentType();
        if(productTypeCode != null){
             for(CoreAttachmenttype coreAttachmenttype : attachment){
            if (productTypeCode.equals(ATMConstant.TRAITEMENT_STOKAGE) && Arrays.asList("DTarif", "CCI", "AT", "CMP", "LI", "TPV", "QPA", "AUTRE").contains(coreAttachmenttype.getAttachementtypeid().trim())){
                this.attachmenttypes.add(coreAttachmenttype);
            }else if (productTypeCode.equals(ATMConstant.INGREDIENT_ADDITIFS) && Arrays.asList("DT", "PDCE", "PDDE", "PPEC", "CRT", "LPI", "RAA", "PCCCEC", "AUTRE").contains(coreAttachmenttype.getAttachementtypeid().trim())){    
                    this.attachmenttypes.add(coreAttachmenttype);
            } else if (productTypeCode.equals(ATMConstant.MATERIEL_EQUIPEMENT) && Arrays.asList("CDPP", "LMEI", "DT", "CRT", "RAA", "AUTRE").contains(coreAttachmenttype.getAttachementtypeid().trim())){    
                    this.attachmenttypes.add(coreAttachmenttype);
                }
        }
        }
  }
      
      
//      
//protected Map<String ,  Boolean>  checkValidityOfAttachment(List<CoreAttachment> attachements){
//   
//    int dTarifcount = 0;
//    int cciCount = 0;
//    int atCount = 0; 
//    int cmpCount = 0;
//    int liCount = 0; 
//    int tpvCount = 0;
//    int qpqCount = 0;  
//    int dtCount = 0;
//    int pdceCount = 0; 
//    int pddeCount = 0;
//    int ppecCount = 0; 
//    int crtCount = 0; 
//    int lpiCoumt = 0;
//    int raaCount = 0;
//    int pCCCECCount = 0;
//    int cDPPCount = 0;
//    int lMEICount = 0;
//
//         Map<String, Boolean> result = new HashMap<>();
//        if(attachements != null && !attachements.isEmpty()){
//            for(CoreAttachment c:attachements){
//                if(c.getPjType().getAttachementtypeid().equals(TypeAttachement.DTarif.name())){
//                   dTarifcount++;
//                }
//                if(c.getPjType().getAttachementtypeid().equals(TypeAttachement.CCI.name())){
//                    cciCount++;
//                }
//                if(c.getPjType().getAttachementtypeid().equals(TypeAttachement.AT.name())){
//                    atCount++;
//                }if(c.getPjType().getAttachementtypeid().equals(TypeAttachement.CMP.name())){
//                       cmpCount++;
//                }if(c.getPjType().getAttachementtypeid().equals(TypeAttachement.LI.name())){
//                    liCount++;
//                   }if(c.getPjType().getAttachementtypeid().equals(TypeAttachement.TPV.name())){
//                    tpvCount++;
//                } if(c.getPjType().getAttachementtypeid().equals(TypeAttachement.QPA.name())){
//                    qpqCount++;
//                }    if(c.getPjType().getAttachementtypeid().equals(TypeAttachement.DT.name())){
//                    dtCount++;
//                }  if(c.getPjType().getAttachementtypeid().equals(TypeAttachement.PDCE.name())){
//                    pdceCount++;
//                }  if(c.getPjType().getAttachementtypeid().equals(TypeAttachement.PDDE.name())){
//                    pddeCount++;
//                } if(c.getPjType().getAttachementtypeid().equals(TypeAttachement.PPEC.name())){
//                    ppecCount++;
//                } if(c.getPjType().getAttachementtypeid().equals(TypeAttachement.CRT.name())){
//                    crtCount++;
//                }if(c.getPjType().getAttachementtypeid().equals(TypeAttachement.LPI.name())){
//                    lpiCoumt++;
//                }if(c.getPjType().getAttachementtypeid().equals(TypeAttachement.RAA.name())){
//                    raaCount++;
//                }if(c.getPjType().getAttachementtypeid().equals(TypeAttachement.PCCCEC.name())){
//                    pCCCECCount++;
//                 }if(c.getPjType().getAttachementtypeid().equals(TypeAttachement.CDPP.name())){
//                   cDPPCount++;
//                 }if(c.getPjType().getAttachementtypeid().equals(TypeAttachement.LMEI.name())){
//                    lMEICount++;
//                }
//                
//            }
//                 result.put(TypeAttachement.DTarif.name(), dTarifcount>0);
//                 result.put(TypeAttachement.CCI.name(), cciCount>0);
//                 result.put(TypeAttachement.AT.name(), atCount >0);
//                 result.put(TypeAttachement.CMP.name(), cmpCount>0);
//                 result.put(TypeAttachement.LI.name(), liCount>0);
//                 result.put(TypeAttachement.TPV.name(), tpvCount>0);
//                 result.put(TypeAttachement.QPA.name(), qpqCount>0);
//                 result.put(TypeAttachement.DT.name(), dtCount>0);
//                 result.put(TypeAttachement.PDCE.name(), pdceCount>0);
//                 result.put(TypeAttachement.PDDE.name(), pddeCount>0);
//                 result.put(TypeAttachement.PPEC.name(), ppecCount>0);
//                 result.put(TypeAttachement.CRT.name(), crtCount>0);
//                 
//                 result.put(TypeAttachement.LPI.name(), lpiCoumt>0);
//                 result.put(TypeAttachement.RAA.name(), raaCount>0);
//                 result.put(TypeAttachement.PCCCEC.name(), pCCCECCount>0);
//                 result.put(TypeAttachement.CDPP.name(), cDPPCount>0);
//                 result.put(TypeAttachement.LMEI.name(), lMEICount>0);
//                 
//                return result;
//        }else{
//                return  null;
//        }
//    }
//      
      
      
      
      
      
     protected HashMap<String, String> generateOffice(String avisTechCode){
     this.officeSelected = new HashMap<String, String>();
     selectedTypeAvisTechnique = this.selectedTypeAvisTechnique;
     if(avisTechCode != null){
         if(selectedTypeAvisTechnique != null && selectedTypeAvisTechnique.equals(ATMConstant.TRAITEMENT_STOKAGE) || 
                 selectedTypeAvisTechnique.equals(ATMConstant.MATERIEL_EQUIPEMENT)){
            this.officeSelected.put("DPAIH", "DPAIH");
         }else if (selectedTypeAvisTechnique.equals(ATMConstant.INGREDIENT_ADDITIFS)){
        this.officeSelected.put("DPAAIE", "DPAAIE");
        }
     } 
     return this.officeSelected;
    }

   
  
    
    
   public HashMap<String, String> getOfficeSelected() {
       return officeSelected;
    }

   public void setOfficeSelected(HashMap<String, String> officeSelected) {
        this.officeSelected = officeSelected;
    }
      

     public List<CoreAttachmenttype> getAttachmenttypes() {
        return attachmenttypes;
    }

    public void setAttachmenttypes(List<CoreAttachmenttype> attachmenttypes) {
        this.attachmenttypes = attachmenttypes;
    }
    
     public String getSelectedTypeAvisTechnique() {
        return selectedTypeAvisTechnique;
    }

    public void setSelectedTypeAvisTechnique(String selectedTypeAvisTechnique) {
        this.selectedTypeAvisTechnique = selectedTypeAvisTechnique;
    }
  
    
    
    
    
}
