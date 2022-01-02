package com.topview.purejoy.home.util

internal object UrlUtil {
    const val IN_EFFECTIVE_URL = "in_effective_url"

    /**
     * 获取有效的链接
     *
     * @return 若链接有效则返回有效的链接，否则返回IN_EFFECTIVE_URL
     * */
    fun effectiveUrl(targetUrl: String): String {
        return if (targetUrl.startsWith("http://") || targetUrl.startsWith("https://")) {
            targetUrl
        } else if (targetUrl.startsWith("www.") || targetUrl.startsWith("WWW.")) {
            "http://$targetUrl"
        } else {
            IN_EFFECTIVE_URL
        }
    }
}

