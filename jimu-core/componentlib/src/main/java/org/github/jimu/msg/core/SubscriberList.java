package org.github.jimu.msg.core;

import org.github.jimu.msg.EventListener;
import org.github.jimu.msg.bean.EventBean;

import java.lang.ref.WeakReference;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <p><b>Package:</b> org.github.jimu.msg.core </p>
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

    public boolean removeCallback(EventListener listener) {
        boolean ret = false;
        for (int i = 0;i<size();i++) {
            WeakReference<EventListener<T>> ref = get(i);
            if (ref == null || ref.get() == null)
                continue;

            if (ref.get().equals(listener)) {
                ret = true;
                remove(ref);
                i--;
                ref.clear();
            }
        }
        return ret;
    }
}
