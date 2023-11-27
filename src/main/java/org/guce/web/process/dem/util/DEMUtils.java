/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guce.web.process.dem.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author NGC
 */
public class DEMUtils {

    private static final Map<Class,Marshaller> marshallers = new HashMap<>();
    private static final Map<Class,Unmarshaller> unmarshallers = new HashMap<>();
    
    public static String objectToXml(Object registration){
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            if(!marshallers.containsKey(registration.getClass()))
                marshallers.put(registration.getClass(),JAXBContext.newInstance(registration.getClass()).createMarshaller());
            marshallers.get(registration.getClass()).marshal(registration, out);
            return new String(out.toByteArray(),"UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DEMUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JAXBException ex) {
            Logger.getLogger(DEMUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static Object xmlToObject(String xml,Class klazz) {
        try {
             if(!unmarshallers.containsKey(klazz))
                unmarshallers.put(klazz,JAXBContext.newInstance(klazz).createUnmarshaller());
           return unmarshallers.get(klazz).unmarshal(new ByteArrayInputStream(xml.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DEMUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JAXBException ex) {
            Logger.getLogger(DEMUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
