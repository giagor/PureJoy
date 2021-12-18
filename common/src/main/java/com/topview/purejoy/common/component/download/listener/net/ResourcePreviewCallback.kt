package com.topview.purejoy.common.component.download.listener.net

/**
 * Created by giagor on 2021/12/18
 * 
 * 获取资源的长度等信息时的回调接口，一般是使用HEAD方法请求资源的信息，然后根据服务端是否支持Range的范围请求，
 * 回调不同的接口
 * */
interface ResourcePreviewCallback {
    /**
     * 服务端支持Range范围请求
     *
     * @param contentLength 资源的长度
     * */
    fun supportRange(contentLength: Long)

    /**
     * 服务端不支持Range范围请求
     *
     * @param contentLength 资源的长度
     * */
    fun unSupportRange(contentLength: Long)

    /**
     * 网络请求出错，则调用该接口
     * */
    fun onFailure(e: Exception)

    /**
     * 资源本身有问题，例如 contentLength<=0
     * */
    fun resourceErr()
}