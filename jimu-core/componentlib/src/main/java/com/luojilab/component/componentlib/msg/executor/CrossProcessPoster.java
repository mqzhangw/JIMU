package com.luojilab.component.componentlib.msg.executor;

import android.os.RemoteException;
import android.support.annotation.NonNull;

import com.luojilab.component.componentlib.msg.EventListener;
import com.luojilab.component.componentlib.msg.bean.EventBean;
import com.luojilab.component.componentlib.msg.bean.RemoteEventBean;
import com.luojilab.component.componentlib.msg.core.MessageFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * <p><b>Package:</b> com.luojilab.component.componentlib.msg.executor </p>
 * <p><b>Project:</b> JIMU </p>
 * <p><b>Classname:</b> CrossProcessPoster </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2018/4/25.
 */
public class CrossProcessPoster implements IPoster{


    private final List<RemoteEventBean> eventCache = new ArrayList<>();

    public <T extends RemoteEventBean> void post(@NonNull T event) {
        synchronized (eventCache) {
            eventCache.add(event);
//            post2Remote();
        }
    }

//    private void post2Remote() {
//        if (!hasAttached)
//            return;
//        synchronized (eventCache) {
//            while (!eventCache.isEmpty()) {
//                RemoteEventBean bean = eventCache.remove(0);
//                try {
//                    remoteMessenger.send(MessageFactory.obtainEventMsg(bean));
//
//                } catch (RemoteException | NullPointerException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }


    @Override
    public <T extends EventBean> void postEvent(@NonNull T event, @NonNull EventListener<T> target) {

    }
}
