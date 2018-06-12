package com.luojilab.reader.runalone;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.github.jimu.msg.AriseAt;
import org.github.jimu.msg.ConsumeOn;
import org.github.jimu.msg.EventListener;
import org.github.jimu.msg.EventManager;
import org.github.jimu.msg.Utils;
import org.github.jimu.msg.bean.EventBean;
import com.luojilab.reader.R;
import com.luojilab.reader.ReaderFragment;

/**
 * Created by mrzhang on 2017/6/15.
 */

public class ReaderTestActivity extends AppCompatActivity {

    ReaderFragment fragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.readerbook_activity_test);
        fragment = new ReaderFragment();
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.tab_content, fragment).commitAllowingStateLoss();

        testEventManager();

    }

    ///////////////////////////////////////////////////////////////////////////
    // 临时放置的测试代码
    ///////////////////////////////////////////////////////////////////////////

    private static final String TAG = "temp-tag";
    private EventManager eventManager = EventManager.getInstance();

    private static final class TestClz implements EventBean {
        private final String string;

        public TestClz(String string) {
            this.string = string;
        }

        @Override
        public String toString() {
            return super.toString() + "  " + string;
        }
    }

    private EventListener<TestClz> eventListener1 = new EventListener<TestClz>() {
        @Override
        public void onEvent(TestClz event) {
            Log.d(TAG, "isMainThread:" + Utils.isMainThread());
            Log.d(TAG, "subscribe on MainThread, receive: " + event.toString());
        }
    };

    private EventListener<TestClz> eventListener2 = new EventListener<TestClz>() {
        @Override
        public void onEvent(TestClz event) {
            Log.d(TAG, "isMainThread:" + Utils.isMainThread());
            //just for filter
            Log.i(TAG, "subscribe on background, receive: " + event.toString());

        }
    };

    Thread forceTestThread;

    public void testEventManager() {
        eventManager.subscribe(TestClz.class, eventListener1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                eventManager.subscribe(TestClz.class, AriseAt.local(), ConsumeOn.Background, eventListener2);
//                        postEvent();
            }
        }).start();

//        postEvent();

        //force test.
       forceTestThread = new ForceTestThread();
       forceTestThread.start();

    }

    public static final class ForceTestThread extends Thread {
        @Override
        public void run() {
            super.run();
            int i = 0;
            while (true) {
                i++;
                EventManager.getInstance().postEvent(new TestClz("forceTest:" + i));
            }
        }
    }

    public void postEvent() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                eventManager.postEvent(new TestClz("post on mainThread"));
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                eventManager.postEvent(new TestClz("post on background"));
            }
        }).start();
    }

    @Override
    public void finish() {
        super.finish();
        EventManager.getInstance().unsubscribe(eventListener1);
        EventManager.getInstance().unsubscribe(eventListener2);
    }

    @Override
    protected void onDestroy() {
        forceTestThread.interrupt();
        super.onDestroy();
    }
}
