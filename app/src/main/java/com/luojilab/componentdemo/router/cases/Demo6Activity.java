package com.luojilab.componentdemo.router.cases;

import android.widget.TextView;

import com.google.gson.Gson;
import com.luojilab.componentdemo.router.UiRouterDemoActivity;
import com.luojilab.router.facade.annotation.Autowired;
import com.luojilab.router.facade.annotation.RouteNode;


@RouteNode(path = "/uirouter/demo/6", desc = "使用json字符串传参")
public class Demo6Activity extends TestActivity {

    @Autowired(name = "EXTRA_OBJ_FOO")
    Foo foo;
    private static Foo test;

    static {
        test = new Foo();
        test.setFooInt(2);
        test.setFooString("foo string");
    }

    /*注意，仅支持在queryString中设置参数，不支持在path中传递参数*/
    public static final UiRouterDemoActivity.Case aCase
            = new UiRouterDemoActivity.Case(false,
            "使用json字符串传参",
            "JIMU://app/uirouter/demo/6?EXTRA_OBJ_FOO=" + new Gson().toJson(test),
            null);

    @Override
    protected void displayInfo(TextView textView) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("使用json字符串传参成功\r\n");
        stringBuilder.append("foo:").append(foo).append("\r\n");

        textView.setText(stringBuilder.toString());
    }

    public static final class Foo {
        private int fooInt;
        private String fooString;

        public int getFooInt() {
            return fooInt;
        }

        public void setFooInt(int fooInt) {
            this.fooInt = fooInt;
        }

        public String getFooString() {
            return fooString;
        }

        public void setFooString(String fooString) {
            this.fooString = fooString;
        }

        @Override
        public String toString() {
            return "Foo:{" +
                    "fooInt:" + fooInt + ","
                    + "fooString:" + fooString +
                    "}";
        }
    }
}
