package com.topview.purejoy.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.topview.purejoy.home.R
import com.topview.purejoy.home.theme.Red243
import com.topview.purejoy.home.theme.Visibility
import com.topview.purejoy.home.theme.VisibilityOff

@Composable
fun PasswordLoginScreen(
    onLoginClick: () -> Unit,
    state: PasswordLoginScreenState = remember {
        PasswordLoginScreenState()
    },
    onClose: (() -> Unit)? = null,
) {
    val snackBarHostState = remember { SnackbarHostState() }
    var buttonEnable by remember {
        mutableStateOf(false)
    }
    Scaffold(
        topBar = {
            LoginScreenTitle(
                modifier = Modifier.padding(
                    start = 10.dp,
                    end = 10.dp,
                    top = 8.dp,
                    bottom = 25.dp),
                onClose = onClose
            )
        }
    ) {
        Column(modifier = Modifier
            .padding(it)
            .padding(16.dp)) {
            PasswordField(
                value = state.password,
                onValueChange = { newValue ->
                    buttonEnable = newValue.isNotEmpty()
                    state.password = newValue
                },
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxWidth()
            )
            Button(
                onClick = onLoginClick,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Red243,
                    disabledBackgroundColor = Red243.copy(alpha = 0.6F)
                ),
                enabled = buttonEnable && ! state.loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp)
            ) {
                Text(text = "登录", color = Color.White)
            }
            if (state.loading) {
                LoadingBox(modifier = Modifier.padding(top = 20.dp)
                    .align(Alignment.CenterHorizontally))
            }
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
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
fun PasswordField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
) {
    var passwordVisible by remember {
        mutableStateOf(false)
    }
    TextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        visualTransformation = if (passwordVisible)
            VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val icon = if (passwordVisible)
                VisibilityOff else Visibility
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.clickable { passwordVisible = !passwordVisible }
            )
        },
        placeholder = {
            Text(
                stringResource(R.string.home_login_password_input_hint),
                fontWeight = FontWeight.Thin,
                style = LocalTextStyle.current.copy(color = Color.Black.copy(alpha = 0.45F)))
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Password
        ),
        textStyle = LocalTextStyle.current.copy(fontSize = 17.sp),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.White,
            focusedIndicatorColor = Color.Black,
            cursorColor = Color.Black
        ),
        modifier = modifier
    )
}

@Composable
@Preview(showBackground = true)
private fun PasswordLoginScreenPreview() {
    Surface(modifier = Modifier.fillMaxWidth()) {
        PasswordLoginScreen(onLoginClick = {})
    }
}

class PasswordLoginScreenState {
    var snackBarState: SnackBarState by mutableStateOf(SnackBarState.None)
    var password: String by mutableStateOf("")
    var loading: Boolean by mutableStateOf(false)
}