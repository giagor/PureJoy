package com.topview.purejoy.home.data.repo

import com.topview.purejoy.home.data.bean.SongDetailJson
import com.topview.purejoy.home.data.source.HomeLocalStore
import com.topview.purejoy.home.data.source.HomeRemoteStore
import com.topview.purejoy.home.entity.*

internal object HomeRepository {
    private val homeRemoteStore: HomeRemoteStore = HomeRemoteStore()
    private val homeLocalStore: HomeLocalStore = HomeLocalStore()

    suspend fun getBanners(): List<HomeDiscoverBannerItem>? = homeRemoteStore.getBanners()

    suspend fun getDailyRecommendPlayList(limit: Int): List<PlayList>? =
        homeRemoteStore.getDailyRecommendPlayList(limit)

    suspend fun getRecommendNewSong(limit: Int): List<Song>? =
        homeRemoteStore.getRecommendNewSong(limit)

    suspend fun getSearchSongByFirst(keyword: String, limit: Int): SongPagerWrapper? =
        homeRemoteStore.getSearchSongByFirst(keyword, limit)


    suspend fun loadMoreSongs(keyword: String, offset: Int, limit: Int): List<Song>? =
        homeRemoteStore.loadMoreSongs(keyword, offset, limit)

    suspend fun getSearchPlayListByFirst(keyword: String, limit: Int): PlayListPagerWrapper? =
        homeRemoteStore.getSearchPlayListByFirst(keyword, limit)

    suspend fun loadMorePlayLists(keyword: String, offset: Int, limit: Int): List<PlayList>? =
        homeRemoteStore.loadMorePlayLists(keyword, offset, limit)

    suspend fun requestSongUrl(id: Long): SongDetailJson? =
        homeRemoteStore.requestSongUrl(id)
}