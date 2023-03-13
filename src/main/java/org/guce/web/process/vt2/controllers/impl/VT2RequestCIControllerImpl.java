package org.guce.web.process.vt2.controllers.impl;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.guce.web.process.vt2.controllers.VT2RequestCIController;

@ManagedBean(
        name = "vT2RequestCIController"
)
@ViewScoped
public class VT2RequestCIControllerImpl extends VT2RequestCIController {
    @Override
    public void prepareSend() {
        selectGoodProductCategories();
        restoreSelectedNshProductCategories();
        super.prepareSend();
    }
}
