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
import org.guce.process.atm.entities.AvisTech;
import org.guce.process.atm.filters.AvisTechFilter;
import org.guce.util.exporter.ExcelExporter;
import org.guce.util.exporter.PDFExporter;
import org.guce.web.process.atm.services.impl.AvisTechServiceImpl;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

public class AvisTechController extends CrudDefaultListController<AvisTech, AvisTechServiceImpl> {
    @EJB
    protected AvisTechServiceImpl service;

    @Override
    public AvisTechServiceImpl getService() {
        return service;
    }

    @Override
    public AvisTech newItem() {
        AvisTech item = new AvisTech();
        item.setActive(true);
        return item;
    }

    @Override
    public AvisTechFilter newFilter() {
        return new AvisTechFilter();
    }

    public StreamedContent exportPDF() {
        DefaultStreamedContent file = null;
        try {
            List<AvisTech> files = getService().search(filter, -1, -1, null, null);
            final SimpleDateFormat dateFormat = new SimpleDateFormat(bundle("dateFormat"));
            Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
            Format numberFormat = NumberFormat.getNumberInstance(locale);
            final PDFExporter exporter = new PDFExporter();
            exporter.setTitle(bundle("AvisTechList"));
            String[] cols = new String[4];
            cols[0] = bundle("registerNumber");
            cols[1] = bundle("immatriculation");
            cols[2] = bundle("name");
            cols[3] = bundle("status");
            exporter.setColumnsNames(cols);
            final int nbCols = cols.length;
            final List<String[]> data = new ArrayList<>();
            int colNum;
            for (final AvisTech cpf : files) {
                final String[] line = new String[nbCols];
                colNum = 0;
                line[colNum++] = cpf.getRegisterNumber();
                line[colNum++] = cpf.getImmatriculation();
                line[colNum++] = cpf.getName();
                line[colNum++] = cpf.getStatus();
                data.add(line);
            }
            exporter.setData(data);
            final String filename = "EXPORT_AvisTech.pdf";
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
            List<AvisTech> files = getService().search(filter, -1, -1, null, null);
            final SimpleDateFormat dateFormat = new SimpleDateFormat(bundle("dateFormat"));
            Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
            Format numberFormat = NumberFormat.getNumberInstance(locale);
            final ExcelExporter exporter = new ExcelExporter();
            exporter.setTitle(bundle("AvisTechList"));
            exporter.getTitleStyle().setAlignment(HorizontalAlignment.CENTER);
            String[] cols = new String[4];
            cols[0] = bundle("registerNumber");
            cols[1] = bundle("immatriculation");
            cols[2] = bundle("name");
            cols[3] = bundle("status");
            exporter.setColumnsNames(cols);
            final int nbCols = cols.length;
            final List<String[]> data = new ArrayList<>();
            int colNum;
            for (final AvisTech cpf : files) {
                final String[] line = new String[nbCols];
                colNum = 0;
                line[colNum++] = cpf.getRegisterNumber();
                line[colNum++] = cpf.getImmatriculation();
                line[colNum++] = cpf.getName();
                line[colNum++] = cpf.getStatus();
                data.add(line);
            }
            exporter.setData(data);
            final String filename = "EXPORT_AvisTech.xlsx";
            final byte[] bytes = exporter.getByteArray();
            file = new DefaultStreamedContent(new ByteArrayInputStream(bytes), "application/octet-stream", filename);
        } catch(Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return file;
    }
}
