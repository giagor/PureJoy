package com.topview.purejoy.home.mine

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import com.bumptech.glide.Glide
import com.topview.purejoy.common.app.CommonApplication
import com.topview.purejoy.common.entity.User
import com.topview.purejoy.common.music.service.MusicService
import com.topview.purejoy.common.music.util.ExecutorInstance
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel
import com.topview.purejoy.common.util.UserManager
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

    @ExperimentalCoilApi
    fun clearCache(context: Context, callback: (Long) -> Unit) {
        ExecutorInstance.getInstance().execute {
            var size = ExecutorInstance.getInstance().submit {
                com.topview.purejoy.common.util.clear(
                    File(
                        context.cacheDir,
                        MusicService.MUSIC_CACHE_DIR
                    )
                ) { f ->
                    false
                }
            }.get()
            val gf = Glide.getPhotoCacheDir(context)
            gf?.let {
                size += ExecutorInstance.getInstance().submit {
                    com.topview.purejoy.common.util.clear(it) { f ->
                        false
                    }
                }.get()
            }
            val cf = ImageLoader(context).diskCache
            cf?.let {
                size += ExecutorInstance.getInstance().submit {
                    com.topview.purejoy.common.util.clear(it.directory) { f ->
                        false
                    }
                }.get()
            }
            callback.invoke(size)
        }
    }
}