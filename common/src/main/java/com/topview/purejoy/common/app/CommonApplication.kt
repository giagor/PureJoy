package com.topview.purejoy.common.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class CommonApplication : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var context: Context

        fun getContext() = context
    }

    override fun onCreate() {
        super.onCreate()
        context = this
    }
}