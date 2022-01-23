package com.topview.purejoy.home.download.adapter

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.topview.purejoy.common.business.download.bean.DownloadSongInfo
import com.topview.purejoy.home.R
import com.topview.purejoy.home.databinding.ItemHomeDownloadManageBinding

class DownloadManageAdapter :
    BaseQuickAdapter<DownloadSongInfo, BaseDataBindingHolder<ItemHomeDownloadManageBinding>>(
        R.layout.item_home_download_manage
    ) {

    private var onClickListener: OnClickListener? = null

    override fun convert(
        holder: BaseDataBindingHolder<ItemHomeDownloadManageBinding>,
        item: DownloadSongInfo
    ) {
        // 获取Binding
        val binding: ItemHomeDownloadManageBinding? = holder.dataBinding
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