package com.topview.purejoy.home.data.repo

import com.topview.purejoy.home.data.source.HomeLocalStore
import com.topview.purejoy.home.data.source.HomeRemoteStore
import com.topview.purejoy.home.entity.DailyRecommendPlayList
import com.topview.purejoy.home.entity.HomeDiscoverBannerItem
import com.topview.purejoy.home.entity.RecommendNewSong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal object HomeRepository {
    private val homeRemoteStore: HomeRemoteStore = HomeRemoteStore()
    private val homeLocalStore: HomeLocalStore = HomeLocalStore()

    suspend fun getBanners(): List<HomeDiscoverBannerItem>? = withContext(Dispatchers.IO) {
        homeRemoteStore.getBanners()
    }

    suspend fun getDailyRecommendPlayList(limit: Int = 6): List<DailyRecommendPlayList>? =
        withContext(Dispatchers.IO) {
            homeRemoteStore.getDailyRecommendPlayList(limit)
        }

    suspend fun getRecommendNewSong(limit: Int = 12): List<RecommendNewSong>? =
        withContext(Dispatchers.IO) {
            homeRemoteStore.getRecommendNewSong(limit)
        }
}