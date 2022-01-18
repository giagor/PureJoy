package com.topview.purejoy.video.util

import java.text.DecimalFormat

object ProgressUtil {

    private val arrayOfByteUnit = arrayOf('K', 'M', 'G')
    private val arrayOfNumberUnit = arrayOf("", "万", "亿")

    /**
     * 将以毫秒为单位的十进制时间转换为00:00的60进制形式
     */
    fun toTimeText(progress: Float): String = toTimeText(progress.toLong())

    fun toTimeText(milliSecond: Long): String {
        val totalSecond = (milliSecond / 1000).toInt()
        val totalMinute = totalSecond / 60
        val second = totalSecond % 60
        val builder = StringBuilder()
        val decimalFormat = DecimalFormat("00")
        builder.append(decimalFormat.format(totalMinute))
        builder.append(':')
        builder.append(decimalFormat.format(second))
        return builder.toString()
    }

    /**
     * 将以字节为单位的网络流量按照2的十次方进制转换为KB、MB、GB单位
     */
    fun toTrafficBytes(bytes: Long): String {
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
        builder.append(arrayOfByteUnit[index])
        builder.append('B')
        return builder.toString()
    }

    /**
     * 获取数量的格式化字符串
     * 例如36500将被转换为"3.6万"，小数仅保留一位
     * 如果输入为空，输出的字符为"--"
     */
    fun getFormatString(number: Double): String {
        var c: Double = number
        var i = 0
        val decimalFormat = DecimalFormat("0.#")
        while (c >= 10000) {
            c /= 10000
            i++
        }
        return "${decimalFormat.format(c)}${arrayOfNumberUnit[i]}"
    }

    fun getFormatString(number: Long): String = getFormatString(number.toDouble())

    fun getFormatString(number: Int): String = getFormatString(number.toDouble())
}