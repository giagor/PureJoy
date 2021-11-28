package com.topview.purejoy.home.search.tab

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.topview.purejoy.common.base.binding.BindingFragment
import com.topview.purejoy.home.R
import com.topview.purejoy.home.databinding.FragmentHomeSearchContentTabBinding
import com.topview.purejoy.home.search.content.song.SearchContentSongFragment

private const val SEARCH_CONTENT_PAGER_COUNTS = 2

class SearchContentTabFragment : BindingFragment<FragmentHomeSearchContentTabBinding>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.pagerAdapter = SearchContentPagerAdapter()
    }

    override fun getLayoutId(): Int = R.layout.fragment_home_search_content_tab

    inner class SearchContentPagerAdapter : FragmentStatePagerAdapter(
        childFragmentManager,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {
        override fun getCount(): Int = SEARCH_CONTENT_PAGER_COUNTS

        override fun getItem(position: Int): Fragment {
            return SearchContentSongFragment.newInstance()
        }

    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SearchContentTabFragment()
    }
}