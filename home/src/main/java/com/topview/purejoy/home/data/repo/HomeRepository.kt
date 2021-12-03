package com.topview.purejoy.home.data.repo

import com.topview.purejoy.home.data.source.HomeLocalStore
import com.topview.purejoy.home.data.source.HomeRemoteStore
import com.topview.purejoy.home.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal object HomeRepository {
    private val homeRemoteStore: HomeRemoteStore = HomeRemoteStore()
    private val homeLocalStore: HomeLocalStore = HomeLocalStore()

    suspend fun getBanners(): List<HomeDiscoverBannerItem>? = withContext(Dispatchers.IO) {
        homeRemoteStore.getBanners()
    }

    suspend fun getDailyRecommendPlayList(limit: Int): List<PlayList>? =
        withContext(Dispatchers.IO) {
            homeRemoteStore.getDailyRecommendPlayList(limit)
        }

    suspend fun getRecommendNewSong(limit: Int): List<Song>? =
        withContext(Dispatchers.IO) {
            homeRemoteStore.getRecommendNewSong(limit)
        }

    suspend fun getSearchSongByFirst(keyword: String, limit: Int): SongPagerWrapper? =
        withContext(Dispatchers.IO) {
            homeRemoteStore.getSearchSongByFirst(keyword, limit)
        }

    suspend fun loadMoreSongs(keyword: String, offset: Int, limit: Int): List<Song>? =
        withContext(Dispatchers.IO) {
            homeRemoteStore.loadMoreSongs(keyword, offset, limit)
        }

    suspend fun getSearchPlayListByFirst(keyword: String, limit: Int): PlayListPagerWrapper? =
        withContext(Dispatchers.IO) {
            homeRemoteStore.getSearchPlayListByFirst(keyword, limit)
        }

    suspend fun loadMorePlayLists(keyword: String, offset: Int, limit: Int): List<PlayList>? =
        withContext(Dispatchers.IO) {
            homeRemoteStore.loadMorePlayLists(keyword, offset, limit)
        }
}