package com.luojilab.componentdemo.router.cases;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.luojilab.component.componentlib.service.AutowiredService;
import com.luojilab.componentdemo.R;

abstract class TestActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        textView = findViewById(R.id.demo_tv_info);
        AutowiredService.Factory.getSingletonImpl()
                .autowire(this);
        displayInfo(textView);
    }

    protected abstract void displayInfo(TextView textView);
}
