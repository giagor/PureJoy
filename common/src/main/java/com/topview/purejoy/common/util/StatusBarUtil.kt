package com.topview.purejoy.common.util

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * 改变状态栏颜色的工具类
 * 典型调用方式，将背景颜色设置为黑色，字体设置为白色
 * StatusBarUtil.initWindow(window).setStatusBarBackground(Color.BLACK).setWhiteTextToStatusBar()
 */
object StatusBarUtil {
    /**
     * 在改变状态栏颜色之前，必须对Window做一些必要的设置。
     */
    fun initWindow(window: Window): Window {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        return window
    }

    fun Window.setStatusBarBackground(@ColorInt color: Int): Window {
        statusBarColor = color
        return this
    }

    /**
     * 将状态栏的字符改为黑色，这是原生的设置方法，但仅支持6.0以上的系统
     */
    @RequiresApi(Build.VERSION_CODES.M)
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
}