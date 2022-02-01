package com.topview.purejoy.common.component.download.configure

import okhttp3.OkHttpClient
import java.util.concurrent.*

/**
 * Created by giagor on 2021/12/18
 *
 * 默认的下载配置类
 * */
open class DefaultDownloadConfiguration : DownloadConfiguration() {

    companion object {
        /**
         * 5MB
         * */
        const val ONE_CONNECTION_UPPER_LIMIT = 5 * 1024 * 1024

        /**
         * 10MB
         * */
        const val TWO_CONNECTION_UPPER_LIMIT = 10 * 1024 * 1024

        /**
         * 20MB
         * */
        const val THREE_CONNECTION_UPPER_LIMIT = 20 * 1024 * 1024
    }

    private val defaultOkClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    override fun getDownloadOkClient(): OkHttpClient = defaultOkClient

    override fun getDownloadThreadNum(taskTotalLength: Long): Int {
        if (taskTotalLength < ONE_CONNECTION_UPPER_LIMIT) {
            return 1
        }

        if (taskTotalLength < TWO_CONNECTION_UPPER_LIMIT) {
            return 2
        }

        if (taskTotalLength < THREE_CONNECTION_UPPER_LIMIT) {
            return 3
        }

        return 6
    }

    override fun getCommonThreadPool(): ExecutorService {
        return ThreadPoolExecutor(
            3, Int.MAX_VALUE,
            60L, TimeUnit.SECONDS,
            SynchronousQueue()
        )
    }
}