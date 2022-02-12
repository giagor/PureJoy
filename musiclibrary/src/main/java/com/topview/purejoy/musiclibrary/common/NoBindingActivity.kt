package com.topview.purejoy.musiclibrary.common

import androidx.lifecycle.ViewModelProvider
import com.topview.purejoy.common.base.CommonActivity
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel

abstract class NoBindingActivity<VM : MVVMViewModel> : CommonActivity() {
    protected val viewModel: VM by lazy {
        ViewModelProvider(this, createFactory()).get(getViewModelClass())
    }

    protected abstract fun createFactory(): ViewModelProvider.Factory

    protected abstract fun getViewModelClass(): Class<VM>
}