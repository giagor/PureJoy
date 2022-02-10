package com.topview.purejoy.common.widget.lyric

import android.content.Context
import android.util.AttributeSet
import androidx.compose.foundation.LocalIndication
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.topview.purejoy.common.R
import com.topview.purejoy.common.widget.compose.NoIndication
import com.topview.purejoy.common.widget.lyric.theme.*

/**
 * 基于Compose的歌词控件
 * 属性：
 * app:highlightColor 高亮文字颜色
 * app:normalColor 普通文字颜色
 * app:indicatorColor 指示器整体颜色
 * android:background 背景颜色，指定颜色之外的值会崩溃
 */
class LyricView
@JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
    defStyle: Int = 0
): AbstractComposeView(context, attr, defStyle) {

    /**
     * 点击播放按钮后触发的事件
     */
    var onPlayIconClick: (Sentence?) -> Unit by mutableStateOf({})

    /**
     * 点击控件内容后触发的事件
     */
    var onContentClick: () -> Unit by mutableStateOf({})

    /**
     * 控件颜色
     */
    val lyricColors: LyricColors

    /**
     * 控件字体设置
     */
    val lyricTypography: LyricTypography

    /**
     * 控件状态
     */
    val lyricState: LyricState

    init {
        // 获取属性集合
        val typedArray = context.obtainStyledAttributes(attr, R.styleable.LyricView)

        val highlightColor = typedArray.getColor(R.styleable.LyricView_highlightColor,
            DefaultHighlightColor.toArgb())
        val normalColor = typedArray.getColor(R.styleable.LyricView_normalColor,
            android.graphics.Color.BLACK)
        val indicatorColor = typedArray.getColor(R.styleable.LyricView_indicatorColor,
            DefaultIndicatorColor.toArgb())
        val backgroundColor = typedArray.getColor(R.styleable.LyricView_android_background,
            DefaultBackgroundColor.toArgb())

        lyricColors = LyricColors(
            indicatorColor = Color(indicatorColor),
            backgroundColor = Color(backgroundColor)
        )

        lyricTypography = LyricTypography(
            highlightTextStyle = TextStyle(
                color = Color(highlightColor),
                fontSize = 16.sp
            ),
            normalTextStyle = TextStyle(
                color = Color(normalColor),
                fontSize = 16.sp
            ),
            indicatorTextStyle = TextStyle(
                fontSize = 12.sp
            )
        )

        lyricState = LyricState(
            contentPadding = DEFAULT_CONTENT_PADDING,
            textMargin = DEFAULT_TEXT_MARGIN,
            indicatorPadding = DEFAULT_INDICATOR_PADDING,
            indicatorDismissTime = DEFAULT_DISMISS_TIME,
            showTransLyric = false,
        )

        typedArray.recycle()
    }

    @Composable
    override fun Content() {
        CompositionLocalProvider(
            LocalLyricColors provides lyricColors,
            LocalLyricTypography provides lyricTypography,
            LocalIndication provides NoIndication
        ) {
            LyricContent(
                lyricState = lyricState,
                loadState = lyricState.loadState,
                onPlayIconClick = onPlayIconClick,
                onContentClick = onContentClick,
            )
        }
    }

    /**
     * 控件状态转换为empty
     */
    fun setEmpty() {
        lyricState.loadState = LyricLoadState.Empty
    }

    /**
     * 控件状态转换为loading
     */
    fun setLoading() {
        lyricState.loadState = LyricLoadState.Loading
    }

    /**
     * 控件状态转换为success
     * @param originSentence 歌词
     * @param translateSentence 与原歌词对应的翻译歌词，对应关系根据fromTime确定
     */
    fun setSuccess(originSentence: List<Sentence>, translateSentence: List<Sentence>? = null) {
        val map: MutableMap<Sentence, Sentence?> = mutableMapOf()
        if (translateSentence != null) {
            val transTimeMap: MutableMap<Long, Sentence> = mutableMapOf()
            translateSentence.forEach {
                transTimeMap[it.fromTime] = it
            }
            originSentence.forEach {
                map[it] = transTimeMap[it.fromTime]
            }
        }
        // 重置高亮歌词位置
        setHighlightLine(-1)
        lyricState.loadState = LyricLoadState.Success(
            lyricList = originSentence.sortedWith { i1, i2 ->
                (i1.fromTime - i2.fromTime).toInt()
            },
            transLyricMap = map
        )
    }

    /**
     * 控件状态转为error
     */
    fun setError() {
        lyricState.loadState = LyricLoadState.Error
    }

    /**
     * 修改高亮歌词的位置
     */
    fun setHighlightLine(index: Int) {
        lyricState.highlightLineIndex.value = index
    }

    /**
     * 根据进度修改高亮歌词的位置
     * @param progress 当前歌曲进度
     */
    fun setHighlightLineByProgress(progress: Long) {
        if (progress < 0) {
            return
        }
        val loadState = lyricState.loadState
        val highlight = lyricState.highlightLineIndex.value
        if (loadState is LyricLoadState.Success) {
            with(loadState) {
                lyricList.indexOfFirst { progress < it.fromTime }
            }.also {
                if (it != highlight) {
                    setHighlightLine(it - 1)
                }
            }
        }
    }

    /**
     * 修改翻译歌词可见性
     * @param visible 为true时翻译歌词可见
     */
    fun setTransLyricVisible(visible: Boolean) {
        lyricState.showTransLyric.value = visible
    }

    companion object {
        const val DEFAULT_CONTENT_PADDING = 50F
        const val DEFAULT_TEXT_MARGIN = 6F
        const val DEFAULT_INDICATOR_PADDING = 10F
        const val DEFAULT_DISMISS_TIME = 3500L
    }
}

