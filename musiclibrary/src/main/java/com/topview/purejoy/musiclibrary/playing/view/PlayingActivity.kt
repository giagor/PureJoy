package com.topview.purejoy.musiclibrary.playing.view

import android.os.Bundle
import android.view.Gravity
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.topview.purejoy.musiclibrary.*
import com.topview.purejoy.musiclibrary.common.MusicBindingActivity
import com.topview.purejoy.musiclibrary.common.factory.DefaultFactory
import com.topview.purejoy.musiclibrary.common.transformation.MusicItemTransformation
import com.topview.purejoy.musiclibrary.common.util.*
import com.topview.purejoy.musiclibrary.data.Wrapper
import com.topview.purejoy.musiclibrary.entity.MusicItem
import com.topview.purejoy.musiclibrary.entity.wrap
import com.topview.purejoy.musiclibrary.player.setting.MediaModeSetting
import com.topview.purejoy.musiclibrary.player.util.castAs
import com.topview.purejoy.musiclibrary.playing.view.pop.MusicPopUpWrapper
import com.topview.purejoy.musiclibrary.playing.view.pop.PopAdapter
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


    private val itemChangeListener = object : IPCItemChangeListener.Stub() {
        override fun onItemChange(wrapper: Wrapper?) {
            viewModel.currentItem.postValue(MusicItemTransformation.transform(wrapper!!))
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
                    MediaModeSetting.LOOP -> R.drawable.music_playing_loop_48
                    MediaModeSetting.RANDOM -> R.drawable.music_playing_random_48
                    else -> R.drawable.music_playing_order_48
                }
                modeBt.setImageResource(resId)
                popWrapper.updateMode(mode)
            }
        }
    }

    private val dataSetChangeListener = object : IPCDataSetChangeListener.Stub() {
        override fun onChange(source: MutableList<Wrapper>) {
            if (source.isEmpty()) {
                viewModel.playingItems.postValue(null)
            } else {
                val value = viewModel.playingItems.value!!
                for (data in source) {
                    value.remove(MusicItemTransformation.transform(data))
                }
                if (value.isEmpty()) finish()
                viewModel.playingItems.postValue(value)
            }
        }

    }

    private val popWrapper: MusicPopUpWrapper by lazy {
        val bound = getDisplaySize()
        val w = MusicPopUpWrapper(this, bound.width(), bound.height() / 2)
        w
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        dataController?.current()?.let {
            viewModel.currentItem.postValue(MusicItemTransformation.transform(it))
        }
        viewModel.progress.postValue(playerController?.progress() ?: 0)
        viewModel.playState.postValue(playerController?.isPlaying ?: false)
        val items = dataController?.allItems()?.map {
            MusicItemTransformation.transform(it)!!
        } ?: listOf()
        viewModel.playingItems.value?.clear()
        viewModel.playingItems.value?.addAll(items)
        viewModel.playingItems.postValue(viewModel.playingItems.value)
        viewModel.duration.observe(this) {
            progressBar.max = it
        }
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

        popWrapper.adapter.itemClickListener = object : PopAdapter.PopItemClickListener {
            override fun onClick(item: MusicItem) {
                val i = viewModel.playingItems.value?.indexOf(item)
                i?.let {
                    if (i >= 0) {
                        playerController?.jumpTo(i)
                    }
                }
            }
        }

        popWrapper.adapter.deleteClickListener = object : PopAdapter.PopItemClickListener {
            override fun onClick(item: MusicItem) {
                dataController?.remove(item.wrap())
            }

        }

        popWrapper.viewHolder.getView<LinearLayout>(R.id.music_playing_pop_mode_layout)
            .setOnClickListener {
            modeController?.nextMode()
        }

        popWrapper.viewHolder.getView<ImageButton>(R.id.music_playing_pop_clear_bt)
            .setOnClickListener {
            showClearDialog()
        }


        binding.root.findViewById<ImageButton>(R.id.music_playing_list_bt).setOnClickListener {
            // show playing list
            popWrapper.updateMode(modeController?.currentMode() ?: MediaModeSetting.RANDOM)
            updatePopWindow()
            popWrapper.popWindow.showAtLocation(binding.root, Gravity.BOTTOM, 0, 0)
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
                if (popWrapper.popWindow.isShowing) {
                    popWrapper.adapter.currentItem = it
                }
                popWrapper.adapter.currentItem = it
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
            } else {
                if (popWrapper.popWindow.isShowing) {
                    updatePopWindow()
                }
            }
        }
    }


    private fun updatePopWindow() {
        val size = popWrapper.adapter.data.size
        popWrapper.adapter.data.clear()
        popWrapper.adapter.data.addAll(viewModel.playingItems.value!!)
        popWrapper.adapter.notifyItemRangeChanged(0, size)
        popWrapper.updatePlayingCount(popWrapper.adapter.data.size)
    }

    override fun onDestroy() {
        if (popWrapper.popWindow.isShowing) {
            popWrapper.popWindow.dismiss()
        }
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

    private fun showClearDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.clear_playing_msg)
            .setPositiveButton(R.string.ensure
            ) { dialog, _ ->
                dataController?.clear()
                dialog?.dismiss()
            }.setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog?.dismiss()
            }
        builder.create().show()
    }

}