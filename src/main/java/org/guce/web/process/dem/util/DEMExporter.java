package org.guce.web.process.dem.util;

import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.Page;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.guce.core.entities.util.CoreProcessingFile;
import org.guce.core.services.CoreServiceLocal;
import org.guce.core.services.SearchFilter;
import org.guce.process.dem.entities.DEMRegistration;
import org.guce.util.exporter.ExcelExporter;
import org.guce.util.exporter.PDFExporter;

public class DEMExporter implements Serializable {
    public static final SimpleDateFormat DateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public byte[] createExcel(String title, String subTitle, CoreServiceLocal service, SearchFilter filter) throws Exception {
        ExcelExporter e = new ExcelExporter();
        List<CoreProcessingFile> data = service.search(filter, 0, -1, null, null);
        e.setColumnsNames(getPdfHeaderLine());
        List<String[]> list = new ArrayList<>();
        for (CoreProcessingFile f : data) {
            String[] aa = getPdfLineData(f);
            list.add(aa);
        }
        e.setData(list);
        e.setTitle(title);
        e.setSubTitle(subTitle);
        return e.getByteArray();
    }

    public byte[] createPdf(String title, String subTitle, CoreServiceLocal service, SearchFilter filter) throws Exception {
        PDFExporter e = new PDFExporter();
        e.setSubTitle(subTitle);
        e.setPageSizeAndOrientation(Page.Page_A4_Landscape());
        Style titleStyle = new Style();
        titleStyle.setFont(new Font(15, Font._FONT_VERDANA, true));
        titleStyle.setHorizontalAlign(HorizontalAlign.CENTER);
        titleStyle.setPadding(5);
        titleStyle.setPaddingTop(15);
        e.setTitleStyle(titleStyle);
        Style stitleStyle = new Style();
        stitleStyle.setFont(new Font(10, Font._FONT_VERDANA, false));
        stitleStyle.setHorizontalAlign(HorizontalAlign.CENTER);
        stitleStyle.setPaddingBottom(10);
        e.setSubTitleStyle(stitleStyle);
        List<CoreProcessingFile> data = service.search(filter, 0, -1, null, null);
        e.setColumnsNames(getExcelHeaderLine());
        List<String[]> list = new ArrayList<>();
        for (CoreProcessingFile f : data) {
            String[] aa = getExcelLineData(f);
            list.add(aa);
        }
        e.setData(list);
        e.setTitle(title);
        e.setSubTitle(subTitle);
        return e.getByteArray();
    }

    protected String[] getLineData(CoreProcessingFile f) {
        DEMRegistration ff = (DEMRegistration) f.getFile();
        return new String[]{
                            f.getFile().getRecordId(),
                            f.getFile().getRecordParent() != null ? f.getFile().getRecordParent().getRecordId() : "",
                            f.getFile().getChargerid().getNumeroContribuable(),
                            f.getFile().getChargerid().getChargername(),
                          //  ff.getSupplier().getChargername()
                        };
    }

    protected String[] getHeaderLine() {
        return new String[]{
                            "Numero Dossier",
                            "Numero Circuit",
                            "NIU",
                            "Importateur",
                            "Exportation"
                        };
    }

    protected String[] getPdfHeaderLine() {
        return getHeaderLine();
    }

    protected String[] getExcelHeaderLine() {
        return getHeaderLine();
    }

    protected String[] getPdfLineData(CoreProcessingFile f) {
        return getLineData(f);
    }

    protected String[] getExcelLineData(CoreProcessingFile f) {
        return getLineData(f);
    }
}
