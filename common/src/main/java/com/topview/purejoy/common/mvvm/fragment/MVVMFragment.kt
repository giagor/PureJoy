package com.topview.purejoy.common.mvvm.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.topview.purejoy.common.base.CommonFragment
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel

/**
 * Created by giagor at 2021/03/15.
 * */
abstract class MVVMFragment<VM : MVVMViewModel, VB : ViewDataBinding> : CommonFragment() {
    protected val viewModel: VM by lazy {
        createViewModel()
    }

    protected lateinit var binding: VB

    /**
     * 返回当前Fragment绑定的ViewModel类型
     * */
    protected abstract fun getViewModelClass(): Class<VM>

    /**
     * 创建与当前Fragment相关联的ViewModel
     * */
    private fun createViewModel(): VM {
        return ViewModelProvider(this, createFactory()).get(getViewModelClass())
    }

    /**
     * 提供一个Factory实例
     * */
    protected abstract fun createFactory(): ViewModelProvider.Factory

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        // 绑定生命周期
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }
}