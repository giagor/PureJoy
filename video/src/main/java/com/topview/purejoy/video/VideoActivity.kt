package com.topview.purejoy.video

import android.graphics.Color
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.*
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.insets.ProvideWindowInsets
import com.topview.purejoy.common.base.ComposeActivity
import com.topview.purejoy.common.util.StatusBarUtil.setAutoFitSystemWindows
import com.topview.purejoy.common.util.StatusBarUtil.setStatusBarBackgroundColor
import com.topview.purejoy.common.util.StatusBarUtil.setStatusBarTextColor
import com.topview.purejoy.video.ui.LocalExoPlayer
import com.topview.purejoy.video.ui.horizontal.HorizontalVideoScreen
import com.topview.purejoy.video.ui.state.VideoLoadState
import com.topview.purejoy.video.util.RouterParamsConstant

class VideoActivity: ComposeActivity() {

    private val viewModel by viewModels<VideoViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    if (modelClass == VideoViewModel::class.java) {
                        return VideoViewModel(
                            intent.extras?.getParcelableArrayList(
                                RouterParamsConstant.VIDEO_ARRAYLIST)!!
                        ) as T
                    } else {
                        throw IllegalArgumentException("Illegal ViewModel Class")
                    }
                }
            }
        }
    )

    override fun setStatusBarStyle() {
        window.setAutoFitSystemWindows(false)
            .setStatusBarBackgroundColor(Color.TRANSPARENT)
            .setStatusBarTextColor(false)
        window.navigationBarColor = Color.BLACK
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProvideWindowInsets(consumeWindowInsets = false) {
                val videoItem = viewModel.getPagingVideoFlow().collectAsLazyPagingItems()
                val videoLoadState by viewModel.videoLoadState.collectAsState()

                // 注册生命周期回调
                LocalLifecycleOwner.current.lifecycle.addObserver(object : LifecycleObserver {
                    @OnLifecycleEvent(Lifecycle.Event.ON_START)
                    fun resumeVideo() {
                        viewModel.play()
                    }

                    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
                    fun pauseVideo() {
                        viewModel.pause()
                    }
                })

                CompositionLocalProvider(LocalExoPlayer provides viewModel.exoPlayer) {
                    HorizontalVideoScreen(
                        items = videoItem,
                        videoLoadState = videoLoadState,
                        onPageChange = {
                            viewModel.onPageChange(it)
                        },
                        onVideoSurfaceClick = {
                            if (videoLoadState is VideoLoadState.Playing) {
                                viewModel.pause()
                            } else if (videoLoadState is VideoLoadState.Pause) {
                                viewModel.play()
                            }
                        },
                        onRetryClick = viewModel::reloadVideo,
                        onBackClick = {
                            finish()
                        },
                    )
                }
            }
        }
    }
}