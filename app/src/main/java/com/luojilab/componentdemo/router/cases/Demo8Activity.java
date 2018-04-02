package com.luojilab.componentdemo.router.cases;

import android.widget.TextView;

import com.luojilab.componentdemo.router.UiRouterDemoActivity;
import com.luojilab.router.facade.annotation.Autowired;
import com.luojilab.router.facade.annotation.RouteNode;

/**
 * <p><b>Package:</b> com.luojilab.componentdemo.router.cases </p>
 * <p><b>Project:</b> JIMU </p>
 * <p><b>Classname:</b> Demo3Activity </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 01/04/2018.
 */
@RouteNode(path = "/uirouter/demo/8", desc = "必须参数2")
public class Demo8Activity extends TestActivity {

    @Autowired(required = true,throwOnNull = true)
    String foo;

    public static final UiRouterDemoActivity.Case aCase
            = new UiRouterDemoActivity.Case(true,
            "必须参数-空指针校验，primitive无效！",
            "JIMU://app/uirouter/demo/8",
            null);

    @Override
    protected void displayInfo(TextView textView) {
        textView.setText("不使用safemode 将直接crash");
    }
}
