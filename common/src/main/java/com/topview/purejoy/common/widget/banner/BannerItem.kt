package com.topview.purejoy.common.widget.banner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * BannerItem需要实现该接口
 *
 * Created by giagor at 2021/11/09
 * */
interface BannerItem {

    /**
     * 创建并返回ItemView
     * */
    fun onCreateView(inflater: LayoutInflater, parent: ViewGroup): View
}