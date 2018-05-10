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
@RouteNode(path = "/uirouter/demo/7", desc = "必须参数")
public class Demo7Activity extends TestActivity {

    @Autowired(required = true)
    String foo;

    public static final UiRouterDemoActivity.Case aCase
            = new UiRouterDemoActivity.Case(false,
            "必须参数-log输出",
            "JIMU://app/uirouter/demo/7",
            null);

    @Override
    protected void displayInfo(TextView textView) {
        textView.setText("此处Autowired没有使用异常抛出功能，\r\n" +
                "通过安全模式可以捕获ParamException\r\n" +
                "直接跳转，控制台输出错误日志，通过AutowiredProcessor 进行过滤\r\n" +
                "显然，通过异常抛出更容易发现问题");

    }
}
