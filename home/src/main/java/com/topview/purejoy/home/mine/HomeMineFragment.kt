package com.topview.purejoy.home.mine

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.alibaba.android.arouter.facade.annotation.Route
import com.topview.purejoy.common.mvvm.fragment.MVVMFragment
import com.topview.purejoy.common.router.CommonRouter
import com.topview.purejoy.home.R
import com.topview.purejoy.home.databinding.FragmentHomeMineBinding
import com.topview.purejoy.home.router.HomeRouter
import com.topview.purejoy.home.util.getAndroidViewModelFactory

@Route(path = HomeRouter.FRAGMENT_HOME_MINE)
class HomeMineFragment : MVVMFragment<HomeMineViewModel, FragmentHomeMineBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel
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

        binding.aboutPageClickListener = View.OnClickListener {
            HomeRouter.routeToAboutActivity()
        }
    }

    override fun createFactory(): ViewModelProvider.Factory = getAndroidViewModelFactory()

    override fun getViewModelClass(): Class<HomeMineViewModel> {
        return HomeMineViewModel::class.java
    }
}