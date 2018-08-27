package org.github.jimu.msg.executor;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import org.github.jimu.msg.EventListener;
import org.github.jimu.msg.bean.EventBean;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import osp.leobert.android.reportprinter.notation.Bug;

/**
 * <p><b>Package:</b> org.github.jimu.msg.executor </p>
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

    /**
     * todo consider the waste of cpu performance in the infinite loop
     */
    @Override
    @SuppressWarnings("InfiniteLoopStatement")
    @Bug(desc = "consider the waste of cpu performance in the infinite loop")
    public void run() {
        try {
            while (true) {
                synchronized (cache) {
                    if (!cache.isEmpty()) {
                        Pair<EventBean, EventListener<EventBean>> temp = cache.remove(0);
                        if (temp.second != null)
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
