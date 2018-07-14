package org.github.jimu.msg.notation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p><b>Package:</b> org.github.jimu.msg.notation </p>
 * <p><b>Project:</b> jimu-core </p>
 * <p><b>Classname:</b> From </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2018/7/14.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface From {

    boolean local() default true;

    /**
     * maybe we should use a alias when integrate-test the sub component
     *
     * @return the full process name alias if from remote process
     */
    String pa() default "";
}
