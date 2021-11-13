package com.topview.purejoy.home.data.repo

import com.topview.purejoy.home.data.bean.BannerJson
import com.topview.purejoy.home.data.source.HomeLocalStore
import com.topview.purejoy.home.data.source.HomeRemoteStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal object HomeRepository {
    private val homeRemoteStore: HomeRemoteStore = HomeRemoteStore()
    private val homeLocalStore: HomeLocalStore = HomeLocalStore()

    suspend fun getBanners(): BannerJson? = withContext(Dispatchers.IO) {
        homeRemoteStore.getBanners()
    }
}