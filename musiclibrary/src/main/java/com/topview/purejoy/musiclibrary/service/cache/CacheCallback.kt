package com.topview.purejoy.musiclibrary.service.cache

import com.topview.purejoy.musiclibrary.data.Item
import com.topview.purejoy.musiclibrary.player.abs.Loader
import com.topview.purejoy.musiclibrary.player.abs.cache.CacheStrategy
import okhttp3.*
import java.io.IOException

class CacheCallback(val callback: Loader.Callback<Item>,
                    val strategy: CacheStrategy,
                    private val client: OkHttpClient
) : Loader.Callback<Item> {
    override fun callback(itemIndex: Int, value: Item) {
        callback.callback(itemIndex, value)
        value.url()?.let {
            val request = Request.Builder().url(it).build()
            val call = client.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {

                }

                override fun onResponse(call: Call, response: Response) {
                    response.body?.byteStream()?.let { stream ->
                        strategy.putInDisk(it, stream)
                    }
                }

            })
        }
    }


}