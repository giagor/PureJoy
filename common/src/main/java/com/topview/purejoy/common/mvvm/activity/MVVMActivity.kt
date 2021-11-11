package com.topview.purejoy.common.mvvm.activity

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.topview.purejoy.common.base.CommonActivity
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel

/**
 * Created by giagor at 2021/03/13
 *
 * MVVM的通用Activity
 * */
abstract class MVVMActivity<VM : MVVMViewModel, VB : ViewDataBinding> : CommonActivity() {
    protected val viewModel: VM by lazy {
        createViewModel()
    }

    protected lateinit var binding: VB

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 绑定生命周期
        binding.lifecycleOwner = this
    }

    override fun setContentView() {
        // 绑定布局，获取DataBinding
        binding = DataBindingUtil.setContentView(this, getLayoutId())
    }

    /**
     * 提供一个Factory实例
     * */
    protected abstract fun createFactory(): ViewModelProvider.Factory

}