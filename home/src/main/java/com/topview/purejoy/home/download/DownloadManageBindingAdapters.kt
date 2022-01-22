package com.topview.purejoy.home.download

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.topview.purejoy.common.component.download.status.DownloadStatus
import com.topview.purejoy.common.component.download.task.DownloadTask
import com.topview.purejoy.common.widget.StatusCircleButton
import com.topview.purejoy.home.R
import com.topview.purejoy.home.download.adapter.DownloadManageAdapter

@BindingAdapter("downloadTasks")
fun setDownloadTasks(recyclerView: RecyclerView, tasks: List<DownloadTask>?) {
    tasks?.let {
        val adapter = recyclerView.adapter as DownloadManageAdapter
        adapter.setList(it)
    }
}

@BindingAdapter("circleButtonStatus")
fun setCircleButtonStatus(button: StatusCircleButton, task: DownloadTask) {
    when (task.getStatus()) {
        DownloadStatus.INITIAL, DownloadStatus.PREPARE_DOWNLOAD, DownloadStatus.CANCELED -> {
            button.visibility = View.GONE
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
fun setDownloadTips(tv: TextView, task: DownloadTask) {
    when (task.getStatus()) {
        DownloadStatus.INITIAL -> {
            tv.visibility = View.VISIBLE
            tv.text = tv.context.getString(R.string.home_download_manage_click_to_download)
        }

        DownloadStatus.DOWNLOADING, DownloadStatus.PAUSED -> {
            tv.visibility = View.INVISIBLE
        }

        DownloadStatus.PREPARE_DOWNLOAD -> {
            tv.visibility = View.VISIBLE
            tv.text = tv.context.getString(R.string.home_download_manage_prepare_download)
        }

        DownloadStatus.CANCELED -> {
            tv.visibility = View.VISIBLE
            tv.text = tv.context.getString(R.string.home_download_manage_task_cancelling)
        }
    }
}

@BindingAdapter("cancelIconVisibility")
fun setCancelIconVisibility(iv: ImageView, task: DownloadTask) {
    when (task.getStatus()) {
        DownloadStatus.INITIAL, DownloadStatus.CANCELED -> {
            iv.visibility = View.GONE
        }

        DownloadStatus.DOWNLOADING, DownloadStatus.PAUSED, DownloadStatus.PREPARE_DOWNLOAD -> {
            iv.visibility = View.VISIBLE
        }
    }
}