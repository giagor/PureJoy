package com.topview.purejoy.common.music.player.client

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.topview.purejoy.common.IBinderPool

open class BinderPoolClient(context: Context,
                            private val clazz: Class<*>) {
    @Volatile
    private var pool: IBinderPool? = null
    private val context: Context = context.applicationContext
    private val flags = 0

    @Volatile
    private var state = INIT_STATE
    protected val connectListeners: MutableList<() -> Unit> = mutableListOf()
    protected val disconnectListeners: MutableList<() -> Unit> = mutableListOf()


    private val connection: ServiceConnection by lazy {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                state = CONNECT_STATE
                pool = IBinderPool.Stub.asInterface(service)
                pool?.asBinder()?.linkToDeath(deathRecipient, flags)
                connected()
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                state = DISCONNECT_STATE
                disconnected()
                pool?.asBinder()?.unlinkToDeath(deathRecipient, flags)
                pool = null
            }

        }
    }

    private val deathRecipient: IBinder.DeathRecipient by lazy {
        IBinder.DeathRecipient {
            pool?.asBinder()?.unlinkToDeath(deathRecipient, flags)
            pool = null
            state = DISCONNECT_STATE
            connectService()
        }
    }

    fun isConnected() = state == CONNECT_STATE

    fun isDisconnected() = state == DISCONNECT_STATE

    fun registerConnectListener(action: () -> Unit) {
        if (isConnected()) {
            action.invoke()
        }
        connectListeners.add(action)
    }

    fun registerDisconnectListener(action: () -> Unit) {
        if (isDisconnected()) {
            action.invoke()
        }
        disconnectListeners.add(action)
    }

    fun unregisterConnectListener(action: () -> Unit) {
        connectListeners.remove(action)
    }

    fun unregisterDisconnectListener(action: () -> Unit) {
        disconnectListeners.remove(action)
    }



    open fun connected() {
        connectListeners.forEach {
            it.invoke()
        }
    }

    open fun disconnected() {
        disconnectListeners.forEach {
            it.invoke()
        }
    }

    fun connectService() {
        if (state != WAITING && !isConnected()) {
            state = WAITING
            val intent = Intent(context, clazz)
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    fun disconnectService() {
        if (isConnected()) {
            context.unbindService(connection)
        }
    }

    fun pool(): IBinderPool? {
        return pool
    }

    open fun release() {
        disconnectService()
        connectListeners.clear()
        disconnectListeners.clear()
        state = INIT_STATE
    }

    companion object {
        // 初始状态
        const val INIT_STATE = 0
        // 连接状态
        const val CONNECT_STATE = 1
        // 连接断开状态
        const val DISCONNECT_STATE = 2
        const val WAITING = 3
    }

}