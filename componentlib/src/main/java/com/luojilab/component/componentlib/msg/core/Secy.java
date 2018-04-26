package com.luojilab.component.componentlib.msg.core;

import android.support.annotation.NonNull;

import com.luojilab.component.componentlib.msg.AriseAt;
import com.luojilab.component.componentlib.msg.ConsumeOn;
import com.luojilab.component.componentlib.msg.EventListener;
import com.luojilab.component.componentlib.msg.bean.EventBean;

/**
 * <p><b>Package:</b> com.luojilab.component.componentlib.msg.core </p>
 * <p><b>Project:</b> JIMU </p>
 * <p><b>Classname:</b> Secy </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2018/4/26.
 */
public final class Secy {

    public <T extends EventBean> void subscribe(@NonNull Class<T> eventClz,
                                                @NonNull AriseAt ariseAt,
                                                @NonNull ConsumeOn consumeOn,
                                                @NonNull EventListener<T> listener) {
        if (ariseAt.isLocal()) {
            subscribeLocalEvent(eventClz,consumeOn,listener);
        } else {
            subscribeRemoteEvent(eventClz,consumeOn,listener);
        }
    }

    private void subscribeLocalEvent(@NonNull Class eventClz,
                                     @NonNull ConsumeOn consumeOn,
                                     @NonNull EventListener listener) {

    }

    private void subscribeRemoteEvent(@NonNull Class eventClz,
                                      @NonNull ConsumeOn consumeOn,
                                      @NonNull EventListener listener) {

    }

}
