package com.luojilab.component.componentlib.msg.core;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.luojilab.component.componentlib.log.ILogger;
import com.luojilab.component.componentlib.msg.Constants;

/**
 * <p><b>Package:</b> com.luojilab.component.componentlib.msg </p>
 * <p><b>Project:</b> JIMU </p>
 * <p><b>Classname:</b> CrossSubscriberHandler </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2018/4/25.
 */
public class CrossSubscriberHandler extends Handler {

    @Override
    public void handleMessage(Message msg) {
//        super.handleMessage(msg);
        if (msg == null) {
            ILogger.logger.error(ILogger.defaultTag, "subscriber handle get a null message");
            return;
        }


        switch (msg.what) {
            case Constants.WHAT_RECEIVE_EVENT_FROM_REMOTE:
                postRemoteEventInCurrentProcess(msg);
                break;
            default:
                break;
        }
    }

    private void postRemoteEventInCurrentProcess(@NonNull Message message) {
        if (message.getData() == null) {
            ILogger.logger.error(ILogger.defaultTag, "subscriber handle get a message without bundle data");
            return;
        }
        Bundle data = message.getData();
        if (!data.containsKey(Constants.BUNDLE_PAR_EVENT) ||
                !data.containsKey(Constants.BUNDLE_STR_EVENT_CLZ)) {
            ILogger.logger.error(ILogger.defaultTag, "subscriber handle get a message missing params in bundle");
            return;
        }

        Parcelable event = data.getParcelable(Constants.BUNDLE_PAR_EVENT);
        String eventClzPath = data.getString(Constants.BUNDLE_STR_EVENT_CLZ);


    }
}
