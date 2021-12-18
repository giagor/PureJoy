package com.topview.purejoy.common.music.player.abs.cache

import java.io.InputStream

// 缓存策略
interface CacheStrategy {
    // 内存缓存
    val memoryCache: Cache<String>
    // 磁盘缓存
    val diskCache: Cache<InputStream>

    var loader: CacheLoader?

    /**
     * @param key 写入缓存时所用的键
     * @return key对应的文件路径，有可能为null
     * 从内存或磁盘缓存中获取相应的文件路径
     */
    fun getRecord(key: String): String?

    /**
     * @param key 写入缓存时所用的键(一般为音乐的URL)
     * @param value 本地文件路径
     * 将本地文件路径写入内存中
     */
    fun putInMemory(key: String, value: String)

    /**
     * @param key 写入缓存时所用的键(一般为音乐的URL)
     * @param value key所对应的流
     * 将音乐相应的流写入本地文件中
     */
    fun putInDisk(key: String, value: InputStream)

}