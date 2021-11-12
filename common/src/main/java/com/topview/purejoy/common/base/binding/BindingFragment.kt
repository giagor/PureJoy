package com.topview.purejoy.common.base.binding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.topview.purejoy.common.base.CommonFragment

/**
 * Created by giagor at 2021/11/11
 *
 * 在CommonFragment的基础上，提供DataBinding的功能
 * */
abstract class BindingFragment<VB : ViewDataBinding> : CommonFragment() {
    protected lateinit var binding: VB

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