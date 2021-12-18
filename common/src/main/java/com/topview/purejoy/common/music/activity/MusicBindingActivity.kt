package com.topview.purejoy.common.music.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.MutableLiveData
import com.topview.purejoy.common.*
import com.topview.purejoy.common.music.activity.pop.MusicPopUpWrapper
import com.topview.purejoy.common.music.activity.pop.PopAdapter
import com.topview.purejoy.common.music.data.Wrapper
import com.topview.purejoy.common.music.player.client.BinderPoolClient
import com.topview.purejoy.common.music.player.impl.ipc.BinderPool
import com.topview.purejoy.common.music.service.MusicService
import com.topview.purejoy.common.music.service.entity.MusicItem
import com.topview.purejoy.common.music.service.entity.getMusicItem
import com.topview.purejoy.common.music.service.entity.wrap
import com.topview.purejoy.common.music.util.ExecutorInstance
import com.topview.purejoy.common.music.util.addViewToContent
import com.topview.purejoy.common.music.util.getAndConnectService
import com.topview.purejoy.common.music.util.getDisplaySize
import com.topview.purejoy.common.mvvm.activity.MVVMActivity
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel
import com.topview.purejoy.common.widget.compose.RoundedCornerImageView

@SuppressLint("NotifyDataSetChanged")
abstract class MusicBindingActivity<VM: MVVMViewModel, T : ViewDataBinding> : MVVMActivity<VM, T>() {
    // 播放逻辑控制
    protected var playerController: IPCPlayerController? = null
    // 数据操作控制
    protected var dataController: IPCDataController? = null
    // 播放模式控制
    protected var modeController: IPCModeController? = null
    // IPC回调控制
    protected var listenerController: IPCListenerController? = null
    // 当前播放歌曲
    protected val currentItem: MutableLiveData<MusicItem?> = MutableLiveData()
    // 当前播放列表
    protected val playItems: MutableLiveData<MutableList<MusicItem>> = MutableLiveData()
    // 当前播放状态
    protected val playState: MutableLiveData<Boolean> = MutableLiveData()
    // 当前播放模式
    protected val currentMode: MutableLiveData<Int> = MutableLiveData()

    private val TAG = "MusicBinding"

    private val itemChangeListener: IPCItemChangeListener by lazy {
        object : IPCItemChangeListener.Stub() {
            override fun onItemChange(wrapper: Wrapper?) {
                currentItem.postValue(wrapper?.getMusicItem())
            }
        }
    }

    private val dataSetChangeListener: IPCDataSetChangeListener by lazy {
        object : IPCDataSetChangeListener.Stub() {
            override fun onChange(source: MutableList<Wrapper>) {
                if (source.isEmpty()) {
                    playItems.postValue(mutableListOf())
                } else {
                    val list = playItems.value ?: mutableListOf()
                    source.forEach { w ->
                        w.getMusicItem()?.let {
                            if (list.contains(it)) {
                                list.remove(it)
                            } else {
                                list.add(it)
                            }
                        }
                    }
                    playItems.postValue(list)
                }
            }
        }
    }

    private val modeChangeListener: IPCModeChangeListener by lazy {
        object : IPCModeChangeListener.Stub() {
            override fun onModeChange(mode: Int) {
                currentMode.postValue(mode)
            }

        }
    }

    private val stateChangeListener: IPCPlayStateChangeListener by lazy {
        object : IPCPlayStateChangeListener.Stub() {
            override fun playStateChange(state: Boolean) {
                playState.postValue(state)
            }
        }
    }

    // 播放列表的弹窗
    protected val musicPopWindow: MusicPopUpWrapper by lazy {
        val size = getDisplaySize()
        val p = MusicPopUpWrapper(this, size.width(), size.height() / 3 * 2, window)
        p.adapter.itemClickListener = object : PopAdapter.PopItemClickListener {
            override fun onClick(item: MusicItem) {
                playerController?.jumpTo(p.adapter.data.indexOf(item))
            }
        }
        p.adapter.deleteClickListener = object : PopAdapter.PopItemClickListener {
            override fun onClick(item: MusicItem) {
                dataController?.remove(item.wrap())
            }

        }
        p.viewHolder.getView<LinearLayout>(R.id.music_playing_pop_mode_layout)
            .setOnClickListener {
                modeController?.nextMode()
            }

        p.viewHolder.getView<ImageButton>(R.id.music_playing_pop_clear_bt)
            .setOnClickListener {
                showClearDialog()
            }
        currentItem.observe(this) {
            p.adapter.apply {
                this.currentItem = it
                notifyDataSetChanged()
            }
            if (it == null) {
                if(p.popWindow.isShowing) {
                    p.popWindow.dismiss()
                }
            }
        }
        playItems.observe(this) {
            if (it == null) {
                if (p.popWindow.isShowing) {
                    p.popWindow.dismiss()
                }
                return@observe
            }
            if (it.isEmpty()) {
                p.updateWindow(it)
                if (p.popWindow.isShowing) {
                    p.popWindow.dismiss()
                }
            } else {
                p.updateWindow(it)
            }
        }
        currentMode.observe(this) {
            p.updateMode(it)
        }
        p
    }

