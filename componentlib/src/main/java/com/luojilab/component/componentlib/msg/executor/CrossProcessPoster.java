package com.luojilab.component.componentlib.msg.executor;

import android.support.annotation.NonNull;

import com.luojilab.component.componentlib.msg.EventListener;
import com.luojilab.component.componentlib.msg.bean.EventBean;

/**
 * <p><b>Package:</b> com.luojilab.component.componentlib.msg.executor </p>
 * <p><b>Project:</b> JIMU </p>
 * <p><b>Classname:</b> CrossProcessPoster </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2018/4/25.
 */
public class CrossProcessPoster implements IPoster{


    @Override
    public <T extends EventBean> void postEvent(@NonNull T event, @NonNull EventListener<T> target) {

    }
}
