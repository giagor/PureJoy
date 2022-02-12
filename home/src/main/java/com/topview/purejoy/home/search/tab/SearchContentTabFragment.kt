package com.topview.purejoy.home.search.tab

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.tabs.TabLayout
import com.topview.purejoy.common.base.binding.BindingFragment
import com.topview.purejoy.common.music.data.Wrapper
import com.topview.purejoy.home.R
import com.topview.purejoy.home.databinding.FragmentHomeSearchContentTabBinding
import com.topview.purejoy.home.entity.Song
import com.topview.purejoy.home.router.HomeRouter
import com.topview.purejoy.home.search.content.song.SearchContentSongFragment

private const val SEARCH_CONTENT_PAGER_COUNTS = 2
private const val SEARCH_CONTENT_SONG_FRAGMENT_POSITION = 0
private const val SEARCH_CONTENT_PLAYLIST_FRAGMENT_POSITION = 1

@Route(path = HomeRouter.FRAGMENT_HOME_SEARCH_TAB)
class SearchContentTabFragment : BindingFragment<FragmentHomeSearchContentTabBinding>(),
    SearchContentSongFragment.SearchSongPlayListener {

    private var searchSongPlayListener: SearchContentSongFragment.SearchSongPlayListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        binding.pagerAdapter = SearchContentPagerAdapter()
        //设置ViewPager和TabLayout的联动
        val vp = binding.homeVpSearchContentPager
        val tl = binding.homeTlSearchTab
        vp.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tl))
        tl.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(vp))
    }

    override fun getLayoutId(): Int = R.layout.fragment_home_search_content_tab

    inner class SearchContentPagerAdapter : FragmentStatePagerAdapter(
        childFragmentManager,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {
        override fun getCount(): Int = SEARCH_CONTENT_PAGER_COUNTS

        override fun getItem(position: Int): Fragment {
            return if (position == SEARCH_CONTENT_SONG_FRAGMENT_POSITION) {
                (HomeRouter.routeToSearchSongFragment() as SearchContentSongFragment).apply {
                    setSearchSongPlayListener(this@SearchContentTabFragment)
                }
            } else {
                HomeRouter.routeToSearchPlayListFragment()!!
            }
        }
    }

    override fun onSearchSongItemClick(position: Int, list: List<Wrapper>) {
        searchSongPlayListener?.onSearchSongItemClick(position, list)
    }

    override fun searchSongNextPlay(wrapper: Wrapper) {
        searchSongPlayListener?.searchSongNextPlay(wrapper)
    }

    override fun onMvClick(song: Song) {
        searchSongPlayListener?.onMvClick(song)
    }

    fun setSearchSongPlayListener(listener: SearchContentSongFragment.SearchSongPlayListener) {
        this.searchSongPlayListener = listener
    }
}