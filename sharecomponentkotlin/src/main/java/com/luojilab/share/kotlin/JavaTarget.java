package com.luojilab.share.kotlin;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.luojilab.component.componentlib.service.AutowiredService;
import com.luojilab.componentservice.share.bean.AuthorKt;
import com.luojilab.router.facade.annotation.Autowired;
import com.luojilab.router.facade.annotation.RouteNode;

import kotlin.jvm.JvmField;

/**
 * <p><b>Package:</b> com.luojilab.share.kotlin </p>
 * <p><b>Project:</b> JIMU </p>
 * <p><b>Classname:</b> JavaTarget </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 08/04/2018.
 */
@RouteNode(path = "/javatest", desc = "test multi java and kotlin in one module")
public class JavaTarget extends Activity {

    @Autowired(name = "bookName")
    String magazineName;

    @Autowired
    AuthorKt author;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kotlin_activity_share);
        AutowiredService.Factory.getSingletonImpl().autowire(this);

        TextView shareTitle = findViewById(R.id.share_title);

        shareTitle.setText("Java is verbose!");

        ((TextView) findViewById(R.id.share_tv_tag)).setText(magazineName);

        TextView tvAuthor = findViewById(R.id.share_tv_author);

        if (author == null || author.getName() == null)
            tvAuthor.setText("leobert");
        else
            tvAuthor.setText(author.getName());

//        share_tv_county.setText(author?.county ?: "China")
    }
}
