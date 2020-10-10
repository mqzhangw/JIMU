package com.luojilab.share.applike;

import android.util.Log;

import com.luojilab.component.componentlib.applicationlike.IApplicationLike;
import com.luojilab.component.componentlib.router.ui.UIRouter;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import kotlinx.coroutines.CoroutineDispatcher;
import kotlinx.coroutines.Dispatchers;
import osp.leobert.android.maat.JOB;
import osp.leobert.android.maat.Maat;

/**
 * Created by mrzhang on 2017/6/15.
 */

public class ShareApplike implements IApplicationLike {

    UIRouter uiRouter = UIRouter.getInstance();

    @Override
    public void onCreate() {
        uiRouter.registerUI("share");
        Log.e("share","share on create");
        Maat.Companion.getDefault().append(new JOB() {
            @NotNull
            @Override
            public String getUniqueKey() {
                return "share";
            }

            @NotNull
            @Override
            public List<String> getDependsOn() {
                return Collections.singletonList("reader");
            }

            @NotNull
            @Override
            public CoroutineDispatcher getDispatcher() {
                return Dispatchers.getMain();
            }

            @Override
            public void init(@NotNull Maat maat) {
                Log.d("share", "模拟初始化share，context：" + maat.getApplication().getClass().getName());
            }

            @Override
            public String toString() {
                return getUniqueKey();
            }
        });
    }

    @Override
    public void onStop() {
        uiRouter.unregisterUI("share");
    }
}
