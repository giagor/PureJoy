package com.topview.purejoy.home.mine

import com.topview.purejoy.common.base.binding.BindingFragment
import com.topview.purejoy.home.R
import com.topview.purejoy.home.databinding.FragmentHomeMineBinding

class HomeMineFragment : BindingFragment<FragmentHomeMineBinding>() {
    override fun getLayoutId(): Int = R.layout.fragment_home_mine

    companion object {
        @JvmStatic
        fun newInstance() = HomeMineFragment()
    }
}