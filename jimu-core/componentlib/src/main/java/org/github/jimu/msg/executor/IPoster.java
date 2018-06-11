package org.github.jimu.msg.executor;

import android.support.annotation.NonNull;

import org.github.jimu.msg.EventListener;
import org.github.jimu.msg.bean.EventBean;

/**
 * <p><b>Package:</b> org.github.jimu.msg.executor </p>
 * <p><b>Project:</b> JIMU </p>
 * <p><b>Classname:</b> IPoster </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2018/4/25.
 */
public interface IPoster {
    <T extends EventBean> void postEvent(@NonNull T event, @NonNull EventListener<T> target);


}
