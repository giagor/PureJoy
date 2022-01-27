package com.topview.purejoy.home.discover

import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper
import com.topview.purejoy.common.music.data.Wrapper
import com.topview.purejoy.common.music.service.entity.wrap
import com.topview.purejoy.common.mvvm.fragment.MVVMFragment
import com.topview.purejoy.common.util.getWindowWidth
import com.topview.purejoy.common.util.showToast
import com.topview.purejoy.home.R
import com.topview.purejoy.home.data.Status
import com.topview.purejoy.home.databinding.FragmentHomeDiscoverBinding
import com.topview.purejoy.home.discover.adapter.DailyRecommendPlayListAdapter
import com.topview.purejoy.home.discover.adapter.RecommendNewSongAdapter
import com.topview.purejoy.home.discover.decoration.DailyRecommendPlayListDecoration
import com.topview.purejoy.home.discover.decoration.RecommendNewSongDecoration
import com.topview.purejoy.home.entity.PlayList
import com.topview.purejoy.home.entity.Song
import com.topview.purejoy.home.entity.toMusicItem
import com.topview.purejoy.home.router.HomeRouter
import com.topview.purejoy.home.util.getAndroidViewModelFactory
import com.topview.purejoy.musiclibrary.router.MusicLibraryRouter

private const val TAG = "HomeDiscoverFragment"

/**
 * 表示展示"推荐新歌曲"的行数
 * */
private const val RECOMMEND_NEW_SONG_ROW_COUNT = 3

@Route(path = HomeRouter.FRAGMENT_HOME_DISCOVER)
class HomeDiscoverFragment : MVVMFragment<HomeDiscoverViewModel, FragmentHomeDiscoverBinding>(),
    DailyRecommendPlayListAdapter.OnClickListener, RecommendNewSongAdapter.ClickListener {

    private lateinit var recommendNewSongAdapter: RecommendNewSongAdapter
    private lateinit var dailyRecommendPlayListAdapter: DailyRecommendPlayListAdapter
    private var recommendNewSongPlayListener: RecommendNewSongPlayListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel
        initView()
        initEvent()
        observe()
        initData()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_home_discover
    }

    override fun getViewModelClass(): Class<HomeDiscoverViewModel> {
        return HomeDiscoverViewModel::class.java
    }

    override fun createFactory(): ViewModelProvider.Factory = getAndroidViewModelFactory()

    private fun initView() {
        initIcon()
        initRecyclerView()
    }

    private fun initEvent() {
        binding.tvSearchClickListener = View.OnClickListener {
            HomeRouter.routeToSearchActivity()
        }
        binding.llTopListClickListener = View.OnClickListener {
            HomeRouter.routeToTopListActivity()
        }
        binding.llDailyRecommendClickListener = View.OnClickListener {
            MusicLibraryRouter.routeToDailyRecommendActivity()
        }
        binding.llPlaylistClickListener = View.OnClickListener {
            MusicLibraryRouter.routeToPlaylistSquareActivity()
        }
    }

    private fun initIcon() {
        val searchIcon =
            ResourcesCompat.getDrawable(requireContext().resources, R.drawable.home_ic_search, null)
        searchIcon?.let {
            val iconWidth = requireContext().resources.getDimension(R.dimen.home_search_ic_width)
            val iconHeight = requireContext().resources.getDimension(R.dimen.home_search_ic_height)
            it.setBounds(0, 0, iconWidth.toInt(), iconHeight.toInt())
            binding.icSearch = it
        }
    }

    private fun initData() {
        viewModel.getBanners()
        viewModel.getDailyRecommendPlayList()
        viewModel.getRecommendNewSong(RECOMMEND_NEW_SONG_ROW_COUNT * 4)
    }

    private fun initRecyclerView() {
        initDailyRecommendPlayListRecycler()
        initRecommendNewSongRecycler()
    }

    private fun initDailyRecommendPlayListRecycler() {
        val layoutManager = LinearLayoutManager(requireContext()).apply {
            orientation = LinearLayoutManager.HORIZONTAL
        }
        dailyRecommendPlayListAdapter = DailyRecommendPlayListAdapter().apply {
            setClickListener(this@HomeDiscoverFragment)
        }
        val decoration = DailyRecommendPlayListDecoration()
        binding.dailyRecommendPlayListLayoutManager = layoutManager
        binding.dailyRecommendPlayListAdapter = dailyRecommendPlayListAdapter
        binding.dailyRecommendPlayListDecoration = decoration
        binding.dailyRecommendPlayListSnapHelper = GravitySnapHelper(Gravity.START).apply {
            // 限制最大的抛掷距离
            maxFlingDistance = getWindowWidth(requireContext())
        }
    }

    private fun initRecommendNewSongRecycler() {
        val layoutManager =
            GridLayoutManager(requireContext(), RECOMMEND_NEW_SONG_ROW_COUNT).apply {
                orientation = GridLayoutManager.HORIZONTAL
            }
        recommendNewSongAdapter = RecommendNewSongAdapter().apply {
            setClickListener(this@HomeDiscoverFragment)
        }
        val decoration = RecommendNewSongDecoration()
        binding.recommendNewSongLayoutManager = layoutManager
        binding.recommendNewSongAdapter = recommendNewSongAdapter
        binding.recommendNewSongDecoration = decoration
        binding.recommendNewSongSnapHelper = GravitySnapHelper(Gravity.START).apply {
            // 限制最大的抛掷距离
            maxFlingDistance = getWindowWidth(requireContext())
        }
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
                Status.DISCOVER_BANNER_NET_ERROR -> showBannerError()
                Status.DISCOVER_DAILY_RECOMMEND_PLAYLIST_NET_ERROR -> showDailyRecommendPlayListError()
            }
        })
    }

    override fun onPlayListClick(playList: PlayList) {
        playList.id?.let {
            MusicLibraryRouter.routeToPlaylistDetailActivity(it)
        }
    }

    override fun onRecommendNewSongClick(song: Song) {
        recommendNewSongPlayListener?.let { listener ->
            val songs = recommendNewSongAdapter.data
            if (songs.isNotEmpty()) {
                val position = recommendNewSongAdapter.getItemPosition(song)
                val wrapperList: MutableList<Wrapper> = mutableListOf()
                songs.forEach {
                    wrapperList.add(it.toMusicItem().wrap())
                }
                listener.onRecommendNewSongClick(position, wrapperList)
            }
        }
    }

    fun setRecommendNewSongPlayListener(listener: RecommendNewSongPlayListener) {
        this.recommendNewSongPlayListener = listener
    }

    interface RecommendNewSongPlayListener {
        fun onRecommendNewSongClick(position: Int, list: List<Wrapper>)
    }
}