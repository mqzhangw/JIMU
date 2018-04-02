package com.luojilab.componentdemo.router.cases;

import android.os.Bundle;
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
@RouteNode(path = "/uirouter/demo/4", desc = "Url和Bundle同时包含参数")
public class Demo4Activity extends TestActivity {
    private static Bundle bundle = new Bundle();

    static {
        bundle.putString("foo", "foo string in bundle");
        bundle.putString("EXTRA_STR_BAR", "bar string in bundle");
    }

    @Autowired()
    String foo;

    @Autowired(name = "EXTRA_STR_BAR")
    String bar;


    public static final UiRouterDemoActivity.Case aCase
            = new UiRouterDemoActivity.Case(false,
            "Url和Bundle同时包含参数",
            "JIMU://app/uirouter/demo/4?foo=foo string in url&EXTRA_STR_BAR=bar string in url",
            null);

    @Override
    protected void displayInfo(TextView textView) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Url和Bundle同时包含参数,以url为准\r\n");
        stringBuilder.append("foo:").append(foo).append("\r\n");
        stringBuilder.append("bar:").append(bar).append("\r\n");

        textView.setText(stringBuilder.toString());
    }
}
