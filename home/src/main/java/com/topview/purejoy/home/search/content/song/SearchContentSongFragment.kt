package com.topview.purejoy.home.search.content.song

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.topview.purejoy.common.component.loadmore.LoadMoreFragment
import com.topview.purejoy.common.music.data.Wrapper
import com.topview.purejoy.common.music.service.entity.wrap
import com.topview.purejoy.common.util.getWindowHeight
import com.topview.purejoy.home.R
import com.topview.purejoy.home.data.Status
import com.topview.purejoy.home.databinding.FragmentHomeSearchContentSongBinding
import com.topview.purejoy.home.databinding.LayoutSearchSongPopBinding
import com.topview.purejoy.home.entity.Song
import com.topview.purejoy.home.entity.toMusicItem
import com.topview.purejoy.home.search.SearchKeywordListener
import com.topview.purejoy.home.search.common.SearchConstant
import com.topview.purejoy.home.search.content.song.adapter.SearchContentSongAdapter
import com.topview.purejoy.home.util.getAndroidViewModelFactory

class SearchContentSongFragment :
    LoadMoreFragment<SearchContentSongViewModel, FragmentHomeSearchContentSongBinding, SearchContentSongAdapter>(),
    SearchContentSongAdapter.ClickListener {

    private val adapter = SearchContentSongAdapter().apply {
        setClickListener(this@SearchContentSongFragment)
        setOnItemClickListener { adapter, view, position ->
            searchSongPlayListener?.let { listener ->
                val songs = adapter.data
                if (songs.isNotEmpty()) {
                    val wrapperList: MutableList<Wrapper> = mutableListOf()
                    songs.forEach {
                        wrapperList.add((it as Song).toMusicItem().wrap())
                    }
                    listener.onSearchSongItemClick(position, wrapperList)
                }
            }
        }
    }

    /**
     * 分页加载，每页的数量
     * */
    private val pagerSize = SearchConstant.PAGER_SIZE

    /**
     * 记录最近一次的关键词搜索
     * */
    private var lastKeyword = ""

    private var searchSongPlayListener: SearchSongPlayListener? = null

    /**
     * 记录点击弹出pop窗口对应的是哪首歌曲
     * */
    private var popClickSong: Song? = null

    private val popView: View by lazy {
        val binding: LayoutSearchSongPopBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(requireContext()),
                R.layout.layout_search_song_pop,
                null,
                false
            )
        binding.nextPlayClickListener = View.OnClickListener {
            popClickSong?.let {

            }
        }
        binding.downloadClickListener = View.OnClickListener {
            popClickSong?.let {
//                DownloadUtil.downloadMusic(SearchContentSongFragment@this)
            }
        }
        binding.root
    }

    private val popWindow: PopupWindow by lazy {
        val popWindow = PopupWindow(popView)
        val windowWidth = WindowManager.LayoutParams.MATCH_PARENT
        val windowHeight = getWindowHeight(requireContext()) * 2 / 3
        popWindow.width = windowWidth
        popWindow.height = windowHeight
        // 设置窗体的背景
        popWindow.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.home_bg_search_pop
            )
        )
        popWindow.isFocusable = true
        // 关闭监听
        popWindow.setOnDismissListener {
            // 恢复父窗口的背景色
            val parentWindow = (requireContext() as Activity).window
            val attr = parentWindow.attributes
            attr.alpha = 1f
            parentWindow.attributes = attr
        }
        popWindow
    }

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

    override fun onMenuClick(song: Song) {
        popClickSong = song
        // 设置屏幕背景透明度
        val parentWindow = (requireContext() as Activity).window
        val attr = parentWindow.attributes
        attr.alpha = 0.7f
        parentWindow.attributes = attr
        // 弹出窗口
        popWindow.showAtLocation(
            (requireContext() as Activity).window.decorView,
            Gravity.BOTTOM,
            0,
            0
        )
    }

    fun setSearchSongPlayListener(listener: SearchSongPlayListener) {
        this.searchSongPlayListener = listener
    }

    interface SearchSongPlayListener {
        fun onSearchSongItemClick(position: Int, list: List<Wrapper>)
    }
}