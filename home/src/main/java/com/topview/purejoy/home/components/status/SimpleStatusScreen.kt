package com.topview.purejoy.home.components.status

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun SimpleStatusScreen(
    state: PageState,
    modifier: Modifier = Modifier,
    emptyContent: @Composable (BoxScope.() -> Unit)? = null,
    errorContent: @Composable (BoxScope.() -> Unit)? = null,
    loadingContent: @Composable (BoxScope.() -> Unit)? = null,
    successScreen: @Composable () -> Unit,
) {
    Crossfade(targetState = state) {
        if (it is PageState.Success) {
            successScreen()
        } else {
            Surface(modifier = modifier) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    when (it) {
                        is PageState.Empty -> {
                            emptyContent?.invoke(this)
                        }
                        is PageState.Error -> {
                            errorContent?.invoke(this)
                        }
                        is PageState.Loading -> {
                            loadingContent?.invoke(this)
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}