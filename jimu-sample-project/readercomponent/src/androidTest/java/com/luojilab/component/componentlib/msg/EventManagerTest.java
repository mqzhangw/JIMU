package org.github.jimu.msg;

import android.support.test.runner.AndroidJUnit4;

import org.github.jimu.msg.bean.EventBean;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * <p><b>Package:</b> org.github.jimu.msg </p>
 * <p><b>Project:</b> JIMU </p>
 * <p><b>Classname:</b> EventManagerTest </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2018/5/2.
 */
@RunWith(AndroidJUnit4.class)
public class EventManagerTest {

    private EventManager eventManager = EventManager.getInstance();
    private static final class TestClz implements EventBean {
        private final String string;

        public TestClz(String string) {
            this.string = string;
        }
    }


    @Test
    public void getInstance() {
        EventManager eventManager = EventManager.getInstance();
        assertNotNull(eventManager);
    }

    @Test
    public void subscribe() {
        System.out.print("start");
        eventManager.subscribe(TestClz.class, new EventListener<TestClz>() {
            @Override
            public void onEvent(TestClz event) {
                System.out.println(event.toString());
            }
        });
        postEvent();

    }

//    @Test
    public void postEvent() {

        eventManager.postEvent(new TestClz("a"));
        System.out.println("event post called");
    }
}