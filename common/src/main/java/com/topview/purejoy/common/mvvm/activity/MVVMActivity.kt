package com.topview.purejoy.common.mvvm.activity

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.topview.purejoy.common.base.CommonActivity
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel

/**
 * Created by giagor at 2021/03/13
 *
 * MVVM的通用Activity
 * */
abstract class MVVMActivity<VM : MVVMViewModel> : CommonActivity() {
    protected lateinit var viewModel: VM

    // 返回当前Activity绑定的ViewModel类型
    protected abstract fun getViewModelClass(): Class<VM>

    // 创建与当前Activity相关联的ViewModel
    protected fun createViewModel(): VM {
        return ViewModelProvider(this, createFactory()).get(getViewModelClass())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = createViewModel()
    }

    // 提供一个Factory实例
    protected abstract fun createFactory(): ViewModelProvider.Factory

}