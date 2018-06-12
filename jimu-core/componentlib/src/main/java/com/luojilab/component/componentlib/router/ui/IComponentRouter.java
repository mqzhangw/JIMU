package com.luojilab.component.componentlib.router.ui;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * router behaviors for component type
 * Created by mrzhang on 2017/6/20.
 */
public interface IComponentRouter {

    boolean openUri(Context context, String url, Bundle bundle);

    boolean openUri(Context context, Uri uri, Bundle bundle);

    boolean openUri(Context context, String url, Bundle bundle, Integer requestCode);

    boolean openUri(Context context, Uri uri, Bundle bundle, Integer requestCode);

    /**
     * 打开一个链接
     *
     * @param url          目标url可以是http 或者 自定义scheme
     * @param bundle       打开目录activity时要传入的参数。建议只传基本类型参数。
     * @param intentDecors decorators to decorate the intent of open on page
     * @return 是否正常打开
     */
    boolean openUri(Context context, String url, Bundle bundle, List<IntentDecor> intentDecors);

    boolean openUri(Context context, Uri uri, Bundle bundle, List<IntentDecor> intentDecors);

    boolean openUri(Context context, String url, Bundle bundle, Integer requestCode, List<IntentDecor> intentDecors);

    boolean openUri(Context context, Uri uri, Bundle bundle, Integer requestCode, List<IntentDecor> intentDecors);

    /**
     * use {@link #verifyUri(Uri, Bundle, boolean)} instead
     *
     * @param uri the uri to be verified
     * @return true if sth accept it
     */
    @Deprecated
    boolean verifyUri(Uri uri);

    @NonNull
    VerifyResult verifyUri(Uri uri, Bundle bundle, boolean checkParams);

    interface IntentDecor {
        void decor(IntentDecorDelegate delegate);
    }

    final class IntentDecorDelegate {
        private final Intent intent;

        private Intent getIntent() {
            return intent;
        }

        public IntentDecorDelegate(Intent intent) {
            this.intent = intent;
        }

        public void setAction(@Nullable String action) {
            intent.setAction(action);
        }

        public void setData(@Nullable Uri data) {
            intent.setData(data);
        }

        public void setType(@Nullable String type) {
            intent.setType(type);
        }

        public void setDataAndType(@Nullable Uri data, @Nullable String type) {
            intent.setDataAndType(data, type);
        }

        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
        public void setSelector(@Nullable Intent selector) {
            intent.setSelector(selector);
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public void setClipData(@Nullable ClipData clip) {
            intent.setClipData(clip);
        }

        public void setFlags(int flags) {
            intent.setFlags(flags);
        }

        public void addFlags(int flags) {
            intent.addFlags(flags);
        }

        @TargetApi(Build.VERSION_CODES.O)
        public void removeFlags(int flags) {
            intent.removeFlags(flags);
        }

        public void setSourceBounds(@Nullable Rect r) {
            intent.setSourceBounds(r);
        }
    }
}