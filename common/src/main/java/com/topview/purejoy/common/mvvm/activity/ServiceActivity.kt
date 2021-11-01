package com.topview.purejoy.common.mvvm.activity

import android.app.Service
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel

/**
 * 使用MVVM设计模式绑定服务的通用Activity
 */
abstract class ServiceActivity<VM : MVVMViewModel, C : ServiceConnection, S : Service> :
    MVVMActivity<VM>() {
    protected lateinit var connection: C

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connection = createConnection()
    }

    override fun onStart() {
        super.onStart()
        connectService()
    }

    override fun onStop() {
        super.onStop()
        disconnectService()
    }

    // 提供服务连接实例
    protected abstract fun createConnection(): C

    // 绑定服务
    protected fun connectService() {
        val intent = Intent(this, serviceClass())
        bindService(intent, connection, serviceFlag())
    }

    // 提供要绑定的服务类型
    protected abstract fun serviceClass(): Class<S>

    // 服务的启动模式
    protected abstract fun serviceFlag(): Int

    // 解绑服务
    protected fun disconnectService() {
        unbindService(connection)
    }
}