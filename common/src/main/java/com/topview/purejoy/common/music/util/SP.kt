package com.topview.purejoy.common.music.util

import android.content.Context
import android.content.SharedPreferences
import com.topview.purejoy.common.app.CommonApplication

class SP {
    companion object {

        const val SP_NAME = "music"

        val sp by lazy {
            getSharePreference(SP_NAME)
        }

        fun getSharePreference(name: String): SharedPreferences {
            return CommonApplication.getContext().getSharedPreferences(name, Context.MODE_PRIVATE)
        }

    }
}