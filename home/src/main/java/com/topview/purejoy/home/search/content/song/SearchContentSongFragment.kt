package com.topview.purejoy.home.search.content.song

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.topview.purejoy.common.mvvm.fragment.MVVMFragment
import com.topview.purejoy.home.R
import com.topview.purejoy.home.databinding.FragmentHomeSearchContentSongBinding
import com.topview.purejoy.home.search.content.song.adapter.SearchContentSongAdapter
import com.topview.purejoy.home.util.getAndroidViewModelFactory

class SearchContentSongFragment :
    MVVMFragment<SearchContentSongViewModel, FragmentHomeSearchContentSongBinding>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel
        initRecyclerView()
        observe()
        initData()
    }

    override fun getLayoutId(): Int = R.layout.fragment_home_search_content_song

    override fun getViewModelClass(): Class<SearchContentSongViewModel> =
        SearchContentSongViewModel::class.java

    override fun createFactory(): ViewModelProvider.Factory = getAndroidViewModelFactory()

    private fun initData() {
        viewModel.getSearchSongByFirst("光辉岁月")
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        val adapter = SearchContentSongAdapter()
        binding.searchContentSongLayoutManager = layoutManager
        binding.searchContentSongAdapter = adapter
    }

    private fun observe() {
        viewModel.searchSongCountLiveData.observe(viewLifecycleOwner, {

        })

        viewModel.searchSongsLiveData.observe(viewLifecycleOwner, {
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SearchContentSongFragment()
    }
}