package com.topview.purejoy.common.component.download.configure

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.telephony.TelephonyManager
import okhttp3.OkHttpClient
import java.util.concurrent.ExecutorService
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.math.min

/**
 * Created by giagor on 2021/12/18
 *
 * 默认的下载配置类
 * */
open class DefaultDownloadConfiguration(
    val context: Context,
    /** 是否要观察网络状态的变化 */
    var observeNetworkChange: Boolean = true
) : DownloadConfiguration() {

    /**
     * 网络状态变化的广播接收器
     * */
    protected val receiver: NetworkBroadcastReceiver = NetworkBroadcastReceiver().apply {
        if (observeNetworkChange) {
            register()
        }
    }

    protected var normalTaskThreadCount = NORMAL_TASK_THREAD_COUNT_DEFAULT

    private companion object {
        /**
         * 20MB
         * */
        const val ONE_CONNECTION_UPPER_LIMIT = 20 * 1024 * 1024

        /**
         * 60MB
         * */
        const val TWO_CONNECTION_UPPER_LIMIT = 60 * 1024 * 1024

        /**
         * 90MB
         * */
        const val THREE_CONNECTION_UPPER_LIMIT = 90 * 1024 * 1024

        /**
         * 5G网络时，下载线程数
         * */
        const val NORMAL_TASK_THREAD_COUNT_5G = 5

        /**
         * WIFI下，下载线程数
         * */
        const val NORMAL_TASK_THREAD_COUNT_WIFI = 4

        /**
         * 默认情况下，下载线程数
         * */
        const val NORMAL_TASK_THREAD_COUNT_DEFAULT = 4

        /**
         * 4G网络时，下载线程数
         * */
        const val NORMAL_TASK_THREAD_COUNT_4G = 4

        /**
         * 3G网络时，下载线程数
         * */
        const val NORMAL_TASK_THREAD_COUNT_3G = 3

        /**
         * 2G网络时，下载线程数
         * */
        const val NORMAL_TASK_THREAD_COUNT_2G = 2
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
            return min(2, normalTaskThreadCount)
        }

        if (taskTotalLength < THREE_CONNECTION_UPPER_LIMIT) {
            return min(3, normalTaskThreadCount)
        }

        return normalTaskThreadCount
    }

    override fun getCommonThreadPool(): ExecutorService {
        return ThreadPoolExecutor(
            3, Int.MAX_VALUE,
            60L, TimeUnit.SECONDS,
            SynchronousQueue()
        )
    }

    /**
     * 根据网络变化，动态调整下载任务的线程数
     * */
    protected fun networkStateChange(networkInfo: NetworkInfo?) {
        if (networkInfo == null || !networkInfo.isConnectedOrConnecting) {
            normalTaskThreadCount = NORMAL_TASK_THREAD_COUNT_DEFAULT
            return
        }

        when (networkInfo.type) {
            // WIFI
            ConnectivityManager.TYPE_WIFI -> {
                normalTaskThreadCount = NORMAL_TASK_THREAD_COUNT_WIFI
            }

            ConnectivityManager.TYPE_MOBILE -> {
                when (networkInfo.subtype) {
                    // 5G 
                    TelephonyManager.NETWORK_TYPE_NR -> {
                        normalTaskThreadCount = NORMAL_TASK_THREAD_COUNT_5G
                    }

                    // 4G
                    TelephonyManager.NETWORK_TYPE_LTE,
                    TelephonyManager.NETWORK_TYPE_HSPAP,
                    TelephonyManager.NETWORK_TYPE_EHRPD -> {
                        normalTaskThreadCount = NORMAL_TASK_THREAD_COUNT_4G
                    }

                    // 3G
                    TelephonyManager.NETWORK_TYPE_UMTS,
                    TelephonyManager.NETWORK_TYPE_CDMA,
                    TelephonyManager.NETWORK_TYPE_EVDO_0,
                    TelephonyManager.NETWORK_TYPE_EVDO_A,
                    TelephonyManager.NETWORK_TYPE_EVDO_B,
                    -> {
                        normalTaskThreadCount = NORMAL_TASK_THREAD_COUNT_3G
                    }

                    // 2G
                    TelephonyManager.NETWORK_TYPE_GPRS,
                    TelephonyManager.NETWORK_TYPE_EDGE -> {
                        normalTaskThreadCount = NORMAL_TASK_THREAD_COUNT_2G
                    }

                    else -> {
                        normalTaskThreadCount = NORMAL_TASK_THREAD_COUNT_DEFAULT
                    }
                }
            }

            else -> {
                normalTaskThreadCount = NORMAL_TASK_THREAD_COUNT_DEFAULT
            }
        }
    }

    fun registerNetworkStateChange() {
        if (!observeNetworkChange) {
            receiver.register()
            observeNetworkChange = true
        }
    }

    fun unregisterNetworkStateChange() {
        if (observeNetworkChange) {
            receiver.unregister()
            observeNetworkChange = false
        }
    }

    inner class NetworkBroadcastReceiver : BroadcastReceiver() {

        fun register() {
            val filter = IntentFilter().apply {
                addAction(ConnectivityManager.CONNECTIVITY_ACTION)
            }
            context.registerReceiver(this, filter)
        }

        fun unregister() {
            context.unregisterReceiver(this)
        }

        override fun onReceive(context: Context, intent: Intent?) {
            if (intent == null) {
                return
            }

            val connectivityManager: ConnectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            networkStateChange(connectivityManager.activeNetworkInfo)
        }
    }
}