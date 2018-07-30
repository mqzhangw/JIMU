package org.github.jimu.msg.notation;

import org.github.jimu.msg.bean.EventBean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p><b>Package:</b> org.github.jimu.msg.notation </p>
 * <p><b>Project:</b> jimu-core </p>
 * <p><b>Classname:</b> Event </p>
 * <p><b>Description:</b> an notation to notate the event class you subscribe to </p>
 * Created by leobert on 2018/7/30.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Event {
    Class<? extends EventBean> clz();
}
