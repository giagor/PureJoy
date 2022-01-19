package com.topview.purejoy.home.components.toplist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.topview.purejoy.home.R
import com.topview.purejoy.home.components.login.ScreenTitle
import com.topview.purejoy.home.components.status.PageState
import com.topview.purejoy.home.components.status.SimpleStatusScreen
import com.topview.purejoy.home.entity.TopList
import com.topview.purejoy.home.entity.TopListTab
import com.topview.purejoy.home.theme.Gray245

@Composable
internal fun TopListScreen(
    state: PageState,
    topListMap: Map<TopListTab, List<TopList>>?,
    onRetryClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopListScreenTitle(
                modifier = Modifier
                    .padding()
                    .fillMaxWidth()
                    .background(Gray245)
                    .padding(
                        rememberInsetsPaddingValues(
                            insets = LocalWindowInsets.current.systemGestures,
                            additionalStart = 15.dp,
                            additionalTop = 5.dp,
                            additionalBottom = 10.dp
                        )
                    ),
                onBackClick = onBackClick
            )
        },
    ) {
        SimpleStatusScreen(
            state = state,
            errorContent = {
                TopListErrorContent(
                    onRetryClick,
                    Gray245
                )
            },
            loadingContent = {
                TopListLoadingContent(Gray245)
            }
        ) {
            if (topListMap != null) {
                TopListContent(
                    topListMap = topListMap,
                )
            }
        }
    }

}

@Composable
internal fun TopListErrorContent(
    onRetryClick: () -> Unit,
    backgroundColor: Color = Color.White
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onRetryClick
            )
    ) {
        Image(painter = painterResource(id = R.drawable.home_ic_toplist_error),
            contentDescription = null,
            modifier = Modifier.size(100.dp)
        )
        Text(
            text = stringResource(id = R.string.home_toplist_error_tip),
            modifier = Modifier.padding(top = 15.dp)
        )
    }
}

@Composable
internal fun TopListLoadingContent(
    backgroundColor: Color = Color.White
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        LinearProgressIndicator()
        Text(
            text = stringResource(id = R.string.home_toplist_loading_tip),
            modifier = Modifier.padding(top = 25.dp)
        )
    }
}

@Composable
internal fun TopListScreenTitle(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    ScreenTitle(
        title = stringResource(id = R.string.home_toplist_title),
        leadingContent = {
            Icon(
                painter = painterResource(id = R.drawable.home_ic_arrow_back_24),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 10.dp)
                    .clickable { onBackClick() }
            )
        },
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun TopListScreenPreview() {
    Surface {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            TopListLoadingContent()
        }
    }
}