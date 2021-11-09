package com.topview.purejoy.musiclibrary.service.url.viewmodel

import androidx.lifecycle.viewModelScope
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel
import com.topview.purejoy.common.net.await
import com.topview.purejoy.musiclibrary.data.Item
import com.topview.purejoy.musiclibrary.entity.MusicItem
import com.topview.purejoy.musiclibrary.player.abs.Loader
import com.topview.purejoy.musiclibrary.service.url.entity.URLItemWrapper
import com.topview.purejoy.musiclibrary.service.url.repository.MusicURLRepository
import com.topview.purejoy.musiclibrary.service.url.repository.MusicURLRepositoryImpl

class MusicURLViewModelImpl(private val repository: MusicURLRepository = MusicURLRepositoryImpl()) : MusicURLViewModel, MVVMViewModel() {
    override fun requestMusicURL(item: MusicItem, index: Int, callback: Loader.Callback<Item>) {
        viewModelScope.rxLaunch<URLItemWrapper> {
            onRequest = {
                repository.requestMusicURL(item.id.toString()).await()
            }
            onSuccess = {
                for (d in it.data) {
                    if (d.id == item.id) {
                        item.url = d.url
                    }
                }
                callback.callback(index, item)
            }
            onError = {
                callback.callback(index, item)
            }
            onEmpty = {
                callback.callback(index, item)
            }

        }
    }


}