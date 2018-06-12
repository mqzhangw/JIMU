package org.github.jimu.msg;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p><b>Package:</b> org.github.jimu.msg </p>
 * <p><b>Project:</b> jimu-core </p>
 * <p><b>Classname:</b> MsgBridgeService </p>
 * <p><b>Description:</b> annotation for MessageBridgeService </p>
 * Created by leobert on 2018/6/11.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME) // not sure if it is applicable to using CLASS
public @interface MsgBridgeService {
    /**
     * @return the full name of the process that the service runs in
     */
    String workProcessName();
}
