package com.topview.purejoy.common.widget.lyric

import androidx.compose.runtime.*
import androidx.lifecycle.MutableLiveData

sealed class LyricLoadState {
    object Loading: LyricLoadState()
    object Empty: LyricLoadState()
    object Error: LyricLoadState()
    class Success(
        /**
         * 缓存原歌词的集合，歌词应当按照时间顺序排列
         */
        val lyricList: List<Sentence>,
        val transLyricMap: Map<Sentence, Sentence?>
    ): LyricLoadState()
}

@Composable
fun rememberLyricState(
    contentPadding: Float,
    textMargin: Float,
    indicatorPadding: Float,
    indicatorDismissTime: Long,
    showTransLyric: Boolean,
    highlightLineIndex: Int? = null,
    initialLoadState: LyricLoadState = LyricLoadState.Empty
): LyricState {
    return remember {
        LyricState(
            contentPadding = contentPadding,
            textMargin = textMargin,
            indicatorPadding = indicatorPadding,
            indicatorDismissTime = indicatorDismissTime,
            showTransLyric = showTransLyric,
            highlightLineIndex = highlightLineIndex,
            initialLoadState = initialLoadState
        )
    }
}

class LyricState(
    contentPadding: Float,
    textMargin: Float,
    indicatorPadding: Float,
    indicatorDismissTime: Long,
    showTransLyric: Boolean,
    highlightLineIndex: Int? = null,
    initialLoadState: LyricLoadState = LyricLoadState.Empty
) {
    /**
     * 歌词显示区域两旁的Padding，单位为dp
     */
    var contentPadding by mutableStateOf(contentPadding)

    /**
     * 两行歌词之间的Margin，单位为dp
     */
    var textMargin by mutableStateOf(textMargin)

    /**
     * Indicator组件与屏幕边缘的间距，单位为dp
     */
    var indicatorPadding by mutableStateOf(indicatorPadding)

    /**
     * Indicator组件自动消失的时间
     */
    var indicatorDismissTime by mutableStateOf(indicatorDismissTime)

    /**
     * 是否显示翻译歌词，设置为true但[loadState]内不包含翻译歌词的结果时，无效果
     */
    var showTransLyric = MutableLiveData(showTransLyric)

    /**
     * 高亮歌词索引
     */
    var highlightLineIndex = MutableLiveData(highlightLineIndex)

    /**
     * 歌词加载状态
     */
    var loadState: LyricLoadState by mutableStateOf(initialLoadState)
}