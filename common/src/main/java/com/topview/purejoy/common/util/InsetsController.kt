package com.topview.purejoy.common.util

import android.view.View
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

/**
 * can also see [com.google.accompanist.systemuicontroller.SystemUiController]
 */
interface InsetsController {
    /**
     * 设置StatusBar类型的Inset是否可见
     */
    var isStatusBarVisible: Boolean

    /**
     * 设置NavigationBar类型的Inset是否可见
     */
    var isNavigationBarVisible: Boolean

    /**
     * 设置SystemGestures类型的Inset是否可见
     */
    var isSystemGesturesVisible: Boolean

    var isSystemBarsVisible: Boolean
        get() = isNavigationBarVisible && isStatusBarVisible
        set(value) {
            isStatusBarVisible = value
            isNavigationBarVisible = value
        }

    /**
     * 设置指定的Insets类型展示或隐藏
     * @param show 如果为true，展示指定Insets，否则隐藏
     */
    fun showOrHideInsets(@WindowInsetsCompat.Type.InsetsType types: Int, show: Boolean)

    /**
     * 访问指定类型的Insets的显示类型
     * @return 如果可见返回true，否则返回false
     */
    fun isInsetsVisible(@WindowInsetsCompat.Type.InsetsType types: Int): Boolean

    /**
     * 设置状态栏的行为，具体的常量包括[WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_TOUCH]<br>
     * [WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_SWIPE]<br>
     * [WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE]<br>
     */
    fun setSystemBarBehavior(behavior: Int)
}


@Composable
fun rememberInsetsController(): InsetsController {
    val view = LocalView.current
    return remember(view) { AndroidInsetsController(view) }
}


internal class AndroidInsetsController(
    private val view: View
) : InsetsController {

    private val windowInsetsController = ViewCompat.getWindowInsetsController(view)!!

    override var isStatusBarVisible: Boolean
        get() = isInsetsVisible(WindowInsetsCompat.Type.statusBars())

        set(value) {
            showOrHideInsets(WindowInsetsCompat.Type.statusBars(), value)
        }

    override var isNavigationBarVisible: Boolean
        get() = isInsetsVisible(WindowInsetsCompat.Type.navigationBars())

        set(value) {
            showOrHideInsets(WindowInsetsCompat.Type.navigationBars(), value)
        }
    override var isSystemGesturesVisible: Boolean
        get() = isInsetsVisible(WindowInsetsCompat.Type.systemGestures())

        set(value) {
            showOrHideInsets(WindowInsetsCompat.Type.systemGestures(), value)
        }

    override fun showOrHideInsets(types: Int, show: Boolean) {
        if (show) {
            windowInsetsController.show(types)
        } else {
            windowInsetsController.hide(types)
        }
    }

    // false或者null都视为不可见
    override fun isInsetsVisible(types: Int): Boolean =
        ViewCompat.getRootWindowInsets(view)?.isVisible(types) == true

    override fun setSystemBarBehavior(behavior: Int) {
        windowInsetsController.systemBarsBehavior = behavior
    }
}