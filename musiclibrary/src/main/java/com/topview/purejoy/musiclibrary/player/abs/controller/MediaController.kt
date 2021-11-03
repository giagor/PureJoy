package com.topview.purejoy.musiclibrary.player.abs.controller

interface MediaController {
    // 播放上一首音乐
    fun last()
    // 播放下一首音乐
    fun next()
    // 根据音乐播放器的状态来暂停或恢复音乐的播放
    fun playOrPause()
    // 获取当前播放音乐的总时长
    fun duration(): Int
    // 移动到指定位置播放
    fun seekTo(progress: Int)
    // 获取音乐播放器的播放状态
    fun isPlaying(): Boolean
    // 重置音乐播放器
    fun reset()
    // 获取当前播放进度
    fun progress(): Int
    // 释放资源
    fun release()


}