package com.topview.purejoy.common.business.download.manage

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.topview.purejoy.common.R
import com.topview.purejoy.common.business.data.bean.DownloadSongInfo
import com.topview.purejoy.common.component.download.status.DownloadStatus
import com.topview.purejoy.common.widget.StatusCircleButton
import com.topview.purejoy.common.business.download.manage.adapter.DownloadManageAdapter

@BindingAdapter("downloadSongInfos")
fun setDownloadSongInfos(recyclerView: RecyclerView, tasks: List<DownloadSongInfo>?) {
    tasks?.let {
        val adapter = recyclerView.adapter as DownloadManageAdapter
        adapter.setList(it)
    }
}

@BindingAdapter("circleButtonStatus")
fun setCircleButtonStatus(button: StatusCircleButton, songInfo: DownloadSongInfo) {
    when (songInfo.status) {
        DownloadStatus.INITIAL, DownloadStatus.PREPARE_DOWNLOAD, DownloadStatus.CANCELED -> {
            button.visibility = View.INVISIBLE
        }

        DownloadStatus.DOWNLOADING -> {
            button.visibility = View.VISIBLE
            button.setStatus(StatusCircleButton.START)
        }

        DownloadStatus.PAUSED -> {
            button.visibility = View.VISIBLE
            button.setStatus(StatusCircleButton.PAUSE)
        }

        else -> {}
    }
}

@BindingAdapter("downloadTips")
fun setDownloadTips(tv: TextView, songInfo: DownloadSongInfo) {
    when (songInfo.status) {
        DownloadStatus.INITIAL -> {
            tv.text = tv.context.getString(R.string.common_download_manage_click_to_download)
        }

        DownloadStatus.DOWNLOADING -> {
            tv.text = tv.context.getString(R.string.common_download_manage_task_downloading)
        }

        DownloadStatus.PAUSED -> {
            tv.text = tv.context.getString(R.string.common_download_manage_task_paused)
        }

        DownloadStatus.PREPARE_DOWNLOAD -> {
            tv.text = tv.context.getString(R.string.common_download_manage_prepare_download)
        }

        DownloadStatus.CANCELED -> {
            tv.text = tv.context.getString(R.string.common_download_manage_task_cancelling)
        }
        
        DownloadStatus.SUCCESS ->{
            tv.text = tv.context.getString(R.string.common_download_manage_task_success)
        }
        
        DownloadStatus.FAILURE -> {
            tv.text = tv.context.getString(R.string.common_download_manage_task_failure)
        }
    }
}

@BindingAdapter("cancelIconVisibility")
fun setCancelIconVisibility(iv: ImageView, songInfo: DownloadSongInfo) {
    when (songInfo.status) {
        DownloadStatus.INITIAL, DownloadStatus.CANCELED -> {
            iv.visibility = View.INVISIBLE
        }

        DownloadStatus.DOWNLOADING, DownloadStatus.PAUSED, DownloadStatus.PREPARE_DOWNLOAD -> {
            iv.visibility = View.VISIBLE
        }
    }
}