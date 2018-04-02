package com.luojilab.componentdemo.router;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.luojilab.component.componentlib.router.ui.UIRouter;
import com.luojilab.component.componentlib.router.ui.VerifyResult;
import com.luojilab.componentdemo.R;
import com.luojilab.componentdemo.router.cases.Demo1Activity;
import com.luojilab.componentdemo.router.cases.Demo2Activity;
import com.luojilab.componentdemo.router.cases.Demo3Activity;
import com.luojilab.componentdemo.router.cases.Demo4Activity;
import com.luojilab.componentdemo.router.cases.Demo5Activity;
import com.luojilab.componentdemo.router.cases.Demo6Activity;
import com.luojilab.componentdemo.router.cases.Demo7Activity;
import com.luojilab.componentdemo.router.cases.Demo8Activity;
import com.luojilab.router.facade.annotation.RouteNode;

import java.util.Arrays;
import java.util.List;

@RouteNode(path = "/uirouter/demo", desc = "UiRouter使用演示页面")
public class UiRouterDemoActivity extends AppCompatActivity {

    private ListView lvCases;
    private List<Case> demos = Arrays.asList(
            Demo1Activity.aCase,
            Demo2Activity.aCase,
            Demo3Activity.aCase,
            Demo4Activity.aCase,
            Demo5Activity.aCase,
            Demo6Activity.aCase,
            Demo7Activity.aCase,
            Demo8Activity.aCase
    );

    private static final String TAG = "UiRouterDemoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui_router_demo);

        lvCases = findViewById(R.id.uidemo_lv_cases);
        lvCases.setAdapter(new SimpleAdapter(demos));

        lvCases.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Case aCase = demos.get(position);
                Log.d(TAG, "display case:" + aCase.desc);

                if (aCase.useSafeMode) {
                    navigateUseSafeMode(aCase);
                } else {
                    UIRouter.getInstance().openUri(UiRouterDemoActivity.this,
                            aCase.url, aCase.bundle);
                }
            }
        });
    }

    private void navigateUseSafeMode(@NonNull Case aCase) {
        //对于外部唤醒等场景，需要对url，参数做一定的校验，以确保程序的稳定运行
        //注意：对于稳定可靠的业务，可以不使用这种校验。
        VerifyResult result = UIRouter.getInstance().verifyUri(Uri.parse(aCase.url), aCase.bundle, true);

        if (result.isSuccess()) {
            UIRouter.getInstance().openUri(UiRouterDemoActivity.this,
                    aCase.url, aCase.bundle);
        } else {
            Log.e("JIMU","安全模式发现异常："+result.getThrowable().getMessage());

            /*可以在此处进行统计收集或者开发阶段控制台打印错误信息*/
            result.getThrowable().printStackTrace();
        }
    }

    public static final class Case {
        private boolean useSafeMode;
        private String desc;
        private String url;
        private Bundle bundle;

        public Case(boolean useSafeMode, String desc, String url, Bundle bundle) {
            this.useSafeMode = useSafeMode;
            this.desc = desc;
            this.url = url;
            this.bundle = bundle;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 下面不用看
    ///////////////////////////////////////////////////////////////////////////

    private static final class SimpleAdapter extends BaseAdapter {

        List<Case> data;

        public SimpleAdapter(List<Case> data) {
            this.data = data;
        }

        @Override
        public int getCount() {
            if (data == null)
                return 0;
            return data.size();
        }

        @Override
        public Case getItem(int position) {
            if (data == null)
                return null;
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView != null && convertView.getTag() instanceof ViewHolder) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                holder = new ViewHolder();
                TextView tv = new TextView(parent.getContext());
                convertView = tv;

                tv.setPadding(30, 30, 30, 30);
                holder.textView = tv;
            }
            convertView.setTag(holder);

            holder.textView.setText(getItem(position).desc);

            return convertView;
        }

        private static final class ViewHolder {
            TextView textView;
        }
    }
}
