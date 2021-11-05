package com.topview.purejoy.musiclibrary.player.impl.cache

import com.topview.purejoy.musiclibrary.player.abs.cache.Cache
import java.io.BufferedOutputStream
import java.io.File
import java.io.InputStream
import java.math.BigInteger
import java.security.MessageDigest
import java.util.concurrent.Executor
import java.util.concurrent.Executors

// 磁盘缓存
class DiskCache(
    val parent: File,
    val executor: Executor = Executors.newCachedThreadPool(),
    val listener: DiskCacheListener? = null,
    val digest: Digest? = MD5Digest(),
    val suffix: String? = null)
    : Cache<InputStream> {


    override fun put(key: String, value: InputStream) {
        executor.execute {
            val path = obtainPath(key)
            var file = File(parent, path)
            if (file.exists()) {
                file.delete()
            }
            runCatching {
                file = File.createTempFile(path, suffix, parent)
                val stream = BufferedOutputStream(file.outputStream())
                val array = ByteArray(1024)
                var len = value.read(array)
                while (len != -1) {
                    stream.write(array, 0, len)
                    len = value.read(array)
                }
                stream.flush()
                runCatching {
                    value.close()
                }
                runCatching {
                    stream.close()
                }
            }.onSuccess {
                listener?.onSuccess(key, file.absolutePath)
            }.onFailure {
                runCatching {
                    value.close()
                }
                listener?.onFailure(key)
            }
        }
    }

    private fun obtainPath(key: String): String {
        return digest?.digest(key) ?: key
    }

    // 磁盘缓存回调
    interface DiskCacheListener {
        /**
         * @param key 音乐的URL
         * @param value 本地文件路径
         * 将音乐URL对应的流写入文件成功后的回调
         */
        fun onSuccess(key: String, value: String)

        /**
         * @param key 音乐URL
         * 将音乐URL对应的流写入文件失败后的回调
         */
        fun onFailure(key: String) {

        }
    }

    interface Digest {
        /**
         * @param url 音乐URL
         * @return 相应的文件路径
         * 将音乐URL经过某种算法进行摘要，避免文件名中出现非法字符
         */
        fun digest(url: String): String
    }

    class MD5Digest : Digest {
        override fun digest(url: String): String {
            val m = MessageDigest.getInstance("MD5")
            m.update(url.toByteArray())
            return BigInteger(1, m.digest()).toString(16)
        }
    }

    override fun get(key: String): String? {
        val file = File(parent, obtainPath(key))
        return if (file.exists()) {
            file.absolutePath
        } else {
            null
        }
    }
}