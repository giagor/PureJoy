package com.topview.purejoy.common.component.download.task

import android.util.SparseArray
import androidx.core.util.valueIterator
import com.topview.purejoy.common.component.download.listener.user.UserDownloadListener
import com.topview.purejoy.common.component.download.util.getDownloadPath

/**
 * Created by giagor on 2021/12/18
 *
 * 批量下载对应的实体类
 * */
class BatchDownloadTask {
    /**
     * 存储要下载的任务，key是任务的标识符
     * */
    private val tasks = SparseArray<DownloadTask>()

    /**
     * 添加任务
     *
     * @param identifier 任务对应的标识符，每个任务的标识符需要不一样
     * @param url 要下载的文件的url
     * @param saveDir 要保存到的目录，例如/storage/emulated/0/Android/data/packagename/files/Music
     * @param name 文件名，需要自己带上后缀名
     * @param listener 监听器，可以继承SimpleUserDownloadListener实现监听
     * */
    fun addTask(
        identifier: Int,
        url: String,
        saveDir: String,
        name: String,
        listener: UserDownloadListener?
    ) = apply {
        val path = getDownloadPath(saveDir, name)
        // 创建任务
        val downloadTask = DownloadTask(
            id = null,
            path = path,
            url = url,
            totalSize = 0,
            threadNum = 0,
            breakPointDownload = false,
            downloadListener = listener
        )
        tasks.put(identifier, downloadTask)
    }

    fun downloadAll() = apply {
        val iterator = tasks.valueIterator()
        while (iterator.hasNext()) {
            val task = iterator.next()
            task.download()
        }
    }

    /**
     * 通过标识符获取任务，如果没有对应的任务，则返回null
     * */
    fun getTask(identifier: Int): DownloadTask? = tasks.get(identifier)
}