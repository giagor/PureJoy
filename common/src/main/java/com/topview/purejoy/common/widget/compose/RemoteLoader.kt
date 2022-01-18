package com.topview.purejoy.common.widget.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

class RemoteLoader {
    var requestBuilder: (ImageRequest.Builder.() -> Unit) = {}

    @Composable
    fun getRemotePainter(
        request: String
    ): AsyncImagePainter {
        val builder = ImageRequest.Builder(LocalContext.current)
        builder.requestBuilder()
        return rememberAsyncImagePainter(
            model = builder.build()
        )
    }
}

