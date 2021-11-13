package com.topview.purejoy.musiclibrary.common.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DefaultFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.newInstance()
    }

    companion object {
        @Volatile
        private var sInstance: ViewModelProvider.Factory? = null

        fun getInstance(): ViewModelProvider.Factory {
            if (sInstance == null) {
                synchronized(DefaultFactory::class.java) {
                    if (sInstance == null) {
                        sInstance = DefaultFactory()
                    }
                }
            }
            return sInstance!!
        }
    }
}