package com.topview.purejoy.common.component.download.listener.subtask

/**
 * Created by giagor on 2021/12/17
 *
 * 监听子任务的下载状态和下载进度，父任务需要实现该接口
 * */
interface SubDownloadListener {
    /**
     * 监听子任务下载进度
     *
     * @param transmission 该参数表示单次从网络读取、写入本地的数据字节数
     * */
    fun onProgress(transmission: Long)

    /**
     * 子任务暂停，一般是父任务让子任务进入暂停状态
     * */
    fun onPaused()

    /**
     * 子任务执行失败，一般是内部下载出错
     * */
    fun onFailure(msg: String = "")

    /**
     * 子任务取消，一般是父任务让子任务取消
     * */
    fun onCancelled()

    /**
     * 子任务执行成功
     * */
    fun onSuccess()
}