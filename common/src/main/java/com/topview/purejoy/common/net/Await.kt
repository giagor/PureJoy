package com.topview.purejoy.common.net

import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Created by giagor at 2021/03/15.
 *
 * 简化Retrofit请求时的异步调用流程，通过该扩展函数，只需在协程中调用 [Call.awaitAsync] 即可获取数据，
 * 要做好异常处理.
 * */
suspend fun <T> Call<T>.awaitAsync(): T? {
    return suspendCancellableCoroutine { continuation ->
        continuation.invokeOnCancellation {
            cancel()
        }
        enqueue(object : Callback<T> {
            override fun onFailure(call: Call<T>, t: Throwable) {
                continuation.resumeWithException(t)
            }

            override fun onResponse(call: Call<T>, response: Response<T>) {
                val body: T? = response.body() // body可能为null
                continuation.resume(body)
            }
        })
    }
}

/**
 * 采用同步的方式进行Retrofit网络请求的处理
 * */
@Deprecated("potential memory leak", ReplaceWith("Call<T>.awaitAsync()"))
fun <T> Call<T>.awaitSync(): T? {
    return execute().body()
}