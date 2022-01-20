package com.topview.purejoy.video.ui

import androidx.lifecycle.viewModelScope
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.topview.purejoy.common.app.CommonApplication
import com.topview.purejoy.common.entity.Video
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel
import com.topview.purejoy.video.data.repo.VideoRepository
import com.topview.purejoy.video.ui.state.VideoLoadState
import com.topview.purejoy.video.videoConfiguration
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow

class VideoViewModel: MVVMViewModel(), Player.Listener {

    private val repository = VideoRepository(
        videoConfiguration.initialList
    )

    val exoPlayer = ExoPlayer.Builder(CommonApplication.getContext()).build()

    private val _videoLoadState: MutableStateFlow<VideoLoadState> = MutableStateFlow(VideoLoadState.Loading)

    val videoLoadState: StateFlow<VideoLoadState> = _videoLoadState

    /**
     * 发送video当前的进度的flow
     */
    val progressFlow = flow {
        while (true) {
            _videoLoadState.value.run {
                // 只有在状态为正在播放才更新进度
                if (this is VideoLoadState.Playing) {
                    emit(exoPlayer.currentPosition)
                }
            }
            delay(1000L)
        }
    }.conflate()

    private var stateReadyCallback: ((ExoPlayer) -> Unit)? = null


    init {
        exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
        exoPlayer.addListener(this)
    }

    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        _videoLoadState.value = VideoLoadState.Error(errorCode = error.errorCode)
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        when(playbackState) {
            Player.STATE_BUFFERING -> {
                _videoLoadState.value = VideoLoadState.Loading
            }
            Player.STATE_READY -> {
                // Loading -> Playing
                val callback = stateReadyCallback
                stateReadyCallback = null
                callback?.invoke(exoPlayer)
                _videoLoadState.value = VideoLoadState.Playing
                exoPlayer.play()
            }
            else -> {}
        }
    }

    fun seekTo(milliSecond: Long) {
        exoPlayer.seekTo(milliSecond)
    }

    /**
     * 暂停视频播放
     * 状态跳转: Playing->Pause
     */
    fun pause() {
        if (_videoLoadState.value is VideoLoadState.Playing) {
            _videoLoadState.value = VideoLoadState.Pause
            exoPlayer.pause()
        }
    }

    /**
     * 继续视频播放。
     * 状态跳转: Pause->Playing
     */
    fun play() {
        if (_videoLoadState.value is VideoLoadState.Pause) {
            _videoLoadState.value = VideoLoadState.Playing
            exoPlayer.play()
        }
    }

    /**
     * 当页面切换时触发的处理函数。这个函数在首次加载到第一页的时候也可以触发
     */
    fun onPageChange(video: Video?) {
        exoPlayer.stop()
        video?.apply {
            if (hasEmptyField()) {
                loadDetail(this)
            }
            videoUrl?.let {
                exoPlayer.setMediaItem(MediaItem.fromUri(it))
                exoPlayer.prepare()
            } ?: applyVideoUrl()
        }
        if (_videoLoadState.value !is VideoLoadState.Loading) {
            _videoLoadState.value = VideoLoadState.Loading
        }
    }

    /**
     * 重新加载的处理函数
     */
    fun reloadVideo(video: Video?) {
        onPageChange(video)
    }

    fun getPagingVideoFlow() = repository.getPagingVideoFlow()


    private fun Video.applyVideoUrl() {
        viewModelScope.rxLaunch<String> {
            onRequest = {
                if (isMv) {
                    repository.getMVPlayUrl(id)
                } else {
                    repository.getVideoPlayUrl(id)
                }
            }
            onSuccess = {
                videoUrl = it
                exoPlayer.setMediaItem(MediaItem.fromUri(it))
                exoPlayer.prepare()
            }
            onError = {
                _videoLoadState.value = VideoLoadState.Error(
                    PlaybackException.ERROR_CODE_UNSPECIFIED
                )
            }
            onEmpty = {
                _videoLoadState.value = VideoLoadState.Error(
                    PlaybackException.ERROR_CODE_UNSPECIFIED
                )
            }
        }
    }

    /**
     * 检测是否存在尚未加载数据的域
     */
    private fun Video.hasEmptyField(): Boolean {
        return duration == Video.UNSPECIFIED_LONG ||
                likedCount == Video.UNSPECIFIED ||
                shareCount == Video.UNSPECIFIED ||
                commentCount == Video.UNSPECIFIED
    }

    private fun loadDetail(video: Video) {
        viewModelScope.rxLaunch<Unit> {
            onRequest = {
                repository.loadDetailOfVideo(video = video)
                // MV的接口可能无法获取到真正的持续时间，我们尝试从ExoPlayer处获取
                if (video.duration <= 0) {
                    exoPlayer.run {
                        if (duration > 0) {
                            video.duration = duration
                        } else {
                            stateReadyCallback = {
                                video.duration = it.duration
                            }
                        }
                    }
                }
            }
            onError = {
                print(it)
            }
        }
    }
}