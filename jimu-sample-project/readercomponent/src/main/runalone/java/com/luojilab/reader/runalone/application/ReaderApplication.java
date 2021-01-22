package com.luojilab.reader.runalone.application;

import android.util.Log;

import com.luojilab.component.basicres.BaseApplication;
import com.luojilab.component.componentlib.router.Router;

import org.github.jimu.msg.EventManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import osp.leobert.android.maat.JOB;
import osp.leobert.android.maat.Maat;

/**
 * Created by mrzhang on 2017/6/20.
 */

public class ReaderApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Maat.Companion.init(this, 8, new Maat.Logger() {
            @Override
            public boolean getEnable() {
                return true;
            }

            @Override
            public void log(@NotNull String s, @Nullable Throwable throwable) {
                if (throwable != null) {
//                    ILogger.logger.error("maat", s);
//                    throwable.printStackTrace();
                    Log.e("maat", s, throwable);
                } else {
//                    ILogger.logger.debug("maat", s);
                    Log.d("maat", s);
                }
            }
        }, new Maat.Callback(new Function1<Maat, Unit>() {
            @Override
            public Unit invoke(Maat maat) {
                Maat.Companion.release();
                return null;
            }
        }, new Function3<Maat, JOB, Throwable, Unit>() {
            @Override
            public Unit invoke(Maat maat, JOB job, Throwable throwable) {
                return null;
            }
        }));
        EventManager.init(this);

        //如果isRegisterCompoAuto为false，则需要通过反射加载组件
        Router.registerComponent("com.luojilab.share.applike.ShareApplike");
        Router.registerComponent("com.luojilab.share.kotlin.applike.KotlinApplike");
    }

}
