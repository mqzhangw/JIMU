package org.github.jimu.msg.notation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p><b>Package:</b> org.github.jimu.msg.notation </p>
 * <p><b>Project:</b> jimu-core </p>
 * <p><b>Classname:</b> Listener </p>
 * <p><b>Description:</b> notate the parameter {@link org.github.jimu.msg.EventListener} </p>
 * Created by leobert on 2018/7/14.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Listener {
    Class eventClz();
}
