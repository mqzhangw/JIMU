package com.luojilab.component.componentlib.msg.bean;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;

/**
 * <p><b>Package:</b> com.luojilab.component.componentlib.msg.bean </p>
 * <p><b>Project:</b> JIMU </p>
 * <p><b>Classname:</b> SubscribeMessage </p>
 * <p><b>Description:</b> a wrap of message, contains the key-point information
 * of on cross-process subscriber and the event class it subscribes to</p>
 * Created by leobert on 2018/4/24.
 */
public class SubscribeMessage {
    private Messenger consumerMessenger;
    public static final int WHAT_SUBSCRIBE_CROSS_PROCESS_EVENT =  9999;

    private Class eventClz;
    public static final String BUNDLE_STR_EVENT_CLZ = "bundle_event_clz";

    public SubscribeMessage(Messenger consumerMessenger,Class eventClz) {
        this.consumerMessenger = consumerMessenger;
        this.eventClz = eventClz;
    }

    public Message obtain() {
        Message message = new Message();
        message.what = WHAT_SUBSCRIBE_CROSS_PROCESS_EVENT;
        message.replyTo = consumerMessenger;
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_STR_EVENT_CLZ,eventClz.getName());
        message.setData(bundle);
        return message;
    }
}
