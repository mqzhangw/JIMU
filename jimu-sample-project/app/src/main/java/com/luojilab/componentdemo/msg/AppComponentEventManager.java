package com.luojilab.componentdemo.msg;

import com.luojilab.componentdemo.msg.event.EventA;
import com.luojilab.componentdemo.msg.event.EventB;

import org.github.jimu.msg.bean.ConsumerMeta;
import org.github.jimu.msg.notation.AriseProcess;
import org.github.jimu.msg.notation.Consumer;
import org.github.jimu.msg.notation.Event;

/**
 * <p><b>Package:</b> com.luojilab.componentdemo.msg </p>
 * <p><b>Project:</b> jimu-sample-project </p>
 * <p><b>Classname:</b> AppComponentEventManager </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2018/7/30.
 */
public interface AppComponentEventManager {
    @AriseProcess()
    @Event(clz = EventA.class)
    void subscribeEventA(@Consumer ConsumerMeta meta);

    @AriseProcess(pa = ":remote")
    @Event(clz = EventB.class)
    void subscribeEventB(@Consumer ConsumerMeta meta);
}
