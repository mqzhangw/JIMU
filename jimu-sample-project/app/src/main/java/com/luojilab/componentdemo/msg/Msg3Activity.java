package com.luojilab.componentdemo.msg;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.view.View;

import org.github.jimu.msg.ConsumeOn;
import org.github.jimu.msg.EventListener;
import org.github.jimu.msg.EventManager;
import org.github.jimu.msg.bean.ConsumerMeta;

import com.luojilab.component.componentlib.router.Router;
import com.luojilab.componentdemo.msg.event.EventA;
import com.luojilab.componentdemo.msg.event.EventB;
import com.luojilab.router.facade.annotation.RouteNode;

@RouteNode(path = "/msg/demo/3", desc = "remote进程页面")
public class Msg3Activity extends Foo {

    EventListener<EventB> eventBEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventBEventListener = new EventListener<EventB>() {
            @Override
            public void onEvent(EventB event) {
                tvMsg.setText("event has been send and received." + event.getMsg());
            }
        };
        EventManager.getInstance().subscribe(EventB.class, eventBEventListener);

        AppComponentEventManager manager = (AppComponentEventManager) Router.getInstance()
                .getService(AppComponentEventManager.class.getSimpleName());



        manager.subscribeEventB(ConsumerMeta.<EventB>newBuilder()
                .consumeOn(ConsumeOn.Main)
                .process(":remote") // 一般来说，都不需要特地写进程了，我们约定""就代表默认进程,其他新开辟的进程中需要写
                .eventListener(eventBEventListener)
                .build());
    }

    @Override
    protected CharSequence getBtnText() {
        SpannableString spans1 = new SpannableString("send eventB");
        spans1.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                EventManager.getInstance().postEvent(new EventB("event b from Msg3Activity"));
            }
        }, 0, spans1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spans1;
    }

    @Override
    protected void onDestroy() {
        EventManager.getInstance().unsubscribe(eventBEventListener);
        super.onDestroy();
    }
}
