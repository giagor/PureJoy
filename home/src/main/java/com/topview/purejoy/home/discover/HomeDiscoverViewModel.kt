package com.topview.purejoy.home.discover

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel
import com.topview.purejoy.home.data.Status
import com.topview.purejoy.home.data.repo.HomeRepository
import com.topview.purejoy.home.entity.DailyRecommendPlayList
import com.topview.purejoy.home.entity.HomeDiscoverBannerItem

class HomeDiscoverViewModel : MVVMViewModel() {
    private val repository = HomeRepository

    val bannerLiveData: MutableLiveData<List<HomeDiscoverBannerItem>> by lazy {
        MutableLiveData<List<HomeDiscoverBannerItem>>()
    }

    val dailyRecommendPlayListLiveData: MutableLiveData<List<DailyRecommendPlayList>> by lazy {
        MutableLiveData<List<DailyRecommendPlayList>>()
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
                status.value = Status.HOME_DISCOVER_BANNER_NET_ERROR
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
                status.value = Status.HOME_DISCOVER_DAILY_RECOMMEND_PLAYLIST_NET_ERROR
            }
        }
    }
}