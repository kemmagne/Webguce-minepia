/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guce.web.process.vt2.controllers;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.guce.core.services.SearchFilter;
import org.guce.process.vt2.entities.AbstractEntity;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.guce.web.process.vt2.services.ICrudService;

/**
 *
 * @author koufana
 * @param <T>
 * @param <R>
 */
public class CrudDefaultLazyDataModel<T,R extends ICrudService> extends LazyDataModel<T> implements Serializable{
    
    private List<T> results;
    
    private final R service;
    
    private final SearchFilter filter;
    
    private int count;

    public CrudDefaultLazyDataModel(R service,SearchFilter filter) {
        this.service = service;
        this.filter = filter;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
    
    

    @Override
    public Object getRowKey(T object) {
        return ((AbstractEntity)object).getId();
    }

    @Override
    public T getRowData(String rowKey) {
        return (T) service.findBy(rowKey);
    }
    
    @Override
    public List<T> load(int i, int i1, String string, SortOrder so, Map<String, Object> map) {
        String order = (so.equals(SortOrder.ASCENDING)) ? "" : "desc";
        order = (so.equals(SortOrder.UNSORTED)) ? null : order;
        count =  service.searchCount(filter);
        results = service.search(filter, i, i1, string, order);
        return results;

    }

    @Override
    public int getRowCount() {
        return count;
    }
}
