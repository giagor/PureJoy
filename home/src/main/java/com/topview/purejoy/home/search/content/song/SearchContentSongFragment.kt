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
import com.alibaba.android.arouter.facade.annotation.Route
import com.topview.purejoy.common.component.loadmore.LoadMoreFragment
import com.topview.purejoy.common.music.data.Wrapper
import com.topview.purejoy.common.music.service.entity.wrap
import com.topview.purejoy.common.util.DownloadUtil
import com.topview.purejoy.common.util.getWindowHeight
import com.topview.purejoy.common.util.showToast
import com.topview.purejoy.home.R
import com.topview.purejoy.home.data.Status
import com.topview.purejoy.home.databinding.FragmentHomeSearchContentSongBinding
import com.topview.purejoy.home.databinding.LayoutSearchSongPopBinding
import com.topview.purejoy.home.entity.Song
import com.topview.purejoy.home.entity.toMusicItem
import com.topview.purejoy.home.router.HomeRouter
import com.topview.purejoy.home.search.SearchKeywordListener
import com.topview.purejoy.home.search.common.SearchConstant
import com.topview.purejoy.home.search.content.song.adapter.SearchContentSongAdapter
import com.topview.purejoy.home.util.getAndroidViewModelFactory

@Route(path = HomeRouter.FRAGMENT_HOME_SEARCH_SONG)
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
     * ??????????????????????????????
     * */
    private val pagerSize = SearchConstant.PAGER_SIZE

    /**
     * ????????????????????????????????????
     * */
    private var lastKeyword = ""

    private var searchSongPlayListener: SearchSongPlayListener? = null

    /**
     * ??????????????????pop??????????????????????????????
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
            popClickSong?.let { song ->
                searchSongPlayListener?.searchSongNextPlay(song.toMusicItem().wrap())
            }
            popWindow.dismiss()
        }
        binding.downloadClickListener = View.OnClickListener {
            popClickSong?.let {
                if (it.url == null) {
                    viewModel.requestSongUrl(it)
                } else {
                    DownloadUtil.downloadMusic(
                        this@SearchContentSongFragment,
                        it.name ?: "",
                        it.url!!
                    )
                }
            }
            popWindow.dismiss()
        }
        binding.root
    }

    private val popWindow: PopupWindow by lazy {
        val popWindow = PopupWindow(popView)
        val windowWidth = WindowManager.LayoutParams.MATCH_PARENT
        val windowHeight = getWindowHeight(requireContext()) * 2 / 3
        popWindow.width = windowWidth
        popWindow.height = windowHeight
        // ?????????????????????
        popWindow.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.home_bg_search_pop
            )
        )
        popWindow.isFocusable = true
        // ????????????
        popWindow.setOnDismissListener {
            // ???????????????????????????
            val parentWindow = (requireContext() as Activity).window
            val attr = parentWindow.attributes
            attr.alpha = 1f
            parentWindow.attributes = attr
            // ????????????????????????????????????
            popClickSong = null
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
        // ???????????????????????????????????????
        (requireActivity() as? SearchKeywordListener)?.getKeywordLiveData()
            ?.observe(viewLifecycleOwner, {
                // ??????????????????????????????????????????????????????
                if (lastKeyword != it) {
                    // ????????????
                    resetLoadMoreStatus()
                    // ???????????????
                    lastKeyword = it
                    viewModel.getSearchSongByFirst(it, pagerSize)
                }
            })

        // ??????????????????????????????????????????
        viewModel.searchSongsByFirstRequestLiveData.observe(viewLifecycleOwner, {
            firstRequestSuccess()
        })

        // ???????????????????????????
        viewModel.searchSongCountLiveData.observe(viewLifecycleOwner, {
            dataTotalCount = it
        })

        // ???????????????????????????
        viewModel.searchSongsLoadMoreLiveData.observe(viewLifecycleOwner, {
            loadMoreSuccess()
        })

        // ?????????url????????????
        viewModel.requestUrlLiveData.observe(viewLifecycleOwner, {
            if (it.url == null) {
                showToast(requireContext(), "???????????????????????????????????????")
            } else {
                DownloadUtil.downloadMusic(this@SearchContentSongFragment, it.name ?: "", it.url!!)
            }
        })

        // ????????????????????????????????????
        viewModel.status.observe(viewLifecycleOwner, {
            when (it) {
                Status.SEARCH_SONG_LOAD_MORE_NET_ERROR -> adapter.loadMoreModule.loadMoreFail()
                Status.SEARCH_SONG_REQUEST_URL_ID_EMPTY, Status.SEARCH_SONG_REQUEST_URL_ERROR -> {
                    showToast(requireContext(), "???????????????????????????????????????")
                }
                Status.SEARCH_SONG_FIRST_ERROR -> showToast(requireContext(), "????????????????????????")
            }
        })
    }

    override fun onMenuClick(song: Song) {
        popClickSong = song
        // ???????????????????????????
        val parentWindow = (requireContext() as Activity).window
        val attr = parentWindow.attributes
        attr.alpha = 0.7f
        parentWindow.attributes = attr
        // ????????????
        popWindow.showAtLocation(
            (requireContext() as Activity).window.decorView,
            Gravity.BOTTOM,
            0,
            0
        )
    }

    override fun onMvClick(song: Song) {
        searchSongPlayListener?.onMvClick(song)
    }

    fun setSearchSongPlayListener(listener: SearchSongPlayListener) {
        this.searchSongPlayListener = listener
    }

    interface SearchSongPlayListener {
        fun onSearchSongItemClick(position: Int, list: List<Wrapper>)
        fun searchSongNextPlay(wrapper: Wrapper)
        fun onMvClick(song: Song)
    }
}