/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guce.web.process.atm.controllers;

import java.util.Arrays;
import javax.annotation.PostConstruct;
import org.guce.core.services.GuceCalendarUtil;
import org.guce.core.services.SearchFilter;
import org.guce.process.atm.entities.AbstractEntity;
import org.guce.web.core.user.controller.SimpleController;
import org.guce.web.core.util.JsfUtil;
import org.guce.web.process.atm.services.ICrudService;

/**
 *
 * @author koufana
 * @param <T>
 * @param <R>
 */
public abstract class CrudDefaultListController<T extends AbstractEntity,R extends ICrudService> extends SimpleController{
    
    private T current;
    
    private CrudDefaultLazyDataModel<T,R> dataModel;
    
    private T[] selecteds;
    
    private SearchFilter itemFilter;
    
    private String mode = "list";
    
    @PostConstruct
    public void init(){
        itemFilter = newFilter();
        loadList();
    }
    
    public void prepareCreate(){
        mode = "form";
        current = newItem();
    }
    
    public void preapareEdit(T item){
        mode = "form";
        current = item;
    }
    
    public void loadList(){
        mode = "list";
        dataModel = new CrudDefaultLazyDataModel<>(getService(),itemFilter);
    }
    
    public void save(){
        if(check()){
            if(current.getCreateUser() == null){
                current.setCreateUser(getUserController().getUserConnecte());
                current.setCreateDate(GuceCalendarUtil.getCalendar().getTime());
            }
            current.setWriteDate(GuceCalendarUtil.getCalendar().getTime());
            current.setWriteUser(getUserController().getUserConnecte());
            current = (T) getService().save(current);
            loadList();
            JsfUtil.addSuccessMessage(bundle(getPreffix()+"_ITEM_SAVE")+" id = "+current.getId());
        }
    }
    public void remove(){
        getService().remove(current);
        loadList();
        JsfUtil.addSuccessMessage(bundle(getPreffix()+"_ITEM_REMOVE"));
    }
    
    public void removes(){
        getService().remove(Arrays.asList(selecteds));
        loadList();
        JsfUtil.addSuccessMessage(bundle(getPreffix()+"_ITEMS_REMOVE"));
    }
    
    public void enables(){
        for (T item : selecteds) {
            item.setActive(Boolean.TRUE);
        }
        getService().save(Arrays.asList(selecteds));
        loadList();
        JsfUtil.addSuccessMessage(bundle(getPreffix()+"_ITEMS_ENABLED"));
    }
    
    public void disables(){
        for (T item : selecteds) {
            item.setActive(Boolean.FALSE);
        }
        getService().save(Arrays.asList(selecteds));
        loadList();
        JsfUtil.addSuccessMessage(bundle(getPreffix()+"_ITEMS_DISABLED"));
    }
    
    public abstract R getService();

    public T getCurrent() {
        return current;
    }

    public void setCurrent(T current) {
        this.current = current;
    }

    public CrudDefaultLazyDataModel<T, R> getDataModel() {
        return dataModel;
    }

    public void setDataModel(CrudDefaultLazyDataModel<T, R> dataModel) {
        this.dataModel = dataModel;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public T[] getSelecteds() {
        return selecteds;
    }

    public void setSelecteds(T[] selecteds) {
        this.selecteds = selecteds;
    }

    public SearchFilter getItemFilter() {
        return itemFilter;
    }

    public void setItemFilter(SearchFilter itemFilter) {
        this.itemFilter = itemFilter;
    }
    

    protected boolean check() {
        return true;
    }

    protected String getPreffix() {
        return "";
    }

    protected abstract T newItem();

    protected abstract SearchFilter newFilter();
    
    
    
}
