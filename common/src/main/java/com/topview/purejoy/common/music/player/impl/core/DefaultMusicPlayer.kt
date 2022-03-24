package com.topview.purejoy.common.music.player.impl.core

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.topview.purejoy.common.music.player.abs.core.MusicPlayer

class DefaultMusicPlayer(
    override var preparedListener: MusicPlayer.PreparedListener? = null,
    private val handler: Handler = Handler(Looper.getMainLooper()),
    override var completeListener: MusicPlayer.CompleteListener? = null,
    override var errorListener: MusicPlayer.ErrorListener<String>? = null
) : MusicPlayer<String> {
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
            handler.post {
                if(errorListener?.onError(this, what, extra) != true) {
                    reset()
                }
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

    override fun setDataSource(source: String) {
        reset()
        player.setDataSource(source)
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