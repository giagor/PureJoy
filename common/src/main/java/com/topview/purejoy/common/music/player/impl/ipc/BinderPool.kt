package com.topview.purejoy.common.music.player.impl.ipc

import android.os.IBinder
import com.topview.purejoy.common.IBinderPool
import java.util.concurrent.ConcurrentHashMap

class BinderPool(val map: MutableMap<Int, IBinder> = ConcurrentHashMap()) : IBinderPool.Stub() {
    override fun queryBinder(code: Int): IBinder {
        return map[code]!!
    }

    companion object {
        // 获取IPCDataController的code
        const val DATA_CONTROL_BINDER = 1
        // 获取IPCListenerController的code
        const val LISTENER_CONTROL_BINDER = 2
        // 获取IPCModeController的code
        const val MODE_CONTROL_BINDER = 3
        // 获取IPCPlayerController的code
        const val PLAYER_CONTROL_BINDER = 4
    }
}