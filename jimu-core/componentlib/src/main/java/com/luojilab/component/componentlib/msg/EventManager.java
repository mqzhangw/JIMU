package com.luojilab.component.componentlib.msg;

import android.app.Application;
import android.support.annotation.NonNull;

import com.luojilab.component.componentlib.log.ILogger;
import com.luojilab.component.componentlib.msg.bean.EventBean;
import com.luojilab.component.componentlib.msg.bean.RemoteEventBean;
import com.luojilab.component.componentlib.msg.bean.State;
import com.luojilab.component.componentlib.msg.core.MessageBridgeService;
import com.luojilab.component.componentlib.msg.core.Secy;
import com.luojilab.component.componentlib.msg.executor.LocalProcessBackgroundPoster;
import com.luojilab.component.componentlib.msg.executor.LocalProcessMainThreadPoster;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p><b>Package:</b> com.luojilab.component.componentlib.msg </p>
 * <p><b>Project:</b> JIMU </p>
 * <p><b>Classname:</b> EventManager </p>
 * <p><b>Description:</b> TODO </p>
 * use package-local to force use api
 * Created by leobert on 2018/4/25.
 */
public final class EventManager {
    private static final ExecutorService DEFAULT_EXECUTOR_SERVICE =
            Executors.newCachedThreadPool();
    private static final Map<String, Class<? extends MessageBridgeService>> processMsgBridgeServiceMapper
            = new HashMap<>();

    private ThreadLocal<State> stateThreadLocal = new ThreadLocal<State>() {
        @Override
        protected State initialValue() {
            return new State();
        }
    };

    public static void init(Application application) {
        if (instance == null) {
            synchronized (EventManager.class) {
                if (instance == null) {
                    instance = new EventManager(application);
                }
            }
        } else {
            ILogger.logger.monitor("EventManager has initialized");
        }
    }

    private EventManager(Application application) {
        secy = new Secy(application,new LocalProcessMainThreadPoster(),
                new LocalProcessBackgroundPoster(DEFAULT_EXECUTOR_SERVICE),
                processMsgBridgeServiceMapper);
    }

    private static volatile EventManager instance;

    private Secy secy;

    public static EventManager getInstance() {
        if (instance == null) {
//            synchronized (EventManager.class) {
//                if (instance == null) {
//                    instance = new EventManager();
//                }
//            }
            throw new IllegalStateException("you must call init(Application app) at first");
        }
        return instance;
    }


//    ExecutorService executorService = DEFAULT_EXECUTOR_SERVICE;

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

        secy.subscribe(eventClz, eventType, consumeOn, listener);
    }

    public <T extends EventBean> void unsubscribe(@NonNull EventListener<T> listener) {
        secy.unsubscribe(listener);
    }


    ///////////////////////////////////////////////////////////////////////////
    // post
    ///////////////////////////////////////////////////////////////////////////

    public <T extends EventBean> void postEvent(@NonNull T event) {
        if (event == null) {
            ILogger.logger.monitor("cannot post null");
            return;
        }
        secy.postNormalEventOnLocalProcess(stateThreadLocal.get(), event);

        if (event instanceof RemoteEventBean) {
            RemoteEventBean remoteEventBean = (RemoteEventBean) event;
            secy.postNormalEventToRemoteProcess(stateThreadLocal.get(),remoteEventBean);
        }
    }


}
