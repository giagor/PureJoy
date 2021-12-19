package com.topview.purejoy.common.component.download.util

import java.security.MessageDigest

/**
 * 对多个字符串进行md5加密
 * */
internal fun md5EncryptForStrings(vararg args: String): String {
    val builder = StringBuilder()
    args.forEach {
        builder.append(it)
    }
    return md5(builder.toString())
}

/**
 * 对单个字符串进行md5加密
 * */
internal fun md5(content: String): String {
    val hash = MessageDigest.getInstance("MD5").digest(content.toByteArray())
    val hex = StringBuilder(hash.size * 2)
    for (b in hash) {
        var str = Integer.toHexString(b.toInt())
        if (b < 0x10) {
            str = "0$str"
        }
        hex.append(str.substring(str.length - 2))
    }
    return hex.toString()
}

/**
 * 返回文件的保存路径
 * */
internal fun getDownloadPath(saveDir: String, saveName: String): String {
    return "$saveDir/$saveName"
}