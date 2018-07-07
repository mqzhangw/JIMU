package com.luojilab.share.core;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <p><b>Package:</b> com.luojilab.share.core </p>
 * <p><b>Project:</b> jimu-sample-project </p>
 * <p><b>Classname:</b> AbsShareBean </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2018/7/6.
 */
public abstract class AbsShareBean {

    @IntDef({Via.QQ, Via.WECHAT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Via {
        int QQ = 0;
        int WECHAT = 1;
    }

    @Via
    protected final int shareVia;

    public AbsShareBean(@Via int shareVia) {
        this.shareVia = shareVia;
    }

    protected final void doShare(@NonNull IShareApi shareCore) {
        String content = getShareContent();
        switch (shareVia) {
            case Via.QQ:
                shareCore.share2QQ(content);
                break;
            default:
            case Via.WECHAT:
                shareCore.share2Wechat(content);
                break;
        }
    }

    //仅仅是一个demo工程，实际场景比这个复杂的多，按照实际需求妥善处理你的代码
    protected abstract String getShareContent();

}
