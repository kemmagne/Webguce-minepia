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
