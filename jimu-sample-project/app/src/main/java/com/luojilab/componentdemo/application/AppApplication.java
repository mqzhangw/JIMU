package com.luojilab.componentdemo.application;

import com.luojilab.component.basicres.BaseApplication;
import com.luojilab.component.componentlib.log.ILogger;
import org.github.jimu.msg.EventManager;
import com.luojilab.component.componentlib.router.Router;
import com.luojilab.component.componentlib.router.ui.UIRouter;
import com.luojilab.componentdemo.msg.AppComponentEventManager;
import com.luojilab.componentdemo.msg.MainProcessMsgService;
import com.luojilab.componentdemo.msg.RemoteMsgService;

/**
 * Created by mrzhang on 2017/6/15.
 */

public class AppApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        ILogger.logger.setDefaultTag("JIMU");
        UIRouter.enableDebug();
//        EventManager.appendMapper("com.luojilab.componentdemo.application", MainProcessMsgService.class);
//        EventManager.appendMapper("com.luojilab.componentdemo.application:remote", RemoteMsgService.class);

        EventManager.init(this);

        UIRouter.getInstance().registerUI("app");

        //如果isRegisterCompoAuto为false，则需要通过反射加载组件
        Router.registerComponent("com.luojilab.reader.applike.ReaderAppLike");
//        Router.registerComponent("com.luojilab.share.applike.ShareApplike");

        Router.getInstance().addService(AppComponentEventManager.class.getSimpleName(),
                EventManager.create(AppComponentEventManager.class));

    }


}
