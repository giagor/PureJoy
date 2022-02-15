package com.topview.purejoy.home.mine

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import coil.annotation.ExperimentalCoilApi
import coil.imageLoader
import com.bumptech.glide.Glide
import com.topview.purejoy.common.app.CommonApplication
import com.topview.purejoy.common.entity.User
import com.topview.purejoy.common.music.service.MusicService
import com.topview.purejoy.common.music.util.ExecutorInstance
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel
import com.topview.purejoy.common.util.UserManager
import com.topview.purejoy.common.util.clearWithFilter
import com.topview.purejoy.common.util.getSubSize
import com.topview.purejoy.common.util.showToast
import com.topview.purejoy.home.data.repo.LoginRepository
import java.io.File

class HomeMineViewModel : MVVMViewModel() {
    val userLiveData: LiveData<User?> = UserManager.userLiveData

    fun logout() {
        if (userLiveData.value == null) {
            return
        }

        viewModelScope.rxLaunch<Unit> {
            onRequest = {
                LoginRepository.logout()
            }

            onSuccess = {
                UserManager.logout()
            }

            onError = {
                showToast(CommonApplication.getContext(), "退出登陆失败")
            }
        }
    }


    @OptIn(ExperimentalCoilApi::class)
    fun clearCache(context: Context, callback: (Long) -> Unit) {
        ExecutorInstance.getInstance().execute {
            var size = ExecutorInstance.getInstance().submit {
                File(
                    context.cacheDir,
                    MusicService.MUSIC_CACHE_DIR
                ).clearWithFilter { true }
            }.get()
            val gf = Glide.getPhotoCacheDir(context)
            gf?.let {
                size += ExecutorInstance.getInstance().submit {
                    it.clearWithFilter { true }
                }.get()
            }
            size += context.imageLoader.diskCache?.let {
                ExecutorInstance.getInstance().submit {
                    // can also use File.clearSubFile()
                    val i = it.directory.getSubSize()
                    it.clear()
                    i
                }.get()
            } ?: 0L
            callback.invoke(size)
        }
    }
}