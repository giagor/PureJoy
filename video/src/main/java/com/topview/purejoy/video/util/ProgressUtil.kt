package com.topview.purejoy.video.util

import java.text.DecimalFormat

object ProgressUtil {

    /**
     * 将以毫秒为单位的十进制时间转换为00:00的60进制形式
     */
    fun toTimeText(progress: Float): String = toTimeText(progress.toLong())

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

    /**
     * 将以字节为单位的网络流量按照2的十次方进制转换为KB、MB、GB单位
     */
    fun toTrafficBytes(bytes: Long): String {
        val array = arrayOf('K', 'M', 'G')
        var index = -1
        var result: Double = bytes.toDouble()
        do {
            result /= 1024
            index ++
        } while (result > 1024)
        val builder = StringBuilder()
        // 如果数值大于10，保留一位小数，否则保留两位小数
        val decimalFormat = if (result > 10) {
            DecimalFormat("0.#")
        } else {
            DecimalFormat("0.##")
        }
        builder.append(decimalFormat.format(result))
        builder.append(array[index])
        builder.append('B')
        return builder.toString()
    }
}