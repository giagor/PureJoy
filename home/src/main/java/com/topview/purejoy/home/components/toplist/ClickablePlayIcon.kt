package com.topview.purejoy.home.components.toplist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.topview.purejoy.common.music.service.entity.wrap
import com.topview.purejoy.home.R
import com.topview.purejoy.home.components.status.PageState
import com.topview.purejoy.home.entity.TopList
import com.topview.purejoy.home.tasks.toplist.TopListViewModel
import com.topview.purejoy.home.util.LocalMusicController

/**
 * 可点击的播放按钮，点击后监听播放状态并更新控件状态
 */
@Composable
internal fun ClickablePlayIcon(
    modifier: Modifier = Modifier,
    topList: TopList,
    viewModel: TopListViewModel = viewModel()
) {
    // 每个控件的状态，这些状态不能单纯依赖于ViewModel，因为我们需要把状态分开
    var pageState: PageState by remember {
        mutableStateOf(PageState.Empty)
    }
    val loadState by viewModel.loadState.collectAsState()
    val controller = LocalMusicController.current
    val playState = controller.playState.observeAsState()

    LaunchedEffect(loadState, playState) {
        if (pageState is PageState.Loading && loadState.data != null) {
            loadState.data?.let {
                controller.dataController?.apply {
                    clear()
                    addAll(it.map { it.wrap() })
                }
                controller.playerController?.apply {
                    jumpTo(0)
                }
            }
            pageState = PageState.Success
        } else if (pageState is PageState.Success && playState.value == false) {
            // 遗忘曾经点击过的状态
            pageState = PageState.Empty
        } else if (pageState is PageState.Loading && loadState.pageState is PageState.Error) {
            pageState = PageState.Error
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when(pageState) {
            is PageState.Empty, is PageState.Error -> {
                Icon(
                    painter = painterResource(id = R.drawable.home_ic_toplist_music_play),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = {
                                controller.playerController?.apply {
                                    if (isPlaying) {
                                        playOrPause()
                                    }
                                }
                                pageState = PageState.Loading
                                viewModel.loadSongsByTopList(topList)
                            },
                            indication = null
                        )
                )
            }
            is PageState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Gray
                )
            }
            is PageState.Success -> {
                Icon(
                    painter = painterResource(id = R.drawable.home_ic_toplist_music_pause),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
