package com.topview.purejoy.common.component.download.dispatcher

import com.topview.purejoy.common.component.download.task.DownloadTask
import com.topview.purejoy.common.component.download.util.Constant
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.min

/**
 * Created by giagor on 2021/12/18
 *
 * 对下载任务进行调度
 * */
class DownloadDispatcher(private var maxDownloadCounts: Int) {
    init {
        maxDownloadCounts = min(maxDownloadCounts, Constant.MAX_CONCURRENT_EXECUTE_TASK)
    }

    /**
     * 存放任务的等待队列
     * */
    private val readyTaskQueue: LinkedList<DownloadTask> = LinkedList()

    /**
     * 任务的执行队列
     * */
    private val runningTaskQueue: LinkedList<DownloadTask> = LinkedList()

    private var executorService: ExecutorService = Executors.newCachedThreadPool()

    fun enqueue(task: DownloadTask) {
        // 加锁保证任务可以正确地被添加
        synchronized(this) {
            readyTaskQueue.addLast(task)
        }
        promoteAndExecute()
    }

    private fun promoteAndExecute() {
        var executedTask: DownloadTask? = null

        // 加锁保证任务可以正确被执行
        synchronized(this) {
            // 如果当前在下载的任务数没有到达最大值，并且等待队列中有任务
            if (runningTaskQueue.size < maxDownloadCounts && readyTaskQueue.isNotEmpty()) {
                executedTask = readyTaskQueue.removeFirst()
                runningTaskQueue.addLast(executedTask)
            }
        }

        executedTask?.executeSubTasks(executorService)
    }

    /**
     * 当任务执行完后，调用该方法，通知任务已执行完成
     * */
    fun finished(task: DownloadTask) {
        synchronized(this) {
            // 在执行队列中移除该任务
            runningTaskQueue.remove(task)
        }
        // 再次尝试执行任务
        promoteAndExecute()
    }
}