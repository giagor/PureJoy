package com.topview.purejoy.video.ui.state

sealed class VideoLoadState {
    object Loading: VideoLoadState()
    object Error: VideoLoadState()
    object Playing: VideoLoadState()
    object Pause: VideoLoadState()
}