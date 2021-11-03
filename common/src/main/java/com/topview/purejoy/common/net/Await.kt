package com.topview.purejoy.common.net

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Created by giagor at 2021/03/15.
 *
 * 简化Retrofit请求时的异步调用流程，通过该扩展函数，只需在协程中调用 Call<...>.await() 即可获取数据，
 * 要做好异常处理.
 * */
suspend fun <T> Call<T>.await(): T? {
    return suspendCoroutine { continuation ->
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