package com.topview.purejoy.common.music.player.impl.controller

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import com.topview.purejoy.common.music.data.Item
import com.topview.purejoy.common.music.player.abs.Loader
import com.topview.purejoy.common.music.player.abs.MediaListenerManger
import com.topview.purejoy.common.music.player.abs.cache.CacheStrategy
import com.topview.purejoy.common.music.player.abs.controller.MediaController
import com.topview.purejoy.common.music.player.abs.core.MusicPlayer
import com.topview.purejoy.common.music.player.abs.core.Position
import com.topview.purejoy.common.music.player.abs.listener.CompleteListener
import com.topview.purejoy.common.music.player.abs.listener.ErrorListener
import com.topview.purejoy.common.music.player.abs.listener.PreparedListener
import com.topview.purejoy.common.music.player.impl.listener.ItemFilter
import com.topview.purejoy.common.music.player.impl.listener.PlayStateFilter
import com.topview.purejoy.common.music.player.util.cast
import java.lang.ref.WeakReference

open class MediaControllerImpl<T : Item>(
    private val player: MusicPlayer<String>,
    private val list: MutableList<T> = mutableListOf(),
    override var position: Position,
    override var listenerManger: MediaListenerManger,
    override var loader: WeakReference<Loader>,
    handler: Handler = Handler(Looper.myLooper()!!),
//    var itemCallback: Loader.Callback<Item>? = null,
    var cacheStrategy: CacheStrategy? = null,
//    var loadErrorListener: LoadErrorListener<T>? = null,
    override var preparedListener: MusicPlayer.PreparedListener? = null,
    override var completeListener: MusicPlayer.CompleteListener? = null,
    override var errorListener: MusicPlayer.ErrorListener<T>? = null
) : MediaController<T> {

    private val TAG = "MediaController"

//    private val callback = DefaultCallback(onSuccess = { index, item ->
//        callbackSuccess(index, item)
////        itemCallback?.callback(index, item)
//    }, handler)

//    open fun callbackSuccess(itemIndex: Int, item: Item) {
//        if (list[position.current()] == item) {
//            item.cast<T> {
//                list[position.current()] = it
//                if (it.url() == null) {
//                    loadErrorListener?.onError(it)
//                }
//            }
//            if (item.url() != null) {
//                setDataSourceInternal(item)
//            }
//        } else {
//            val index = list.indexOf(item)
//            if (index != -1) {
//                item.cast<T> {
//                    list[index] = it
//                }
//            }
//        }
//    }

    override fun last() {
        if (list.isNotEmpty()) {
            val last = position.current()
            setDataSourceInternal(list[position.last()])
            if (isItemChange(last)) {
                notifyItemChange()
            }
        }
    }

    private fun notifyItemChange() {
        listenerManger.invokeChangeListener(list[position.current()], ItemFilter)
    }

    private fun isItemChange(last: Int): Boolean {
        return last != position.current()
    }

    override fun next() {
        if (list.isNotEmpty()) {
            val last = position.current()
            setDataSourceInternal(list[position.next()])
            if (isItemChange(last)) {
                notifyItemChange()
            }
        }

    }

    private fun setDataSourceInternal(item: Item) {
        if (player.isPlaying()) {
            player.playOrPause()
        }
        if (TextUtils.isEmpty(item.url())) {
            val index = list.indexOf(item)
            loader.get()?.onLoadItem(index, item)
        } else {
            val path = cacheStrategy?.getRecord(item.url()!!)
            player.setDataSource(path ?: item.url()!!)
        }
    }

    override fun playOrPause() {
        if (player.isPrepared()) {
            player.playOrPause()
        } else {
            setDataSourceInternal(list[position.current()])
            notifyItemChange()
        }
        listenerManger.invokeChangeListener(player.isPlaying(), PlayStateFilter)
    }

    override fun duration(): Int {
        return player.duration()
    }

    override fun seekTo(progress: Int) {
        player.seekTo(progress)
    }

//    private class DefaultCallback(
//        val onSuccess: (Int, Item) -> Unit,
//        val handler: Handler) : Loader.Callback<Item> {
//        override fun callback(itemIndex: Int, value: Item) {
//            handler.post {
//                onSuccess.invoke(itemIndex, value)
//            }
//        }
//
//
//    }

    override fun isPlaying(): Boolean {
        return player.isPlaying()
    }

    override fun reset() {
        player.reset()
    }

    override fun setDataSource(source: T) {
        setDataSourceInternal(source)
    }

    override fun progress(): Int {
        return player.progress()
    }

    override fun release() {
        player.release()
    }

    override fun jumpTo(index: Int) {
        val p = TempPosition(max = position.max, current = index)
        position.with(p)
        val item = list[position.current()]
        setDataSourceInternal(item)
        notifyItemChange()
    }

    override fun isPrepared(): Boolean {
        return player.isPrepared()
    }

    private class TempPosition(override var max: Int, val current: Int) : Position {
        override fun current(): Int {
            return current
        }

        override fun next(): Int {
            return 0
        }

        override fun last(): Int {
            return 0
        }

        override fun with(position: Position) {

        }

    }

}