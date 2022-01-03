package com.topview.purejoy.musiclibrary.common.util

import com.topview.purejoy.common.app.CommonApplication
import com.topview.purejoy.common.component.download.DownloadManager
import com.topview.purejoy.common.music.player.impl.cache.DiskCache

fun download(url: String) {
    val name = "${
        DiskCache.MD5Digest.getInstance()
            .digest(url)
    }/${url.substring(url.lastIndexOf('.'))}"
    DownloadManager.download(
        url,
        CommonApplication.musicPath.absolutePath,
        name, null
    )
}