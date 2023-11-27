package org.guce.web.process.dem.controllers;

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
import org.guce.process.dem.entities.DEMDeclaration;
import org.guce.process.dem.filters.DEMDeclarationFilter;
import org.guce.util.exporter.ExcelExporter;
import org.guce.util.exporter.PDFExporter;
import org.guce.web.process.dem.services.impl.DEMDeclarationServiceImpl;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

public class DEMDeclarationController extends CrudDefaultListController<DEMDeclaration, DEMDeclarationServiceImpl> {
    @EJB
    protected DEMDeclarationServiceImpl service;

    @Override
    public DEMDeclarationServiceImpl getService() {
        return service;
    }

    @Override
    public DEMDeclaration newItem() {
        DEMDeclaration item = new DEMDeclaration();
        item.setActive(true);
        return item;
    }

    @Override
    public DEMDeclarationFilter newFilter() {
        return new DEMDeclarationFilter();
    }

    public StreamedContent exportPDF() {
        DefaultStreamedContent file = null;
        try {
            List<DEMDeclaration> files = getService().search(filter, -1, -1, null, null);
            final SimpleDateFormat dateFormat = new SimpleDateFormat(bundle("dateFormat"));
            Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
            Format numberFormat = NumberFormat.getNumberInstance(locale);
            final PDFExporter exporter = new PDFExporter();
            exporter.setTitle(bundle("DEMDeclarationList"));
            String[] cols = new String[1];
            cols[0] = bundle("declaration");
            exporter.setColumnsNames(cols);
            final int nbCols = cols.length;
            final List<String[]> data = new ArrayList<>();
            int colNum;
            for (final DEMDeclaration cpf : files) {
                final String[] line = new String[nbCols];
                colNum = 0;
                line[colNum++] = cpf.getDeclaration();
                data.add(line);
            }
            exporter.setData(data);
            final String filename = "EXPORT_DEMDeclaration.pdf";
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
            List<DEMDeclaration> files = getService().search(filter, -1, -1, null, null);
            final SimpleDateFormat dateFormat = new SimpleDateFormat(bundle("dateFormat"));
            Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
            Format numberFormat = NumberFormat.getNumberInstance(locale);
            final ExcelExporter exporter = new ExcelExporter();
            exporter.setTitle(bundle("DEMDeclarationList"));
            exporter.getTitleStyle().setAlignment(HorizontalAlignment.CENTER);
            String[] cols = new String[1];
            cols[0] = bundle("declaration");
            exporter.setColumnsNames(cols);
            final int nbCols = cols.length;
            final List<String[]> data = new ArrayList<>();
            int colNum;
            for (final DEMDeclaration cpf : files) {
                final String[] line = new String[nbCols];
                colNum = 0;
                line[colNum++] = cpf.getDeclaration();
                data.add(line);
            }
            exporter.setData(data);
            final String filename = "EXPORT_DEMDeclaration.xlsx";
            final byte[] bytes = exporter.getByteArray();
            file = new DefaultStreamedContent(new ByteArrayInputStream(bytes), "application/octet-stream", filename);
        } catch(Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return file;
    }
}
