package com.topview.purejoy.common.music.player.impl.cache

import android.util.LruCache
import com.topview.purejoy.common.music.player.abs.cache.Cache
import com.topview.purejoy.common.music.player.abs.cache.CacheLoader
import com.topview.purejoy.common.music.player.abs.cache.CacheStrategy
import com.topview.purejoy.common.music.util.ExecutorInstance
import java.io.File
import java.io.InputStream
import java.util.concurrent.Executor

open class CacheStrategyImpl(
    cacheDirectory: File,
    maxMemorySize: Int,
    executor: Executor = ExecutorInstance.getInstance().service,
    suffix: String? = null
): CacheStrategy {
    override var loader: CacheLoader? = null
    override val memoryCache: Cache<String> = MemoryCache(LruCache(maxMemorySize))
    override val diskCache: Cache<InputStream> = DiskCache(
        parent = cacheDirectory,
        executor = executor, object : DiskCache.DiskCacheListener {
            override fun onSuccess(key: String, value: String) {
                putInMemory(key, value)
            }
        }, suffix = suffix)

    override fun getRecord(key: String): String? {
        val value = memoryCache.get(key) ?: diskCache.get(key)
        if (value == null) {
            loader?.load(key)
        }
        return value
    }

    override fun putInMemory(key: String, value: String) {
        memoryCache.put(key, value)
    }

    override fun putInDisk(key: String, value: InputStream) {
        diskCache.put(key, value)
    }


}