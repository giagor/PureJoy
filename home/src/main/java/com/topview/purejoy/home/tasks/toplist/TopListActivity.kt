package com.topview.purejoy.home.tasks.toplist

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.accompanist.insets.ProvideWindowInsets
import com.topview.purejoy.common.base.ComposeActivity
import com.topview.purejoy.common.music.view.bottom.MusicBottomView
import com.topview.purejoy.common.util.StatusBarUtil.setStatusBarBackgroundColor
import com.topview.purejoy.common.util.StatusBarUtil.setStatusBarTextColor
import com.topview.purejoy.home.components.status.PageState
import com.topview.purejoy.home.components.toplist.TopListScreen
import com.topview.purejoy.home.router.HomeRouter
import com.topview.purejoy.home.theme.Gray245
import com.topview.purejoy.home.util.ProvideMusicController
import com.topview.purejoy.home.util.produceViewInsetSize

@Route(path = HomeRouter.ACTIVITY_HOME_TOPLIST)
class TopListActivity : ComposeActivity() {

    private val viewModel by viewModels<TopListViewModel>()

    private val bar: MusicBottomView by lazy {
        MusicBottomView(this)
    }

    override fun setStatusBarStyle() {
        window.setStatusBarBackgroundColor(Gray245.toArgb())
            .setStatusBarTextColor(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 要求ViewModel开始加载数据
        viewModel.loadTopListData()
        // 添加底部状态控制栏
        bar.addMusicBottomBar()
        setContent {
            ProvideWindowInsets(consumeWindowInsets = false) {
                ProvideMusicController {
                    val uiState by viewModel.screenState.collectAsState()
                    val data by viewModel.topListData.collectAsState()
                    val loadedUrl by viewModel.loadedCoverUrl.collectAsState()
                    val musicLoadState by viewModel.loadState.collectAsState()

                    val bottomViewSize by produceViewInsetSize(view = bar.bar
                        ?: bar.addMusicBottomBar()
                    )

                    LaunchedEffect(uiState) {
                        if (uiState is PageState.Success && !loadedUrl) {
                            viewModel.loadCovers()
                        }
                    }

                    TopListScreen(
                        modifier = Modifier.padding(bottom = bottomViewSize.height.dp),
                        state = uiState,
                        musicLoadState = musicLoadState,
                        topListMap = data,
                        onRetryClick = { viewModel.loadTopListData() },
                        onBackClick = {
                            finish()
                        }
                    )
                }
            }
        }
    }
}