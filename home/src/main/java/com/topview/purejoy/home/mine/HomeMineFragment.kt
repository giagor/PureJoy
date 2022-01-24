package com.topview.purejoy.home.mine

import android.os.Bundle
import android.view.View
import androidx.lifecycle.LiveData
import com.alibaba.android.arouter.facade.annotation.Route
import com.topview.purejoy.common.base.binding.BindingFragment
import com.topview.purejoy.common.entity.User
import com.topview.purejoy.common.router.CommonRouter
import com.topview.purejoy.common.util.UserManager
import com.topview.purejoy.home.R
import com.topview.purejoy.home.databinding.FragmentHomeMineBinding
import com.topview.purejoy.home.router.HomeRouter

@Route(path = HomeRouter.FRAGMENT_HOME_MINE)
class HomeMineFragment : BindingFragment<FragmentHomeMineBinding>() {

    val userLiveData: LiveData<User?> = UserManager.userLiveData

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.homeMineFragment = this
        initEvent()
    }

    override fun getLayoutId(): Int = R.layout.fragment_home_mine

    private fun initEvent() {
        binding.tvLoginTipsClickListener = View.OnClickListener {
            HomeRouter.routeToLoginActivity()
        }
        
        binding.goDownloadManageClickListener = View.OnClickListener { 
            CommonRouter.routeToDownloadManageActivity()
        }
    }
}