package com.topview.purejoy.video.ui.state

sealed class VideoLoadState {
    object Loading: VideoLoadState()
    class Error(
        val errorCode: Int
    ): VideoLoadState()
    object Playing: VideoLoadState()
    object Pause: VideoLoadState()
}