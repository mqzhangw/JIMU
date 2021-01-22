package com.luojilab.share.runalone.application;

import android.util.Log;

import com.luojilab.component.basicres.BaseApplication;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlinx.coroutines.CoroutineDispatcher;
import kotlinx.coroutines.Dispatchers;
import osp.leobert.android.maat.JOB;
import osp.leobert.android.maat.Maat;

/**
 * Created by mrzhang on 2017/8/16.
 */

public class ShareApplication extends BaseApplication {

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
                    Log.e("maat",s,throwable);
                } else {
//                    ILogger.logger.debug("maat", s);
                    Log.d("maat",s);
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

        Maat.Companion.getDefault().append(new JOB() {
            @NotNull
            @Override
            public String getUniqueKey() {
                return "reader";
            }

            @NotNull
            @Override
            public List<String> getDependsOn() {
                return Collections.emptyList();
            }

            @NotNull
            @Override
            public CoroutineDispatcher getDispatcher() {
                return Dispatchers.getIO();
            }

            @Override
            public void init(@NotNull Maat maat) {
                Log.d("share", "如果组件环境不全，手动添加的模拟初始化reader，context：" + maat.getApplication().getClass().getName());
            }

            @Override
            public String toString() {
                return getUniqueKey();
            }
        });
    }

}