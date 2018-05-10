package com.luojilab.component.componentlib.msg.core;

import com.luojilab.component.componentlib.msg.bean.EventBean;

import java.util.HashMap;

/**
 * <p><b>Package:</b> com.luojilab.component.componentlib.msg.core </p>
 * <p><b>Project:</b> JIMU </p>
 * <p><b>Classname:</b> SubscriberCache </p>
 * <p><b>Description:</b> just avoid too verbose rawType </p>
 * Created by leobert on 2018/4/27.
 */
public final class SubscriberCache extends HashMap<Class,
        SubscriberList<EventBean>> {

    public SubscriberCache(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public SubscriberCache(int initialCapacity) {
        super(initialCapacity);
    }

    public SubscriberCache() {
    }

    @Override
    public SubscriberList<EventBean> get(Object key) {
        return super.get(key);
    }

    @Override
    public SubscriberList<EventBean> put(Class key, SubscriberList<EventBean> value) {
        return super.put(key, value);
    }
}
