package com.topview.purejoy.musiclibrary.playing.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.topview.purejoy.common.IPCPlayerController
import com.topview.purejoy.common.base.CommonFragment
import com.topview.purejoy.common.music.instance.BinderPoolClientInstance
import com.topview.purejoy.common.music.player.client.BinderPoolClient
import com.topview.purejoy.common.music.player.impl.ipc.BinderPool
import com.topview.purejoy.common.music.service.MusicService
import com.topview.purejoy.common.widget.lyric.LyricView
import com.topview.purejoy.common.widget.lyric.Sentence
import com.topview.purejoy.common.widget.lyric.parser.LyricParser
import com.topview.purejoy.musiclibrary.R
import com.topview.purejoy.musiclibrary.playing.viewmodel.PlayingViewModel

class PlayingLrcFragment : CommonFragment() {
    private lateinit var lrcView: LyricView
    private val viewModel: PlayingViewModel by activityViewModels()
    private val lrcMap: MutableMap<Long, List<Sentence>> = mutableMapOf()
    private val transLrcMap: MutableMap<Long, List<Sentence>> = mutableMapOf()
    private val lrcDesc: MutableMap<Long, Boolean> = mutableMapOf()
    private val client: BinderPoolClient by lazy {
        BinderPoolClientInstance.getInstance().getClient(MusicService::class.java)
    }

    private var playerController: IPCPlayerController? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lrcView = view.findViewById(R.id.music_playing_lrc_view)
        lrcView.apply {
            setTransLyricVisible(true)
        }
        lrcView.onPlayIconClick = {
            it?.let {
                playerController?.seekTo(it.fromTime.toInt())
                lrcView.setHighlightLine(it.index)
            }
        }
        lrcView.onContentClick = {
            val manager = requireActivity().supportFragmentManager
            manager.beginTransaction().hide(this@PlayingLrcFragment)
                .show(manager.findFragmentByTag(PlayingImageFragment::class.java.simpleName)!!)
                .commit()
        }
        viewModel.progress.observe(this.viewLifecycleOwner) {
            lrcView.setHighlightLineByProgress(it.toLong())
        }
        viewModel.currentItem.observe(this.viewLifecycleOwner) {
            it?.apply {
                lrcMap[id]?.let { list ->
                    lrcView.setSuccess(list, transLrcMap[id])
                } ?: let {
                    lrcView.setLoading()
                    viewModel.requestLrc(id)
                }
            }
        }
        viewModel.lrcResponse.observe(this.viewLifecycleOwner) {
            it.wrapper?.apply {
                val lrcList: List<Sentence> = lrc?.let { lrc ->
                    LyricParser(lrc.lyric).parse().sentence.apply {
                        lrcMap[it.id] = this
                    }
                } ?: emptyList()

                val transList = tlyric?.let { tLyric ->
                    LyricParser(tLyric.lyric).parse().sentence.apply {
                        if (this.isNotEmpty()) {
                            transLrcMap[it.id] = this
                        }
                    }
                }
                lrcDesc[it.id] = needDesc
                if (viewModel.currentItem.value?.id == it.id) {
                    if (lrcList.isEmpty()) {
                        lrcView.setEmpty()
                    } else {
                        lrcView.setSuccess(lrcList, transList)
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