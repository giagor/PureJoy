package com.topview.purejoy.musiclibrary.common.instance

import com.topview.purejoy.common.app.CommonApplication
import com.topview.purejoy.musiclibrary.player.client.BinderPoolClient

class BinderPoolClientInstance {
    private val clientMap: MutableMap<Class<*>, BinderPoolClient> = mutableMapOf()


    fun getClient(clazz: Class<*>): BinderPoolClient {
        if (clientMap.containsKey(clazz)) {
            return clientMap[clazz]!!
        }
        val client = BinderPoolClient(CommonApplication.getContext(), clazz)
        clientMap[clazz] = client
        return client
    }



    companion object {
        @Volatile
        private var instance: BinderPoolClientInstance? = null

        fun getInstance(): BinderPoolClientInstance {
            if (instance == null) {
                synchronized(BinderPoolClientInstance::class.java) {
                    if (instance == null) {
                        instance = BinderPoolClientInstance()
                    }
                }
            }

            return instance!!
        }
    }

}