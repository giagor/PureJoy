package com.topview.purejoy.home.search

import androidx.lifecycle.LiveData

/**
 * Created by giagor at 2021/11/26
 *
 * SearchActivity需要实现该接口
 * 接口的作用：让Fragment可以监听用户搜索的关键字
 * */
interface SearchKeywordListener {
    /**
     * 该方法需要提供一个LiveData，Fragment通过观察该LiveData，可以监听用户搜索的关键字
     * */
    fun getKeywordLiveData(): LiveData<String>
}