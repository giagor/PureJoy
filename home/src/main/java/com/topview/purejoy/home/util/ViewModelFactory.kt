package com.topview.purejoy.home.util

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.topview.purejoy.common.app.CommonApplication

fun getAndroidViewModelFactory() =
    ViewModelProvider.AndroidViewModelFactory.getInstance(
        CommonApplication.getContext() as Application
    ) 