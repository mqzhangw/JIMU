package com.luojilab.component.componentlib.msg;

import android.os.Parcelable;

/**
 * <p><b>Package:</b> com.luojilab.component.componentlib.msg </p>
 * <p><b>Project:</b> JIMU </p>
 * <p><b>Classname:</b> EventListener </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2018/4/25.
 */
public interface EventListener<T extends Parcelable> {
    void onEvent(T event);
}
