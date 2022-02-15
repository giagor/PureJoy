package com.topview.purejoy.home.data.repo

import com.topview.purejoy.common.music.service.entity.MusicItem
import com.topview.purejoy.home.data.bean.LimitSongJson
import com.topview.purejoy.home.data.bean.toMusicItems
import com.topview.purejoy.home.data.source.TopListRemoteStore
import com.topview.purejoy.home.entity.TopList
import com.topview.purejoy.home.entity.TopListTab
import com.topview.purejoy.home.entity.topListIdMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext

object TopListRepository {

    private val remoteStore = TopListRemoteStore()

    suspend fun getTopListDetail(): List<TopList> =
        withContext(Dispatchers.IO) {
            val json = remoteStore.getTopListDetail()
            if (json == null || json.code != 200) {
                return@withContext emptyList()
            } else {
                val list: MutableList<TopList> = mutableListOf()
                json.list?.forEach { data ->
                    runCatching {
                        TopList(
                            data.id!!,
                            data.name!!,
                            data.updateFrequency!!,
                            data.coverImgUrl!!,
                            data.trackCount!!
                        ).also {
                            it.playCount = data.playCount ?: 0
                            it.description = data.description
                            it.subscribedCount = data.subscribedCount ?: 0
                            it.updateTime = data.updateTime
                            // 获取tracks数据，只有官方榜才会提供这个数据
                            data.tracks?.run {
                                val cache: MutableList<TopList.Track> = mutableListOf()
                                forEach { originTrack ->
                                    cache.add(TopList.Track(originTrack.first!!,
                                        originTrack.second!!))
                                }
                                it.tracks = cache
                            }
                        }
                    }.onSuccess {
                        list.add(it)
                    }.onFailure {}
                }
                return@withContext list
            }
        }

    /**
     * 根据榜单信息，获取排在前三位的歌曲的封面。
     * @param list 榜单的id的列表
     */
    suspend fun getPicUrlForTopList(list: List<TopList>) {
        withContext(Dispatchers.IO) {
            val defaultUrl = ""
            for (topList in list) {
                if (topList.trackCoverUrl == null) {
                    val dataOfSong = remoteStore.getLimitTopListSong(topList.id, 3)
                    dataOfSong?.code?.let {
                        if (it == 200) {
                            val urlList = listOf(
                                dataOfSong.getPicUrlOfDefault(0, defaultUrl),
                                dataOfSong.getPicUrlOfDefault(1, defaultUrl),
                                dataOfSong.getPicUrlOfDefault(2, defaultUrl)
                            )
                            topList.trackCoverUrl = urlList
                        }
                    }
                }
            }
        }
    }

    /**
     * 将整体的榜单根据不同类别进行拆分
     */
    suspend fun collectTopListMap(list: List<TopList>): Map<TopListTab, List<TopList>> {
        val result = mutableMapOf<TopListTab, List<TopList>>()
        var cacheList: List<TopList>
        TopListTab.values().forEach { tab ->
            cacheList = list.asFlow()
                .filter {
                    topListIdMap[tab]?.contains(it.id) ?: false
                }
                .flowOn(Dispatchers.IO)
                .toList()
            if (cacheList.isNotEmpty()) {
                result[tab] = cacheList
            }
        }
        return result
    }

    suspend fun getSongs(topList: TopList): List<MusicItem> {
        val json = remoteStore.getLimitTopListSong(topList.id, null)
        if (json == null || json.code != 200) {
            error("cannot get the correct json object")
        }
        return json.toMusicItems() ?: error("data is empty")
    }


    private fun LimitSongJson.getPicUrlOfDefault(index: Int, default: String): String =
        this.songs?.get(index)?.al?.picUrl ?: default
}