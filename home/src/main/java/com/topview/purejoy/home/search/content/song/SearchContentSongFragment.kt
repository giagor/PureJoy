package com.topview.purejoy.home.search.content.song

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.topview.purejoy.common.mvvm.fragment.MVVMFragment
import com.topview.purejoy.home.R
import com.topview.purejoy.home.databinding.FragmentHomeSearchContentSongBinding
import com.topview.purejoy.home.search.SearchKeywordListener
import com.topview.purejoy.home.search.content.song.adapter.SearchContentSongAdapter
import com.topview.purejoy.home.util.getAndroidViewModelFactory

class SearchContentSongFragment :
    MVVMFragment<SearchContentSongViewModel, FragmentHomeSearchContentSongBinding>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel
        initRecyclerView()
        observe()
    }

    override fun getLayoutId(): Int = R.layout.fragment_home_search_content_song

    override fun getViewModelClass(): Class<SearchContentSongViewModel> =
        SearchContentSongViewModel::class.java

    override fun createFactory(): ViewModelProvider.Factory = getAndroidViewModelFactory()
    
    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        val adapter = SearchContentSongAdapter()
        binding.searchContentSongLayoutManager = layoutManager
        binding.searchContentSongAdapter = adapter
    }

    private fun observe() {
        (requireActivity() as? SearchKeywordListener)?.getKeywordLiveData()?.observe(viewLifecycleOwner,{
            viewModel.getSearchSongByFirst(it)
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SearchContentSongFragment()
    }
}