package org.guce.web.process.atm.controllers.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.apache.commons.lang.StringUtils;
import org.guce.core.entities.CoreAttachmenttype;
import org.guce.process.atm.ATMConstant;
import org.guce.web.process.atm.controllers.ATMController;


public abstract class ATMControllerImpl extends ATMController {
    
    protected String selectedTypeAvisTechnique = "";

   

    protected HashMap<String, String> officeSelected;

    
   protected List<CoreAttachmenttype> attachmenttypes; 
   
   private Logger LOGGER = Logger.getLogger(ATMControllerImpl.class.getName());

   
   public void loadProductType(){
    
      if(application.getStringParam("typeAtech")==null){
         selectedTypeAvisTechnique = (current != null && current.getTypeAtech() != null)?current.getTypeAtech().getLabel():null;
       }else{
         selectedTypeAvisTechnique = application.getStringParam("typeAtech");
         //select typeAvisT by Label 
       //  current.setAvisTech(selectedTypeAvisTechnique);
         LOGGER.log(Level.INFO, "ATMControllerImpl -- Type avis Technique " + selectedTypeAvisTechnique);
       }     
      if(selectedTypeAvisTechnique!=null){this.initAttachement(selectedTypeAvisTechnique);}  
    }
    
    
      public void initAttachement(String productTypeCode){
      
        this.attachmenttypes = new ArrayList<>();
        List<CoreAttachmenttype> attachment = this.getAvaibleAttachmentType();
        if(productTypeCode != null){
             for(CoreAttachmenttype coreAttachmenttype : attachment){
            if (productTypeCode.equals(ATMConstant.TRAITEMENT_STOKAGE) && Arrays.asList("PCCCEC", "DTarif","AT").contains(coreAttachmenttype.getAttachementtypeid().trim())){
                this.attachmenttypes.add(coreAttachmenttype);
            }else if (productTypeCode.equals(ATMConstant.INGREDIENT_ADDITIFS) && Arrays.asList("PCCCEC").contains(coreAttachmenttype.getAttachementtypeid().trim())){    
                    this.attachmenttypes.add(coreAttachmenttype);
            } else if (productTypeCode.equals(ATMConstant.MATERIEL_EQUIPEMENT) && Arrays.asList("AT").contains(coreAttachmenttype.getAttachementtypeid().trim())){    
                    this.attachmenttypes.add(coreAttachmenttype);
                }
        }
        }
  }
      
      
      
    public HashMap<String, String> generateOffice(String avisTechCode){
     this.officeSelected = new HashMap<String, String>();
     selectedTypeAvisTechnique = this.selectedTypeAvisTechnique;
     if(avisTechCode != null){
         if(avisTechCode.equals(ATMConstant.TRAITEMENT_STOKAGE)){
            this.officeSelected.put("DPAIH", "DPAIH");
         }else if (avisTechCode.equals(ATMConstant.INGREDIENT_ADDITIFS)){
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
