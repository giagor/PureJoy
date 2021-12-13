package com.topview.purejoy.video.util

object ProgressUtil {
    fun toTimeText(milliSecond: Long): String {
        val totalSecond = (milliSecond / 1000).toInt()
        val totalMinute = totalSecond / 60
        val second = totalSecond % 60
        val minuteString = if (totalMinute < 10) {
            "0$totalMinute"
        } else {
            totalMinute
        }
        val secondString = if (second < 10) {
            "0$second"
        } else {
            second
        }
        return "$minuteString:$secondString"
    }
}