package com.topview.purejoy.home.util

internal object LinkUtil {
    /**
     * 判断链接是否有效并且执行相应的操作
     *
     * @param link 即Url
     * @param validOperation 链接有效时对应的操作
     * @param invalidOperation 链接无效时对应的操作
     * */
    fun executeLink(
        link: String?,
        validOperation: ((String) -> Unit)?,
        invalidOperation: ((String?) -> Unit)?
    ) {
        if (link == null) {
            invalidOperation?.invoke(link)
            return
        }

        if (link.startsWith("http://") || link.startsWith("https://")) {
            validOperation?.invoke(link)
        } else if (link.startsWith("www.") || link.startsWith("WWW.")) {
            validOperation?.invoke("http://$link")
        } else {
            invalidOperation?.invoke(link)
        }
    }
}

