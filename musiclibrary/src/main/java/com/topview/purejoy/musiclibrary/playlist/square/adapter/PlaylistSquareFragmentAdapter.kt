package com.topview.purejoy.musiclibrary.playlist.square.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.topview.purejoy.musiclibrary.playlist.square.entity.PlaylistTag
import com.topview.purejoy.musiclibrary.playlist.square.view.PlaylistSquareFragment

class PlaylistSquareFragmentAdapter(
    fm: FragmentManager,
    lifecycle: Lifecycle,
    val fragments: MutableList<Fragment> = mutableListOf())
    : FragmentStateAdapter(fm, lifecycle) {
    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}