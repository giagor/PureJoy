package com.topview.purejoy.video.ui.horizontal

import android.app.KeyguardManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.exoplayer2.ui.PlayerView
import com.topview.purejoy.common.entity.Video
import com.topview.purejoy.common.util.InsetsController
import com.topview.purejoy.common.util.rememberInsetsController
import com.topview.purejoy.video.ui.LocalExoPlayer
import com.topview.purejoy.video.ui.VideoViewModel
import com.topview.purejoy.video.ui.state.BottomSliderState
import com.topview.purejoy.video.ui.state.HorizontalVideoScreenState
import com.topview.purejoy.video.ui.state.VideoLoadState
import com.topview.purejoy.video.util.setOrientation
import kotlinx.coroutines.delay

@Composable
internal fun HorizontalVideoScreen(
    video: Video?,
    sliderState: BottomSliderState,
    state: HorizontalVideoScreenState = remember { HorizontalVideoScreenState() },
    videoViewModel: VideoViewModel = viewModel(),
    onBackClick: () -> Unit,
    onRetryClick: (Video?) -> Unit,
) {
    val context = LocalContext.current
    val exoPlayer = LocalExoPlayer.current
    val insetsController = rememberInsetsController()
    val loadState by videoViewModel.videoLoadState.collectAsState()
    val videoMutableState by remember {
        mutableStateOf(video)
    }

    // ????????????????????????????????????????????????????????????????????????????????????
    val keyguardManager = LocalContext.current.getSystemService(
        Context.KEYGUARD_SERVICE) as KeyguardManager
    LaunchedEffect(keyguardManager.isKeyguardLocked) {
        if (!keyguardManager.isKeyguardLocked) {
            dismissSystemInsets(insetsController)
        }
    }

    // ????????????????????????????????????????????????????????????
    DisposableEffect(Unit) {
        dismissSystemInsets(insetsController)
        onDispose {
            insetsController.isSystemBarsVisible = true
            insetsController.setSystemBarBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_TOUCH
            )
        }
    }

    // ?????????????????????????????????
    LaunchedEffect(!sliderState.dragging && state.isControlShowing) {
        delay(CONTROL_DISMISS_TIME)
        if (!sliderState.dragging && state.isControlShowing) {
            state.isControlShowing = false
        }
    }

    // ???????????????????????????
    LaunchedEffect(state.playbackSpeed) {
        exoPlayer.setPlaybackSpeed(state.playbackSpeed.speed)
    }

    // ??????????????????????????????
    BackHandler {
        when {
            state.isLocked -> {
                state.isLocked = false
            }
            state.isSpeedListShowing -> {
                state.isSpeedListShowing = false
            }
            else -> {
                context.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            }
        }
    }

    Surface(
        color = Color.Black
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxSize()
        ) {
            val (content, speedList) = createRefs()
            AndroidView(
                factory = {
                    PlayerView(context).apply {
                        useController = false
                        player = exoPlayer
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .constrainAs(content) {
                        centerTo(parent)
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                if (!state.isLocked) {
                                    // ????????????/??????
                                    if (loadState is VideoLoadState.Playing) {
                                        videoViewModel.pause()
                                    } else {
                                        videoViewModel.play()
                                    }
                                }
                            },
                            onTap = {
                                if (state.isSpeedListShowing) {
                                    state.isSpeedListShowing = false
                                } else {
                                    // ????????????/??????????????????
                                    state.isControlShowing = !state.isControlShowing
                                }
                            }
                        )
                    }
            )
            // ????????????
            HorizontalSpeedList(
                state = state,
                onItemClick = {
                    // ???????????????????????????????????????
                    state.playbackSpeed = it
                    state.isSpeedListShowing = false
                },
                modifier = Modifier
                    .constrainAs(speedList) {
                        centerVerticallyTo(parent)
                    }
                    .fillMaxHeight()
            )
            if (state.isControlShowing) {
                val (rightLock, title, bottomBanner, stateOfVideoLoad) = createRefs()
                // ????????????????????????????????????????????????????????????BiliBili?????????
                if (state.isLocked) {
                    val leftLock = createRef()
                    HorizontalLockIcon(
                        state = state,
                        modifier = Modifier
                            .clickable {
                                state.isLocked = !state.isLocked
                            }
                            .constrainAs(leftLock) {
                                centerVerticallyTo(parent)
                                start.linkTo(parent.start, 10.dp)
                            }
                    )

                } else {
                    // ??????
                    HorizontalTopBanner(
                        video = videoMutableState,
                        onBackClick = onBackClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .constrainAs(title) {
                                top.linkTo(parent.top, 15.dp)
                                start.linkTo(parent.start)
                            }
                    )
                    // ?????????
                    HorizontalBottomBanner(
                        modifier = Modifier
                            .constrainAs(bottomBanner) {
                                bottom.linkTo(parent.bottom, 15.dp)
                                start.linkTo(parent.start, 10.dp)
                                end.linkTo(parent.end, 10.dp)
                            }
                            .fillMaxWidth(),
                        screenState = state,
                        sliderState = sliderState,
                        video = videoMutableState
                    )
                    // ????????????????????????
                    HorizontalVideoStateComponents(
                        modifier = Modifier
                            .constrainAs(stateOfVideoLoad) {
                                centerTo(parent)
                            },
                        loadState = loadState,
                        video = videoMutableState,
                        onRetryClick = onRetryClick,
                        onPlayIconClick = {
                            videoViewModel.play()
                        },
                        onPauseIconClick = {
                            videoViewModel.pause()
                        }
                    )
                }
                // ???????????????Lock????????????Lock?????????????????????
                HorizontalLockIcon(
                    state = state,
                    modifier = Modifier
                        .clickable {
                            state.isLocked = !state.isLocked
                        }
                        .constrainAs(rightLock) {
                            centerVerticallyTo(parent)
                            end.linkTo(parent.end, 10.dp)
                        }
                )
            }
            // ???????????????Loading???????????????????????????
            if (loadState is VideoLoadState.Loading) {
                val loading = createRef()
                HorizontalLoadingComponents(
                    modifier = Modifier.constrainAs(loading) {
                        centerTo(parent)
                    }
                )
            }
        }
    }

}


private fun dismissSystemInsets(insetsController: InsetsController) {
    insetsController.isSystemBarsVisible = false
    insetsController.setSystemBarBehavior(
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    )
}

/**
 * ??????????????????
 */
private const val CONTROL_DISMISS_TIME: Long = 5000L