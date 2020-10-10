package com.luojilab.reader.applike;

import android.util.Log;

import com.luojilab.component.componentlib.applicationlike.IApplicationLike;
import com.luojilab.component.componentlib.applicationlike.RegisterCompManual;
import com.luojilab.component.componentlib.router.Router;
import com.luojilab.component.componentlib.router.ui.UIRouter;
import com.luojilab.componentservice.readerbook.ReadBookService;
import com.luojilab.reader.serviceimpl.ReadBookServiceImplKotlin;

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
@RegisterCompManual
public class ReaderAppLike implements IApplicationLike {

    Router router = Router.getInstance();
    UIRouter uiRouter = UIRouter.getInstance();

    @Override
    public void onCreate() {
        Log.e("reader","onCreate");
        uiRouter.registerUI("reader");
//        router.addService(ReadBookService.class.getSimpleName(), new ReadBookServiceImpl());
        router.addService(ReadBookService.class.getSimpleName(), new ReadBookServiceImplKotlin());
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
                Log.d("reader", "模拟初始化reader，context：" + maat.getApplication().getClass().getName());
            }

            @Override
            public String toString() {
                return getUniqueKey();
            }
        });
    }

    @Override
    public void onStop() {
        uiRouter.unregisterUI("reader");
        router.removeService(ReadBookService.class.getSimpleName());
    }
}
