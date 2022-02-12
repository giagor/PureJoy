package com.topview.purejoy.home.entity

import com.topview.purejoy.common.entity.Video
import com.topview.purejoy.video.data.bean.MVData
import com.topview.purejoy.video.data.bean.RecommendData
import com.topview.purejoy.video.data.bean.RecommendVideoJson
import com.topview.purejoy.video.data.bean.toVideo

class ExternVideo(
    val video: Video,
    val playCount: Long,
    val previewUrl: String? = null,
    // 视频所属的类别，仅针对推荐视频时才会储存这个值
    val tag: String? = null
)


/**
 * 将一个[RecommendVideoJson]对象转换为若干个[ExternVideo]信息
 * @param needTag 是否需要保存视频的tag信息
 */
internal fun RecommendVideoJson.toExternVideos(
    needTag: Boolean = false
): List<ExternVideo> {
    val list = mutableListOf<ExternVideo>()
    this.outerList?.forEach {
        if (it.type == 1) {
            val data = it.data as RecommendData
            list.add(
                ExternVideo(
                    video = data.toVideo(),
                    playCount = data.playCount,
                    previewUrl = data.previewUrl,
                    tag = if (needTag) data.videoGroup?.getOrNull(0)?.name else null
                )
            )
        } else if (it.type == 2) {
            val data = it.data as MVData
            list.add(
                ExternVideo(
                    video = data.toVideo(),
                    playCount = data.playCount,
                    previewUrl = null,
                    tag = if (needTag) data.videoGroup?.getOrNull(0)?.name else null
                )
            )
        }
    }
    return list
}
