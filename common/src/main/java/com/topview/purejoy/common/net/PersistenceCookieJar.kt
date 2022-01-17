package com.topview.purejoy.common.net

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class PersistenceCookieJar(
    private val cookieStore: CookieStore
): CookieJar {
    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return cookieStore.getCookies(url)
    }


    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookieStore.addCookies(url, cookies)
    }
}


interface CookieStore {
    fun addCookies(httpUrl: HttpUrl, cookies: List<Cookie>)
    fun getCookies(httpUrl: HttpUrl,): List<Cookie>

    /**
     * 移除Cookie
     */
    fun removeCookies(httpUrl: HttpUrl)
}