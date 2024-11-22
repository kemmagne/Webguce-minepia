/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guce.web.process.atm.util;

import java.util.HashMap;

/**
 *
 * @author NGC
 */
public class ATMAddArticleToAttachment {
    
      public static String  addArticleToAttachment(String Value){
       String valueMapped = "";
          // Create a HashMap
        HashMap<String, String> hashMap = new HashMap<>();

        // Add some key-value pairs to the HashMap
        hashMap.put("Demande de tarif", "La demande de tarif");
        hashMap.put("Autorisation de création", "L'autorisation de création");
        hashMap.put("Certificat de conformité", "Le certificat de conformité");
        hashMap.put("Certificats médicaux du personnel", "Les certificats médicaux du personnel");
        hashMap.put("Licence d’importation", "La licence d’importation");
        hashMap.put("Titre de patente valide", "Le titre de patente valide");
        hashMap.put("Quittance de paiement des frais afférents", "La quittance de paiement des frais afférents");
        hashMap.put("Photocopie de la décision de création de l’établissement", "La photocopie de la décision de création de l’établissement");
        hashMap.put("Photocopie de la décision d’ouverture de l’établissement", "La photocopie de la décision d’ouverture de l’établissement");
        hashMap.put("Photocopie de la patente de l’exercice en cours", "La photocopie de la patente de l’exercice en cours");
        hashMap.put("Copie des reçus de la taxe ISV", "La copie des reçus de la taxe ISV");
        hashMap.put("Liste des produits et adresses des fournisseurs", "La liste des produits et adresses des fournisseurs");
        hashMap.put("Rapport d’activités de l’année", "Le rapport d’activités de l’année");
        hashMap.put("Photocopie certifiée du certificat de conformité", "La photocopie certifiée du certificat de conformité");
        hashMap.put("Copie de la décision d’ouverture et photocopie de la patente", "La copie de la décision d’ouverture et photocopie de la patente");
        hashMap.put("Liste de matériel et des équipements à importer", "La liste de matériel et des équipements à importer");
        hashMap.put("Copie certifiée conforme de l’ancien avis technique", "La copie certifiée conforme de l’ancien avis technique");
        hashMap.put("Rapport d’activité de l’exercice écoulée", "Le rapport d’activité de l’exercice écoulée");
        hashMap.put("Certificat de conformité des installations", "Le certificat de conformité des installations");
        hashMap.put("Certificat d’origine des produits importés", "Le certificat d’origine des produits importés");
        hashMap.put("Copie de déclaration des douanes ; Laissez-passer et Certificat sanitaire des produits importés", "La copie de déclaration des douanes ; Laissez-passer et Certificat sanitaire des produits importés");
        hashMap.put("Liste prévisionnelle des produits à importer", "La liste prévisionnelle des produits à importer");
        hashMap.put("Copies des certificats médicaux du personnel", "Les copies des certificats médicaux du personnel");
        hashMap.put("Patente en cours de validité", "La patente en cours de validité");
        hashMap.put("Quittance de paiement des frais afférents", "La quittance de paiement des frais afférents");
  
        // Retrieve the value associated with the key "banana"
        if(hashMap.get(Value) != null && Value != null){
              valueMapped = hashMap.get(Value);
         }else{
            valueMapped = Value;
        }
        // Print the value
       return valueMapped;
    
    }
    
}
