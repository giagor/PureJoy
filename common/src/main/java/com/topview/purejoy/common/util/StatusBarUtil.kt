package com.topview.purejoy.common.util

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * 对状态栏进行修改的工具类
 */
object StatusBarUtil {
    /**
     * 在改变状态栏颜色之前，必须对Window做一些必要的设置。
     */
    @Deprecated("不再需要调用这个方法")
    fun initWindow(window: Window): Window {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        return window
    }

    /**
     * 将状态栏的字符改为黑色，这是原生的设置方法，但仅支持6.0以上的系统
     */
    @RequiresApi(Build.VERSION_CODES.M)
    @Deprecated("deprecated in API 30", ReplaceWith("setStatusBarTextColor()"))
    fun Window.setBlackTextToStatusBar() {
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    /**
     * 这是MIUI系统在6.0以下时曾经使用过的将状态栏的字符改为黑色的方法
     */
    @SuppressLint("PrivateApi")
    fun Window.setBlackTextToStatusBarInMiui() {
        val clazz: Class<out Window?> = this::class.java
        try {
            val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
            val field: Field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
            val darkModeFlag = field.getInt(layoutParams)
            val extraFlagField: Method = clazz.getMethod(
                "setExtraFlags",
                Int::class.javaPrimitiveType, Int::class.javaPrimitiveType
            )
            extraFlagField.invoke(this, darkModeFlag, darkModeFlag)
        } catch (ignore: Exception) {
        }
    }

    /**
     * 将状态栏的字符改为白色
     */
    @Deprecated("deprecated in API 30", ReplaceWith("setStatusBarTextColor()"))
    fun Window.setWhiteTextToStatusBar() {
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }

    /**
     * 将状态栏设为沉浸式。沉浸效果必须设置SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN这个flag，
     * 但界面会变成全屏模式，此时布局将可能与状态栏重叠。
     * 解决重叠问题需要在根布局中设置android:fitsSystemWindows="true"，让布局自动预留状态栏的位置。
     * 如果fitsSystemWindows属性失效，可以使用status_bar_height这个自定义的dimension设置paddingTop。
     * 不使用这个方法也可以做出沉浸效果，可以在每次Fragment切换时，及时更新背景颜色。
     * @param dark 指定状态栏文字的颜色，为true时表示设置为黑色
     */
    @Deprecated("don't use this function any more")
    fun Window.immersiveStatusBar(dark: Boolean) {
        this.statusBarColor = Color.TRANSPARENT
        if (dark) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setBlackTextToStatusBar()
            } else {
                // 尝试使用MIUI的API
                setBlackTextToStatusBarInMiui()
            }
        } else {
            setWhiteTextToStatusBar()
        }
        // 设置为全屏模式
        decorView.systemUiVisibility = decorView.systemUiVisibility or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

    /**
     * 是否允许DecorView自动适应SystemWindow的高度。注意，关闭适应同时影响StatusBar和NavigationBar
     */
    fun Window.setAutoFitSystemWindows(decorFitsSystemWindows: Boolean): Window {
        WindowCompat.setDecorFitsSystemWindows(this, decorFitsSystemWindows)
        return this
    }

    /**
     * 修改系统顶部栏的背景颜色
     */
    fun Window.setStatusBarBackgroundColor(@ColorInt color: Int): Window {
        statusBarColor = color
        return this
    }

    /**
     * 修改系统底部导航栏的背景颜色
     */
    fun Window.setNavigationBarBackgroundColor(@ColorInt color: Int): Window {
        navigationBarColor = color
        return this
    }

    /**
     * 修改状态栏的字符的颜色。设置为黑色的调用在API 23以下不起作用
     * @param dark 传入true以将状态栏颜色修改为黑色
     */
    fun Window.setStatusBarTextColor(dark: Boolean): Window {
        WindowCompat.getInsetsController(
            this, decorView)?.isAppearanceLightStatusBars = dark
        return this
    }

    /**
     * 为传入的[view]设置全局的top padding和bottom padding，以消除StatusBar、NavigationBar与界面的重叠。
     * 使用前请务必调用[setAutoFitSystemWindows]来关闭DecorView对StatusBar的适配，否则将会重复设置padding
     * 不建议传入window.decorView作为接受padding的view，这会导致无法通过[setStatusBarBackgroundColor]
     * 来修改状态栏背景。[consumed]默认为false，表示不消费Insets，在API>=30，同层的Insets消费与否不再会互相干扰
     */
    fun fitSystemBar(view: View, consumed: Boolean = true) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBar = insets.getInsets(
                WindowInsetsCompat.Type.systemGestures()
                    or WindowInsetsCompat.Type.navigationBars())
            v.updatePadding(top = systemBar.top, bottom = systemBar.bottom)
            if (consumed) {
                WindowInsetsCompat.CONSUMED
            } else {
                insets
            }
        }
        view.requestApplyInsets()
    }
}