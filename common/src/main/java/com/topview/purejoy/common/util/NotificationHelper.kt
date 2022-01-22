package com.topview.purejoy.common.util

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import androidx.core.app.NotificationCompat
import com.topview.purejoy.common.R

/**
 * Notification帮助类：创建渠道、构建通知、发送通知
 * */
@SuppressLint("StaticFieldLeak")
object NotificationHelper {
    private var initialize = false
    private lateinit var context: Context
    private lateinit var manager: NotificationManager
    const val DOWNLOAD_CHANNEL_ID = "download_channel_id"
    const val DOWNLOAD_CHANNEL_NAME = "download_channel"

    fun init(context: Context) {
        if (initialize) {
            return
        }

        initialize = true
        this.context = context
        this.manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        checkInitialize()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val downloadChannel = NotificationChannel(
                DOWNLOAD_CHANNEL_ID, DOWNLOAD_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(downloadChannel)
        }
    }

    fun getCommonNotifyBuilder(channelId: String): NotificationCompat.Builder {
        checkInitialize()

        val builder: NotificationCompat.Builder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationCompat.Builder(context, channelId)
            } else {
                NotificationCompat.Builder(context)
            }

        // TODO 确定通知小图标
        builder.setWhen(System.currentTimeMillis())
            .setSmallIcon(R.mipmap.ic_launcher)
        return builder
    }

    fun showNotification(notificationId: Int, notification: Notification) {
        checkInitialize()

        manager.notify(notificationId, notification)
    }

    private fun checkInitialize() {
        check(initialize) { "NotificationHelper hasn't initialized" }
    }
}