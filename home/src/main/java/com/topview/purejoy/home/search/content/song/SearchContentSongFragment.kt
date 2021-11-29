package com.topview.purejoy.home.search.content.song

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.topview.purejoy.common.mvvm.fragment.MVVMFragment
import com.topview.purejoy.home.R
import com.topview.purejoy.home.data.Status
import com.topview.purejoy.home.databinding.FragmentHomeSearchContentSongBinding
import com.topview.purejoy.home.search.SearchKeywordListener
import com.topview.purejoy.home.search.content.song.adapter.SearchContentSongAdapter
import com.topview.purejoy.home.util.getAndroidViewModelFactory

class SearchContentSongFragment :
    MVVMFragment<SearchContentSongViewModel, FragmentHomeSearchContentSongBinding>() {

    private val adapter = SearchContentSongAdapter()

    /**
     * 记录分页加载的当前页数
     * */
    private var curPage = 0

    /**
     * 记录分页加载的歌曲总数量
     * */
    private var songTotalCount = 0

    /**
     * 分页加载，每页的数量
     * */
    private val pagerSize = 20

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

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        adapter.loadMoreModule.setOnLoadMoreListener {
            // "加载更多"的偏移量
            val offset = curPage * pagerSize
            // 剩余未加载的歌曲总量
            val remainingCount = songTotalCount - offset
            // 判断未加载的歌曲是否够一页
            if (remainingCount - offset >= pagerSize) {
                // 未加载的歌曲够一页，加载一页的歌曲
                viewModel.loadMoreSongs(lastKeyword, offset, pagerSize)
            } else {
                // 未加载的歌曲不够一页，加载剩余的所有歌曲
                viewModel.loadMoreSongs(lastKeyword, remainingCount - offset, pagerSize)
            }
        }
        binding.searchContentSongLayoutManager = layoutManager
        binding.searchContentSongAdapter = adapter
    }

    private fun observe() {
        // 对用户输入的关键词进行观察
        (requireActivity() as? SearchKeywordListener)?.getKeywordLiveData()
            ?.observe(viewLifecycleOwner, {
                // 记录关键词
                lastKeyword = it
                // 让adapter可以加载更多
                adapter.loadMoreModule.isEnableLoadMore = true
                viewModel.getSearchSongByFirst(it, pagerSize)
            })

        // 对关键词的第一次请求进行观察
        viewModel.searchSongsByFirstRequestLiveData.observe(viewLifecycleOwner, {
            // 设置当前页数
            curPage = 1
        })

        // 对歌曲总数进行观察
        viewModel.searchSongCountLiveData.observe(viewLifecycleOwner, {
            songTotalCount = it
        })

        // 对分页加载进行观察
        viewModel.searchSongsLoadMoreLiveData.observe(viewLifecycleOwner, {
            // 当前页数的更新
            curPage++
            // 如果数据已经全部加载完，那么就设置adapter为无法加载更多
            if (curPage * pagerSize >= songTotalCount) {
                adapter.loadMoreModule.isEnableLoadMore = false
            }
        })

        // 对数据请求的状态进行观察
        viewModel.status.observe(viewLifecycleOwner, {
            when (it) {
                Status.SEARCH_SONG_LOAD_MORE_NET_ERROR -> adapter.loadMoreModule.loadMoreFail()
                /**
                 * 分页加载时，有些歌曲后半部分的数据获取不到，这里做一个特殊处理，直接让它结束分页加载
                 * */
                Status.SEARCH_SONG_LOAD_MORE_NET_EMPTY -> adapter.loadMoreModule.isEnableLoadMore =
                    false
            }
        })


    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SearchContentSongFragment()
    }
}