package org.github.jimu.msg;

import android.app.Application;
import android.os.Messenger;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.luojilab.component.componentlib.log.ILogger;

import org.github.jimu.msg.bean.EventBean;
import org.github.jimu.msg.bean.ManagerMethod;
import org.github.jimu.msg.bean.RemoteEventBean;
import org.github.jimu.msg.bean.State;
import org.github.jimu.msg.core.CrossSubscriberHandler;
import org.github.jimu.msg.core.MessageBridgeService;
import org.github.jimu.msg.core.Secy;
import org.github.jimu.msg.executor.CrossProcessPoster;
import org.github.jimu.msg.executor.LocalProcessBackgroundPoster;
import org.github.jimu.msg.executor.LocalProcessMainThreadPoster;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import osp.leobert.android.reportprinter.notation.ChangeLog;

/**
 * <p><b>Package:</b> org.github.jimu.msg </p>
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

    private ThreadLocal<State> stateThreadLocal2 = new ThreadLocal<State>() {
        @Override
        protected State initialValue() {
            return new State();
        }
    };

    private CrossProcessPoster crossProcessPoster;

    public static void appendMapper(@NonNull String s, @NonNull Class<? extends MessageBridgeService> serviceClz) {
        if (!TextUtils.isEmpty(s))
            processMsgBridgeServiceMapper.put(s, serviceClz);
    }

    public static void init(Application application) {
        if (instance == null) {
            synchronized (EventManager.class) {
                if (instance == null) {
                    instance = new EventManager(application);
                }
            }
        } else {
            ILogger.logger.monitor("EventManager has been initialized previous");
        }
    }


    private EventManager(Application application) {
        crossProcessPoster = new CrossProcessPoster();
        secy = new Secy(application, new LocalProcessMainThreadPoster(),
                new LocalProcessBackgroundPoster(DEFAULT_EXECUTOR_SERVICE),
                crossProcessPoster,
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
            secy.postNormalEventToRemoteProcess(stateThreadLocal2.get(), remoteEventBean);
        }
    }


    public void updateMessenger(String processName, Messenger currentProcessMessenger) {
        ILogger.logger.monitor("eventManager updateMessenger :" + processName
                + "; check:" + Utils.getProcessName() + ";em:" + getInstance().toString()
                + ";poster:" + crossProcessPoster.toString());
        crossProcessPoster.setRemoteMessenger(currentProcessMessenger);
    }

    public void fastHandleLocalProcessEvent(CrossSubscriberHandler handler, RemoteEventBean event, Class clz) {
        handler.onFastHandleLocalProcessEvent(secy, stateThreadLocal.get(), event, clz);
    }

    @SuppressWarnings("unchecked")
    @ChangeLog(version = "1.3.3", changes = {
            "add static factory to create proxy for manager api"
    })
    public static <T> T create(final Class<T> service) {
        Utils.validateCompoEventManagerInterface(service);

        return (T) Proxy.newProxyInstance(service.getClassLoader(),
                new Class<?>[]{service},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if (method.getDeclaringClass() == Object.class) { //avoid proxy the inherited method from Object
                            return method.invoke(this, args);
                        }

                        ManagerMethod.parse(method).invoke(args);
                        return null;
                    }
                });
    }
}
