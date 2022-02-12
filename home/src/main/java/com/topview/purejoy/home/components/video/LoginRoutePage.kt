package com.topview.purejoy.home.components.video

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.topview.purejoy.home.R
import com.topview.purejoy.home.router.HomeRouter

@Composable
internal fun LoginRoutePage() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .clickable(
                    indication = null,
                    interactionSource = remember {
                        MutableInteractionSource()
                    },
                    onClick = {
                        HomeRouter.routeToLoginActivity()
                    }
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.home_video_route_to_login),
                modifier = Modifier.padding(horizontal = 30.dp, vertical = 15.dp),
                fontSize = 16.sp
            )
            Icon(
                painter = painterResource(id = R.drawable.home_ic_video_to_login_sign),
                contentDescription = null,
                modifier = Modifier.size(50.dp)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun RoutePreview() {
    Surface {
        LoginRoutePage()
    }
}