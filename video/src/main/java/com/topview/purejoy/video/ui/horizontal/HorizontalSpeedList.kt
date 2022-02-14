package com.topview.purejoy.video.ui.horizontal

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.topview.purejoy.video.ui.state.HorizontalVideoScreenState
import com.topview.purejoy.video.ui.state.PlaySpeed
import com.topview.purejoy.video.ui.theme.HalfAlphaBlack
import com.topview.purejoy.video.ui.theme.Pink

/**
 * 倍速选择列表
 */
@Composable
internal fun HorizontalSpeedList(
    modifier: Modifier = Modifier,
    state: HorizontalVideoScreenState,
    onItemClick: (PlaySpeed) -> Unit,
) {
    AnimatedVisibility(
        visible = state.isSpeedListShowing,
        enter = expandHorizontally(),
        exit = shrinkHorizontally()
    ) {
        Surface(
            modifier = modifier,
            color = HalfAlphaBlack
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(horizontal = 15.dp)
            ) {
                LazyColumn {
                    val itemModifier = Modifier
                        .padding(vertical = 15.dp, horizontal = 30.dp)
                    items(PlaySpeed.values()) {
                        if (it == state.playbackSpeed) {
                            Text(
                                text = "${it}X",
                                color = Pink,
                                fontSize = 17.sp,
                                modifier = itemModifier
                            )
                        } else {
                            Text(
                                text = "${it}X",
                                color = Color.White,
                                fontSize = 17.sp,
                                modifier = itemModifier
                                    .clickable {
                                        onItemClick(it)
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}