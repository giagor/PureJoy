package com.topview.purejoy.musiclibrary.playlist.square.view

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.topview.purejoy.common.util.showToast
import com.topview.purejoy.musiclibrary.R
import com.topview.purejoy.musiclibrary.common.MusicCommonActivity
import com.topview.purejoy.musiclibrary.playlist.square.adapter.PlaylistSquareFragmentAdapter
import com.topview.purejoy.musiclibrary.playlist.square.viewmodel.PlaylistSquareViewModel

class PlaylistSquareActivity : MusicCommonActivity<PlaylistSquareViewModel>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val toolBar: Toolbar = findViewById(R.id.playlist_square_tool_bar)
        toolBar.setTitle(R.string.playlist_square)
        val tabLayout: TabLayout = findViewById(R.id.playlist_square_tab_layout)
        val viewPager: ViewPager2 = findViewById(R.id.playlist_square_view_pager)
        val tm = TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val tag = viewModel.playlistTagsResponse.value?.sub?.get(position)
            tag?.let {
                tab.text = it.name
            }
        }

        val adapter = PlaylistSquareFragmentAdapter(supportFragmentManager, this.lifecycle)
        viewPager.adapter = adapter
        tm.attach()
        viewModel.playlistTagsResponse.observe(this) {
            if (it == null) {
                showToast(this, "加载错误")
            } else {
                val fragments = mutableListOf<Fragment>()
                for (t in it.sub) {
                    fragments.add(PlaylistSquareFragment(t))
                }

                adapter.fragments.addAll(fragments)
                adapter.notifyItemRangeInserted(0, adapter.fragments.size)
            }
        }
        viewModel.requireTags()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_playlist_square
    }

    override fun getViewModelClass(): Class<PlaylistSquareViewModel> {
        return PlaylistSquareViewModel::class.java
    }
}