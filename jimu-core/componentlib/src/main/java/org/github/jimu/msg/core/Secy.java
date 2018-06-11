package org.github.jimu.msg.core;

import android.app.Application;
import android.app.Service;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.luojilab.component.componentlib.log.ILogger;
import org.github.jimu.msg.AriseAt;
import org.github.jimu.msg.ConsumeOn;
import org.github.jimu.msg.EventListener;
import org.github.jimu.msg.bean.EventBean;
import org.github.jimu.msg.bean.RemoteEventBean;
import org.github.jimu.msg.bean.State;
import org.github.jimu.msg.executor.IPoster;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <p><b>Package:</b> org.github.jimu.msg.core </p>
 * <p><b>Project:</b> JIMU </p>
 * <p><b>Classname:</b> Secy </p>
 * <p><b>Description:</b> the real realization of EventManager </p>
 * Created by leobert on 2018/4/26.
 */
public final class Secy {

    private final IPoster mainThreadPoster;
    private final IPoster workThreadPoster;
    private final IPoster crossProcessPoster;


    private final SubscriberCache mainThreadSubscriber = new SubscriberCache();

    private final SubscriberCache workThreadSubscriber = new SubscriberCache();

    private final Map<String, Class<? extends MessageBridgeService>> processMsgBridgeServiceMapper;
    /**
     * BridgeAttacher cache;
     * <p>
     * K: processName
     * <p>
     * V: {@link MessageBridgeAttacher}
     */
    private final Map<String, MessageBridgeAttacher> remoteBridgeAttachers = new HashMap<>();
    private Application application;


    public Secy(Application application, IPoster mainThreadPoster,
                IPoster workThreadPoster,
                IPoster crossProcessPoster,
                Map<String, Class<? extends MessageBridgeService>> processMsgBridgeServiceMapper) {
        this.application = application;
        this.mainThreadPoster = mainThreadPoster;
        this.workThreadPoster = workThreadPoster;
        this.crossProcessPoster = crossProcessPoster;
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
                try {
                    attacher.onSubscribe((Class<? extends RemoteEventBean>) eventClz);
                } catch (ClassCastException e) {
                    e.printStackTrace();
                    ILogger.logger.error(ILogger.defaultTag, e.getMessage());
                }
            }
            subscribeEvent(eventClz, consumeOn, listener);
        }
    }

    @NonNull
    private synchronized MessageBridgeAttacher createAttacher(String processName) {
        if (TextUtils.isEmpty(processName)) {
            ILogger.logger.error(ILogger.defaultTag, "cannot execute for a empty process name");
            throw new IllegalArgumentException("processName cannot be empty");
        }
        Class<? extends Service> bridgeServiceClz = null;
        if (processMsgBridgeServiceMapper.containsKey(processName))
            bridgeServiceClz = processMsgBridgeServiceMapper.get(processName);
        else {
            String errorMsg = "cannot find target bridge service for" + processName + " are you missing decline it?";
            ILogger.logger.error(ILogger.defaultTag, errorMsg);
            throw new RuntimeException(errorMsg);
        }

        MessageBridgeAttacher attacher = new MessageBridgeAttacher(processName, bridgeServiceClz, application);

        return attacher;
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
                    postOneOnLocalProcess(temp, threadLocalState, clz);
                }
            } finally {
                threadLocalState.onPosting = false;
            }
        } else {
            ILogger.logger.error(ILogger.defaultTag, "bug!");
        }
    }

    public <T extends RemoteEventBean> void postNormalEventToRemoteProcess(State threadLocalState, T event) {
        threadLocalState.addEvent2Queue(event);

        if (!threadLocalState.onPosting) {
            threadLocalState.onPosting = true;
            ILogger.logger.monitor("on post remote:");
            try {
                while (!threadLocalState.isQueueEmpty()) {
                    RemoteEventBean temp = threadLocalState.peekFromQueue();//, threadState;
                    Class clz = event.getClass();
                    ILogger.logger.monitor("post remote:" + clz.getName());
                    postOne2RemoteProcess(temp);
                }

                ILogger.logger.monitor("all remote posted");
            } finally {
                threadLocalState.onPosting = false;
            }
        } else {
            ILogger.logger.error(ILogger.defaultTag, "bug!");
        }
    }


    private <T extends RemoteEventBean> void postOne2RemoteProcess(T event/*, State state, Class<T> eventClass*/) {
        crossProcessPoster.postEvent(event, EventListener.NONE_NULL);
    }

    /**
     * post one event in local process
     *
     * @param event      event to be post
     * @param state      current thread state of EventManager
     * @param eventClass Class of the event
     * @param <T>        any class impl EventBean {@link EventBean}
     */
    /*private*/ <T extends EventBean> void postOneOnLocalProcess(T event, State state, Class<T> eventClass) {
        SubscriberList<EventBean> mainThreadSubscribers;
        SubscriberList<EventBean> workThreadSubscribers;

        synchronized (this) {
            mainThreadSubscribers = mainThreadSubscriber.get(eventClass);
            workThreadSubscribers = workThreadSubscriber.get(eventClass);
        }

        //send to local process, callback invoked at main thread
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

        //send to local process, callback invoked at background thread
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
