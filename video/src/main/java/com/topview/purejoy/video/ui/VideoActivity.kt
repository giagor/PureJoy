package com.topview.purejoy.video.ui

import android.graphics.Color
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.paging.compose.collectAsLazyPagingItems
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.accompanist.insets.ProvideWindowInsets
import com.topview.purejoy.common.base.ComposeActivity
import com.topview.purejoy.common.util.StatusBarUtil.setAutoFitSystemWindows
import com.topview.purejoy.common.util.StatusBarUtil.setNavigationBarBackgroundColor
import com.topview.purejoy.common.util.StatusBarUtil.setStatusBarBackgroundColor
import com.topview.purejoy.common.util.StatusBarUtil.setStatusBarTextColor
import com.topview.purejoy.video.router.VideoRouter
import com.topview.purejoy.video.ui.state.VideoLoadState
import com.topview.purejoy.video.videoConfiguration

@Route(path = VideoRouter.ACTIVITY_VIDEO)
class VideoActivity: ComposeActivity() {

    private val viewModel by viewModels<VideoViewModel>()

    override fun setStatusBarStyle() {
        window.setAutoFitSystemWindows(false)
            .setStatusBarBackgroundColor(Color.TRANSPARENT)
            .setStatusBarTextColor(false)
            .setNavigationBarBackgroundColor(Color.TRANSPARENT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProvideWindowInsets(consumeWindowInsets = false) {
                val videoItem = viewModel.getPagingVideoFlow().collectAsLazyPagingItems()
                val videoLoadState by viewModel.videoLoadState.collectAsState()

                // 注册生命周期回调
                LocalLifecycleOwner.current.lifecycle.addObserver(object :
                    DefaultLifecycleObserver {
                    override fun onResume(owner: LifecycleOwner) {
                        viewModel.play()
                    }
                    override fun onPause(owner: LifecycleOwner) {
                        viewModel.pause()
                    }
                })

                CompositionLocalProvider(
                    LocalExoPlayer provides viewModel.exoPlayer
                ) {
                    VideoScreen(
                        items = videoItem,
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
                            onBackPressed()
                        },
                    )
                }
            }
        }
        registerLifecycleObserver()
    }

    private fun registerLifecycleObserver() {
        this.lifecycle.addObserver(object : DefaultLifecycleObserver {

            override fun onDestroy(owner: LifecycleOwner) {
                videoConfiguration.onCloseListener?.invoke()
                videoConfiguration.onCloseListener = null
                viewModel.exoPlayer.release()
            }
        })
    }
}