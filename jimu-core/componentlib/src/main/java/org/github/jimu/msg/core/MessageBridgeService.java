package org.github.jimu.msg.core;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Messenger;

import com.luojilab.component.componentlib.log.ILogger;
import org.github.jimu.msg.EventManager;
import org.github.jimu.msg.Utils;

/**
 * <p><b>Package:</b> org.github.jimu.msg </p>
 * <p><b>Project:</b> JIMU </p>
 * <p><b>Classname:</b> MessageBridgeService </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2018/4/25.
 */
public abstract class MessageBridgeService extends Service {

    private Messenger currentProcessMessenger = new Messenger(new RemoteObservableHandler());

    @Override
    public final IBinder onBind(Intent intent) {
        String pn = Utils.getProcessName();
        ILogger.logger.monitor("set messenger in order: " + pn);
        EventManager.getInstance().updateMessenger(pn, currentProcessMessenger);
        return currentProcessMessenger.getBinder();
    }
}
