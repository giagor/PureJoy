package com.topview.purejoy.common.component.loadmore

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.topview.purejoy.common.mvvm.fragment.MVVMFragment
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel

/**
 * Created by giagor at 2021/11/30
 *
 * 对"加载更多"功能的一个简单封装，避免在各个碎片中出现重复的代码，主要负责的功能：
 * 1.集中定义"加载更多"的数据
 * 2.为适配器添加"加载更多"的监听器
 * 3.提供resetLoadMoreStatus方法，用于重置"加载更多"的数据状态
 * 4.提供manageEnableLoadMoreByCurPages方法，该方法会根据"当前已加载的页数"和"每页大小"，以及"数据总量"，
 * 判断"加载更多"是否已经结束，并且设置是否允许适配器"加载更多"
 *
 * 子类需要做的：
 * 1.实现抽象方法(提供的适配器需要是BaseQuickAdapter的子类，并实现了"加载更多模块"的接口LoadMoreModule)
 * 2.数据加载第一页前，要调用resetLoadMoreStatus方法，重置"加载更多"的状态
 * 3.数据加载第一页成功后，要调用firstRequestSucceess方法
 * 4.子类需要对数据的总量进行记录
 * 5.后续的每次分页加载成功后，子类都要调用loadMoreSuccess方法
 * */
abstract class LoadMoreFragment<VM : MVVMViewModel, VB : ViewDataBinding, AT> :
    MVVMFragment<VM, VB>() where AT : BaseQuickAdapter<*, *>,
                                 AT : LoadMoreModule {
    /**
     * 记录分页加载的当前页数
     * */
    protected var curPage = 0

    /**
     * 记录分页加载的数据总数量
     * */
    protected var dataTotalCount = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAdapterLoadMoreListener()
    }

    /**
     * 子类实现该方法，获取分页的大小
     * */
    protected abstract fun getPageSize(): Int

    /**
     * 子类返回适配器
     * */
    protected abstract fun getAdapter(): AT

    /**
     * 子类实现该方法，实现"加载更多"中数据的加载
     *
     * @param offset 本次"加载更多"的偏移量
     * @param limit 本次"加载更多"的数量
     * */
    protected abstract fun loadMoreData(offset: Int, limit: Int)

    /**
     * 重置一些"加载更多"的状态
     * */
    protected fun resetLoadMoreStatus() {
        curPage = 0
        dataTotalCount = 0
        // 允许adapter可以加载更多
        getAdapter().loadMoreModule.isEnableLoadMore = true
    }

    /**
     * 根据当前的页数，管理是否要加载更多
     * */
    protected fun manageEnableLoadMoreByCurPages() {
        // 如果数据已经全部加载完，那么就设置adapter为无法加载更多
        getAdapter().loadMoreModule.isEnableLoadMore = curPage * getPageSize() < dataTotalCount
    }

    /**
     * 子类中，第一页数据请求成功后，要调用该方法（不是第一次"加载更多"成功，是第一页数据请求成功），该方法会
     * 将"当前页数"置为1，表示第1页，并且根据当前页数、数据总量、每页大小，设置是否允许适配器"加载更多"
     * */
    protected fun firstRequestSuccess() {
        // 设置当前页数为1
        curPage = 1
        // 是否允许适配器"加载更多"
        manageEnableLoadMoreByCurPages()
    }

    /**
     * 子类每一次"加载更多"的数据请求成功后，都要调用该方法，该方法会将"当前页数"加1，并且根据当前页数、数据总量、
     * 每页大小，设置是否允许适配器"加载更多"
     * */
    protected fun loadMoreSuccess() {
        // 当前页数加1
        curPage++
        // 是否允许适配器"加载更多"
        manageEnableLoadMoreByCurPages()
    }

    /**
     * 为Adapter增加"加载更多"的监听
     * */
    private fun setAdapterLoadMoreListener() {
        getAdapter().loadMoreModule.setOnLoadMoreListener {
            // "加载更多"的偏移量
            val offset = curPage * getPageSize()
            // 剩余未加载的数据总量
            val remainingCount = dataTotalCount - offset
            // 判断未加载的数据是否够一页
            if (remainingCount >= getPageSize()) {
                // 未加载的数据够一页，加载一页的数据
                loadMoreData(offset, getPageSize())
            } else {
                // 未加载的数据不够一页，加载剩余的所有数据
                loadMoreData(offset, remainingCount)
            }
        }
    }
}