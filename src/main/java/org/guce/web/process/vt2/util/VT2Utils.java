/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guce.web.process.vt2.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXB;

/**
 *
 * @author NGC
 */
public class VT2Utils {
    
    public static String objectToXml(Object registration){
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            JAXB.marshal(registration, out);
            return new String(out.toByteArray(),"UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(VT2Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static Object xmlToObject(String xml,Class klazz) {
        try {
            return JAXB.unmarshal(new ByteArrayInputStream(xml.getBytes("UTF-8")), klazz);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(VT2Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
