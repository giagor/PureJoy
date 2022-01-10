package com.topview.purejoy.video.ui.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.text.DecimalFormat

internal class HorizontalVideoScreenState {
    /**
     * 是否处于锁定状态
     */
    var isLocked by mutableStateOf(false)

    /**
     * 播放界面的控制控件是否被显示出来
     */
    var isControlShowing by mutableStateOf(false)

    /**
     * 是否正在展示倍速列表
     */
    var isSpeedListShowing by mutableStateOf(false)

    /**
     * 视频播放速度
     */
    var playbackSpeed by mutableStateOf(PlaySpeed.NORMAL)
}

internal enum class PlaySpeed(
    val speed: Float
) {
    EXTRA_SLOW(0.5F), SLOW(0.75F),
    NORMAL(1F), FAST(1.25F),
    EXTRA_FAST(1.5F), FASTEST(2F);

    override fun toString(): String {
        return DecimalFormat("0.##").format(speed)
    }
}