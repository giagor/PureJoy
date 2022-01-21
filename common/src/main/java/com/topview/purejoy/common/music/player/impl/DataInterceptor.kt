package com.topview.purejoy.common.music.player.impl

import com.topview.purejoy.common.music.data.Item
import com.topview.purejoy.common.music.data.Wrapper

// 数据拦截器
interface DataInterceptor<T : Item> {
    /**
     * @param wrapper 将要添加的[Wrapper]
     * @param wrappers 已存在数据集中的[Wrapper]
     * @param source 已存在数据集中的[Item]
     * @param item 将要添加的[Item]
     * 当此方法返回true时，将拦截数据，不添加到数据集中，并调用[intercept]方法，
     * 返回false时，将添加数据到数据集中
     */
    fun isIntercepted(wrapper: Wrapper, wrappers: List<Wrapper>, source: List<T>, item: T): Boolean
    fun intercept(wrapper: Wrapper, item: T)
}