package org.github.jimu.msg.core;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.luojilab.component.componentlib.log.ILogger;
import org.github.jimu.msg.bean.RemoteEventBean;

import java.util.ArrayList;
import java.util.List;

/**
 * <p><b>Package:</b> org.github.jimu.msg.core </p>
 * <p><b>Project:</b> JIMU </p>
 * <p><b>Classname:</b> MessageBridgeAttacher </p>
 * <p><b>Description:</b> cross process messenger attacher, maintain the bridge and subscribe
 * the event subscriber delegate to the remote process via the bridge</p>
 * Created by leobert on 2018/4/27.
 */

// TODO: 2018/5/12 多线程测试下锁
final class MessageBridgeAttacher {
    private String remoteProcessName;
    private Class<?> msgBridgeServiceClz;
    private Application application;

    @Nullable
    private Messenger remoteMessenger;//keep jobs in subscribeCache if still on initializing
    private Messenger localMessenger = new Messenger(new CrossSubscriberHandler());

    private boolean hasAttached = false;

    private List<Class> subscribeCache = new ArrayList<>();
    private final List<Class> trash = new ArrayList<>();


    MessageBridgeAttacher(String remoteProcessName, Class<?> msgBridgeServiceClz, Application application) {
        this.remoteProcessName = remoteProcessName;
        this.msgBridgeServiceClz = msgBridgeServiceClz;
        this.application = application;

        onCreate();
    }

    private void onCreate() {
        Intent intent = new Intent(application, msgBridgeServiceClz);

        PackageManager packageManager = application.getPackageManager();
        List<ResolveInfo> targets = packageManager.queryIntentServices(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (targets == null || targets.isEmpty()) {
            ILogger.logger.error(ILogger.defaultTag, "cannot attach remote message bridge for process:" + remoteProcessName);
            // TODO: 2018/4/27 throw exception

            return;
        }

        application.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                synchronized (MessageBridgeAttacher.this) {
                    remoteMessenger = new Messenger(service);
                    hasAttached = true;
                    subscribe2Remote();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Context.BIND_AUTO_CREATE);

    }

    public void onSubscribe(Class<? extends RemoteEventBean> clz) {
        synchronized (this) {
            if (trash.contains(clz)) {
                ILogger.logger.monitor("has attach messenger to remote " + remoteProcessName + " for:" + clz.getName());
                return;
            }
            subscribeCache.add(clz);
            subscribe2Remote();
        }
    }

    private void subscribe2Remote() {
        if (!hasAttached)
            return;
        while (!subscribeCache.isEmpty()) {
            Class clz = subscribeCache.remove(0);
            try {
                remoteMessenger.send(MessageFactory.obtainSubscribeMsg(localMessenger, clz));

                //consider ack?
                trash.add(clz);
            } catch (RemoteException | NullPointerException e) {
                e.printStackTrace();
            }
        }
    }


}
