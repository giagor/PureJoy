package com.topview.purejoy.home.data.source

import com.topview.purejoy.common.net.ServiceCreator
import com.topview.purejoy.common.net.await
import com.topview.purejoy.home.data.api.HomeService
import com.topview.purejoy.home.data.bean.BannerJson

private const val BANNER_TYPE: Int = 1

class HomeRemoteStore {
    private val homeService = ServiceCreator.create(HomeService::class.java)

    suspend fun getBanners(): BannerJson? = homeService.getBanners(BANNER_TYPE).await()
}