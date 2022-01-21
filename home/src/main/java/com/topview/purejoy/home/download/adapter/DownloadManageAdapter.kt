package com.topview.purejoy.home.download.adapter

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.topview.purejoy.common.component.download.task.DownloadTask
import com.topview.purejoy.home.R
import com.topview.purejoy.home.databinding.ItemHomeDownloadManageBinding

class DownloadManageAdapter :
    BaseQuickAdapter<DownloadTask, BaseDataBindingHolder<ItemHomeDownloadManageBinding>>(
        R.layout.item_home_download_manage
    ) {

    private var statusButtonClickListener: StatusButtonClickListener? = null

    override fun convert(
        holder: BaseDataBindingHolder<ItemHomeDownloadManageBinding>,
        item: DownloadTask
    ) {
        // 获取Binding
        val binding: ItemHomeDownloadManageBinding? = holder.dataBinding
        binding?.let {
            // 设置数据
            it.downloadTask = item
            // 设置监听器
            statusButtonClickListener?.let { listener ->
                it.statusButtonClickListener = View.OnClickListener {
                    listener.onStatusButtonClick(item)
                }
            }
        }
    }

    /**
     * 设置监听器
     * */
    fun setStatusButtonClickListener(listener: StatusButtonClickListener) {
        this.statusButtonClickListener = listener
    }

    interface StatusButtonClickListener {
        fun onStatusButtonClick(downloadTask: DownloadTask)
    }
}