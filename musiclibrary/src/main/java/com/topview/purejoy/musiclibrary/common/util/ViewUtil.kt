package com.topview.purejoy.musiclibrary.common.util

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity

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