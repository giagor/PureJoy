package com.topview.purejoy.video.router

import com.topview.purejoy.common.router.CommonRouter

internal object VideoRouter {
    internal const val ACTIVITY_VIDEO = "/video/ui/VideoActivity"

    internal fun routeToVideoActivity() {
        CommonRouter.routeWithoutParams(ACTIVITY_VIDEO)
    }
}