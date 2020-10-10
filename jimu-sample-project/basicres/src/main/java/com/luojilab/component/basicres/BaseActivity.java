package com.luojilab.component.basicres;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.luojilab.component.componentlib.service.AutowiredService;

/**
 * Created by mrzhang on 2018/1/16.
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AutowiredService.Factory.getSingletonImpl().autowire(this);
    }
}
