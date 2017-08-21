package com.mrzhang.share.activator;

import com.mrzhang.component.componentlib.activator.IActivator;
import com.mrzhang.component.componentlib.router.ui.UIRouter;
import com.mrzhang.share.compouirouter.ShareUIRouter;

/**
 * Created by mrzhang on 2017/6/15.
 */

public class ShareActivator implements IActivator {

    UIRouter uiRouter = UIRouter.getInstance();
    ShareUIRouter shareUIRouter = ShareUIRouter.getInstance();

    @Override
    public void onCreate() {
        uiRouter.registerUI(shareUIRouter);
    }

    @Override
    public void onStop() {
        uiRouter.unregisterUI(shareUIRouter);
    }
}
