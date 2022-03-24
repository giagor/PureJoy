package com.topview.purejoy.common.music.service.url.viewmodel

import androidx.lifecycle.viewModelScope
import com.topview.purejoy.common.music.data.Item
import com.topview.purejoy.common.music.player.abs.Loader
import com.topview.purejoy.common.music.service.entity.MusicItem
import com.topview.purejoy.common.music.service.url.entity.URLItemWrapper
import com.topview.purejoy.common.music.service.url.repository.MusicURLRepository
import com.topview.purejoy.common.music.service.url.repository.MusicURLRepositoryImpl
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel
import com.topview.purejoy.common.net.awaitAsync
import kotlinx.coroutines.launch

class MusicURLViewModelImpl(private val repository: MusicURLRepository = MusicURLRepositoryImpl()) : MusicURLViewModel, MVVMViewModel() {
    override fun requestMusicURLSync(item: MusicItem, index: Int): MusicItem {

        kotlin.runCatching {
            val data = repository.requestMusicURL(item.id.toString()).execute().body()?.data

            data?.forEach {
                if (item.id == it.id) {
                    item.url = it.url
                }
            }
        }

        return item

//        viewModelScope.rxLaunch<URLItemWrapper> {
//            onRequest = {
//                repository.requestMusicURL(item.id.toString()).awaitAsync()
//            }
//            onSuccess = {
//                for (d in it.data) {
//                    if (d.id == item.id) {
//                        item.url = d.url
//                    }
//                }
//            }
//        }
    }


}