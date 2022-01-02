package com.topview.purejoy.home.tasks.toplist

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import com.google.accompanist.insets.ProvideWindowInsets
import com.topview.purejoy.common.base.ComposeActivity
import com.topview.purejoy.common.util.StatusBarUtil.setStatusBarBackgroundColor
import com.topview.purejoy.common.util.StatusBarUtil.setStatusBarTextColor
import com.topview.purejoy.home.components.status.PageState
import com.topview.purejoy.home.components.toplist.TopListScreen
import com.topview.purejoy.home.theme.Gray245

class TopListActivity: ComposeActivity() {

    private val viewModel by viewModels<TopListViewModel>()

    override fun setStatusBarStyle() {
        window.setStatusBarBackgroundColor(Gray245.toArgb())
            .setStatusBarTextColor(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 要求ViewModel开始加载数据
        viewModel.loadTopListData()
        setContent {
            ProvideWindowInsets(consumeWindowInsets = false) {
                val uiState by viewModel.pageState.collectAsState()
                val data by viewModel.topListData.collectAsState()
                val loadedUrl by viewModel.loadedCoverUrl.collectAsState()

                if (uiState is PageState.Success && !loadedUrl) {
                    viewModel.loadCovers()
                }
                TopListScreen(
                    state = uiState,
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