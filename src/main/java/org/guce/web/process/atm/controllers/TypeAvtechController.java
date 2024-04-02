package org.guce.web.process.atm.controllers;

import java.io.ByteArrayInputStream;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.guce.process.atm.entities.TypeAvtech;
import org.guce.process.atm.filters.TypeAvtechFilter;
import org.guce.util.exporter.ExcelExporter;
import org.guce.util.exporter.PDFExporter;
import org.guce.web.process.atm.services.impl.TypeAvtechServiceImpl;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

public class TypeAvtechController extends CrudDefaultListController<TypeAvtech, TypeAvtechServiceImpl> {
    @EJB
    protected TypeAvtechServiceImpl service;

    @Override
    public TypeAvtechServiceImpl getService() {
        return service;
    }

    @Override
    public TypeAvtech newItem() {
        TypeAvtech item = new TypeAvtech();
        item.setActive(true);
        return item;
    }

    @Override
    public TypeAvtechFilter newFilter() {
        return new TypeAvtechFilter();
    }

    public StreamedContent exportPDF() {
        DefaultStreamedContent file = null;
        try {
            List<TypeAvtech> files = getService().search(filter, -1, -1, null, null);
            final SimpleDateFormat dateFormat = new SimpleDateFormat(bundle("dateFormat"));
            Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
            Format numberFormat = NumberFormat.getNumberInstance(locale);
            final PDFExporter exporter = new PDFExporter();
            exporter.setTitle(bundle("TypeAvtechList"));
            String[] cols = new String[2];
            cols[0] = bundle("label");
            cols[1] = bundle("code");
            exporter.setColumnsNames(cols);
            final int nbCols = cols.length;
            final List<String[]> data = new ArrayList<>();
            int colNum;
            for (final TypeAvtech cpf : files) {
                final String[] line = new String[nbCols];
                colNum = 0;
                line[colNum++] = cpf.getLabel();
                line[colNum++] = cpf.getCode();
                data.add(line);
            }
            exporter.setData(data);
            final String filename = "EXPORT_TypeAvtech.pdf";
            final byte[] bytes = exporter.getByteArray();
            file = new DefaultStreamedContent(new ByteArrayInputStream(bytes), "application/pdf", filename);
        } catch(Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return file;
    }

    public StreamedContent exportExcel() {
        DefaultStreamedContent file = null;
        try {
            List<TypeAvtech> files = getService().search(filter, -1, -1, null, null);
            final SimpleDateFormat dateFormat = new SimpleDateFormat(bundle("dateFormat"));
            Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
            Format numberFormat = NumberFormat.getNumberInstance(locale);
            final ExcelExporter exporter = new ExcelExporter();
            exporter.setTitle(bundle("TypeAvtechList"));
            exporter.getTitleStyle().setAlignment(HorizontalAlignment.CENTER);
            String[] cols = new String[2];
            cols[0] = bundle("label");
            cols[1] = bundle("code");
            exporter.setColumnsNames(cols);
            final int nbCols = cols.length;
            final List<String[]> data = new ArrayList<>();
            int colNum;
            for (final TypeAvtech cpf : files) {
                final String[] line = new String[nbCols];
                colNum = 0;
                line[colNum++] = cpf.getLabel();
                line[colNum++] = cpf.getCode();
                data.add(line);
            }
            exporter.setData(data);
            final String filename = "EXPORT_TypeAvtech.xlsx";
            final byte[] bytes = exporter.getByteArray();
            file = new DefaultStreamedContent(new ByteArrayInputStream(bytes), "application/octet-stream", filename);
        } catch(Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return file;
    }
}
