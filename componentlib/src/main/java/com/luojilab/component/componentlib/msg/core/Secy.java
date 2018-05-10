package com.luojilab.component.componentlib.msg.core;

import android.app.Application;
import android.support.annotation.NonNull;

import com.luojilab.component.componentlib.log.ILogger;
import com.luojilab.component.componentlib.msg.AriseAt;
import com.luojilab.component.componentlib.msg.ConsumeOn;
import com.luojilab.component.componentlib.msg.EventListener;
import com.luojilab.component.componentlib.msg.bean.EventBean;
import com.luojilab.component.componentlib.msg.bean.State;
import com.luojilab.component.componentlib.msg.executor.IPoster;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <p><b>Package:</b> com.luojilab.component.componentlib.msg.core </p>
 * <p><b>Project:</b> JIMU </p>
 * <p><b>Classname:</b> Secy </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2018/4/26.
 */
public final class Secy {

    private final IPoster mainThreadPoster;
    private final IPoster workThreadPoster;

    private final Map<String,Class<? extends MessageBridgeService>> processMsgBridgeServiceMapper;

    private final SubscriberCache mainThreadSubscriber = new SubscriberCache();

    private final SubscriberCache workThreadSubscriber = new SubscriberCache();

    private final Map<String, MessageBridgeAttacher> remoteBridgeAttachers = new HashMap<>();
    private Application application;



    public Secy(Application application, IPoster mainThreadPoster, IPoster workThreadPoster,
                Map<String, Class<? extends MessageBridgeService>> processMsgBridgeServiceMapper) {
        this.application = application;
        this.mainThreadPoster = mainThreadPoster;
        this.workThreadPoster = workThreadPoster;
        this.processMsgBridgeServiceMapper = processMsgBridgeServiceMapper;
    }




    public <T extends EventBean> void subscribe(@NonNull Class<T> eventClz,
                                                @NonNull AriseAt ariseAt,
                                                @NonNull ConsumeOn consumeOn,
                                                @NonNull EventListener<T> listener) {
        synchronized (Secy.class) {
            if (!ariseAt.isLocal()) {
                //寻找绑定服务
                String processFullName = ariseAt.getProcessFullName();
                MessageBridgeAttacher attacher;
                if (remoteBridgeAttachers.containsKey(processFullName))
                    attacher = remoteBridgeAttachers.get(processFullName);
                else {
                    attacher = createAttacher(processFullName);
                    remoteBridgeAttachers.put(processFullName, attacher);
                }

                attacher.onSubscribe(eventClz);
            }
            subscribeEvent(eventClz, consumeOn, listener);
        }
    }

    @NonNull
    private MessageBridgeAttacher createAttacher(String processName) {
        // TODO: 2018/4/27
        return null;
    }

    //caution: maintain thread safety!
    private <T extends EventBean> void subscribeEvent(@NonNull Class<T> eventClz,
                                                      @NonNull ConsumeOn consumeOn,
                                                      @NonNull EventListener<T> listener) {
        SubscriberCache cache;
        if (ConsumeOn.Main.equals(consumeOn)) {
            cache = mainThreadSubscriber;
        } else {
            cache = workThreadSubscriber;
        }

        SubscriberList<EventBean> subscribers = cache.get(eventClz);

        if (subscribers == null) {
            subscribers = new SubscriberList<>();
            cache.put(eventClz, subscribers);
        }
        WeakReference<EventListener<EventBean>> listenerRef = new WeakReference<>((EventListener<EventBean>) listener);

        //ignore duplicate subscriber
        subscribers.add(listenerRef);

    }

    public <T extends EventBean> void postNormalEventOnLocalProcess(State threadLocalState, T event) {

        threadLocalState.addEvent2Queue(event);

        if (!threadLocalState.onPosting) {
            threadLocalState.onPosting = true;

            try {
                while (!threadLocalState.isQueueEmpty()) {
                    EventBean temp = threadLocalState.peekFromQueue();//, threadState;
                    Class clz = event.getClass();
                    postOne(temp, threadLocalState, clz);
                }
            } finally {
                threadLocalState.onPosting = false;
            }
        } else {
            ILogger.logger.error(ILogger.defaultTag, "bug!");
        }
    }


    private <T extends EventBean> void postOne(T event, State state, Class<T> eventClass) {
        SubscriberList<EventBean> mainThreadSubscribers;
        SubscriberList<EventBean> workThreadSubscribers;

        synchronized (this) {
            mainThreadSubscribers = mainThreadSubscriber.get(eventClass);
            workThreadSubscribers = workThreadSubscriber.get(eventClass);
        }

        if (mainThreadSubscribers != null && !mainThreadSubscribers.isEmpty()) {
            for (WeakReference<EventListener<EventBean>> subscriberRef : mainThreadSubscribers) {
                if (subscriberRef.get() != null) {
                    try {
                        if (state.onMainThread)
                            subscriberRef.get().onEvent(event);
                        else
                            mainThreadPoster.postEvent(event, subscriberRef.get());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (workThreadSubscribers != null && !workThreadSubscribers.isEmpty()) {
            for (WeakReference<EventListener<EventBean>> subscriberRef : workThreadSubscribers) {
                if (subscriberRef.get() != null) {
                    try {
//                        if (!state.onMainThread)
//                            subscriberRef.get().onEvent(event);
//                        else //always use the work thread
                        workThreadPoster.postEvent(event, subscriberRef.get());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public <T extends EventBean> void unsubscribe(final @NonNull EventListener<T> listener) {
        removeSubscribe(mainThreadSubscriber, listener);
        removeSubscribe(workThreadSubscriber, listener);
    }

    private void removeSubscribe(SubscriberCache cache, EventListener listener) {
        synchronized (Secy.class) {
            Set<Class> keys = cache.keySet();
            for (Class clz : keys) {
                SubscriberList list = cache.get(clz);
                if (list == null)
                    continue;
                boolean b = list.removeCallback(listener);
                if (b) {
                    ILogger.logger.monitor("remove one subscribe success");
                }
            }
        }
    }
}
