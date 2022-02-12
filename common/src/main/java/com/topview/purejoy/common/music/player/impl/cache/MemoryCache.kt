package com.topview.purejoy.common.music.player.impl.cache

import android.util.LruCache
import com.topview.purejoy.common.music.player.abs.cache.Cache

class MemoryCache(val cache: LruCache<String, String>) : Cache<String> {
    override fun put(key: String, value: String) {
        cache.put(key, value)
    }

    override fun get(key: String): String? {
        return cache.get(key)
    }
}