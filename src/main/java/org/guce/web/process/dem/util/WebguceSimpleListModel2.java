package org.guce.web.process.dem.util;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.guce.core.entities.CoreUser;
import org.guce.core.entities.util.CoreProcessingFile;
import org.guce.core.services.SearchFilter;
import org.guce.core.services.CoreServiceLocal;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author VISTA Laptops
 */
public class WebguceSimpleListModel2 extends LazyDataModel<CoreProcessingFile> implements Serializable {

    CoreServiceLocal facade;
    SearchFilter filter;
    int maxSize;
    List<CoreProcessingFile> list;
    String title;
    String fileType;

    public WebguceSimpleListModel2(CoreServiceLocal facade, SearchFilter filter) {
        this.facade = facade;
        this.filter = filter;
        setLoggedUser();
    }

    public WebguceSimpleListModel2(CoreServiceLocal facade, SearchFilter filter, String fileType) {
        this.facade = facade;
        this.filter = filter;
        this.fileType = fileType;
        setLoggedUser();
    }

    @Override
    public List<CoreProcessingFile> load(int i, int i1, String string, SortOrder so, Map<String, Object> map) {
        String order = (so.equals(SortOrder.ASCENDING)) ? "" : "desc";
        order = (so.equals(SortOrder.UNSORTED)) ? null : order;
        if (fileType != null) {
            facade.setFileType(fileType);
        }
        maxSize = facade.searchCount(filter);
        list = facade.search(filter, i, i1, string, order);
        return list;

    }

    @Override
    public int getRowCount() {
        return maxSize;
    }

    public List<CoreProcessingFile> getList() {
        return list;
    }

    public String getTitle() {
        return title;
    }

    private void setLoggedUser() {

        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String userLogin = request.getRemoteUser();
        if (StringUtils.isBlank(userLogin)) {
            return;
        }

        CoreUser loggedUser = facade.find(CoreUser.class, userLogin);
        filter.setLoggedUser(loggedUser);
    }

}
