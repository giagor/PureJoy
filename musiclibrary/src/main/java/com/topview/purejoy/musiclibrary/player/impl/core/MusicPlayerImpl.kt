package com.topview.purejoy.musiclibrary.player.impl.core

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.topview.purejoy.musiclibrary.player.abs.core.MusicPlayer

class MusicPlayerImpl(
    var preparedListener: MusicPlayer.PreparedListener? = null,
    private val handler: Handler = Handler(Looper.getMainLooper()),
    var completeListener: MusicPlayer.CompleteListener? = null,
    var errorListener: MusicPlayer.ErrorListener? = null
) : MusicPlayer {
    private val player = MediaPlayer()
    @Volatile
    private var isPrepared: Boolean = false
    private val TAG = "MusicPlayerImpl"


    init {
        player.setOnPreparedListener {
            isPrepared = true
            handler.post {
                preparedListener?.prepared()
            }
            player.start()
        }
        player.setOnCompletionListener {
            handler.post {
                completeListener?.completed()
            }
        }
        player.setOnErrorListener { _, what, extra ->
            reset()
            handler.post {
                errorListener?.onError(what, extra)
            }
            return@setOnErrorListener true
        }
    }

    override fun isPrepared() = isPrepared

    override fun isPlaying() = player.isPlaying


    override fun duration(): Int {
        return if (isPrepared) {
            player.duration
        } else {
            0
        }
    }



    override fun playOrPause() {
        if (isPrepared) {
            if (isPlaying()) {
                player.pause()
            } else {
                player.start()
            }
        }
    }

    override fun reset() {
        player.reset()
        isPrepared = false
    }

    override fun setDataSource(url: String) {
        reset()
        player.setDataSource(url)
        player.prepareAsync()
    }

    override fun seekTo(progress: Int) {
        if (isPrepared) {
            player.seekTo(progress)
        }
    }

    override fun progress(): Int {
        return if (isPrepared) {
            player.currentPosition
        } else {
            0
        }
    }

    override fun release() {
        reset()
        player.release()
    }


}