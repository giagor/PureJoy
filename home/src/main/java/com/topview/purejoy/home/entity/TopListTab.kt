package com.topview.purejoy.home.entity

/**
 * 榜单相关的Tab的枚举
 */
enum class TopListTab(override val content: String): TabData {
    Official("官方"),
    Handpick("精选"),
    Genre("曲风"),
    Global("全球"),
    Characteristic("特色")
}