package com.luojilab.component.componentlib.msg;

import android.support.annotation.NonNull;

import com.luojilab.component.componentlib.log.ILogger;
import com.luojilab.component.componentlib.msg.bean.EventBean;
import com.luojilab.component.componentlib.msg.bean.State;
import com.luojilab.component.componentlib.msg.core.Secy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p><b>Package:</b> com.luojilab.component.componentlib.msg </p>
 * <p><b>Project:</b> JIMU </p>
 * <p><b>Classname:</b> EventManager </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2018/4/25.
 */
public final class EventManager {
    private final static ExecutorService DEFAULT_EXECUTOR_SERVICE =
            Executors.newCachedThreadPool();

    private ThreadLocal<State> stateThreadLocal = new ThreadLocal<State>() {
        @Override
        protected State initialValue() {
            return new State();
        }
    };

    private EventManager() {
        secy = new Secy();
    }

    private static volatile EventManager instance;

    private Secy secy;

    public static EventManager getInstance() {
        if (instance == null) {//锁细化
            synchronized (EventManager.class) {
                if (instance == null) {
                    instance = new EventManager();
                }
            }
        }
        return instance;
    }


    ExecutorService executorService = DEFAULT_EXECUTOR_SERVICE;

    ///////////////////////////////////////////////////////////////////////////
    // subscribe
    ///////////////////////////////////////////////////////////////////////////

    public <T extends EventBean> void subscribe(@NonNull Class<T> eventClz,
                                                @NonNull EventListener<T> listener) {
        this.subscribe(eventClz, AriseAt.local(), listener);
    }

    public <T extends EventBean> void subscribe(@NonNull Class<T> eventClz,
                                                @NonNull AriseAt eventType,
                                                @NonNull EventListener<T> listener) {
        this.subscribe(eventClz, eventType, ConsumeOn.Main, listener);
    }

    public <T extends EventBean> void subscribe(@NonNull Class<T> eventClz,
                                                @NonNull AriseAt eventType,
                                                @NonNull ConsumeOn consumeOn,
                                                @NonNull EventListener<T> listener) {
        ILogger.logger.monitor(">>> subscribe " + eventClz
                + "\rcurrentProcess:" + Utils.getProcessName()
                + "\rconsume on:" + consumeOn);
        eventType.log();

        secy.subscribe(eventClz,eventType,consumeOn,listener);
    }


    ///////////////////////////////////////////////////////////////////////////
    // post
    ///////////////////////////////////////////////////////////////////////////

    public <T> void postEvent(T event) {

    }

    public void info() {
// TODO: 2018/4/26 print current state info in logcat
    }

}
