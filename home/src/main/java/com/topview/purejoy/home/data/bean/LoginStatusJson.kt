package com.topview.purejoy.home.data.bean

class LoginStatusJson(
    val data: LoginStatusData
) {
    class LoginStatusData(
        val code: Int,
        val profile: UserJson.Profile?
    )
}