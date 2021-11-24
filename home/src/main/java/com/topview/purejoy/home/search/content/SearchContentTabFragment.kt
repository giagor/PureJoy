package com.topview.purejoy.home.search.content

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.topview.purejoy.home.R
import com.topview.purejoy.home.search.content.song.SearchContentSongFragment

private const val SEARCH_CONTENT_PAGER_COUNTS = 2

class SearchContentTabFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_search_content_tab, container, false)
    }

    private inner class SearchContentPagerAdapter : FragmentStateAdapter(this) {
        override fun getItemCount(): Int {
            return SEARCH_CONTENT_PAGER_COUNTS
        }

        override fun createFragment(position: Int): Fragment {
            return SearchContentSongFragment.newInstance()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SearchContentTabFragment()
    }
}