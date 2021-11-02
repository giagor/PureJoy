package com.topview.purejoy.musiclibrary.player.abs

interface Position {
    // 当前位置
    fun current(): Int
    // 下一个位置
    fun next(): Int
    // 上一个位置
    fun last(): Int
    // 根据传入的position重构此position的参数
    fun with(position: Position)
    // 最大位置，为数据集合的大小
    var max: Int
}