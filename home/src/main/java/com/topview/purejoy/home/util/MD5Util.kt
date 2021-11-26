package com.topview.purejoy.home.util

import java.math.BigInteger
import java.security.MessageDigest

class MD5Util {
    companion object {
        /**
         * @param string 要加密的字符串
         * 仅仅是进行简单的MD5加密
         */
        fun simpleEncode(string: String): String {
            val m = MessageDigest.getInstance("MD5")
            m.update(string.toByteArray())
            return BigInteger(1, m.digest()).toString(16)
        }
    }
}