package com.topview.purejoy.home.search.content.song

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.topview.purejoy.common.component.loadmore.LoadMoreFragment
import com.topview.purejoy.home.R
import com.topview.purejoy.home.data.Status
import com.topview.purejoy.home.databinding.FragmentHomeSearchContentSongBinding
import com.topview.purejoy.home.search.SearchKeywordListener
import com.topview.purejoy.home.search.common.SearchConstant
import com.topview.purejoy.home.search.content.song.adapter.SearchContentSongAdapter
import com.topview.purejoy.home.util.getAndroidViewModelFactory

class SearchContentSongFragment :
    LoadMoreFragment<SearchContentSongViewModel, FragmentHomeSearchContentSongBinding, SearchContentSongAdapter>() {

    private val adapter = SearchContentSongAdapter()

    /**
     * 分页加载，每页的数量
     * */
    private val pagerSize = SearchConstant.PAGER_SIZE

    /**
     * 记录最近一次的关键词搜索
     * */
    private var lastKeyword = ""

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

    override fun getPageSize(): Int = pagerSize

    override fun getAdapter(): SearchContentSongAdapter = adapter

    override fun loadMoreData(offset: Int, limit: Int) {
        viewModel.loadMoreSongs(lastKeyword, offset, limit)
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.searchContentSongLayoutManager = layoutManager
        binding.searchContentSongAdapter = adapter
    }

    private fun observe() {
        // 对用户输入的关键词进行观察
        (requireActivity() as? SearchKeywordListener)?.getKeywordLiveData()
            ?.observe(viewLifecycleOwner, {
                // 仅当搜索关键词不同时，才执行搜索操作
                if (lastKeyword != it) {
                    // 数据重置
                    resetLoadMoreStatus()
                    // 记录关键词
                    lastKeyword = it
                    viewModel.getSearchSongByFirst(it, pagerSize)
                }
            })

        // 对关键词的第一次请求进行观察
        viewModel.searchSongsByFirstRequestLiveData.observe(viewLifecycleOwner, {
            firstRequestSuccess()
        })

        // 对歌曲总数进行观察
        viewModel.searchSongCountLiveData.observe(viewLifecycleOwner, {
            dataTotalCount = it
        })

        // 对分页加载进行观察
        viewModel.searchSongsLoadMoreLiveData.observe(viewLifecycleOwner, {
            loadMoreSuccess()
        })

        // 对数据请求的状态进行观察
        viewModel.status.observe(viewLifecycleOwner, {
            when (it) {
                Status.SEARCH_SONG_LOAD_MORE_NET_ERROR -> adapter.loadMoreModule.loadMoreFail()
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SearchContentSongFragment()
    }
}