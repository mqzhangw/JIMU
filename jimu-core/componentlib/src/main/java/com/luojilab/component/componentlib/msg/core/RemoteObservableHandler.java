package com.luojilab.component.componentlib.msg.core;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;

import com.luojilab.component.componentlib.log.ILogger;
import com.luojilab.component.componentlib.msg.Constants;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p><b>Package:</b> com.luojilab.component.componentlib.msg </p>
 * <p><b>Project:</b> JIMU </p>
 * <p><b>Classname:</b> RemoteObservableHandler </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2018/4/24.
 */
public class RemoteObservableHandler extends Handler {
    private final Map<String, List<WeakReference<Messenger>>> eventSubscriber = new HashMap<>();

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case MessageFactory.WHAT_SUBSCRIBE_CROSS_PROCESS_EVENT:
                handleSubscribe(msg);
                break;
            case Constants.WHAT_POST_EVENT_TO_SUBSCRIBER:
                handlePostEvent(msg);
                break;
            default:
                break;
        }
    }

    private void handleSubscribe(Message msg) {
        Bundle bundle = msg.getData();
        String eventClz = bundle.getString(MessageFactory.BUNDLE_STR_EVENT_CLZ);

        List<WeakReference<Messenger>> subscribes;
        if (!eventSubscriber.containsKey(eventClz)) {
            subscribes = new ArrayList<>();
            WeakReference<Messenger> subscriberRef = new WeakReference<>(msg.replyTo);
            subscribes.add(subscriberRef);
            eventSubscriber.put(eventClz, subscribes);
        } else {
            subscribes = eventSubscriber.get(eventClz);
            WeakReference<Messenger> subscriberRef = new WeakReference<>(msg.replyTo);
            subscribes.add(subscriberRef);
        }
    }

    private void handlePostEvent(Message msg) {
        Bundle bundle = msg.getData();
        String eventClz = bundle.getString(Constants.BUNDLE_STR_EVENT_CLZ);
        if (TextUtils.isEmpty(eventClz)) {
            ILogger.logger.error(ILogger.defaultTag,"cannot handle non class event");
            return;
        }

        List<WeakReference<Messenger>> subscribes = eventSubscriber.get(eventClz);
        if (subscribes == null)
            return;

        for (int i = 0;i<subscribes.size();i++) {
            WeakReference<Messenger> subscriberRef = subscribes.get(i);
            if (subscriberRef == null || subscriberRef.get() == null) {
                subscribes.remove(i);
                i--;
                continue;
            }

            Messenger messenger = subscriberRef.get();

            Message cm = Message.obtain();
            cm.what = Constants.WHAT_RECEIVE_EVENT_FROM_REMOTE;
            cm.setData(msg.getData());
            try {
                messenger.send(cm);
            } catch (RemoteException e) {
                e.printStackTrace();
                //remove the error subscriber
                subscriberRef.clear();
                subscribes.remove(i);
                i--;
//                continue;
            }
        }

    }
}
