package com.topview.purejoy.home.data.bean

class VideoCategoryJson(
    val code: Int,
    val data: List<Category>?
) {
    class Category(
        val id: Long,
        val name: String
    )
}

