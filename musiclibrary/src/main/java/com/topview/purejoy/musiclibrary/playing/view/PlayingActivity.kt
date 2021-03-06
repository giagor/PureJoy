package com.topview.purejoy.musiclibrary.playing.view

import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.alibaba.android.arouter.facade.annotation.Route
import com.topview.purejoy.common.music.view.bottom.MusicController
import com.topview.purejoy.common.music.view.bottom.MusicPopHelper
import com.topview.purejoy.common.music.player.setting.MediaModeSetting
import com.topview.purejoy.common.music.util.getDisplaySize
import com.topview.purejoy.common.mvvm.activity.MVVMActivity
import com.topview.purejoy.common.router.CommonRouter
import com.topview.purejoy.musiclibrary.BR
import com.topview.purejoy.musiclibrary.R
import com.topview.purejoy.musiclibrary.common.factory.DefaultFactory
import com.topview.purejoy.musiclibrary.common.util.*
import com.topview.purejoy.musiclibrary.playing.view.widget.MusicProgressBar
import com.topview.purejoy.musiclibrary.playing.viewmodel.PlayingViewModel

@Route(path = CommonRouter.ACTIVITY_PLAYING)
class PlayingActivity : MVVMActivity<PlayingViewModel, ViewDataBinding>() {

    private val layout: LinearLayout by lazy {
        binding.root.findViewById(R.id.music_playing_layout)
    }

    private val musicController: MusicController = MusicController()

    private val popHelper: MusicPopHelper by lazy {
        val size = getDisplaySize()
        MusicPopHelper(this, size.width(),
            size.height() / 3 * 2, musicController)
    }
    
    private val TAG = "Playing"

    private val progressTx: TextView by lazy {
        binding.root.findViewById(R.id.music_playing_progress_tx)
    }

    private val listener: MusicProgressBar.MusicProgressBarListener by lazy {
        object : MusicProgressBar.MusicProgressBarListenerAdapter {
            override fun onStopTracking(musicProgressBar: MusicProgressBar) {
                musicController.playerController?.seekTo(musicProgressBar.value)
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
            viewModel.progress.postValue(musicController.playerController?.progress() ?: 0)
        }
    }



    fun onServiceConnected() {
        viewModel.progress.postValue(musicController.playerController?.progress() ?: 0)
        viewModel.duration.postValue(musicController.playerController?.duration() ?: 0)
        timerWrapper.start()
    }


    fun onServiceDisconnected() {
        timerWrapper.reset()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        musicController.registerServiceListener()
        musicController.client.registerConnectListener(this::onServiceConnected)
        musicController.client.registerDisconnectListener(this::onServiceDisconnected)
        musicController.client.connectService()
        becomeImmersive(binding.root)
        initView()
        observeData()
        observeViewModel()
        addAndShowFragment()
    }

    private fun observeData() {
        musicController.currentItem.observe(this) {
            viewModel.currentItem.postValue(it)
            viewModel.progress.postValue(0)
            val duration = musicController.playerController?.duration() ?: 0
            viewModel.duration.postValue(duration)

        }
        musicController.playState.observe(this) {
            viewModel.playState.postValue(it)
        }
        musicController.currentMode.observe(this) {
            modeBt.post {
                val resId = when(it) {
                    MediaModeSetting.ORDER -> R.drawable.music_playing_order_48
                    MediaModeSetting.LOOP -> R.drawable.music_playing_loop_48
                    MediaModeSetting.RANDOM -> R.drawable.music_playing_random_48
                    else -> R.drawable.music_playing_order_48
                }
                modeBt.setImageResource(resId)
            }
        }
        musicController.playItems.observe(this) { source ->
            if (source != null) {
                viewModel.playingItems.postValue(source)
                if (source.isEmpty()) {
                    finish()
                }
            }
        }
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
            musicController.playerController?.last()
        }
        binding.root.findViewById<ImageButton>(R.id.music_playing_next_bt).setOnClickListener {
            musicController.playerController?.next()
        }
        controlBt.setOnClickListener {
            musicController.playerController?.playOrPause()
        }
        modeBt.setOnClickListener {
            musicController.modeController?.nextMode()
        }




        binding.root.findViewById<ImageButton>(R.id.music_playing_list_bt).setOnClickListener {
            popHelper.musicPopWindow.showDownAt(binding.root, 1f)
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
            progressBar.max = it
        }
        viewModel.playState.observe(this) {
            val resId = if (it) {
                R.drawable.music_playing_pause_64
            } else {
                R.drawable.music_playing_play_64
            }
            controlBt.setImageResource(resId)
            if (viewModel.duration.value != null || viewModel.duration.value == 0) {
                viewModel.duration.postValue(musicController.playerController?.duration() ?: 0)
            }
        }
        viewModel.playingItems.observe(this) {
            if (it == null) {
                finish()
            }
        }
    }




    override fun onDestroy() {
        timerWrapper.reset()
        musicController.unregisterServiceListener()
        musicController.client.unregisterConnectListener(this::onServiceConnected)
        musicController.client.unregisterDisconnectListener(this::onServiceDisconnected)
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