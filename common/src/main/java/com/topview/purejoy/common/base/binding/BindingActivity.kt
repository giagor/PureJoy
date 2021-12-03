package com.topview.purejoy.common.base.binding

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.topview.purejoy.common.base.CommonActivity

/**
 * Created by giagor at 2021/11/11
 *
 * 在CommonActivity的基础上，提供DataBinding的功能
 * */
abstract class BindingActivity<VB : ViewDataBinding> : CommonActivity() {
    protected lateinit var binding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 绑定生命周期
        binding.lifecycleOwner = this
    }

    override fun setContentView() {
        // 绑定布局，获取DataBinding
        binding = DataBindingUtil.setContentView(this, getLayoutId())
    }
}