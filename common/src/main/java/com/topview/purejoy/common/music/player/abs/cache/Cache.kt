package com.topview.purejoy.common.music.player.abs.cache

/**
 * 缓存用的接口
 */
interface Cache<T> {
    /**
     * @param key 缓存所用的键
     * @param value 需要缓存的值
     * 将value写入缓存中
     */
    fun put(key: String, value: T)

    /**
     * @param key 写入缓存时的键
     * @return 当缓存中不存在于 key 对应的 value 时返回null，否则返回相应的路径
     */
    fun get(key: String): String?
}