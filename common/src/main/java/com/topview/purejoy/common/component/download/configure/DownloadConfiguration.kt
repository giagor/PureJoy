package com.topview.purejoy.common.component.download.configure

import okhttp3.OkHttpClient
import java.util.concurrent.ExecutorService

/**
 * Created by giagor on 2021/12/18
 *
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
    abstract fun getDownloadThreadNum(taskTotalLength: Long): Int

    /**
     * 获取一个线程池，用于处理一些普通的后台任务。
     * 该线程池不用于下载，下载有另外的线程池负责。
     * */
    abstract fun getCommonThreadPool(): ExecutorService
}