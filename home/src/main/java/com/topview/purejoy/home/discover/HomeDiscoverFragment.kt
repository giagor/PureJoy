package com.topview.purejoy.home.discover

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.topview.purejoy.common.mvvm.fragment.MVVMFragment
import com.topview.purejoy.home.R
import com.topview.purejoy.home.databinding.FragmentHomeDiscoverBinding
import com.topview.purejoy.home.util.getAndroidViewModelFactory

private const val TAG = "HomeDiscoverFragment"

class HomeDiscoverFragment : MVVMFragment<HomeDiscoverViewModel, FragmentHomeDiscoverBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        viewModel.status.observe(this, {

        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        viewModel.getBanners()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_home_discover
    }

    override fun getViewModelClass(): Class<HomeDiscoverViewModel> {
        return HomeDiscoverViewModel::class.java
    }

    override fun createFactory(): ViewModelProvider.Factory = getAndroidViewModelFactory()
    
    companion object {
        @JvmStatic
        fun newInstance() = HomeDiscoverFragment()
    }
}