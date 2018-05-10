package com.luojilab.component.componentlib.msg.bean;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.luojilab.component.componentlib.msg.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p><b>Package:</b> com.luojilab.component.componentlib.msg.bean </p>
 * <p><b>Project:</b> JIMU </p>
 * <p><b>Classname:</b> State </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2018/4/25.
 */
public class State {
    private final List<Object> eventQueue = new ArrayList<>();
    public boolean onPosting= false;
    public final boolean onMainThread;

    public State() {
        onMainThread = Utils.isMainThread();
    }

    public <T extends EventBean>void addEvent2Queue(@NonNull T event) {
        eventQueue.add(event);
    }

    public boolean isQueueEmpty() {
        return eventQueue.isEmpty();
    }

    @VisibleForTesting
    public int queneSize() {
        return eventQueue.size();
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends EventBean> T peekFromQueue() {
        try {
            return (T) eventQueue.remove(0);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }
}
