package com.topview.purejoy.musiclibrary.player.setting

import com.topview.purejoy.musiclibrary.player.abs.core.Position
import com.topview.purejoy.musiclibrary.player.impl.position.LoopPosition
import com.topview.purejoy.musiclibrary.player.impl.position.OrderPosition
import com.topview.purejoy.musiclibrary.player.impl.position.RandomPosition

class MediaModeSetting {
    private val map = mutableMapOf<Int, Position>()
    private val modeList = mutableListOf<Int>()

    /**
     * @param max Position的上限
     * @param current Position指向的值
     * 初始化播放模式及位置信息
     */
    fun init(max: Int, current: Int) {
        addMode(ORDER, OrderPosition(max, current))
        addMode(RANDOM, RandomPosition(max, current))
        addMode(LOOP, LoopPosition(max, current))
    }

    /**
     * @param mode 播放模式
     * 获取相应的Position
     */
    fun getPosition(mode: Int): Position? {
        return map[mode]
    }

    /**
     * 获取位于列表首部的播放模式
     */
    fun getFirstMode(): Int {
        return modeList[0]
    }

    /**
     * @param mode 播放模式
     * @param position 位置信息
     * 添加播放模式及位置信息,可通过此方法来添加自己想要的播放模式
     */
    fun addMode(mode: Int, position: Position): Boolean {
        return if (map.containsKey(mode)) {
            false
        } else {
            map[mode] = position
            modeList.add(mode)
            return true
        }
    }

    /**
     * @param mode 播放模式
     * 根据当前播放模式获取下一个播放模式
     */
    fun getNextMode(mode: Int): Int {
        val index = modeList.indexOf(mode)
        return modeList[(index + 1) % modeList.size]
    }

    /**
     * @param mode 播放模式
     * 移除该播放模式及位置信息
     */
    fun removeMode(mode: Int): Position? {
        modeList.remove(mode)
        return map.remove(mode)
    }

    companion object {
        @Volatile
        private var instance: MediaModeSetting? = null
        const val INIT_POSITION = -1
        const val ORDER = 1
        const val RANDOM = ORDER + 1
        const val LOOP = ORDER + 2

        fun getInstance(): MediaModeSetting {
            if (instance == null) {
                synchronized(MediaModeSetting::class.java) {
                    if (instance == null) {
                        instance = MediaModeSetting()
                    }
                }
            }
            return instance!!
        }
    }

}