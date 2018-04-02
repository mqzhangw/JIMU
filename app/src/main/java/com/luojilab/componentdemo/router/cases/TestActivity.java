package com.luojilab.componentdemo.router.cases;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.luojilab.component.componentlib.service.AutowiredService;
import com.luojilab.component.componentlib.service.AutowiredServiceImpl;
import com.luojilab.componentdemo.R;

abstract class TestActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        AutowiredService.Factory.getSingletonImpl()
                .autowire(this);
    }
}
