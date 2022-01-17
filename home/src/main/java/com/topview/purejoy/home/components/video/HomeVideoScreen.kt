package com.topview.purejoy.home.components.video

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.topview.purejoy.home.R
import com.topview.purejoy.home.components.status.PageState
import com.topview.purejoy.home.components.status.SimpleStatusScreen
import com.topview.purejoy.home.components.toplist.TabIndicator
import com.topview.purejoy.home.entity.ExternVideo
import com.topview.purejoy.home.entity.VideoCategoryTab
import com.topview.purejoy.home.tasks.video.HomeVideoViewModel
import com.topview.purejoy.home.theme.Gray245
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
internal fun HomeVideoScreen(
    viewModel: HomeVideoViewModel
) {
    val screenState by viewModel.screenState.collectAsState()

    LaunchedEffect(true) {
        viewModel.loadVideoCategory()
    }

    SimpleStatusScreen(
        state = screenState.pagerState,
        loadingContent = {
            CircularProgressIndicator(
                color = Color.Gray,
            )
        },
        errorContent = {
            Box(
                modifier = Modifier.clickable { viewModel.loadVideoCategory() }
            ) {
                Text(
                    text = stringResource(id = R.string.home_video_unknown_error),
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.Center),
                    color = LocalContentColor.current.copy(alpha = 0.7F)
                )
            }

        },
    ) {
        HomeVideoScreenContent(
            tabArray = screenState.tabArray,
            viewModel = viewModel
        )
    }

}


@ExperimentalPagerApi
@Composable
private fun HomeVideoScreenContent(
    tabArray: Array<VideoCategoryTab>,
    viewModel: HomeVideoViewModel
) {
    val pagerState: PagerState = rememberPagerState()

    val lazyPagingMap: MutableMap<Long, LazyPagingItems<ExternVideo>> = remember {
        mutableMapOf()
    }

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TabIndicator(
                selectedTabIndex = pagerState.currentPage,
                tabArray = tabArray,
                onTabClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(it)
                    }
                },
                modifier = Modifier.padding(vertical = 10.dp),
            )
        }
    ) {
        HorizontalPager(
            count = tabArray.size,
            state = pagerState
        ) { page ->
            // TODO 刷新功能
            // TODO 指示器动画
            val items = lazyPagingMap[tabArray[page].id] ?: let {
                val newItems = viewModel.getVideoByCategory(
                    tabArray[page].id).collectAsLazyPagingItems()
                lazyPagingMap[tabArray[page].id] = newItems
                newItems
            }

            val state = rememberSwipeRefreshState(isRefreshing = false)

            /*LaunchedEffect(key1 = items.loadState.refresh) {
                state.isRefreshing = items.loadState.refresh is LoadState.Loading
            }*/

            Surface(
               color = Gray245
            ) {
                VideoInfoList(
                    videoItems = items,
                    state = state,
                    onRefresh = {},
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

class HomeVideoScreenState(
    val pagerState: PageState,
    val tabArray: Array<VideoCategoryTab>
)