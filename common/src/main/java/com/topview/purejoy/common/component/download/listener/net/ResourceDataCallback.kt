package com.topview.purejoy.common.component.download.listener.net

import java.io.InputStream

/**
 * Created by giagor on 2021/12/18
 *
 * 获取资源实体信息时的回调接口
 * */
interface ResourceDataCallback {
    /**
     * 成功获取
     *
     * @param inputStream 资源的输入流
     * */
    fun onSuccess(inputStream: InputStream)

    /**
     * 获取失败
     * */
    fun onFailure(t: Throwable)
}