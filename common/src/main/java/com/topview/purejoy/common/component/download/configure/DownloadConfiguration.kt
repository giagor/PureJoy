package com.topview.purejoy.common.component.download.configure

import okhttp3.OkHttpClient

/**
 * 下载的配置类
 * */
abstract class DownloadConfiguration {
    /**
     * 下载使用到的OkHttpClient
     * */
    abstract fun getDownloadOkClient(): OkHttpClient

    /**
     * 默认情况下，使用多少个线程去下载一个任务
     * */
    abstract fun getDownloadThreadNum(): Int
}