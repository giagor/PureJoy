package com.topview.purejoy.musiclibrary.common

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.topview.purejoy.common.base.CommonActivity
import com.topview.purejoy.common.mvvm.activity.MVVMActivity
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel
import com.topview.purejoy.musiclibrary.IPCDataController
import com.topview.purejoy.musiclibrary.IPCListenerController
import com.topview.purejoy.musiclibrary.IPCModeController
import com.topview.purejoy.musiclibrary.IPCPlayerController
import com.topview.purejoy.musiclibrary.common.factory.DefaultFactory
import com.topview.purejoy.musiclibrary.common.util.getAndConnectService
import com.topview.purejoy.musiclibrary.player.client.BinderPoolClient
import com.topview.purejoy.musiclibrary.player.impl.ipc.BinderPool
import com.topview.purejoy.musiclibrary.service.MusicService

abstract class MusicCommonActivity<VM : MVVMViewModel>
    : CommonActivity() {

    protected val viewModel: VM by lazy {
        ViewModelProvider(this, createFactory()).get(getViewModelClass())
    }

    protected var playerController: IPCPlayerController? = null
    protected var dataController: IPCDataController? = null
    protected var modeController: IPCModeController? = null
    protected var listenerController: IPCListenerController? = null

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
    }

    protected open fun onServiceDisconnected() {
        playerController = null
        dataController = null
        modeController = null
        listenerController = null
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

    protected open fun createFactory(): ViewModelProvider.Factory {
        return DefaultFactory.getInstance()
    }

    protected abstract fun getViewModelClass(): Class<VM>

}