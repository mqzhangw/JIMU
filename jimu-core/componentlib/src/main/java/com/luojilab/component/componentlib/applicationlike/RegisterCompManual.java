package com.luojilab.component.componentlib.applicationlike;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <p><b>Package:</b> com.dd.buildgradle.exten </p>
 * <p><b>Project:</b> jimu-core </p>
 * <p><b>Classname:</b> RegisterCompManual </p>
 * <p><b>Description:</b> annotate the Applike with this annotation
 * represent to register it manual.
 * it will never auto register </p>
 * Created by leobert on 2018/5/10.
 */
@Retention(RetentionPolicy.CLASS)
public @interface RegisterCompManual {
}
