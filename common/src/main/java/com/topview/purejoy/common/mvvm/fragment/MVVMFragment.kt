package com.topview.purejoy.common.mvvm.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel

/**
 * Created by giagor at 2021/03/15.
 * */
abstract class MVVMFragment<VM : MVVMViewModel> : CommonFragment() {
    protected lateinit var viewModel: VM

    // 返回当前Fragment绑定的ViewModel类型
    protected abstract fun getViewModelClass(): Class<VM>


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = createViewModel()
    }

    // 创建与当前Fragment相关联的ViewModel
    protected fun createViewModel(): VM {
        return ViewModelProvider(this, createFactory()).get(getViewModelClass())
    }

    // 提供一个Factory实例
    protected abstract fun createFactory(): ViewModelProvider.Factory


}