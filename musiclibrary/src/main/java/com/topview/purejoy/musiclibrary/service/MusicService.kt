package com.topview.purejoy.musiclibrary.service

import android.content.IntentFilter
import com.topview.purejoy.musiclibrary.IPCPlayerController
import com.topview.purejoy.musiclibrary.data.Item
import com.topview.purejoy.musiclibrary.data.Wrapper
import com.topview.purejoy.musiclibrary.entity.MusicItem
import com.topview.purejoy.musiclibrary.player.abs.Loader
import com.topview.purejoy.musiclibrary.player.abs.cache.CacheStrategy
import com.topview.purejoy.musiclibrary.player.impl.cache.CacheStrategyImpl
import com.topview.purejoy.musiclibrary.player.impl.ipc.BinderPool
import com.topview.purejoy.musiclibrary.player.impl.ipc.IPCDataControllerImpl
import com.topview.purejoy.musiclibrary.player.service.MediaService
import com.topview.purejoy.musiclibrary.player.util.DataSource
import com.topview.purejoy.musiclibrary.player.util.cast
import com.topview.purejoy.musiclibrary.service.notification.MusicNotification
import com.topview.purejoy.musiclibrary.service.notification.MusicNotificationReceiver
import com.topview.purejoy.musiclibrary.service.url.viewmodel.MusicURLViewModel
import com.topview.purejoy.musiclibrary.service.url.viewmodel.MusicURLViewModelImpl
import java.io.File

class MusicService : MediaService<MusicItem>() {
    private val musicNotification: MusicNotification by lazy {
        MusicNotification(applicationContext, NOTIFICATION_ID)
    }
    private val receiver: MusicNotificationReceiver by lazy {
        val controller = pool.queryBinder(BinderPool.PLAYER_CONTROL_BINDER).cast<IPCPlayerController>()!!
        MusicNotificationReceiver(controller = controller,
            listener = object : MusicNotificationReceiver.ClearListener {
            override fun onClear() {
                if (musicNotification.showForeground) {
                    stopForeground(true)
                    musicNotification.showForeground = false
                }
            }
        })
    }
    private val TAG = "MusicService"

    private val viewModel: MusicURLViewModel by lazy {
        MusicURLViewModelImpl()
    }

    override fun onCreate() {
        super.onCreate()
        val dataController = pool.queryBinder(BinderPool.DATA_CONTROL_BINDER)
            .cast<IPCDataControllerImpl<MusicItem>>()!!
        dataController.source.changeListeners.add(object : DataSource.DataSetChangeListener<Wrapper> {
            override fun onChange(changes: MutableList<Wrapper>?) {
                if (changes == null) {
                    // 清除通知
                    if (musicNotification.showForeground) {
                        stopForeground(true)
                        musicNotification.showForeground = false
                    }
                }
            }
        })
        val filter = IntentFilter()
        filter.addAction(MusicNotificationReceiver.CLEAR_ACTION)
        filter.addAction(MusicNotificationReceiver.STATE_ACTION)
        filter.addAction(MusicNotificationReceiver.NEXT_ACTION)
        filter.addAction(MusicNotificationReceiver.PREVIOUS_ACTION)
        registerReceiver(receiver, filter)
    }

    override fun onLoadItem(itemIndex: Int, item: Item, callback: Loader.Callback<Item>) {
        viewModel.requestMusicURL(item.cast()!!, itemIndex, callback)
    }

    override fun showForeground(value: MusicItem, state: Boolean) {
        musicNotification.updateNotification(value, state)
        if (musicNotification.showForeground) {
            musicNotification.send()
        } else {
            musicNotification.showForeground = true
            startForeground(NOTIFICATION_ID, musicNotification.notification)
        }
    }

    override fun cacheStrategy(): CacheStrategy {
        val file = File(applicationContext.cacheDir, MUSIC_CACHE_DIR)
        if (!file.exists()) {
            file.mkdirs()
        }
        val maxSize = Runtime.getRuntime().freeMemory() / 8
        return CacheStrategyImpl(cacheDirectory = file, maxMemorySize = maxSize.toInt())
    }

    override fun onDestroy() {
        unregisterReceiver(receiver)
        super.onDestroy()
    }

    companion object {
        const val MUSIC_CACHE_DIR = "musicCache"
        const val NOTIFICATION_ID = 100
    }
}