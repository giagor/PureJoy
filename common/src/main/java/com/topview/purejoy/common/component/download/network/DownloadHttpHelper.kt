package com.topview.purejoy.common.component.download.network

import com.topview.purejoy.common.component.download.listener.net.ResourceDataCallback
import com.topview.purejoy.common.component.download.listener.net.ResourcePreviewCallback
import com.topview.purejoy.common.component.download.task.SubDownloadTask

/**
 * Created by giagor on 2021/12/18
 * */
interface DownloadHttpHelper {
    fun getContentLength(downloadUrl: String, callback: ResourcePreviewCallback)
    fun getInputStreamByRange(subDownloadTask: SubDownloadTask, callback: ResourceDataCallback)
}