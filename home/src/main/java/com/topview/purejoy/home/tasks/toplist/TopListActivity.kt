package com.topview.purejoy.home.tasks.toplist

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.accompanist.insets.ProvideWindowInsets
import com.topview.purejoy.common.base.ComposeActivity
import com.topview.purejoy.common.util.StatusBarUtil.setAutoFitSystemWindows
import com.topview.purejoy.common.util.StatusBarUtil.setStatusBarBackground
import com.topview.purejoy.common.util.StatusBarUtil.setStatusBarTextColor
import com.topview.purejoy.common.util.pxToDp
import com.topview.purejoy.home.components.status.PageState
import com.topview.purejoy.home.components.toplist.TopListScreen
import com.topview.purejoy.home.router.HomeRouter
import com.topview.purejoy.home.theme.Gray245

@Route(path = HomeRouter.ACTIVITY_HOME_TOPLIST)
class TopListActivity : ComposeActivity() {

    private val viewModel by viewModels<TopListViewModel>()

    private var statusBarHeight by mutableStateOf(20)

    override fun setStatusBarStyle() {
        window.setAutoFitSystemWindows(false)
            .setStatusBarBackground(Gray245.toArgb())
            .setStatusBarTextColor(true)
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { v, insets ->
            val systemBar = insets.getInsets(
                WindowInsetsCompat.Type.statusBars() or
                        WindowInsetsCompat.Type.navigationBars()
            )
            v.updatePadding(top = 0, bottom = systemBar.bottom)
            statusBarHeight = pxToDp(systemBar.top)
            WindowInsetsCompat.CONSUMED
        }
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
                    statusBarPadding = statusBarHeight,
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