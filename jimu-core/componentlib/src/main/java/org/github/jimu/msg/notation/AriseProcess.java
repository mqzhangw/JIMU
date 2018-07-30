package org.github.jimu.msg.notation;

import org.github.jimu.msg.bean.ConsumerMeta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p><b>Package:</b> org.github.jimu.msg.notation </p>
 * <p><b>Project:</b> jimu-core </p>
 * <p><b>Classname:</b> AriseProcess </p>
 * <p><b>Description:</b> all event are created in the process it running at,if the subscriber
 * is running in the same process, we call say that the event arises at the local process, otherwise,
 * a remote process.
 * But, when we code the code, we knows the arise process of event, or said: the component itself knows
 * what process the event arise at.
 * The sense may be complicated, so we just notate the method to tell what process the event arise at, then
 * use {@link ConsumerMeta} and {@link Consumer} to tell what process the consumer
 * ran at.
 * </p>
 * Created by leobert on 2018/7/14.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface AriseProcess {

    /**
     * maybe we should use a alias when integrate-test the sub component
     *
     * @return the full process name alias if from remote process, if is the default ""
     * means it arise at the default process when application is created.
     */
    String pa() default "";
}
