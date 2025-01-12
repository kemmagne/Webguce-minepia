package org.guce.process.atm;

public interface ATMConstant {
    
   //String BILL_MONTANT_TTC = "atm.config.stampfee";
   
   String BILL_MONTANT_TTC = "1500";
   
   String BILL_MONTANT_HT ="1500" ;
   String BILL_BENEFICIAIRE = "MINEPIA" ;

   String RENEW= "RENEW";
    /**
     * process code */
    String PROCESS_CODE = "ATM";

    /**
     * form request code */
    String FORM_REQUEST = "ATM01";

    /**
     * form response additionnal request code */
    String FORM_RESPONSE_CI = "ATM11";
    
    
      /**
     * form response additionnal renouvellement request code */
    String FORM_RENOUVELLEMENT_RESPONSE_CI = "ATM11R";

    /**
     * form response code */
    String FORM_CONSULTATION = "ATM";

    /**
     * form modification request code */
    String FORM_MODIFICATION_REQUEST = "ATM09";
    
    /**
     * form modification request code */
    String FORM_RENOUVELLEMENT_REQUEST = "ATM01R";
    
    /**
     * form renouvelement consultation code */
    String FORM_MODIFICATION_CONSULTATION = "ATMM";

    /**
     * form modification consultation code */
    String FORM_RENOUVELLEMENT_CONSULTATION = "ATMMR";

    /**
     * form modification reject code */
    String FORM_MODIFICATION_REJECT = "ATM03";
    
    /**
     * form renouvellement reject code */
    String FORM_RENOUVELLEMENT_REJECT = "ATM03R";

    /**
     * form invoice code */
    String FORM_INVOICE = "ATM601";

    /**
     * form payment code */
    String FORM_PAYMENT = "ATM602";

    /**
     * processing request code */
    String PROCESSING_REQUEST = "ATM01";

    /**
     * processing additionnal information request code */
    String PROCESSING_CI = "ATM02";

    /**
     * processing response additionnal request code */
    String PROCESSING_RESPONSE_CI = "ATM11";
    
    /**
     * processing response additionnal request code for renouvellement 
     */
    
    String PROCESSING_RENOUVELLEMENT_RESPONSE_CI = "ATM11R";

    /**
     * processing validation code */
    String PROCESSING_VALIDATION = "ATM04";

    /**
     * processing modification request code */
    String PROCESSING_MODIFICATION_REQUEST = "ATM09";
    
    /**
     * processing renouvellement request code */
    String PROCESSING_RENOUVELLEMENT_REQUEST = "ATM01R";

    /**
     * processing modification validation code */
    String PROCESSING_MODIFICATION_VALIDATION = "ATM10";
    
    /**
     * processing renouvellement validation code */
    String PROCESSING_RENOUVELLEMENT_VALIDATION = "ATM10R";

    /**
     * processing modification reject code */
    String PROCESSING_MODIFICATION_REJECT = "ATM03";
    
    /**
     * processing renouvellement reject code */
    String PROCESSING_RENOUVELLEMENT_REJECT = "ATM03R";

    /**
     * processing invoice code */
    String PROCESSING_INVOICE = "ATM601";

    /**
     * processing payment code */
    String PROCESSING_PAYMENT = "ATM602";

    /**
     * processing invoice code */
    String PROCESSING_CONSULTATION = "ATM";

    /**
     * processing payment code */
    String PROCESSING_CONSULTATION_MODIFICATION = "ATMM";
    
    /**
     * processing payment code */
    String PROCESSING_CONSULTATION_RENOUVELLEMENT = "ATMMR";

    /**
     * entity name */
    String REGISTRATION_RECORD_ENTITY = "ATMRegistration";
    
    String TRAITEMENT_STOKAGE = "ATM01";
    
    String INGREDIENT_ADDITIFS = "ATM02";
    
    String MATERIEL_EQUIPEMENT = "ATM03";
    
    
     String OFFICE_CODES = "atm.config.bureau";

    
    public static String MINEPIA = "MINEPIA";

  static final String[] UNITS = {"", "un", "deux", "trois", "quatre", "cinq", "six", "sept", "huit", "neuf", "dix", "onze", "douze", "treize","quatorze", "quinze", "seize", "dix-sept", "dix-huit", "dix-neuf"};
       static final String[] TENS = {"", "", "vingt", "trente", "quarante", "cinquante", "soixante", "soixante-dix", "quatre-vingt", "quatre-vingt-dix"};
}
