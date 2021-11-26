package com.topview.purejoy.musiclibrary.playing.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.topview.purejoy.common.base.CommonFragment
import com.topview.purejoy.musiclibrary.IPCPlayerController
import com.topview.purejoy.musiclibrary.R
import com.topview.purejoy.musiclibrary.common.instance.BinderPoolClientInstance
import com.topview.purejoy.musiclibrary.player.client.BinderPoolClient
import com.topview.purejoy.musiclibrary.player.impl.ipc.BinderPool
import com.topview.purejoy.musiclibrary.playing.entity.LrcItem
import com.topview.purejoy.musiclibrary.playing.util.LrcParser
import com.topview.purejoy.musiclibrary.playing.view.widget.LrcView
import com.topview.purejoy.musiclibrary.playing.viewmodel.PlayingViewModel
import com.topview.purejoy.musiclibrary.service.MusicService

class PlayingLrcFragment : CommonFragment() {
    private lateinit var lrcView: LrcView
    private val viewModel: PlayingViewModel by activityViewModels()
    private val lrcMap: MutableMap<Long, List<LrcItem>> = mutableMapOf()
    private val lrcDesc: MutableMap<Long, Boolean> = mutableMapOf()
    private val parser: LrcParser = LrcParser()
    private val client: BinderPoolClient by lazy {
        BinderPoolClientInstance.getInstance().getClient(MusicService::class.java)
    }

    private var playerController: IPCPlayerController? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lrcView = view.findViewById(R.id.music_playing_lrc_view)
        lrcView.listener = object : LrcView.ThumbClickListener {
            override fun onClick(item: LrcItem) {
                playerController?.seekTo(item.time.toInt())
            }
        }
        lrcView.lrcClickListener = object : LrcView.LrcViewClickListener {
            override fun onClick() {
                val manager = requireActivity().supportFragmentManager
                manager.beginTransaction().hide(this@PlayingLrcFragment)
                    .show(manager.findFragmentByTag(PlayingImageFragment::class.java.simpleName)!!)
                    .commit()
            }
        }
        viewModel.progress.observe(this.viewLifecycleOwner) {
            if (lrcView.getState() == LrcView.State.NORMAL) {
                lrcView.setTime(it.toLong())
            }
        }
        viewModel.currentItem.observe(this.viewLifecycleOwner) {
            it?.let {
                updateLrcView(it.id)
            }
        }
        viewModel.lrcResponse.observe(this.viewLifecycleOwner) {
            it.wrapper?.lrc?.lyric?.let { lrc ->
                val lrcList = parser.parse(lrc)
                lrcList?.let { list ->
                    lrcDesc[it.id] = it.wrapper.needDesc
                    lrcMap[it.id] = lrcList
                    if (viewModel.currentItem.value?.id == it.id) {
                        updateLrcView(it.id)
                    }
                }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        client.registerConnectListener(this::onServiceConnected)
        client.registerDisconnectListener(this::onServiceDisconnected)

    }

    private fun updateLrcView(id: Long) {
        if (lrcMap[id] == null) {
            lrcView.setDataSource(emptyList())
            viewModel.requestLrc(id)
        } else {
            lrcView.canDrag = lrcDesc[id] != true
            lrcView.setDataSource(lrcMap[id]!!)
        }
    }

    override fun onDestroy() {
        client.unregisterConnectListener(this::onServiceConnected)
        client.unregisterDisconnectListener(this::onServiceDisconnected)
        super.onDestroy()
    }

    private fun onServiceConnected() {
        playerController = IPCPlayerController.Stub.asInterface(
            client.pool()?.queryBinder(BinderPool.PLAYER_CONTROL_BINDER))
    }

    private fun onServiceDisconnected() {
        playerController = null
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_music_playing_lrc
    }
}