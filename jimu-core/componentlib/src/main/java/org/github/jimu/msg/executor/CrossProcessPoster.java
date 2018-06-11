package org.github.jimu.msg.executor;

import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.luojilab.component.componentlib.log.ILogger;
import org.github.jimu.msg.EventListener;
import org.github.jimu.msg.bean.EventBean;
import org.github.jimu.msg.bean.RemoteEventBean;
import org.github.jimu.msg.core.MessageFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * <p><b>Package:</b> org.github.jimu.msg.executor </p>
 * <p><b>Project:</b> JIMU </p>
 * <p><b>Classname:</b> CrossProcessPoster </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2018/4/25.
 */
public class CrossProcessPoster implements IPoster {

    @Nullable
    private Messenger remoteMessenger;

    public void setRemoteMessenger(@Nullable Messenger remoteMessenger) {
        this.remoteMessenger = remoteMessenger;
        post2Remote();
    }

    private final List<RemoteEventBean> eventCache = new ArrayList<>();

    private <T extends RemoteEventBean> void post(@NonNull T event) {
        synchronized (eventCache) {
            eventCache.add(event);
            post2Remote();
        }
    }

    private void post2Remote() {
        if (remoteMessenger == null) {
            ILogger.logger.monitor("remoteMessenger is not in order ");
            return;
        }

        ILogger.logger.monitor("post to remote process");

        synchronized (eventCache) {
            while (!eventCache.isEmpty()) {
                RemoteEventBean bean = eventCache.remove(0);
                try {
                    remoteMessenger.send(MessageFactory.obtainEventMsg(bean));
                } catch (RemoteException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public <T extends EventBean> void postEvent(@NonNull T event, @NonNull EventListener<T> target) {
        post((RemoteEventBean) event);
    }
}
