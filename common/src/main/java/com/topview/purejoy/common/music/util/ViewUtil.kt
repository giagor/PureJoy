package com.topview.purejoy.common.music.util

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Rect
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.topview.purejoy.common.base.CommonActivity
import com.topview.purejoy.common.music.instance.BinderPoolClientInstance
import com.topview.purejoy.common.music.player.client.BinderPoolClient

fun AppCompatActivity.addViewToContent(view: View, marginBottom: Int,
                                       duration: Long = 1000L,
                                       width: Int = ViewGroup.LayoutParams.MATCH_PARENT,
                                       height: Int = ViewGroup.LayoutParams.WRAP_CONTENT) {
    val contentView = ((window.decorView as ViewGroup).getChildAt(0)
            as ViewGroup).getChildAt(1) as FrameLayout
    val lp = FrameLayout.LayoutParams(width, height)
    lp.gravity = Gravity.BOTTOM
    val animator = MarginBottomAnimator(contentView, view, lp, duration, marginBottom)
    animator.start()
}

fun AppCompatActivity.addViewToContent(@LayoutRes layoutId: Int, marginBottom: Int,
                                       duration: Long = 1000L,
                                       width: Int = ViewGroup.LayoutParams.MATCH_PARENT,
                                       height: Int = ViewGroup.LayoutParams.WRAP_CONTENT) {
    val view = LayoutInflater.from(this).inflate(layoutId, null)
    addViewToContent(view, marginBottom, duration, width, height)
}


@SuppressLint("Recycle")
private class MarginBottomAnimator(
    val contentView: FrameLayout,
    val childView: View,
    val lp: FrameLayout.LayoutParams,
    duration: Long,
    marginBottom: Int) : ValueAnimator(), ValueAnimator.AnimatorUpdateListener {

    init {
        addUpdateListener(this)
        this.duration = duration
        setIntValues(0, marginBottom)
        interpolator = LinearInterpolator()
    }

    override fun onAnimationUpdate(animation: ValueAnimator?) {
        lp.bottomMargin = animatedValue as Int
        contentView.removeView(childView)
        contentView.addView(childView, lp)
    }

}

/**
 * CommonActivity的拓展方法，为了方便连接音乐服务和获取服务客户端的单例
 */
fun CommonActivity.getAndConnectService(clazz: Class<*>): BinderPoolClient {
    val client = BinderPoolClientInstance.getInstance().getClient(clazz)
    client.connectService()
    return client
}


fun CommonActivity.getDisplaySize(): Rect {
    val rect = Rect()
    rect.left = 0
    rect.top = 0
    val metrics = resources.displayMetrics
    rect.right = metrics.widthPixels
    rect.bottom = metrics.heightPixels
    return rect
}
