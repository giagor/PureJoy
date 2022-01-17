package com.topview.purejoy.common.component.db.entity

import androidx.room.Entity
import okhttp3.Cookie

@Entity(primaryKeys = ["domain", "path", "name"], tableName = "cookie_data")
data class CookieData(
    val name: String,
    val value: String,
    val expiresAt: Long,
    val domain: String,
    val path: String,
    val secure: Boolean,
    val httpOnly: Boolean,
    val hostOnly: Boolean,
    val persistent: Boolean,
)

fun CookieData.toCookie(): Cookie {
    val builder = Cookie.Builder()
        .name(name)
        .value(value)
        .expiresAt(expiresAt)
        .path(path)
    if (hostOnly) {
        builder.hostOnlyDomain(domain)
    } else {
        builder.domain(domain)
    }
    if (secure) {
        builder.secure()
    }
    if (httpOnly) {
        builder.httpOnly()
    }
    return builder.build()
}

fun Cookie.toCookieData(): CookieData =
    CookieData(name, value, expiresAt, domain, path,
        secure, httpOnly, hostOnly, persistent)