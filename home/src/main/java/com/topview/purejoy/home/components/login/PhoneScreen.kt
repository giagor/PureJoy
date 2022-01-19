package com.topview.purejoy.home.components.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.topview.purejoy.home.R
import com.topview.purejoy.home.components.status.SnackBarState
import com.topview.purejoy.home.theme.Red243

@Composable
internal fun PhoneScreen(
    phoneUiState: PhoneUiState = remember { PhoneUiState() },
    onNextStepClick: () -> Unit = {},
    onClose: (() -> Unit)? = null,
    onTextChanged: (String) -> Unit = {}
) {
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            LoginScreenTitle(
                modifier = Modifier.padding(
                    start = 10.dp, end = 10.dp, top = 8.dp, bottom = 25.dp
                ),
                onClose = onClose
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = it,
            )
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            PhoneContentTitle(
                modifier = Modifier.padding(
                    horizontal = 15.dp, vertical = 8.dp
                )
            )
            PhoneNumberInput(
                modifier = Modifier.padding(
                    horizontal = 15.dp, vertical = 8.dp
                ),
                phoneUiState = phoneUiState,
                onButtonClick = onNextStepClick,
                onTextChanged = onTextChanged
            )
            if (phoneUiState.loading) {
                LoadingBox(
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }

    if (phoneUiState.snackBarState is SnackBarState.Show) {
        LaunchedEffect(phoneUiState.snackBarState) {
            scaffoldState.snackbarHostState.showSnackbar(
                (phoneUiState.snackBarState as SnackBarState.Show).message
            )
            phoneUiState.snackBarState = SnackBarState.None
        }
    }
}

@Composable
internal fun PhoneNumberInput(
    modifier: Modifier = Modifier,
    phoneUiState: PhoneUiState,
    onButtonClick: () -> Unit,
    onTextChanged: (String) -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        TextField(
            value = phoneUiState.text,
            onValueChange = onTextChanged,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth(),
            trailingIcon = {
                if (phoneUiState.text.isNotEmpty()) {
                    Icon(painter = painterResource(
                        id = R.drawable.home_ic_baseline_close_24
                    ),
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            // 设置为空串
                            onTextChanged("")
                        }
                    )
                }
            },
            placeholder = {
                Text(
                    stringResource(R.string.home_login_phone_input_hint),
                    fontWeight = FontWeight.Thin,
                    style = LocalTextStyle.current.copy(color = Color.Black.copy(alpha = 0.45F))
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            ),
            textStyle = LocalTextStyle.current.copy(fontSize = 17.sp),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
                focusedIndicatorColor = Color.Black,
                cursorColor = Color.Black
            )
        )
        Button(
            onClick = onButtonClick,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Red243,
                disabledBackgroundColor = Red243.copy(alpha = 0.6F)
            ),
            enabled = phoneUiState.buttonEnable,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 35.dp)
        ) {
            Text(text = stringResource(R.string.home_login_next_step), color = Color.White)
        }
    }
}

@Composable
internal fun PhoneContentTitle(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.home_login_main_title),
            fontSize = 17.sp
        )
        Text(
            text = stringResource(R.string.home_login_subtitle),
            modifier = Modifier.padding(top = 7.dp),
            fontSize = 13.sp,
            color = Color.Black.copy(alpha = 0.5F)
        )
    }
}

@Composable
internal fun LoginScreenTitle(
    modifier: Modifier = Modifier,
    onClose: (() -> Unit)? = null,
    trailingContent: @Composable RowScope.() -> Unit = {}
) {
    var iconModifier = Modifier.size(32.dp)
    if (onClose != null) {
        iconModifier = iconModifier.clickable { onClose() }
    }
    ScreenTitle(
        title = stringResource(R.string.home_login_phone_title),
        modifier = modifier,
        leadingContent = {
            Icon(
                painter = painterResource(id = R.drawable.home_ic_baseline_close_24),
                contentDescription = null,
                modifier = iconModifier
            )
        },
        trailingContent = trailingContent
    )
}

@Composable
internal fun LoadingBox(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .wrapContentSize()
    ) {
        CircularProgressIndicator(
            color = Color.Gray,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

class PhoneUiState {
    var text: String by mutableStateOf("")
    var buttonEnable by mutableStateOf(false)
    var loading by mutableStateOf(false)
    var snackBarState by mutableStateOf<SnackBarState>(SnackBarState.None)
}

@Composable
@Preview(showBackground = true)
private fun ContentPreview() {
    Surface(modifier = Modifier.fillMaxWidth()) {
        PhoneScreen()
    }
}
