package com.topview.purejoy.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.topview.purejoy.home.R
import com.topview.purejoy.home.theme.Blue219

/**
 * 验证码输入屏幕
 * @param time 重新发送的倒计时
 */
@Composable
fun CaptchaScreen(
    modifier: Modifier = Modifier,
    time: Int,
    state: CaptchaScreenState = remember {
        CaptchaScreenState(CaptchaFieldState(), "") },
    onResendClick: () -> Unit = {},
    onChangePhoneClick: () -> Unit = {},
    onPasswordClick: () -> Unit = {},
    onClose: (() -> Unit)? = null,
    onCaptchaFull: (() ->Unit)? = null
) {
    val snackBarHostState = remember { SnackbarHostState() }
    Scaffold(
        modifier = modifier,
        topBar = {
            LoginScreenTitle(
                modifier = Modifier.padding(
                    start = 10.dp, end = 10.dp,
                    top = 8.dp, bottom = 25.dp),
                onClose = onClose
            ) {
                Spacer(modifier = Modifier.weight(1f))
                OutlinedButton(
                    onClick = onPasswordClick,
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(
                        vertical = 0.dp,
                        horizontal = 10.dp),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .height(27.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.home_login_password_login_option),
                        color = Color.Black,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Light,
                        letterSpacing = 0.sp
                    )
                }
            }
        }
    ) {
        Column(
            modifier = Modifier.padding(it),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                CaptchaContentTitle(
                    phone = state.phone,
                    onChangePhoneClick = onChangePhoneClick,
                    modifier = Modifier.padding(horizontal = 15.dp)
                )
                Spacer(modifier = Modifier.weight(1F))
                CountDownButton(
                    time = time,
                    onClick = onResendClick,
                    modifier = Modifier.padding(end = 15.dp)
                )
            }
            CaptchaField(
                parts = 4,
                state = state.captchaFieldState,
                modifier = Modifier.padding(horizontal = 15.dp,
                    vertical = 15.dp)
                    .width(240.dp),
                onFull = onCaptchaFull
            )
            SnackbarHost(
                hostState = snackBarHostState
            )
        }
    }
    if (state.snackBarState is SnackBarState.Show) {
        LaunchedEffect(state.snackBarState) {
            snackBarHostState.showSnackbar(
                (state.snackBarState as SnackBarState.Show).message)
            state.snackBarState = SnackBarState.None
        }
    }
}

@Composable
fun CaptchaContentTitle(
    phone: String,
    modifier: Modifier = Modifier,
    onChangePhoneClick: () -> Unit = {}
) {
    val topPadding = 7.dp

    val phoneText = "+86 " + if (phone.length < 11) "**********" else
        "${phone.substring(0, 3)}****${phone.substring(7)}"

    Column(modifier = modifier) {
        Text(text = stringResource(R.string.home_login_input_captcha_title),
            fontSize = 19.sp
        )
        Row {
            Text(
                text = stringResource(
                    R.string.home_login_input_captcha_subtitle) + phoneText,
                modifier = Modifier.padding(top = topPadding),
                fontSize = 15.sp,
                color = Color.Black.copy(alpha = 0.5F),
            )
            Icon(
                painter = painterResource(
                    id = R.drawable.ic_home_login_modify_phone
                ),
                contentDescription = null,
                modifier = Modifier
                    .padding(top = topPadding, start = 5.dp)
                    .size(14.dp)
                    .align(Alignment.CenterVertically)
                    .clickable { onChangePhoneClick() },
                tint = LocalContentColor.current.copy(alpha = 0.7F)
            )
        }
    }
}

@Composable
fun CountDownButton(
    modifier: Modifier = Modifier,
    time: Int,
    onClick: () -> Unit = {},
) {
    val clickableModifier: Modifier = Modifier.clickable {
        onClick()
    }
    Surface(modifier = modifier) {
        Text(
            text = if(time > 0) "${time}s" else stringResource(
                id = R.string.home_login_send_captcha_again),
            modifier = if (time > 0) Modifier else clickableModifier,
            color = if (time > 0) Color.Gray else Blue219,
            fontSize = 16.sp
        )
    }
}

class CaptchaScreenState(
    val captchaFieldState: CaptchaFieldState,
    val phone: String
) {
    var snackBarState: SnackBarState by mutableStateOf(SnackBarState.None)
}

@Preview(showBackground = true)
@Composable
private fun CaptchaScreenPreview() {
    val state = remember {
        CaptchaScreenState(
            CaptchaFieldState(),
            "12345678910"
        )
    }
    Surface(modifier = Modifier.fillMaxSize()) {
        CaptchaScreen(time = 59, state = state)
    }
}