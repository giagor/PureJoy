package com.topview.purejoy.video

import com.topview.purejoy.common.entity.Video
import com.topview.purejoy.video.entity.VideoPlayerConfiguration
import com.topview.purejoy.video.router.VideoRouter

object VideoPlayerLauncher {

    var onCloseListener: (() -> Unit)? = null

    /**
     * 设置播放界面关闭时的监听
     */
    fun setOnCloseListener(listener: () -> Unit): VideoPlayerLauncher {
        onCloseListener = listener
        return this
    }

    /**
     * 使用视频的id来启动播放器
     * @param isMv 视频是否均为MV
     */
    fun launch(
        isMv: Boolean,
        vararg videoId: String
    ) {
        val list = videoId.flatMapTo(
            mutableListOf()
        ) {
            listOf(
                Video(
                    id = it,
                    isMv = isMv,
                )
            )
        }
        launch(list, false)
    }

    fun launch(
        videos: List<Video>
    ) {
       launch(videos, true)
    }

    private fun launch(
        videos: List<Video>,
        needCopy: Boolean = true
    ) {
        val closeListener = onCloseListener
        val list = mutableListOf<Video>()
        if (needCopy) {
            videos.forEach {
                list.add(it.copy())
            }
        } else {
            list.addAll(videos)
        }
        videoConfiguration = VideoPlayerConfiguration(
            list,
            closeListener
        )
        onCloseListener = null
        VideoRouter.routeToVideoActivity()
    }
}

internal lateinit var videoConfiguration: VideoPlayerConfiguration