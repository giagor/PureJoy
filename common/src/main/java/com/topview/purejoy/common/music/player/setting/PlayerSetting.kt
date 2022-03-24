package com.topview.purejoy.common.music.player.setting

import com.topview.purejoy.common.music.data.Item
import com.topview.purejoy.common.music.player.abs.cache.CacheStrategy
import com.topview.purejoy.common.music.player.abs.controller.MediaController
import com.topview.purejoy.common.music.player.abs.core.MusicPlayer
import com.topview.purejoy.common.music.player.abs.transformation.IWrapperTransformation
import com.topview.purejoy.common.music.player.abs.transformation.ItemTransformation
import com.topview.purejoy.common.music.player.impl.DataInterceptor
import com.topview.purejoy.common.music.player.impl.OperatorCallback
import java.lang.IllegalArgumentException

class PlayerSetting<T : Item>(
    val itemTransformation: ItemTransformation<T>,
    val wrapperTransformation: IWrapperTransformation<T>
) {

    // 数据拦截器
    var dataInterceptor: DataInterceptor<T>? = null

    // 缓存策略
    var cacheStrategy: CacheStrategy? = null

    // 数据操作回调
    var operatorCallback: OperatorCallback? = null

    // 播放错误配置
    var errorSetting: ErrorSetting<T>? = null

    // 音乐播放器
    var player: MediaController<T>? = null

    // 基础音乐播放器
    var basePlayer: MusicPlayer<String>? = null

    fun dataInterceptor(dataInterceptor: DataInterceptor<T>?) = apply {
        this.dataInterceptor = dataInterceptor
    }

    fun cacheStrategy(cacheStrategy: CacheStrategy?) = apply {
        this.cacheStrategy = cacheStrategy
    }

    fun operatorCallback(operatorCallback: OperatorCallback?) = apply {
        this.operatorCallback = operatorCallback
    }

    fun errorSetting(errorSetting: ErrorSetting<T>?) = apply {
        this.errorSetting = errorSetting
    }

    fun player(player: MediaController<T>?) = apply {
        if (basePlayer != null) throw IllegalArgumentException()
        this.player = player
    }

    fun basePlayer(base: MusicPlayer<String>?) = apply {
        if (player != null) throw IllegalArgumentException()
        this.basePlayer = base
    }

}