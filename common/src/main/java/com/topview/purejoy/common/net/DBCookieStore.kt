package com.topview.purejoy.common.net

import com.topview.purejoy.common.component.db.CookieDatabase
import com.topview.purejoy.common.component.db.entity.CookieData
import com.topview.purejoy.common.component.db.entity.toCookie
import com.topview.purejoy.common.component.db.entity.toCookieData
import com.topview.purejoy.common.music.service.recover.db.initDB
import com.topview.purejoy.common.music.util.ExecutorInstance
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.util.concurrent.CopyOnWriteArrayList

class DBCookieStore: CookieStore {

    private val database = initDB(
        CookieDatabase::class.java,
        CookieDatabase.COOKIE_DATABASE_NAME
    )

    private val cookieList: CopyOnWriteArrayList<Cookie> = CopyOnWriteArrayList()

    init {
        // 每次加载数据的同时，删除失效的Cookie
        ExecutorInstance.getInstance().execute {
            val time = System.currentTimeMillis()
            database.cookieDao().selectByTime(time).forEach { cookieData ->
                cookieList.add(cookieData.toCookie())
            }
            database.cookieDao().deleteByTime(time)
        }
    }

    override fun addCookies(httpUrl: HttpUrl, cookies: List<Cookie>) {
        val insertList = mutableListOf<CookieData>()
        cookies.forEach { newCookie ->
            cookieList.find {
                it.domain == newCookie.domain
                        && it.path == newCookie.path
                        && it.name == newCookie.name
            }?.let {
                cookieList.remove(it)
            }
            cookieList.add(newCookie)
            if (newCookie.persistent) {
                insertList.add(newCookie.toCookieData())
            }
        }
        ExecutorInstance.getInstance().execute {
            database.cookieDao().insertAll(insertList)
        }
    }

    override fun getCookies(httpUrl: HttpUrl): List<Cookie> {
        return cookieList.filter {
            it.matches(httpUrl)
        }
    }

    override fun removeCookies(httpUrl: HttpUrl) {
        val deleteList = mutableListOf<CookieData>()
        cookieList.forEach {
            if (it.domain == httpUrl.host) {
                cookieList.remove(it)
                deleteList.add(it.toCookieData())
            }
        }
        ExecutorInstance.getInstance().execute {
            database.cookieDao().delete(
                deleteList
            )
        }
    }

    companion object {
        fun getCookieJar(): CookieJar = PersistenceCookieJar(DBCookieStore())
    }
}