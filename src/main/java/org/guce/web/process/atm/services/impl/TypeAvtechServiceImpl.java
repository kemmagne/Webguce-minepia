package org.guce.web.process.atm.services.impl;

import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import org.guce.process.atm.entities.TypeAvtech;
import org.guce.process.atm.repositories.impl.TypeAvtechRepositoryImpl;
import org.guce.web.process.atm.services.TypeAvtechService;

@Stateless
@LocalBean
public class TypeAvtechServiceImpl extends TypeAvtechService {
    
    @EJB
    private TypeAvtechRepositoryImpl typeAvtechRepositoryImpl;
    
    public List<TypeAvtech>  findTypeTypeAvtechs(String  key1 , String key2, String key3){
        return typeAvtechRepositoryImpl.findTypeAvtechOfATM(key1,  key2, key3);
    }
    
    public List<TypeAvtech> findTypeTypeAvtechByCode(String code){
        return typeAvtechRepositoryImpl.findTypeAvtechByCode(code);
    }
}
