package org.guce.process.dem;

public interface DEMConstant {

   String BILL_MONTANT_TTC = "1500";

    String BILL_MONTANT_HT ="1500" ;

  String BILL_BENEFICIAIRE = "MINCOMMERCE" ;
  
    /**
     * process code */
    String PROCESS_CODE = "DEM";

    /**
     * form request code */
    String FORM_REQUEST = "DEM01";

    /**
     * form response additionnal request code */
    String FORM_RESPONSE_CI = "DEM11";

    /**
     * form response code */
    String FORM_CONSULTATION = "DEM";

    /**
     * form modification request code */
    String FORM_MODIFICATION_REQUEST = "DEM09";

    /**
     * form modification consultation code */
    String FORM_MODIFICATION_CONSULTATION = "DEMM";

    /**
     * form modification reject code */
    String FORM_MODIFICATION_REJECT = "DEM03";

    /**
     * form invoice code */
    String FORM_INVOICE = "DEM601";

    /**
     * form payment code */
    String FORM_PAYMENT = "DEM602";

    /**
     * processing request code */
    String PROCESSING_REQUEST = "DEM01";

    /**
     * processing additionnal information request code */
    String PROCESSING_CI = "DEM02";

    /**
     * processing response additionnal request code */
    String PROCESSING_RESPONSE_CI = "DEM11";

    /**
     * processing validation code */
    String PROCESSING_VALIDATION = "DEM04";

    /**
     * processing modification request code */
    String PROCESSING_MODIFICATION_REQUEST = "DEM09";

    /**
     * processing modification validation code */
    String PROCESSING_MODIFICATION_VALIDATION = "DEM10";

    /**
     * processing modification reject code */
    String PROCESSING_MODIFICATION_REJECT = "DEM03";

    /**
     * processing invoice code */
    String PROCESSING_INVOICE = "DEM601";

    /**
     * processing payment code */
    String PROCESSING_PAYMENT = "DEM602";

    /**
     * processing invoice code */
    String PROCESSING_CONSULTATION = "DEM";

    /**
     * processing payment code */
    String PROCESSING_CONSULTATION_MODIFICATION = "DEMM";

    /**
     * entity name */
    String REGISTRATION_RECORD_ENTITY = "DEMRegistration";
    
    String DEFAULT_HS_CODE = "85369000000";
    
    String DEFAULT_UNIT = "KG";
}
