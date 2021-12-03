package com.topview.purejoy.common.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Created by giagor at 2021/03/15
 * */
abstract class MVVMViewModel : ViewModel() {
    /**
     * 该LiveData用于状态管理，ViewModel在请求完数据之后，根据请求的结果，对status设置不同的
     * 状态，Activity可以去观察该LiveData，可以根据不同的状态，显示出不同的UI
     * */
    val status: MutableLiveData<String> = MutableLiveData()

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