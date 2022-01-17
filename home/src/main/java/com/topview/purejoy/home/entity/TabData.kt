package com.topview.purejoy.home.entity

/**
 * Tab的信息
 */
interface TabData {
    val content: String
}

data class SimpleTabData(
    override val content: String
): TabData