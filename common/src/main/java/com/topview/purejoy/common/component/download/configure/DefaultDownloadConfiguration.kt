package com.topview.purejoy.common.component.download.configure

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * 默认的下载配置类
 * */
open class DefaultDownloadConfiguration : DownloadConfiguration() {
    private val defaultOkClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val defaultDownloadThreadNum: Int

    init {
        val cpuCores = Runtime.getRuntime().availableProcessors()
        // 默认的下载线程数为 (CPU核心数-1)
        defaultDownloadThreadNum = if (cpuCores >= 2) cpuCores - 1 else 1
    }

    override fun getDownloadOkClient(): OkHttpClient = defaultOkClient

    override fun getDownloadThreadNum(): Int = defaultDownloadThreadNum
}