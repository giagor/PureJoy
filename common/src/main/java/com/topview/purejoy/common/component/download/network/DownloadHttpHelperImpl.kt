package com.topview.purejoy.common.component.download.network

import com.topview.purejoy.common.component.download.listener.net.ResourceDataCallback
import com.topview.purejoy.common.component.download.listener.net.ResourcePreviewCallback
import com.topview.purejoy.common.component.download.task.SubDownloadTask
import okhttp3.*
import java.io.IOException
import java.lang.Exception

/**
 * Created by giagor on 2021/12/18
 * */
class DownloadHttpHelperImpl(private val client: OkHttpClient) : DownloadHttpHelper {
    override fun getContentLength(downloadUrl: String, callback: ResourcePreviewCallback) {
        val request: Request = Request.Builder()
            .head()
            .url(downloadUrl)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onFailure(e)
            }

            override fun onResponse(call: Call, response: Response) {
                val contentLengthHeader: Long? = response.headers["Content-Length"]?.toLongOrNull()
                // 不存在 Content-Length Header 或者 Content-Length 的值不合理，则认为资源错误
                if (contentLengthHeader == null || contentLengthHeader <= 0) {
                    callback.resourceErr()
                    response.body?.close()
                    return
                }

                val acceptRange: String? = response.headers["Accept-Ranges"]
                // 服务端不支持Range的范围请求
                if (acceptRange == null || acceptRange == "none") {
                    callback.unSupportRange(contentLengthHeader)
                    response.body?.close()
                    return
                } else if (acceptRange == "bytes") {
                    // Accept-Ranges的值为 bytes，表示服务端支持Range范围请求
                    callback.supportRange(contentLengthHeader)
                    response.body?.close()
                    return
                }
            }
        })
    }

    override fun getInputStreamByRange(subDownloadTask: SubDownloadTask, callback: ResourceDataCallback) {
        val rangeLeft = subDownloadTask.startPos + subDownloadTask.downloadedSize
        val rangeRight = subDownloadTask.startPos + subDownloadTask.subTaskSize - 1
        val request = Request.Builder()
            .url(subDownloadTask.url)
            .addHeader(
                "RANGE",
                "bytes=${rangeLeft}-${rangeRight}"
            ).build()

        // 调用OkHttp的同步执行方法，需要自己处理异常
        var body: ResponseBody? = null
        try {
            val response = client.newCall(request).execute()
            body = response.body
            body?.let {
                callback.onSuccess(it.byteStream())
            }
        } catch (e: IOException) {
            e.printStackTrace()
            callback.onFailure(e)
        } catch (t: Throwable) {
            t.printStackTrace()
            callback.onFailure(t)
        } finally {
            body?.let {
                try {
                    it.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

}