package com.topview.purejoy.common.music.player.abs.core

interface MusicPlayer<T> {
    fun isPrepared(): Boolean
    fun isPlaying(): Boolean
    fun duration(): Int

    fun playOrPause()
    fun reset()
    fun setDataSource(source: T)
    fun seekTo(progress: Int)
    fun progress(): Int

    fun release()

    var preparedListener: PreparedListener?

    var completeListener: CompleteListener?

    var errorListener: ErrorListener<T>?

    interface PreparedListener {
        fun prepared()
    }

    interface CompleteListener {
        fun completed()
    }

    interface ErrorListener<T> {
        fun onError(player: MusicPlayer<T>, what: Int, extra: Int): Boolean
    }
}