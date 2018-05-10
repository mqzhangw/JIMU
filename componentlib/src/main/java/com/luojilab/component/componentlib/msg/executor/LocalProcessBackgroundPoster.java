package com.luojilab.component.componentlib.msg.executor;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.luojilab.component.componentlib.msg.EventListener;
import com.luojilab.component.componentlib.msg.bean.EventBean;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * <p><b>Package:</b> com.luojilab.component.componentlib.msg.executor </p>
 * <p><b>Project:</b> JIMU </p>
 * <p><b>Classname:</b> LocalProcessBackgroundPoster </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2018/5/3.
 */
public class LocalProcessBackgroundPoster implements IPoster, Runnable {
    private final List<Pair<EventBean, EventListener<EventBean>>> cache;

    private boolean started = false;
    private final ExecutorService executorService;

    public LocalProcessBackgroundPoster(@NonNull ExecutorService executorService) {
        cache = new ArrayList<>();
        this.executorService = executorService;
    }

    @Override
    public <T extends EventBean> void postEvent(@NonNull T event, @NonNull EventListener<T> target) {
        synchronized (cache) {
            Pair<EventBean, EventListener<EventBean>> pair = new Pair<>((EventBean) event, (EventListener<EventBean>) target);
            cache.add(pair);
            if (!started) {
                started = true;
                executorService.execute(this);
            }
        }
    }

    @Override
    @SuppressWarnings("InfiniteLoopStatement")
    public void run() {
        try {
            while (true) {
                synchronized (cache) {
                    if (!cache.isEmpty()) {
                        Pair<EventBean, EventListener<EventBean>> temp = cache.remove(0);
                        temp.second.onEvent(temp.first);
                    }
                }
            }
        } finally { //thread must be interrupted
            synchronized (cache) {
                started = false;
            }
        }

    }
}
