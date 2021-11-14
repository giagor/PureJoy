package com.topview.purejoy.home.data.source

import com.topview.purejoy.common.net.ServiceCreator
import com.topview.purejoy.common.net.await
import com.topview.purejoy.home.data.api.HomeService
import com.topview.purejoy.home.data.bean.BannerJson
import com.topview.purejoy.home.data.bean.DailyRecommendPlayListJson
import com.topview.purejoy.home.entity.DailyRecommendPlayList
import com.topview.purejoy.home.entity.HomeDiscoverBannerItem

private const val BANNER_TYPE: Int = 1

class HomeRemoteStore {
    private val homeService = ServiceCreator.create(HomeService::class.java)

    suspend fun getBanners(): List<HomeDiscoverBannerItem>? {
        val bannerJson: BannerJson? = homeService.getBanners(BANNER_TYPE).await()
        if (bannerJson != null) {
            val banners = bannerJson.banners
            if (banners != null) {
                val list = mutableListOf<HomeDiscoverBannerItem>()
                banners.forEach {
                    list.add(HomeDiscoverBannerItem(it.pic, it.url))
                }
                return list
            }
        }
        return null
    }

    suspend fun getDailyRecommendPlayList(limit: Int): List<DailyRecommendPlayList>? {
        val dailyRecommendPlayListJson: DailyRecommendPlayListJson? =
            homeService.getDailyRecommendPlayList(limit).await()
        if (dailyRecommendPlayListJson != null) {
            val result = dailyRecommendPlayListJson.result
            if (result != null) {
                val list = mutableListOf<DailyRecommendPlayList>()
                result.forEach {
                    val dailyRecommendPlayList = DailyRecommendPlayList().apply {
                        id = it.id
                        name = it.name
                        playCount = it.playCount
                        picUrl = it.picUrl
                    }
                    list.add(dailyRecommendPlayList)
                }
                return list
            }
        }
        return null
    }
}