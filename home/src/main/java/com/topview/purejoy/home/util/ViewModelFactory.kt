package com.topview.purejoy.home.util

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelProvider

fun getAndroidViewModelFactory(context: Context) =
    ViewModelProvider.AndroidViewModelFactory.getInstance(
        context.applicationContext as Application
    ) 