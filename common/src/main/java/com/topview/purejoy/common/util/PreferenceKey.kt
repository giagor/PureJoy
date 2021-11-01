package com.topview.purejoy.common.util

import android.content.Context
import android.preference.PreferenceManager
import com.topview.purejoy.common.R
import com.topview.purejoy.common.entity.TextSizeScale

/**
 * 储存能够读取设置项的相对应的键。使用SharedPreference储存。
 * 读取设置项示例：PreferenceManager.getDefaultSharedPreferences(context).getFloat(FONT_SCALE_KEY,
 *                 TextSizeScale.STANDARD.scale)
 * 类内字段的值在MainActivity初始化的时候被读取并设置到这个类中，
 * 需要字体放大倍数这个信息的话尽量读取现成的数据
 */
object PreferenceKey {

    private lateinit var keyAcceptNotification: String

    /**
     * 是否允许发送推送消息，储存的值的类型为Boolean
     */
    val KEY_ACCEPT_NOTIFICATION
        get() = keyAcceptNotification

    private lateinit var keyLoadImageWifiOnly: String

    /**
     * 是否仅允许在wifi环境下加载图片，储存的值的类型为Boolean
     */
    val KEY_LOAD_IMAGE_WIFI_ONLY
        get() = keyLoadImageWifiOnly

    private lateinit var keyAutoPlayVideoOnWifi: String

    /**
     * 是否允许在wifi环境下自动播放视频，储存的值的类型为Boolean
     */
    val KEY_AUTO_PLAY_VIDEO_ON_WIFI
        get() = keyAutoPlayVideoOnWifi

    private lateinit var keyDisplayAnimal: String

    /**
     * 首页是否展示宠物，储存的值的类型为Boolean
     */
    val KEY_DISPLAY_ANIMAL
        get() = keyDisplayAnimal

    private lateinit var keySelectedAnimal: String

    /**
     * 展示的宠物的种类，储存的值的类型为Int
     */
    val KEY_SELECTED_ANIMAL
        get() = keySelectedAnimal

    private lateinit var keyAllowUseVoice: String

    /**
     * 是否开启语音播报功能，储存的值的类型为Boolean
     */
    val KEY_ALLOW_USE_VOICE
        get() = keyAllowUseVoice

    private lateinit var keyVoiceType: String

    /**
     * 语音播报的语音包选择，储存的值的类型为Int
     */
    val KEY_VOICE_TYPE
        get() = keyVoiceType

    /**
     * 全局字体放大倍数，储存的值的类型为Float
     */
    const val FONT_SCALE_KEY = "globalFontScale"

    /**
     * 全局字体大小缩放系数
     */
    var globalTextSizeScale: Float = 0F
        private set

    /**
     * 是否允许使用位置信息
     */
    var allowUseLocation: Boolean = false

    /**
     * 将字符串从xml加载到幕后字段上
     */
    fun refreshResourceKey(context: Context?) {
        context?.let {
            if (! ::keyAcceptNotification.isInitialized) {
                it.resources?.apply {
                    keyAcceptNotification = getString(R.string.mine_key_accept_notification)
                    keyLoadImageWifiOnly = getString(R.string.mine_key_load_image_wifi_only)
                    keyAutoPlayVideoOnWifi = getString(R.string.mine_key_auto_play_video_on_wifi)
                    keyDisplayAnimal = getString(R.string.mine_key_display_animal)
                    keySelectedAnimal = getString(R.string.mine_key_selected_animal)
                    keyAllowUseVoice = getString(R.string.mine_key_allow_use_voice)
                    keyVoiceType = getString(R.string.mine_key_voice_type)
                }
            }
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(it)
            globalTextSizeScale = sharedPreferences.getFloat(
                FONT_SCALE_KEY, TextSizeScale.STANDARD.scale)
        }
    }

}