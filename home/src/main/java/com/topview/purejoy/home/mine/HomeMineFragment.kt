package com.topview.purejoy.home.mine

import android.os.Bundle
import android.view.View
import androidx.lifecycle.LiveData
import com.topview.purejoy.common.base.binding.BindingFragment
import com.topview.purejoy.common.entity.User
import com.topview.purejoy.common.util.UserManager
import com.topview.purejoy.home.R
import com.topview.purejoy.home.databinding.FragmentHomeMineBinding

class HomeMineFragment : BindingFragment<FragmentHomeMineBinding>() {
    
    val userLiveData: LiveData<User?> = UserManager.userLiveData

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.homeMineFragment = this
    }

    override fun getLayoutId(): Int = R.layout.fragment_home_mine

    companion object {
        @JvmStatic
        fun newInstance() = HomeMineFragment()
    }
}