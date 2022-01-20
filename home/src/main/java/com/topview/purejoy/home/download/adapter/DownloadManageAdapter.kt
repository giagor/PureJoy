package com.topview.purejoy.home.download.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.topview.purejoy.common.component.download.task.DownloadTask
import com.topview.purejoy.home.R
import com.topview.purejoy.home.databinding.ItemHomeDownloadManageBinding

class DownloadManageAdapter :
    BaseQuickAdapter<DownloadTask, BaseDataBindingHolder<ItemHomeDownloadManageBinding>>(
        R.layout.item_home_download_manage
    ) {

    override fun convert(
        holder: BaseDataBindingHolder<ItemHomeDownloadManageBinding>,
        item: DownloadTask
    ) {
        // 获取Binding
        val binding: ItemHomeDownloadManageBinding? = holder.dataBinding
        binding?.let {
            // 设置数据
            it.downloadTask = item
        }
    }
}