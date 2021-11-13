package com.topview.purejoy.home.discover

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel
import com.topview.purejoy.home.data.Status
import com.topview.purejoy.home.data.bean.BannerJson
import com.topview.purejoy.home.data.repo.HomeRepository

class HomeDiscoverViewModel : MVVMViewModel() {
    private val repository = HomeRepository

    val bannerLiveData: MutableLiveData<List<BannerJson.Banner>> by lazy {
        MutableLiveData<List<BannerJson.Banner>>()
    }

    fun getBanners() {
        viewModelScope.rxLaunch<BannerJson> {
            onRequest = {
                repository.getBanners()
            }

            onSuccess = {
                bannerLiveData.value = it.banners
            }
            
            onError = {
                status.value = Status.HOME_DISCOVER_BANNER_NET_ERROR
            }
        }
    }
}