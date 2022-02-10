package com.topview.purejoy.common.widget.compose

import androidx.compose.runtime.Composable
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.topview.purejoy.common.util.createImageRequestForCoil

class RemoteLoader {
    var requestBuilder: (ImageRequest.Builder.() -> Unit) = {}

    @Composable
    fun getRemotePainter(
        request: Any
    ): AsyncImagePainter {
        return rememberAsyncImagePainter(
            model = createImageRequestForCoil(
                data = request,
                requestBuilder = requestBuilder
            )
        )
    }
}

