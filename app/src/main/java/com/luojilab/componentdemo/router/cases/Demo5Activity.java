package com.luojilab.componentdemo.router.cases;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.TextView;

import com.luojilab.componentdemo.router.UiRouterDemoActivity;
import com.luojilab.router.facade.annotation.Autowired;
import com.luojilab.router.facade.annotation.RouteNode;

import java.io.Serializable;

/**
 * <p><b>Package:</b> com.luojilab.componentdemo.router.cases </p>
 * <p><b>Project:</b> JIMU </p>
 * <p><b>Classname:</b> Demo3Activity </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 01/04/2018.
 */
@RouteNode(path = "/uirouter/demo/5", desc = "Parcelable和Serializable")
public class Demo5Activity extends TestActivity {

    // TODO: 02/04/2018 error  not support serializable

    /*
     * 仅支持bundle传参
     * */
    private static Bundle bundle = new Bundle();

    static {
        BarSerial barSerial = new BarSerial();
        barSerial.setBarString("bar string");

        FooParcel fooParcel = new FooParcel();
        fooParcel.setFooInt(2);

        bundle.putParcelable("foo", fooParcel);
        bundle.putSerializable("EXTRA_STR_BAR", barSerial);
    }

    @Autowired()
    FooParcel foo;

//    @Autowired(name = "EXTRA_STR_BAR")
    BarSerial bar = new BarSerial();

    public static final UiRouterDemoActivity.Case aCase
            = new UiRouterDemoActivity.Case(false,
            "Parcelable和Serializable",
            "JIMU://app/uirouter/demo/5",
            bundle);

    @Override
    protected void displayInfo(TextView textView) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("使用bundle传递参数成功\r\n");
        stringBuilder.append("foo:").append(foo.toString()).append("\r\n");
        stringBuilder.append("bar:").append(bar.toString()).append("\r\n");

        textView.setText(stringBuilder.toString());
    }


    ///////////////////////////////////////////////////////////////////////////
    // 下面的可忽略
    ///////////////////////////////////////////////////////////////////////////
    public static final class FooParcel implements Parcelable {
        @Override
        public String toString() {
            return "FooParcel:{" +
                    "fooInt:" + fooInt +
                    "}";
        }

        private int fooInt;

        public FooParcel() {
        }

        public int getFooInt() {
            return fooInt;
        }

        public void setFooInt(int fooInt) {
            this.fooInt = fooInt;
        }

        protected FooParcel(Parcel in) {
            fooInt = in.readInt();
        }

        public static final Creator<FooParcel> CREATOR = new Creator<FooParcel>() {
            @Override
            public FooParcel createFromParcel(Parcel in) {
                return new FooParcel(in);
            }

            @Override
            public FooParcel[] newArray(int size) {
                return new FooParcel[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(fooInt);
        }
    }

    public static final class BarSerial implements Serializable {
        private String barString = "暂不支持使用Serializable";

        public BarSerial() {
        }

        public String getBarString() {
            return barString;
        }

        public void setBarString(String barString) {
//            this.barString = barString;
        }

        @Override
        public String toString() {
            return "BarSerial:{" +
                    "barString:" + barString +
                    "}";
        }
    }

}
