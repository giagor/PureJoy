package com.topview.purejoy.musiclibrary.player.impl.controller

import android.text.TextUtils
import com.topview.purejoy.musiclibrary.data.Item
import com.topview.purejoy.musiclibrary.player.abs.controller.MediaController
import com.topview.purejoy.musiclibrary.player.abs.core.MusicPlayer
import com.topview.purejoy.musiclibrary.player.abs.core.Position
import com.topview.purejoy.musiclibrary.player.abs.listener.CompleteListener
import com.topview.purejoy.musiclibrary.player.abs.listener.ErrorListener
import com.topview.purejoy.musiclibrary.player.abs.listener.PreparedListener

open class MediaControllerImpl<T : Item>(
    private val player: MusicPlayer,
    private val list: MutableList<T> = mutableListOf(),
    var position: Position,
    var preparedListener: PreparedListener<T>? = null,
    var errorListener: ErrorListener<T>? = null,
    var completeListener: CompleteListener<T>? = null,
) : MediaController {

    private val TAG = "MediaController"


    override fun last() {
        val last = position.current()
        setDataSource(list[position.last()])

    }



    private fun isItemChange(last: Int): Boolean {
        return last != position.current()
    }

    override fun next() {
        val last = position.current()
        setDataSource(list[position.next()])
        // maybe notify client

    }

    private fun setDataSource(item: Item) {
        if (TextUtils.isEmpty(item.url())) {
            // handle error
        } else {
            // maybe use cache
            player.setDataSource(item.url()!!)
        }
    }

    override fun playOrPause() {
        if (player.isPrepared()) {
            player.playOrPause()
        } else {
            setDataSource(list[position.current()])
            // maybe notify client the current playing songs has changed
        }
    }

    override fun duration(): Int {
        return player.duration()
    }

    override fun seekTo(progress: Int) {
        player.seekTo(progress)
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