package com.luojilab.component.componentlib.msg.core;

import com.luojilab.component.componentlib.msg.EventListener;
import com.luojilab.component.componentlib.msg.bean.EventBean;

import java.lang.ref.WeakReference;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <p><b>Package:</b> com.luojilab.component.componentlib.msg.core </p>
 * <p><b>Project:</b> JIMU </p>
 * <p><b>Classname:</b> SubscriberList </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2018/4/27.
 */
public final class SubscriberList<T extends EventBean> extends CopyOnWriteArrayList<WeakReference<EventListener<T>>> {

    @Override
    public WeakReference<EventListener<T>> get(int index) {
        return super.get(index);
    }
}
