package com.luojilab.component.componentlib.msg.executor;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.luojilab.component.componentlib.msg.EventListener;
import com.luojilab.component.componentlib.msg.bean.EventBean;

import java.util.ArrayList;
import java.util.List;

/**
 * <p><b>Package:</b> com.luojilab.component.componentlib.msg.executor </p>
 * <p><b>Project:</b> JIMU </p>
 * <p><b>Classname:</b> LocalProcessPoster </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2018/4/25.
 */
public class LocalProcessMainThreadPoster extends Handler implements IPoster {

    private final List<Pair<EventBean, EventListener<EventBean>>> cache;
    private boolean started;
    private static final long threshold = 10;

    // TODO: 2018/4/27  考虑负载问题

    public LocalProcessMainThreadPoster() {
        super(Looper.getMainLooper());
        cache = new ArrayList<>();
    }

    @Override
    public <T extends EventBean> void postEvent(@NonNull T event, @NonNull EventListener<T> target) {
        synchronized (cache) {

            Pair<EventBean, EventListener<EventBean>> pair = new Pair<>((EventBean)event, (EventListener<EventBean>)target);
            cache.add(pair);
            if (!started) {
                removeMessages(0);
                start();
            }
        }
    }

    @Override
    public void handleMessage(Message msg) {
//        super.handleMessage(msg);
        long startStamp = System.currentTimeMillis();
        while (true) {
            long t = System.currentTimeMillis();
            if (t > startStamp + threshold) {
                started = false;
                resumeLater();
                return;
            }
            synchronized (cache) {
                if (cache.size() > 0) {
                    Pair<EventBean, EventListener<EventBean>> temp = cache.remove(0);
                    try {
                        temp.second.onEvent(temp.first);
                    } finally {
                        //ignore
                    }
                } else {
                    started = false;
                    resumeLater();
                    break;
                }
            }
        }
    }

    private void start() {
        sendEmptyMessage(0);
    }

    private void resumeLater() {
        sendEmptyMessageDelayed(0, 100);
    }
}
