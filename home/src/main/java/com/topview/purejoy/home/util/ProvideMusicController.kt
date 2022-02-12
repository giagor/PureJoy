package com.topview.purejoy.home.util

import androidx.compose.runtime.*
import com.topview.purejoy.common.music.view.bottom.MusicController

@Composable
fun ProvideMusicController(
    content: @Composable () -> Unit
) {
    val musicController = remember { MusicController() }

    DisposableEffect(true) {
        musicController.registerServiceListener()
        onDispose {
            musicController.unregisterServiceListener()
        }
    }

    CompositionLocalProvider(LocalMusicController provides musicController) {
        content()
    }
}

val LocalMusicController = compositionLocalOf<MusicController> { error("LocalMusicController not present") }