package org.github.jimu.msg.core;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import com.luojilab.component.componentlib.log.ILogger;
import org.github.jimu.msg.Constants;
import org.github.jimu.msg.EventManager;
import org.github.jimu.msg.bean.RemoteEventBean;
import org.github.jimu.msg.bean.State;

/**
 * <p><b>Package:</b> org.github.jimu.msg </p>
 * <p><b>Project:</b> JIMU </p>
 * <p><b>Classname:</b> CrossSubscriberHandler </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2018/4/25.
 */
public class CrossSubscriberHandler extends Handler {

    CrossSubscriberHandler() {
    }

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

    /**
     * handle event posting in current process after receiving an event from remote process
     *
     * @param message msg contains event info
     */
    private void postRemoteEventInCurrentProcess(@NonNull Message message) {
        if (message.getData() == null) {
            ILogger.logger.error(ILogger.defaultTag, "subscriber handle get a message without bundle data");
            return;
        }
        Bundle data = message.getData();
        data.setClassLoader(getClass().getClassLoader());
        if (!data.containsKey(Constants.BUNDLE_PARCEL_EVENT) ||
                !data.containsKey(Constants.BUNDLE_STR_EVENT_CLZ)) {
            ILogger.logger.error(ILogger.defaultTag, "subscriber handle get a message missing params in bundle");
            return;
        }

        RemoteEventBean event = data.getParcelable(Constants.BUNDLE_PARCEL_EVENT);
        String eventClzPath = data.getString(Constants.BUNDLE_STR_EVENT_CLZ);
        try {
            Class clz = Class.forName(eventClzPath);
            EventManager.getInstance().fastHandleLocalProcessEvent(this, event, clz);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            ILogger.logger.error(ILogger.defaultTag, "wtf:" + e.getMessage());
        }
    }

    public void onFastHandleLocalProcessEvent(Secy secy, State state, RemoteEventBean event, Class clz) {
        secy.postOneOnLocalProcess(event, state, clz);
    }
}
