package com.luojilab.share.core;

/**
 * <p><b>Package:</b> com.luojilab.share </p>
 * <p><b>Project:</b> jimu-sample-project </p>
 * <p><b>Classname:</b> SampleShareCore </p>
 * <p><b>Description:</b> just a sample to display the share operate </p>
 * Created by leobert on 2018/7/6.
 */
public class SampleShareCore implements IShareApi {
    private static SampleShareCore instance = null;

    private SampleShareCore() {
        // single
    }

    public static SampleShareCore getInstance() {
        if (instance == null)
            instance = new SampleShareCore();
        return instance;
    }


    public final void share(AbsShareBean bean) {
        bean.doShare(this);
    }

    public void share2QQ(String content) {
// TODO: 2018/7/6
    }

    public void share2Wechat(String content) {
// TODO: 2018/7/6
    }
}
