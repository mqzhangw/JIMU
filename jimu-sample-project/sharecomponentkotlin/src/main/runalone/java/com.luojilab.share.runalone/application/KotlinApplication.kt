package com.luojilab.share.runalone.application

import android.util.Log
import com.luojilab.component.basicres.BaseApplication
import osp.leobert.android.maat.JOB
import osp.leobert.android.maat.Maat
import osp.leobert.android.maat.Maat.Callback
import osp.leobert.android.maat.Maat.Companion.release

/**
 * Created by mrzhang on 2018/1/5.
 */
class KotlinApplication : BaseApplication() {

    override fun onCreate() {
        super.onCreate()

        //注意，如果准备使用Maat进行按序初始化，则需要先进行配置：
        Maat.init(this, 8, object : Maat.Logger() {
            override val enable: Boolean
                get() = true

            override fun log(msg: String, throws: Throwable?) {
                if (throws != null) {
//                    ILogger.logger.error("maat", s);
//                    throwable.printStackTrace();
                    Log.e("maat", msg, throws)
                } else {
//                    ILogger.logger.debug("maat", s);
                    Log.d("maat", msg)
                }
            }
        }, Callback({ maat: Maat? ->
            release()
        }) { maat: Maat?, job: JOB?, throwable: Throwable? -> null })

        //如果真的使用了Maat，而又缺乏相关组件，则说明组件环境依赖不全，会抛出异常，例如
        //java.lang.RuntimeException: Unable to create application
        // com.luojilab.share.runalone.application.KotlinApplication:
        // osp.leobert.android.maat.MaatException: missing jobs:[reader]

        //如果确实有其他组件的初始化依赖，而目前又不方便引入，例如并行开发时，我们可以先把maat禁用或者添加模拟任务,参考sharecomponent
    }

}