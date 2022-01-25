package com.topview.purejoy.home.util

import android.view.View
import android.view.ViewTreeObserver
import androidx.compose.runtime.*
import androidx.core.view.isVisible
import com.topview.purejoy.common.util.pxToDp

@Composable
internal fun produceViewInsetSize(
    view: View
): State<InsetSize> {
    val observer = remember(view) {
        ViewInsetObserver(view)
    }
    return produceState(initialValue = ZeroSize) {
        observer.start(this)
        awaitDispose {
            observer.stop()
        }
    }
}

class ViewInsetObserver(
    private val view: View
) {
   private val listener: ViewTreeObserver.OnGlobalLayoutListener =
        ViewTreeObserver.OnGlobalLayoutListener {
            getInsetSize(view).let {
                if (state.value != it) {
                    state.value = it
                }
            }
        }

    private lateinit var state: MutableState<InsetSize>

    /**
     * 开始观察尺寸变化
     */
    fun start(state: MutableState<InsetSize>) {
        this.state = state
        state.value = getInsetSize(view)
        view.viewTreeObserver.addOnGlobalLayoutListener(listener)
    }

    /**
     * 停止观察尺寸变化
     */
    fun stop() {
        view.viewTreeObserver.removeOnGlobalLayoutListener(listener)
    }

    private fun getInsetSize(v: View): InsetSize =
        if (view.isVisible) InsetSize(pxToDp(v.width), pxToDp(v.height)) else ZeroSize
}


data class InsetSize(
    val width: Int,
    val height: Int
)

internal val ZeroSize = InsetSize(0, 0)
