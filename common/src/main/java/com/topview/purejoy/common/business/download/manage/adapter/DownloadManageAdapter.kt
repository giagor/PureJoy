package com.topview.purejoy.common.business.download.manage.adapter

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.topview.purejoy.common.R
import com.topview.purejoy.common.business.data.bean.DownloadSongInfo
import com.topview.purejoy.common.databinding.ItemCommonDownloadManageBinding

class DownloadManageAdapter :
    BaseQuickAdapter<DownloadSongInfo, BaseDataBindingHolder<ItemCommonDownloadManageBinding>>(
        R.layout.item_common_download_manage
    ) {

    private var onClickListener: OnClickListener? = null

    override fun convert(
        holder: BaseDataBindingHolder<ItemCommonDownloadManageBinding>,
        item: DownloadSongInfo
    ) {
        // 获取Binding
        val binding: ItemCommonDownloadManageBinding? = holder.dataBinding
        binding?.let {
            // 设置数据
            it.downloadSongInfo = item
            // 设置监听器
            onClickListener?.let { listener ->
                it.statusButtonClickListener = View.OnClickListener {
                    listener.onStatusButtonClick(item)
                }

                it.cancelTaskClickListener = View.OnClickListener {
                    listener.onCancelTaskClick(item)
                }
            }
        }
    }

    /**
     * 设置监听器
     * */
    fun setStatusButtonClickListener(listener: OnClickListener) {
        this.onClickListener = listener
    }

    interface OnClickListener {
        fun onStatusButtonClick(songInfo: DownloadSongInfo)
        fun onCancelTaskClick(songInfo: DownloadSongInfo)
    }
}