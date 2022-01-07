package com.topview.purejoy.video.ui.horizontal

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.topview.purejoy.video.ui.theme.Gray92

@Composable
internal fun HorizontalVideoSlider(

) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        var value by remember {
            mutableStateOf(0F)
        }
        Text(text = "00:00")
        Slider(
            value = value,
            onValueChange = {
                value = it
            },
            modifier = Modifier
                .height(20.dp)
                .weight(1F),
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Gray92
            )
        )
        Text(text = "03:55")
    }
}