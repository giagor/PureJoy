package com.topview.purejoy.video.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import com.google.android.exoplayer2.ExoPlayer

@Composable
internal fun VideoScreen() {
}


internal val LocalExoPlayer = compositionLocalOf<ExoPlayer> {
    error("CompositionLocal LocalExoPlayer not present")
}