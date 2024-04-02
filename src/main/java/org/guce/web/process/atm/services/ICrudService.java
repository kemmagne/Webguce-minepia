/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guce.web.process.atm.services;

import java.util.List;
import org.guce.core.services.SearchFilter;

/**
 *
 * @author koufana
 * @param <T>
 */
public interface ICrudService<T> {

    int count();

    List<T> findAll();

    List<T> findAll(int[] range);

    T findBy(String primaryKey);

    void remove(T entity);

    T save(T entity);

    List<T> search(SearchFilter filter,int start, int count, String orderField,String order);

    int searchCount(SearchFilter filter);
    
    void remove(List<T> entity);

    void save(List<T> entity);
    
}
