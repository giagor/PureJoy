package com.topview.purejoy.home.entity

/**
 * 与Video的类别相关的Tab信息
 */
data class VideoCategoryTab(
    val id: Long,
    override val content: String
): TabData

/**
 * "推荐"分类的虚拟id，这个分类实际并不存在
 */
internal const val RecommendTabId = Long.MIN_VALUE
/**
 * 一个特殊的Tab，代表“推荐”分类，这个分类有对应的特殊接口但没有相关的分类id
 */
internal val recommendCategoryTab = VideoCategoryTab(id = RecommendTabId, content = "推荐")

