package com.luojilab.componentdemo.msg.event;

import android.os.Parcel;

import org.github.jimu.msg.bean.RemoteEventBean;

/**
 * <p><b>Package:</b> com.luojilab.componentdemo.msg.event </p>
 * <p><b>Project:</b> jimu-sample-project </p>
 * <p><b>Classname:</b> EventB </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2018/6/7.
 */
public class EventB implements RemoteEventBean{
    private String msg;

    public EventB(String msg) {
        this.msg = msg;
    }

    protected EventB(Parcel in) {
        msg = in.readString();
    }

    public static final Creator<EventB> CREATOR = new Creator<EventB>() {
        @Override
        public EventB createFromParcel(Parcel in) {
            return new EventB(in);
        }

        @Override
        public EventB[] newArray(int size) {
            return new EventB[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(msg);
    }

    public String getMsg() {
        return msg;
    }
}
