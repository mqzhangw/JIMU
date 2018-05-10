package com.luojilab.componentdemo;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.luojilab.component.componentlib.router.ui.AbsDispatcherActivity;
import com.luojilab.component.componentlib.router.ui.UIRouter;

import java.util.Arrays;
import java.util.List;

/**
 * this is just a demo to display one case in outer awake & pager direct:
 * <em>
 * As we all know, we may run into the user sense: Jone open one url
 * generated or related to our APP in mobile browser,
 * we need to awake the APP and navigate to the correct page, to create
 * a immersion-operation-experience for Jone.
 * <p>
 * But, your website may uses REST-arch to realize or not. thus, in some cases
 * the web page will know some information from it's url without request the server,
 * and if those information is enough for navigation, you can use this way to realize
 * page navigation.
 * </em>
 * <p>
 * remember: this is just a demo to show how to solve one case. not the rule to realize
 * outer awake function.
 * <p>
 * <em>
 * suggestion to solve some typical real cases will be post in doc.
 * and this demo will be removed in future.
 * </em>
 */
@Deprecated
public class DispatcherActivity extends AbsDispatcherActivity {
    private static final String TAG = "DispatcherActivity";
   /*
    * use ddcompo://www.luojilab.com/compodemo?target=share/shareBook?bookName=Dummy to display
    *
    * */

    private static final List<String> transferHost = Arrays.asList(
            "www.luojilab.com",
            "www.luojilab.cn"
    );

    @Override
    protected void onBeforeHandle() {
        Log.d(TAG, "onBeforeHandle");
    }

    @Override
    protected boolean needTransferUri(Uri uri) {
        if (uri == null)
            return false;

        String host = uri.getHost();
        return transferHost.contains(host);
    }

    @Override
    protected Uri transferUri(Uri uri) {
        String target = uri.getQueryParameter("target");
        if (!TextUtils.isEmpty(target))
            return Uri.parse("ddcompo://" + target);
        return uri;
    }


    @Override
    protected void onNullUri() {
        Log.e(TAG, "onNullUri");
        navigate2MainPage();
    }

    @Override
    protected void onVerifyFailed(@Nullable Throwable throwable) {
        Log.e(TAG, "onVerifyFailed", throwable);
        navigate2MainPage();
    }

    @Override
    protected void onExceptionWhenOpenUri(Uri uri, Exception e) {
        Log.e(TAG, e.getMessage());
        navigate2MainPage();
    }

    @Override
    protected void onHandled() {
        finish();
    }

    private void navigate2MainPage() {
        UIRouter.getInstance().openUri(this, "/main", null);
    }
}
