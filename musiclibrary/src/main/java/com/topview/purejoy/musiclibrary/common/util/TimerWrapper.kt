package com.topview.purejoy.musiclibrary.common.util

import java.util.*

class TimerWrapper(val duration: Long, val delay: Long = 0, val task: () -> Unit) {

    private var timer: Timer? = null

    fun reset() {
        timer?.cancel()
        timer = null
    }

    fun start() {
        reset()
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                task.invoke()
            }
        }, delay, duration)
    }

}