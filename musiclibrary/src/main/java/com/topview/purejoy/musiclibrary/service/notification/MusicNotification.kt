package com.topview.purejoy.musiclibrary.service.notification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.topview.purejoy.musiclibrary.R
import com.topview.purejoy.musiclibrary.entity.MusicItem

@SuppressLint("UnspecifiedImmutableFlag")
class MusicNotification(
    private val context: Context,
    private val nid: Int) {

    val notification: Notification
    private val notificationManager: NotificationManager = context.getSystemService(
        Context.NOTIFICATION_SERVICE) as NotificationManager
    private val remoteViews: RemoteViews
    @Volatile
    var showForeground = false

    init {
        val builder = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            channel.setSound(null, null)

            notificationManager.createNotificationChannel(channel)
            NotificationCompat.Builder(context, CHANNEL_ID)
        } else {
            NotificationCompat.Builder(context)
        }

        remoteViews = RemoteViews(context.packageName, R.layout.linearlayout_music_notification)

        notification = builder.setCustomContentView(remoteViews)
            .setCustomBigContentView(remoteViews)
            .setPriority(NotificationCompat.PRIORITY_HIGH).build()
        setOnClickPendingIntent(R.id.music_notification_previous_img,
            MusicNotificationReceiver.PREVIOUS_ACTION)

        setOnClickPendingIntent(R.id.music_notification_next_img,
            MusicNotificationReceiver.NEXT_ACTION)

        setOnClickPendingIntent(R.id.music_notification_state_img,
            MusicNotificationReceiver.STATE_ACTION)

    }

    private fun setOnClickPendingIntent(id: Int, action: String) {
        remoteViews.setOnClickPendingIntent(id,
            PendingIntent.getBroadcast(context, 0, Intent(action), 0))
    }

    fun updateNotification(item: MusicItem, state: Boolean) {
        remoteViews.setTextViewText(R.id.music_notification_name_tx, item.name)
        remoteViews.setTextViewText(R.id.music_notification_author_tx,
            "${item.getAuthors()} - ${item.al.name}")
        // 更新歌曲图片
        val id = if (state) {
            R.drawable.music_notification_pause_24
        } else {
            R.drawable.music_notification_play_24
        }
        remoteViews.setImageViewResource(R.id.music_notification_state_img, id)


    }

    fun send() {
        notificationManager.notify(nid, notification)
    }



//    fun cancel() {
//        showForeground = false
//        notificationManager.cancel(nid)
//    }

    companion object {
        const val CHANNEL_ID = "music"
        const val CHANNEL_NAME = "music_channel"
    }

}