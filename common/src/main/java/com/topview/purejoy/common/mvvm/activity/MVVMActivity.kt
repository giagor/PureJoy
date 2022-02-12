package com.topview.purejoy.common.mvvm.activity

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.topview.purejoy.common.base.binding.BindingActivity
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel

/**
 * Created by giagor at 2021/03/13
 *
 * MVVM的通用Activity
 * */
abstract class MVVMActivity<VM : MVVMViewModel, VB : ViewDataBinding> : BindingActivity<VB>() {
    protected val viewModel: VM by lazy {
        createViewModel()
    }

    /**
     * 返回当前Activity绑定的ViewModel类型
     * */
    protected abstract fun getViewModelClass(): Class<VM>

    /**
     * 创建与当前Activity相关联的ViewModel
     * */
    private fun createViewModel(): VM {
        return ViewModelProvider(this, createFactory()).get(getViewModelClass())
    }

    /**
     * 提供一个Factory实例
     * */
    protected abstract fun createFactory(): ViewModelProvider.Factory

}