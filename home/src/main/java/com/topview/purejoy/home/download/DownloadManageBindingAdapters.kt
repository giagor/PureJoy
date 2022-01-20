package com.topview.purejoy.home.download

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.topview.purejoy.common.component.download.task.DownloadTask
import com.topview.purejoy.home.download.adapter.DownloadManageAdapter

@BindingAdapter("downloadTasks")
fun setDownloadTasks(recyclerView: RecyclerView, tasks: List<DownloadTask>?) {
    tasks?.let {
        val adapter = recyclerView.adapter as DownloadManageAdapter
        adapter.setList(it)
    }
}