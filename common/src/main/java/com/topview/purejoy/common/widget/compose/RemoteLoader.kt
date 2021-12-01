package com.topview.purejoy.common.widget.compose

import androidx.compose.runtime.Composable
import coil.compose.rememberImagePainter
import coil.request.ImageRequest

class RemoteLoader {
    var requestBuilder: (ImageRequest.Builder.() -> Unit) = {}

    @Composable
    fun getRemotePainter(
        request: String
    ) = rememberImagePainter(data = request, builder = requestBuilder)
}

