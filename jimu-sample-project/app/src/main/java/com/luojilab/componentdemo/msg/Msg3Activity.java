package com.luojilab.componentdemo.msg;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.view.View;

import org.github.jimu.msg.EventListener;
import org.github.jimu.msg.EventManager;
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
