package com.topview.purejoy.home.discover

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel
import com.topview.purejoy.home.data.Status
import com.topview.purejoy.home.data.repo.HomeRepository
import com.topview.purejoy.home.entity.HomeDiscoverBannerItem

class HomeDiscoverViewModel : MVVMViewModel() {
    private val repository = HomeRepository

    val bannerLiveData: MutableLiveData<List<HomeDiscoverBannerItem>> by lazy {
        MutableLiveData<List<HomeDiscoverBannerItem>>()
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
}