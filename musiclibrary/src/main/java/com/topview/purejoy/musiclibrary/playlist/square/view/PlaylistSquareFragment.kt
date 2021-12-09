package com.topview.purejoy.musiclibrary.playlist.square.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.topview.purejoy.common.util.showToast
import com.topview.purejoy.musiclibrary.R
import com.topview.purejoy.musiclibrary.common.adapter.DataClickListener
import com.topview.purejoy.musiclibrary.playlist.detail.view.PlaylistDetailActivity
import com.topview.purejoy.musiclibrary.playlist.entity.Playlist
import com.topview.purejoy.musiclibrary.playlist.square.adapter.PlaylistSquareAdapter
import com.topview.purejoy.musiclibrary.playlist.square.entity.PlaylistTag
import com.topview.purejoy.musiclibrary.playlist.square.viewmodel.PlaylistSquareViewModel

class PlaylistSquareFragment(val tag: PlaylistTag) : Fragment(R.layout.fragment_playlist_square) {

    private val viewModel: PlaylistSquareViewModel by activityViewModels()
    private val limit = 48
    private var loading: Boolean = false
    private val order = "hot"

    private var more = true

    private val adapter: PlaylistSquareAdapter by lazy {
        val a = PlaylistSquareAdapter()
        a.itemClickListener = object : DataClickListener<Playlist> {
            override fun onClick(value: Playlist, position: Int) {
                val intent = Intent(requireActivity(), PlaylistDetailActivity::class.java)
                intent.putExtra(PlaylistDetailActivity.PLAYLIST_EXTRA, value.id)
                startActivity(intent)
            }
        }
        a
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rv: RecyclerView = view.findViewById(R.id.playlist_square_rv)
        rv.layoutManager = GridLayoutManager(requireContext(), 3)
        val footLayout: View = LayoutInflater.from(requireContext()).inflate(
            R.layout.loading_foot_view, null)
        rv.adapter = adapter
        viewModel.playlistResponse.observe(requireActivity()) { response ->
            response?.let {
                if (it.cat == tag.name) {
                    val ip = adapter.data.size
                    adapter.data.addAll(it.playlists)
                    adapter.notifyItemRangeInserted(ip, it.playlists.size)
                    more = it.more
                    loading = false
                    adapter.removeFooterView(footLayout)

                }
            }
        }
        rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                    !recyclerView.canScrollVertically(1)) {
                   if (more) {
                       if (!loading) {
                           loadData()
                           adapter.addFooterView(footLayout)
                       }
                   } else {
                       showToast(requireContext(), "没有更多了")
                   }
                }
            }
        })
        loadData()
        adapter.addFooterView(footLayout)
    }

    private fun loadData() {
        loading = true
        viewModel.requirePlaylists(limit, order, tag.name, adapter.data.size)
    }
}