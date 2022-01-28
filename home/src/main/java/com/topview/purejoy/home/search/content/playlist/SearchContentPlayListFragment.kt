package com.topview.purejoy.home.search.content.playlist

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.topview.purejoy.common.component.loadmore.LoadMoreFragment
import com.topview.purejoy.home.R
import com.topview.purejoy.home.data.Status
import com.topview.purejoy.home.databinding.FragmentHomeSearchContentPlaylistBinding
import com.topview.purejoy.home.entity.PlayList
import com.topview.purejoy.home.router.HomeRouter
import com.topview.purejoy.home.search.SearchKeywordListener
import com.topview.purejoy.home.search.common.SearchConstant
import com.topview.purejoy.home.search.content.playlist.adapter.SearchContentPlayListAdapter
import com.topview.purejoy.home.util.getAndroidViewModelFactory
import com.topview.purejoy.musiclibrary.router.MusicLibraryRouter

@Route(path = HomeRouter.FRAGMENT_HOME_SEARCH_PLAYLIST)
class SearchContentPlayListFragment :
    LoadMoreFragment<SearchContentPlayListViewModel, FragmentHomeSearchContentPlaylistBinding, SearchContentPlayListAdapter>(),
    SearchContentPlayListAdapter.ClickListener {

    private val adapter = SearchContentPlayListAdapter().apply {
        setClickListener(this@SearchContentPlayListFragment)
    }

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

    override fun getLayoutId(): Int = R.layout.fragment_home_search_content_playlist

    override fun getViewModelClass(): Class<SearchContentPlayListViewModel> =
        SearchContentPlayListViewModel::class.java

    override fun createFactory(): ViewModelProvider.Factory = getAndroidViewModelFactory()

    override fun getPageSize(): Int = pagerSize

    override fun getAdapter(): SearchContentPlayListAdapter = adapter

    override fun loadMoreData(offset: Int, limit: Int) {
        viewModel.loadMorePlayLists(lastKeyword, offset, limit)
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.searchContentPlayListLayoutManager = layoutManager
        binding.searchContentPlayListAdapter = adapter
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
                    viewModel.getSearchPlayListByFirst(it, pagerSize)
                }
            })

        // 对关键词的第一次请求进行观察
        viewModel.searchPlayListByFirstRequestLiveData.observe(viewLifecycleOwner, {
            firstRequestSuccess()
        })

        // 对歌单总数进行观察
        viewModel.searchPlayListCountLiveData.observe(viewLifecycleOwner, {
            dataTotalCount = it
        })

        // 对分页加载进行观察
        viewModel.searchPlayListsLoadMoreLiveData.observe(viewLifecycleOwner, {
            loadMoreSuccess()
        })

        // 对数据请求的状态进行观察
        viewModel.status.observe(viewLifecycleOwner, {
            when (it) {
                Status.SEARCH_PLAYLIST_LOAD_MORE_NET_ERROR -> adapter.loadMoreModule.loadMoreFail()
            }
        })
    }

    override fun onPlaylistClick(playList: PlayList) {
        playList.id?.let {
            MusicLibraryRouter.routeToPlaylistDetailActivity(it)
        }
    }
}