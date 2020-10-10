package com.luojilab.share.kotlin.applike

import android.util.Log
import com.luojilab.component.componentlib.applicationlike.IApplicationLike
import com.luojilab.component.componentlib.router.ui.UIRouter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import osp.leobert.android.maat.JOB
import osp.leobert.android.maat.Maat

/**
 * Created by mrzhang on 2018/1/3.
 */
class KotlinApplike : IApplicationLike {

    val uiRouter = UIRouter.getInstance()

    override fun onCreate() {
        uiRouter.registerUI("kotlin")
        Log.e("shareKt", "shareKt on create")
        Maat.getDefault().append(object : JOB() {
            override val uniqueKey: String = "shareKt"

            override val dependsOn: List<String> = arrayListOf("reader")

            override val dispatcher: CoroutineDispatcher = Dispatchers.Main

            override fun init(maat: Maat) {
                Log.d("shareKt", "模拟初始化shareKt，context：" + maat.application.javaClass.name)
            }

            override fun toString(): String {
                return uniqueKey
            }
        })
    }

    override fun onStop() {
        uiRouter.unregisterUI("kotlin")
    }
}