package com.topview.purejoy.common.music.service.url.cache

import com.topview.purejoy.common.music.player.impl.cache.CacheStrategyImpl
import com.topview.purejoy.common.music.player.impl.cache.DiskCache
import com.topview.purejoy.common.music.util.ExecutorInstance
import java.io.File
import java.util.concurrent.Executor

class MusicCacheStrategy(val downloadDir: File,
                         cacheDirectory: File,
                         maxMemorySize: Int,
                         executor: Executor = ExecutorInstance.getInstance().service,
                         val suffix: String? = null) :
    CacheStrategyImpl(cacheDirectory, maxMemorySize, executor, suffix) {
    override fun getRecord(key: String): String? {
        var value = super.getRecord(key)
        if (value == null) {
            val file = File(downloadDir, DiskCache.MD5Digest.getInstance()
                .digest(key) + (suffix ?: key.substring(key.lastIndexOf('.'))))
            if (file.exists()) {
                value = file.absolutePath
            }
        }
        return value
    }
}