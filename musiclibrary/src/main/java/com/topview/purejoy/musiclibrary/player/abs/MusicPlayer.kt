package com.topview.purejoy.musiclibrary.player.abs

interface MusicPlayer {
    fun isPrepared(): Boolean
    fun isPlaying(): Boolean
    fun duration(): Int

    fun playOrPause()
    fun reset()
    fun setDataSource(url: String)
    fun seekTo(progress: Int)
    fun progress(): Int

    fun release()

    interface PreparedListener {
        fun prepared()
    }

    interface CompleteListener {
        fun completed()
    }

    interface ErrorListener {
        fun onError(what: Int, extra: Int)
    }
}