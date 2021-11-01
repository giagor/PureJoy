package com.topview.purejoy.common.mvvm.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Created by giagor at 2021/03/15
 * */
abstract class MVVMViewModel : ViewModel() {
    /**
     * 利用扩展函数的方式，简化对协程的调用，统一处理异常.
     * */
    fun <T> CoroutineScope.rxLaunch(init: CoroutineBuilder<T>.() -> Unit) {
        val result = CoroutineBuilder<T>().apply(init)
        // 对异常的处理
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, t ->
            result.onError?.invoke(t)
        }

        launch(coroutineExceptionHandler) {
            // onRequest为null，则不发起数据请求的操作
            result.onRequest?.let { onRequest ->
                val res: T? = onRequest.invoke()
                if (res == null) result.onEmpty?.invoke(Unit)// 获取到的数据为null
                else result.onSuccess?.invoke(res)// 成功获取数据，数据不为null
            }
        }
    }
}