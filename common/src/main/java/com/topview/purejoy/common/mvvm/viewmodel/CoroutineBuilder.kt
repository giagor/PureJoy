package com.topview.purejoy.common.mvvm.viewmodel

/**
 * Created by giagor at 2021/03/15.
 *
 * 主要用于帮助构建协程程序，简化在ViewModel中协程的操作.
 * */
class CoroutineBuilder<T> {
    /**
     * 请求数据.
     * */
    var onRequest: (suspend () -> T?)? = null

    /**
     * 成功请求，并拿到相应的数据.
     * */
    var onSuccess: ((T) -> Unit)? = null

    /**
     * 请求成功，但是获得的对象为null.
     * */
    var onEmpty: ((Unit) -> Unit)? = null

    /**
     * 请求过程出现错误.
     * */
    var onError: ((Throwable) -> Unit)? = null
}