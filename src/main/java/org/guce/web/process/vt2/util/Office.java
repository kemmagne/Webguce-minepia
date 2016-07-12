/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guce.web.process.vt2.util;

/**
 *
 * @author Anyam
 */
public class Office {

    String code;
    String libelle;

    public Office() {
    }

    public Office(String code, String libelle) {
        this.code = code;
        this.libelle = libelle;
    }
    

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }
}
