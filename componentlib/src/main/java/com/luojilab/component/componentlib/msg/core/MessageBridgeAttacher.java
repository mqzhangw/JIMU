package com.luojilab.component.componentlib.msg.core;

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

import com.luojilab.component.componentlib.log.ILogger;
import com.luojilab.component.componentlib.msg.bean.EventBean;
import com.luojilab.component.componentlib.msg.bean.SubscribeMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * <p><b>Package:</b> com.luojilab.component.componentlib.msg.core </p>
 * <p><b>Project:</b> JIMU </p>
 * <p><b>Classname:</b> MessageBridgeAttacher </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2018/4/27.
 */
final class MessageBridgeAttacher {
    private String remoteProcessName;
    private Class<?> msgBridgeServiceClz;
    private Application application;

    private Messenger remoteMessenger;
    private Messenger localMessenger = new Messenger(new CrossSubscriberHandler());

    private boolean hasAttached = false;

    private List<Class> cache = new ArrayList<>();
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
            ILogger.logger.error(ILogger.defaultTag, "cannot attach remove message bridge for process:" + remoteProcessName);
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

    public void onSubscribe(Class<? extends EventBean> clz) {
        synchronized (this) {
            if (trash.contains(clz)) {
                ILogger.logger.monitor("has attach messenger to remote "+ remoteProcessName+" for:"+clz.getName());
                return;
            }
            cache.add(clz);
            subscribe2Remote();
        }
    }

    private void subscribe2Remote() {
        if (!hasAttached)
            return;
        while (!cache.isEmpty()) {
            Class clz = cache.remove(0);
            try {
                remoteMessenger.send(new SubscribeMessage(localMessenger, clz).obtain());

                //consider ack?
                trash.add(clz);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

}
