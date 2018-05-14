package com.luojilab.component.componentlib.msg.core;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.NonNull;

import com.luojilab.component.componentlib.msg.bean.RemoteEventBean;

/**
 * <p><b>Package:</b> com.luojilab.component.componentlib.msg.core </p>
 * <p><b>Project:</b> JIMU </p>
 * <p><b>Classname:</b> SubscribeMessage </p>
 * <p><b>Description:</b> </p>
 * Created by leobert on 2018/4/24.
 */
public class MessageFactory {
    public static final int WHAT_SUBSCRIBE_CROSS_PROCESS_EVENT =  9999;

    public static final int WHAT_SEND_CROSS_PROCESS_EVENT =  10000;

    public static final String BUNDLE_STR_EVENT_CLZ = "bundle_event_clz";

    public static final String BUNDLE_STR_EVENT = "bundle_event_parcelable";


    /**
     * obtain subscribe message, contains the key-point information
     * of on cross-process subscriber and the event class it subscribes to
     * @param consumerMessenger Local side Messenger,if the event arise at the remote,
     *                          remote will send it through this
     * @param eventClz Class of the event want to subscribe
     * @return the subscribe message
     */
    public static Message obtainSubscribeMsg(Messenger consumerMessenger, Class eventClz) {
        Message message = Message.obtain();// new Message();
        message.what = WHAT_SUBSCRIBE_CROSS_PROCESS_EVENT;
        message.replyTo = consumerMessenger;
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_STR_EVENT_CLZ,eventClz.getName());
        message.setData(bundle);
        return message;
    }

    public static <T extends RemoteEventBean> Message obtainEventMsg(@NonNull T event) {
        Message message = Message.obtain();// new Message();
        message.what = WHAT_SEND_CROSS_PROCESS_EVENT;
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_STR_EVENT_CLZ,event.getClass().getName());
        bundle.putParcelable(BUNDLE_STR_EVENT,event);
        message.setData(bundle);
        return message;
    }
}
