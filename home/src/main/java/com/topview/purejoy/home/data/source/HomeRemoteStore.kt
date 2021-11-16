package com.topview.purejoy.home.data.source

import com.topview.purejoy.common.net.ServiceCreator
import com.topview.purejoy.common.net.await
import com.topview.purejoy.home.data.api.HomeService
import com.topview.purejoy.home.data.bean.BannerJson
import com.topview.purejoy.home.data.bean.DailyRecommendPlayListJson
import com.topview.purejoy.home.data.bean.RecommendNewSongJson
import com.topview.purejoy.home.entity.DailyRecommendPlayList
import com.topview.purejoy.home.entity.HomeDiscoverBannerItem
import com.topview.purejoy.home.entity.RecommendNewSong

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
                    val dailyRecommendPlayList =
                        DailyRecommendPlayList(it.id, it.name, it.picUrl, it.playCount)
                    list.add(dailyRecommendPlayList)
                }
                return list
            }
        }
        return null
    }

    suspend fun getRecommendNewSong(limit: Int): List<RecommendNewSong>? {
        val recommendNewSongJson: RecommendNewSongJson? =
            homeService.getRecommendNewSong(limit).await()
        if (recommendNewSongJson != null) {
            val result = recommendNewSongJson.result
            if (result != null) {
                val list = mutableListOf<RecommendNewSong>()
                result.forEach {
                    val artistNameBuilder = StringBuilder()

                    // 拼接歌手的名字
                    val artists: List<RecommendNewSongJson.Result.Song.Artist>? = it.song?.artists
                    artists?.forEach { artist ->
                        artistNameBuilder.append(artist.name + " ")
                    }
                    list.add(
                        RecommendNewSong(
                            it.id,
                            it.name,
                            it.picUrl,
                            artistNameBuilder.toString()
                        )
                    )
                }
                return list
            }
        }
        return null
    }
}