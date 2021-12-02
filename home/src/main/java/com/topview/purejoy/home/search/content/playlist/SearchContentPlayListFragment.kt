package com.topview.purejoy.home.search.content.playlist

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.topview.purejoy.common.mvvm.fragment.MVVMFragment
import com.topview.purejoy.home.R
import com.topview.purejoy.home.databinding.FragmentHomeSearchContentPlaylistBinding
import com.topview.purejoy.home.util.getAndroidViewModelFactory

class SearchContentPlayListFragment :
    MVVMFragment<SearchContentPlayListViewModel, FragmentHomeSearchContentPlaylistBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel
        viewModel.getSearchPlayListByFirst("红日", 20)
        observe()
    }

    override fun getLayoutId(): Int = R.layout.fragment_home_search_content_playlist

    override fun getViewModelClass(): Class<SearchContentPlayListViewModel> =
        SearchContentPlayListViewModel::class.java

    override fun createFactory(): ViewModelProvider.Factory = getAndroidViewModelFactory()

    private fun observe() {

    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SearchContentPlayListFragment()
    }
}