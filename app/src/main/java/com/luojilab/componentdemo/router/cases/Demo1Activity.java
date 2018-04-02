package com.luojilab.componentdemo.router.cases;

import android.widget.TextView;

import com.luojilab.componentdemo.router.UiRouterDemoActivity;
import com.luojilab.router.facade.annotation.RouteNode;

/**
 * <p><b>Package:</b> com.luojilab.componentdemo.router.cases </p>
 * <p><b>Project:</b> JIMU </p>
 * <p><b>Classname:</b> Demo1Activity </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 28/03/2018.
 */
@RouteNode(path = "/uirouter/demo/1", desc = "无参数")
public class Demo1Activity extends TestActivity {
    public static final UiRouterDemoActivity.Case aCase
            = new UiRouterDemoActivity.Case(false,
            "无参数跳转",
            "JIMU://app/uirouter/demo/1",
            null);


    @Override
    protected void displayInfo(TextView textView) {
        textView.setText("无参数跳转成功");
    }
}
