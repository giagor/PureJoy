package com.topview.purejoy.musiclibrary.playing.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.topview.purejoy.common.base.CommonActivity
import com.topview.purejoy.common.base.binding.BindingActivity
import com.topview.purejoy.musiclibrary.*
import com.topview.purejoy.musiclibrary.common.MusicBindingActivity
import com.topview.purejoy.musiclibrary.common.factory.DefaultFactory
import com.topview.purejoy.musiclibrary.common.instance.BinderPoolClientInstance
import com.topview.purejoy.musiclibrary.common.util.*
import com.topview.purejoy.musiclibrary.data.Wrapper
import com.topview.purejoy.musiclibrary.entity.MusicItem
import com.topview.purejoy.musiclibrary.entity.removeAll
import com.topview.purejoy.musiclibrary.player.client.BinderPoolClient
import com.topview.purejoy.musiclibrary.player.impl.ipc.BinderPool
import com.topview.purejoy.musiclibrary.player.setting.MediaModeSetting
import com.topview.purejoy.musiclibrary.player.util.cast
import com.topview.purejoy.musiclibrary.playing.view.widget.MusicProgressBar
import com.topview.purejoy.musiclibrary.playing.viewmodel.PlayingViewModel
import com.topview.purejoy.musiclibrary.service.MusicService
import java.util.*

class PlayingActivity : MusicBindingActivity<PlayingViewModel, ViewDataBinding>() {

    private val layout: LinearLayout by lazy {
        binding.root.findViewById(R.id.music_playing_layout)
    }

    private val progressTx: TextView by lazy {
        binding.root.findViewById(R.id.music_playing_progress_tx)
    }

    private val listener: MusicProgressBar.MusicProgressBarListener by lazy {
        object : MusicProgressBar.MusicProgressBarListenerAdapter {
            override fun onStopTracking(musicProgressBar: MusicProgressBar) {
                playerController?.seekTo(musicProgressBar.value)
            }
        }
    }

    private val progressBar: MusicProgressBar by lazy {
        binding.root.findViewById(R.id.music_playing_progress_bar)
    }


    private val durationTx: TextView by lazy {
        binding.root.findViewById(R.id.music_playing_max_progress_tx)
    }

    private val controlBt: ImageButton by lazy {
        binding.root.findViewById(R.id.music_playing_control_bt)
    }

    private val modeBt: ImageButton by lazy {
        binding.root.findViewById(R.id.music_playing_mode_bt)
    }

    private val timerWrapper = TimerWrapper(duration = 100) {
        if (viewModel.playState.value == true) {
            viewModel.progress.postValue(playerController?.progress() ?: 0)
        }
    }


    private val itemChangeListener = object : IPCItemChangeListener.Stub() {
        override fun onItemChange(wrapper: Wrapper?) {
            viewModel.currentItem.postValue(dataController?.current()?.value?.cast())
            viewModel.progress.postValue(0)
            viewModel.duration.postValue(playerController?.duration())
        }
    }

    private val playStateChangeListener = object : IPCPlayStateChangeListener.Stub() {
        override fun playStateChange(state: Boolean) {
            viewModel.playState.postValue(state)
        }

    }

    private val modeChangeListener = object : IPCModeChangeListener.Stub() {
        override fun onModeChange(mode: Int) {
            modeBt.post {
                val resId = when(mode) {
                    MediaModeSetting.ORDER -> R.drawable.music_playing_order_48
                    MediaModeSetting.LOOP -> R.drawable.music_playing_circle_48
                    MediaModeSetting.RANDOM -> R.drawable.music_playing_random_48
                    else -> R.drawable.music_playing_order_48
                }
                modeBt.setImageResource(resId)
            }
        }
    }

    private val dataSetChangeListener = object : IPCDataSetChangeListener.Stub() {
        override fun onChange(source: MutableList<Wrapper>?) {
            if (source == null) {
                viewModel.playingItems.postValue(null)
            } else {
                viewModel.playingItems.value?.removeAll(source)
                viewModel.playingItems.postValue(viewModel.playingItems.value)
            }
        }

    }



    override fun onServiceConnected() {
        super.onServiceConnected()
        viewModel.currentItem.postValue(dataController?.current()?.value?.cast())
        viewModel.progress.postValue(playerController?.progress() ?: 0)
        viewModel.playState.postValue(playerController?.isPlaying ?: false)
        val items = dataController?.allItems()?.map { it.value?.cast<MusicItem>()!! }!!
        viewModel.playingItems.value?.addAll(items)
        viewModel.playingItems.postValue(viewModel.playingItems.value)
        viewModel.duration.observe(this) {
            progressBar.max = it
        }
        Log.d("PlayingActivity", "onServiceConnected: value = ")
        listenerController?.apply {
            addItemChangeListener(itemChangeListener)
            addModeChangeListener(modeChangeListener)
            addPlayStateChangeListener(playStateChangeListener)
            addDataChangeListener(dataSetChangeListener)
        }
        timerWrapper.start()
    }


    override fun onServiceDisconnected() {
        listenerController?.apply {
            removeItemChangeListener(itemChangeListener)
            removeModeChangeListener(modeChangeListener)
            removePlayStateChangeListener(playStateChangeListener)
            removeDataChangeListener(dataSetChangeListener)
        }
        timerWrapper.reset()
        super.onServiceDisconnected()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        observeViewModel()
        addAndShowFragment()
    }

    private fun addAndShowFragment() {
        val lrcFragment = PlayingLrcFragment()
        val imageFragment = PlayingImageFragment()

        addFragment(R.id.music_playing_fragment, imageFragment)
        addFragment(R.id.music_playing_fragment, lrcFragment)
        hide(lrcFragment)
        show(imageFragment)
    }

    private fun initView() {
        binding.root.findViewById<ImageButton>(R.id.music_playing_back_bt).setOnClickListener {
            finish()
        }
        binding.root.findViewById<ImageButton>(R.id.music_playing_previous_bt).setOnClickListener {
            playerController?.last()
        }
        binding.root.findViewById<ImageButton>(R.id.music_playing_next_bt).setOnClickListener {
            playerController?.next()
        }
        controlBt.setOnClickListener {
            playerController?.playOrPause()
        }
        modeBt.setOnClickListener {
            modeController?.nextMode()
        }
        binding.root.findViewById<ImageButton>(R.id.music_playing_list_bt).setOnClickListener {
            // show playing list
        }
        progressBar.listener = listener
    }

    private fun observeViewModel() {
        viewModel.currentItem.observe(this) {
            it?.let {
                loadBlurBackground(layout, url = it.al.picUrl, blurRadius = 25)
                loadBitmapColor(it.al.picUrl) {
                    progressBar.color = it
                }
                binding.setVariable(BR.playingItem, it)
            }
        }
        viewModel.progress.observe(this) {
            progressTx.text = numToString(it)
            if (!progressBar.getTouched()) {
                progressBar.value = it
            }
        }
        viewModel.duration.observe(this) {
            durationTx.text = numToString(it)
        }
        viewModel.playState.observe(this) {
            val resId = if (it) {
                R.drawable.music_playing_pause_64
            } else {
                R.drawable.music_playing_play_64
            }
            controlBt.setImageResource(resId)
        }
        viewModel.playingItems.observe(this) {
            if (it == null) {
                finish()
            }
        }
    }

    override fun onDestroy() {
        timerWrapper.reset()
        super.onDestroy()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_playing_layout
    }

    override fun getViewModelClass(): Class<PlayingViewModel> {
        return PlayingViewModel::class.java
    }

    override fun createFactory(): ViewModelProvider.Factory {
        return DefaultFactory.getInstance()
    }

}