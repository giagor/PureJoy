package com.topview.purejoy.musiclibrary.player.impl.controller

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import com.topview.purejoy.musiclibrary.data.Item
import com.topview.purejoy.musiclibrary.player.abs.Loader
import com.topview.purejoy.musiclibrary.player.abs.MediaListenerManger
import com.topview.purejoy.musiclibrary.player.abs.cache.CacheStrategy
import com.topview.purejoy.musiclibrary.player.abs.controller.MediaController
import com.topview.purejoy.musiclibrary.player.abs.core.MusicPlayer
import com.topview.purejoy.musiclibrary.player.abs.core.Position
import com.topview.purejoy.musiclibrary.player.abs.listener.CompleteListener
import com.topview.purejoy.musiclibrary.player.abs.listener.ErrorListener
import com.topview.purejoy.musiclibrary.player.abs.listener.PreparedListener
import com.topview.purejoy.musiclibrary.player.impl.listener.ItemFilter
import com.topview.purejoy.musiclibrary.player.util.cast
import java.lang.ref.WeakReference

open class MediaControllerImpl<T : Item>(
    private val player: MusicPlayer,
    private val list: MutableList<T> = mutableListOf(),
    var position: Position,
    var listenerManger: MediaListenerManger,
    var loader: WeakReference<Loader>,
    handler: Handler = Handler(Looper.myLooper()!!),
    var itemCallback: Loader.Callback<Item>? = null,
    var preparedListener: PreparedListener<T>? = null,
    var errorListener: ErrorListener<T>? = null,
    var completeListener: CompleteListener<T>? = null,
    var cacheStrategy: CacheStrategy? = null
) : MediaController {

    private val TAG = "MediaController"

    private val callback = DefaultCallback(onSuccess = { index, item ->
        callbackSuccess(index, item)
        itemCallback?.callback(index, item)
    }, handler)

    open fun callbackSuccess(itemIndex: Int, item: Item) {
        if (list[position.current()].isSame(item)) {
            item.cast<T> {
                list[position.current()] = it
                if (it.url() == null) {
                    errorListener?.onError(it)
                }
            }
            if (item.url() != null) {
                setDataSource(item)
            }
        } else {
            var index = -1;
            for (i in 0 until list.size) {
                if (list[i].isSame(item)) {
                    index = i
                    break
                }
            }
            if (index != -1) {
                item.cast<T> {
                    list[index] = it
                }
            }
        }
    }

    override fun last() {
        val last = position.current()
        setDataSource(list[position.last()])
        if (isItemChange(last)) {
            notifyItemChange()
        }
    }

    private fun notifyItemChange() {
        listenerManger.invokeChangeListener(list[position.current()], ItemFilter)
    }

    private fun isItemChange(last: Int): Boolean {
        return last != position.current()
    }

    override fun next() {
        val last = position.current()
        setDataSource(list[position.next()])
        if (isItemChange(last)) {
            notifyItemChange()
        }

    }

    private fun setDataSource(item: Item) {
        if (TextUtils.isEmpty(item.url())) {
            var index = -1
            for(i in 0 until list.size) {
                if (list[i].isSame(item)) {
                    index = i
                    break
                }
            }
            loader.get()?.onLoadItem(index, item, callback)
        } else {
            val path = cacheStrategy?.getRecord(item.url()!!)
            player.setDataSource(path ?: item.url()!!)
        }
    }

    override fun playOrPause() {
        if (player.isPrepared()) {
            player.playOrPause()
        } else {
            setDataSource(list[position.current()])
            notifyItemChange()
        }
    }

    override fun duration(): Int {
        return player.duration()
    }

    override fun seekTo(progress: Int) {
        player.seekTo(progress)
    }

    private class DefaultCallback(
        val onSuccess: (Int, Item) -> Unit,
        val handler: Handler) : Loader.Callback<Item> {
        override fun callback(itemIndex: Int, value: Item) {
            handler.post {
                onSuccess.invoke(itemIndex, value)
            }
        }


    }

    override fun isPlaying(): Boolean {
        return player.isPlaying()
    }

    override fun reset() {
        player.reset()
    }

    override fun progress(): Int {
        return player.progress()
    }

    override fun release() {
        player.release()
    }

}