package com.topview.purejoy.home.discover

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel
import com.topview.purejoy.home.data.Status
import com.topview.purejoy.home.data.repo.HomeRepository
import com.topview.purejoy.home.entity.HomeDiscoverBannerItem
import com.topview.purejoy.home.entity.PlayList
import com.topview.purejoy.home.entity.Song

class HomeDiscoverViewModel : MVVMViewModel() {
    private val repository = HomeRepository

    private val _bannerLiveData: MutableLiveData<List<HomeDiscoverBannerItem>> by lazy {
        MutableLiveData<List<HomeDiscoverBannerItem>>()
    }
    val bannerLiveData: MutableLiveData<List<HomeDiscoverBannerItem>> = _bannerLiveData


    private val _dailyRecommendPlayListLiveData: MutableLiveData<List<PlayList>> by lazy {
        MutableLiveData<List<PlayList>>()
    }
    val dailyRecommendPlayListLiveData: LiveData<List<PlayList>> = _dailyRecommendPlayListLiveData


    private val _recommendNewSongLiveData: MutableLiveData<List<Song>> by lazy {
        MutableLiveData<List<Song>>()
    }
    val recommendNewSongLiveData: LiveData<List<Song>> = _recommendNewSongLiveData

    fun getBanners() {
        viewModelScope.rxLaunch<List<HomeDiscoverBannerItem>> {
            onRequest = {
                repository.getBanners()
            }

            onSuccess = {
                _bannerLiveData.value = it
            }

            onError = {
                status.value = Status.DISCOVER_BANNER_NET_ERROR
            }
        }
    }

    fun getDailyRecommendPlayList(limit: Int = 6) {
        viewModelScope.rxLaunch<List<PlayList>> {
            onRequest = {
                repository.getDailyRecommendPlayList(limit)
            }

            onSuccess = {
                _dailyRecommendPlayListLiveData.value = it
            }

            onError = {
                status.value = Status.DISCOVER_DAILY_RECOMMEND_PLAYLIST_NET_ERROR
            }
        }
    }

    fun getRecommendNewSong(limit: Int = 12) {
        viewModelScope.rxLaunch<List<Song>> {
            onRequest = {
                repository.getRecommendNewSong(limit)
            }

            onSuccess = {
                _recommendNewSongLiveData.value = it
            }

            onError = {
                status.value = Status.DISCOVER_RECOMMEND_NEW_SONG_NET_ERROR
            }
        }
    }
}