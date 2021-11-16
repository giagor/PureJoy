package com.topview.purejoy.home.discover

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel
import com.topview.purejoy.home.data.Status
import com.topview.purejoy.home.data.repo.HomeRepository
import com.topview.purejoy.home.entity.DailyRecommendPlayList
import com.topview.purejoy.home.entity.HomeDiscoverBannerItem
import com.topview.purejoy.home.entity.RecommendNewSong

class HomeDiscoverViewModel : MVVMViewModel() {
    private val repository = HomeRepository

    val bannerLiveData: MutableLiveData<List<HomeDiscoverBannerItem>> by lazy {
        MutableLiveData<List<HomeDiscoverBannerItem>>()
    }

    val dailyRecommendPlayListLiveData: MutableLiveData<List<DailyRecommendPlayList>> by lazy {
        MutableLiveData<List<DailyRecommendPlayList>>()
    }

    val recommendNewSongLiveData: MutableLiveData<List<RecommendNewSong>> by lazy {
        MutableLiveData<List<RecommendNewSong>>()
    }

    fun getBanners() {
        viewModelScope.rxLaunch<List<HomeDiscoverBannerItem>> {
            onRequest = {
                repository.getBanners()
            }

            onSuccess = {
                bannerLiveData.value = it
            }

            onError = {
                status.value = Status.DISCOVER_BANNER_NET_ERROR
            }
        }
    }

    fun getDailyRecommendPlayList() {
        viewModelScope.rxLaunch<List<DailyRecommendPlayList>> {
            onRequest = {
                repository.getDailyRecommendPlayList()
            }

            onSuccess = {
                dailyRecommendPlayListLiveData.value = it
            }

            onError = {
                status.value = Status.DISCOVER_DAILY_RECOMMEND_PLAYLIST_NET_ERROR
            }
        }
    }

    fun getRecommendNewSong() {
        viewModelScope.rxLaunch<List<RecommendNewSong>> {
            onRequest = {
                repository.getRecommendNewSong()
            }

            onSuccess = {
                recommendNewSongLiveData.value = it
            }

            onError = {
                status.value = Status.DISCOVER_RECOMMEND_NEW_SONG_NET_ERROR
            }
        }
    }
}