    protected val client: BinderPoolClient by lazy {
        getAndConnectService(MusicService::class.java)
    }

    protected open fun onServiceConnected() {
        playerController = IPCPlayerController.Stub.asInterface(
            client.pool()?.queryBinder(BinderPool.PLAYER_CONTROL_BINDER))
        dataController = IPCDataController.Stub.asInterface(
            client.pool()?.queryBinder(BinderPool.DATA_CONTROL_BINDER))
        modeController = IPCModeController.Stub.asInterface(
            client.pool()?.queryBinder(BinderPool.MODE_CONTROL_BINDER))
        listenerController = IPCListenerController.Stub.asInterface(
            client.pool()?.queryBinder(BinderPool.LISTENER_CONTROL_BINDER))
        listenerController?.apply {
            addItemChangeListener(itemChangeListener)
            addDataChangeListener(dataSetChangeListener)
            addPlayStateChangeListener(stateChangeListener)
            addModeChangeListener(modeChangeListener)
        }
        currentItem.postValue(dataController?.current()?.getMusicItem())
        playState.postValue(playerController?.isPlaying ?: false)
        dataController?.let { controller ->
            ExecutorInstance.getInstance().execute {
                val list = playItems.value ?: mutableListOf()
                val data = controller.allItems()
                for (d in data) {
                    d.getMusicItem()?.let {
                        list.add(it)
                    }
                }
                playItems.postValue(list)
            }
        }
        modeController?.currentMode()?.let {
            currentMode.postValue(it)
        }
    }

    protected open fun onServiceDisconnected() {
        playerController = null
        dataController = null
        modeController = null
        listenerController = null
        playItems.postValue(mutableListOf())
        currentItem.postValue(null)
        playState.postValue(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        client.registerConnectListener(this::onServiceConnected)
        client.registerDisconnectListener(this::onServiceDisconnected)
    }



    /**
     * @param marginBottom 底部的距离
     * @param duration 动画时间
     * @return 底部音乐播放栏的View
     * 显示底部音乐播放栏
     */
    protected fun addMusicBottomBar(marginBottom: Int, duration: Long = 1000): View {
        val bar = LayoutInflater.from(this).inflate(R.layout.music_bottom_bar, null)
        val iv = bar.findViewById<RoundedCornerImageView>(R.id.music_bottom_bar_iv)
        val nameTx = bar.findViewById<TextView>(R.id.music_bottom_bar_name_tx)
        val stateIv = bar.findViewById<ImageView>(R.id.music_bottom_bar_status_iv)
        val listIv = bar.findViewById<ImageView>(R.id.music_bottom_bar_playlist_iv)
        val contentView = ((window.decorView as ViewGroup).getChildAt(0)
                as ViewGroup).getChildAt(1) as FrameLayout
        currentItem.observe(this) {
            if (it == null) {
                bar.visibility = View.GONE
            } else {
                bar.visibility = View.VISIBLE
                iv.loadImageRequest = it.al.picUrl
                nameTx.text = it.name
            }
        }
        playState.observe(this) {
            if (it) {
                stateIv.setImageResource(R.drawable.music_bottom_bar_pause_32)
            } else {
                stateIv.setImageResource(R.drawable.music_bottom_bar_play_32)
            }
        }
        stateIv.setOnClickListener {
            playerController?.playOrPause()
        }
        currentItem.observe(this) {
            if (it == null) {
                bar.visibility = View.GONE
            } else {
                if (bar.visibility != View.VISIBLE) {
                    bar.visibility = View.VISIBLE
                }
            }
        }
        listIv.setOnClickListener {
            if (!musicPopWindow.popWindow.isShowing) {
                musicPopWindow.showDownAt(contentView)
            }
        }
        bar.setOnClickListener {
//            startActivity(Intent(this, PlayingActivity::class.java))
        }
        addViewToContent(bar, marginBottom, duration)
        return bar
    }


    override fun onDestroy() {
        kotlin.runCatching {
            if (client.isConnected()) {
                listenerController?.apply {
                    removeItemChangeListener(itemChangeListener)
                    removeDataChangeListener(dataSetChangeListener)
                    removePlayStateChangeListener(stateChangeListener)
                    removeModeChangeListener(modeChangeListener)
                }
            }
        }
        if (musicPopWindow.popWindow.isShowing) {
            musicPopWindow.popWindow.dismiss()
        }
        client.unregisterConnectListener(this::onServiceConnected)
        client.unregisterDisconnectListener(this::onServiceDisconnected)
        super.onDestroy()
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