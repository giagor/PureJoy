package com.topview.purejoy.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


/**
 * 验证码输入框
 * @param parts 验证码位数，设计时按照6位验证码进行外观设计
 * @param state 整个控件的状态
 * @param modifier 整个大盒子的修饰
 * @param onFull 当控件满输入后触发
 */
@Composable
fun CaptchaField(
    parts: Int,
    state: CaptchaFieldState = remember { CaptchaFieldState() },
    modifier: Modifier = Modifier,
    onFull: (() -> Unit)? = null
) {
    Box(modifier = modifier) {
        val singleCaptchaModifier = Modifier.width(36.dp)
        val lineModifier = Modifier
            .width(36.dp)
            .height(1.dp)

        Row(modifier = Modifier.align(Alignment.Center)) {
            for (i in 0 until parts - 1) {
                SingleCaptchaText(
                    text = getStringOrEmpty(state.text, i),
                    modifier = singleCaptchaModifier,
                    lineModifier = lineModifier
                )
                Spacer(modifier = Modifier.weight(1F)
                    .align(Alignment.CenterVertically))
            }
            SingleCaptchaText(
                text = getStringOrEmpty(state.text, parts - 1),
                modifier = singleCaptchaModifier,
                lineModifier = lineModifier
            )
        }
        TextField(
            value = state.text,
            onValueChange = {
                if (it.length <= parts) {
                    state.text = it
                    if (it.length == parts) {
                        onFull?.invoke()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            colors = TextFieldDefaults.textFieldColors(
                cursorColor = Color.Unspecified,
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                unfocusedLabelColor = Color.Transparent,
                textColor = Color.Transparent
            )
        )
    }
}

@Composable
fun SingleCaptchaText(
    text: String,
    modifier: Modifier = Modifier,
    lineModifier: Modifier = Modifier
) {
    val emptyColor = Color.Black.copy(alpha = 0.2F)
    val lineColor: Color by animateColorAsState(
        targetValue = if (text.isEmpty()) emptyColor else Color.Black
    )
    Column(modifier = modifier) {
        Text(text = text,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 6.dp),
            fontWeight = FontWeight.Light,
            fontSize = 24.sp
        )
        Box(modifier = lineModifier
            .background(lineColor)
            .align(Alignment.CenterHorizontally)
        )
    }
}
class CaptchaFieldState {
    var text by mutableStateOf("")
}

private fun getStringOrEmpty(text: String, index: Int) =
    if (text.length > index) text[index].toString() else ""


@Preview(showBackground = true)
@Composable
private fun CaptchaFieldPreview() {
    Surface {
        CaptchaField(
            parts = 6,
            modifier = Modifier.padding(horizontal = 12.dp).fillMaxWidth(),
        )
    }
}