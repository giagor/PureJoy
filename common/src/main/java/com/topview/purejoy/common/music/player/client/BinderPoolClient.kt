package com.topview.purejoy.common.music.player.client

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.topview.purejoy.common.IBinderPool

/**
 * BinderPoolClient有四种状态：
 *   INIT_STATE:初始状态
 *   WAITING_STATE:已调用connectService方法，正在等待连接，在此状态下，再次调用
 *                 connectService方法无任何作用
 *   CONNECT_STATE:连接服务成功后处于此状态
 *   DISCONNECT_STATE:服务断开后处于此状态
 *
 */
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

    private val TAG = "BinderClient"

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

    /**
     * 注册服务连接时的回调
     */
    fun registerConnectListener(action: () -> Unit) {
        if (isConnected()) {
            action.invoke()
        }
        connectListeners.add(action)
    }

    /**
     * 注册服务断开时的回调
     */
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

    /**
     * 连接服务，当不处于WAITING_STATE且不处于CONNECT_STATE有效
     */
    fun connectService() {
        if (state != WAITING_STATE && !isConnected()) {
            state = WAITING_STATE
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
        const val WAITING_STATE = 3
    }

}