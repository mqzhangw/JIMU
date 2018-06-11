package org.github.jimu.msg;

import org.github.jimu.msg.bean.EventBean;

/**
 * <p><b>Package:</b> org.github.jimu.msg </p>
 * <p><b>Project:</b> JIMU </p>
 * <p><b>Classname:</b> EventListener </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2018/4/25.
 */
public interface EventListener<T extends EventBean> {
    void onEvent(T event);

    EventListener NONE_NULL = new EventListener() {
        @Override
        public void onEvent(EventBean event) {
        }
    };
}
