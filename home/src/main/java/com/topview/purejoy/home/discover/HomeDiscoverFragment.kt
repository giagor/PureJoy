package com.topview.purejoy.home.discover

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.topview.purejoy.common.mvvm.fragment.MVVMFragment
import com.topview.purejoy.common.util.showToast
import com.topview.purejoy.home.R
import com.topview.purejoy.home.data.Status
import com.topview.purejoy.home.databinding.FragmentHomeDiscoverBinding
import com.topview.purejoy.home.discover.adapter.DailyRecommendPlayListAdapter
import com.topview.purejoy.home.util.getAndroidViewModelFactory

private const val TAG = "HomeDiscoverFragment"

class HomeDiscoverFragment : MVVMFragment<HomeDiscoverViewModel, FragmentHomeDiscoverBinding>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel
        initRecyclerView()
        observe()
        viewModel.getBanners()
        viewModel.getDailyRecommendPlayList()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_home_discover
    }

    override fun getViewModelClass(): Class<HomeDiscoverViewModel> {
        return HomeDiscoverViewModel::class.java
    }

    override fun createFactory(): ViewModelProvider.Factory = getAndroidViewModelFactory()

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext()).apply {
            orientation = LinearLayoutManager.HORIZONTAL
        }
        val adapter = DailyRecommendPlayListAdapter()
        binding.layoutManager = layoutManager
        binding.adapter = adapter
    }

    private fun showBannerError() {
        showToast(requireContext(), "请求轮播图出错")
    }

    private fun showDailyRecommendPlayListError() {
        showToast(requireContext(), "请求每日推荐歌单出错")
    }

    private fun observe() {
        viewModel.status.observe(viewLifecycleOwner, {
            when (it) {
                Status.HOME_DISCOVER_BANNER_NET_ERROR -> showBannerError()
                Status.HOME_DISCOVER_DAILY_RECOMMEND_PLAYLIST_NET_ERROR -> showDailyRecommendPlayListError()
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeDiscoverFragment()
    }
}