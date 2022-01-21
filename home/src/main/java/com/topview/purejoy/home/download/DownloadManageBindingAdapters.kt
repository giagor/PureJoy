package com.topview.purejoy.home.download

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.topview.purejoy.common.component.download.status.DownloadStatus
import com.topview.purejoy.common.component.download.task.DownloadTask
import com.topview.purejoy.common.widget.StatusCircleButton
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
        DownloadStatus.INITIAL, DownloadStatus.PREPARE_DOWNLOAD -> {
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