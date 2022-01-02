package com.topview.purejoy.musiclibrary.playing.view

import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.topview.purejoy.common.music.activity.MusicBindingActivity
import com.topview.purejoy.common.music.player.setting.MediaModeSetting
import com.topview.purejoy.musiclibrary.BR
import com.topview.purejoy.musiclibrary.R
import com.topview.purejoy.musiclibrary.common.factory.DefaultFactory
import com.topview.purejoy.musiclibrary.common.util.TimerWrapper
import com.topview.purejoy.musiclibrary.common.util.loadBitmapColor
import com.topview.purejoy.musiclibrary.common.util.loadBlurBackground
import com.topview.purejoy.musiclibrary.common.util.numToString
import com.topview.purejoy.musiclibrary.playing.view.widget.MusicProgressBar
import com.topview.purejoy.musiclibrary.playing.viewmodel.PlayingViewModel

class PlayingActivity : MusicBindingActivity<PlayingViewModel, ViewDataBinding>() {

    private val layout: LinearLayout by lazy {
        binding.root.findViewById(R.id.music_playing_layout)
    }
    
    private val TAG = "Playing"

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



    override fun onServiceConnected() {
        super.onServiceConnected()
        viewModel.progress.postValue(playerController?.progress() ?: 0)
        viewModel.duration.postValue(playerController?.duration() ?: 0)
        timerWrapper.start()
    }


    override fun onServiceDisconnected() {
        timerWrapper.reset()
        super.onServiceDisconnected()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        observeData()
        observeViewModel()
        addAndShowFragment()
    }

    private fun observeData() {
        currentItem.observe(this) {
            viewModel.currentItem.postValue(it)
            viewModel.progress.postValue(0)
            val duration = playerController?.duration() ?: 0
            viewModel.duration.postValue(duration)

        }
        playState.observe(this) {
            viewModel.playState.postValue(it)
        }
        currentMode.observe(this) {
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
        playItems.observe(this) { source ->
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
            musicPopWindow.showDownAt(binding.root, 1f)
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