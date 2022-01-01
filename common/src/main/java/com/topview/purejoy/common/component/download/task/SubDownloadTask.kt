package com.topview.purejoy.common.component.download.task

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import com.topview.purejoy.common.component.download.DownloadManager
import com.topview.purejoy.common.component.download.listener.net.ResourceDataCallback
import com.topview.purejoy.common.component.download.listener.subtask.SubDownloadListener
import com.topview.purejoy.common.component.download.status.DownloadStatus
import java.io.File
import java.io.InputStream
import java.io.RandomAccessFile

private const val BUFFER_SIZE = 1024 * 1024

/**
 * Created by giagor on 2021/12/18
 *
 * 实体类-下载的子任务
 *
 * 注意，为了支持SubDownloadTask使用Room存储在数据库中，需要
 * 1.构造器的字段需要是可变(var)的
 * 2.提供一个空构造器
 * */
@Entity(
    foreignKeys = [ForeignKey(
        entity = DownloadTask::class,
        parentColumns = ["id"],
        childColumns = ["parentId"],
        onUpdate = CASCADE,
        onDelete = CASCADE
    )]
)
data class SubDownloadTask(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    /** Room中，外键需要添加索引，不然无法通过编译 */
    @ColumnInfo(index = true) var parentId: Long?,
    @Ignore var url: String,
    @Ignore var path: String,
    @Ignore var tag: String,
    /** 子任务开始的位置 */
    var startPos: Long,
    /** 已经下载的大小 */
    var downloadedSize: Long,
    /** 子任务的大小 */
    var subTaskSize: Long,
    /** 是否要断点续传下载 */
    @Ignore var breakPointDownload: Boolean = true
) : Runnable {

    @Ignore
    var subDownloadListener: SubDownloadListener? = null

    @Ignore
    @Volatile
    private var status: Int = DownloadStatus.INITIAL

    /**
     * 记录暂停时已下载的大小
     * */
    @Ignore
    private var downloadSizeOnPause: Long = -1

    constructor() : this(null, 0, "", "", "", 0, 0, 0, true)

    override fun run() {
        status = DownloadStatus.DOWNLOADING
        getResourceStream()
    }

    private fun getResourceStream() {
        DownloadManager.downHttpHelper.getInputStreamByRange(this, object : ResourceDataCallback {
            override fun onSuccess(inputStream: InputStream) {
                if (!handleContinueDownloading()) return
                writeToFile(inputStream)
            }

            override fun onFailure(t: Throwable) {
                status = DownloadStatus.FAILURE
                subDownloadListener?.onFailure(t.message ?: "")
            }
        })
    }

    private fun writeToFile(inputStream: InputStream) {
        var randomAccessFile: RandomAccessFile? = null

        try {
            val file = File(path)
            randomAccessFile = RandomAccessFile(file, "rwd")
            randomAccessFile.seek(startPos + downloadedSize)

            // 这里要做一个判断的原因，是防止子任务在从暂停恢复后，将原来已下载的进度重复通知给父任务
            if (downloadedSize > downloadSizeOnPause) {
                // 先将已下载的进度通知给监听器
                subDownloadListener?.onProgress(downloadedSize)
            }

            val bytes = ByteArray(BUFFER_SIZE)
            var length = inputStream.read(bytes)
            // 循环读取，直到读完输入流
            while (length != -1) {
                // 检测是否继续下载
                if (!handleContinueDownloading()) return

                randomAccessFile.write(bytes, 0, length)
                // 更新downloadedSize的值
                downloadedSize += length
                // 数据库中更新
                // （注意要：先向file写入，再写入数据库。因为向file写入和更新数据库之间有可能程序退出，
                // 如果数据库操作后面执行，顶多是有部分文件内容下次再下载一次。但是如果文件的写入后面
                // 操作，就有可能数据库中记录了，但是实际上文件没有写入，这样就可能导致文件的不完整、损坏）
                if (breakPointDownload) {
                    DownloadManager.downDbHelper.updateSubDownloadTask(this)
                }

                // 通知监听者
                subDownloadListener?.onProgress(length.toLong())
                length = inputStream.read(bytes)
            }
            // 子任务下载成功
            status = DownloadStatus.SUCCESS
            subDownloadListener?.onSuccess()
        } catch (e: Exception) {
            e.printStackTrace()
            status = DownloadStatus.FAILURE
            subDownloadListener?.onFailure(e.message ?: "")
        } finally {
            try {
                inputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            randomAccessFile?.let {
                try {
                    it.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * 暂停子任务
     * */
    @Synchronized
    fun pauseSubTask() {
        if (canPause()) {
            status = DownloadStatus.PAUSED
        }
    }

    /**
     * 取消子任务
     * */
    @Synchronized
    fun cancelSubTask() {
        if (canCancel()) {
            status = DownloadStatus.CANCELED
        }
    }

    /**
     * 处理是否要继续下载
     *
     * @return 继续下载返回true，不继续下载返回false
     * */
    fun handleContinueDownloading(): Boolean {
        if (checkCancel()) {
            subDownloadListener?.onCancelled()
            return false
        }
        if (checkPause()) {
            downloadSizeOnPause = downloadedSize
            subDownloadListener?.onPaused()
            return false
        }
        return true
    }

    /**
     * 配置下载的相关信息
     * */
    fun configureDownloadInfo(
        url: String,
        path: String,
        tag: String,
        breakPointDownload: Boolean,
        subDownloadListener: SubDownloadListener?
    ) {
        this.url = url
        this.path = path
        this.tag = tag
        this.breakPointDownload = breakPointDownload
        this.subDownloadListener = subDownloadListener
    }

    /**
     * 是否完成
     * */
    fun checkCompleted(): Boolean = status == DownloadStatus.SUCCESS

    /**
     * 判断是否可以暂停下载
     * */
    private fun canPause() = status == DownloadStatus.DOWNLOADING

    private fun canCancel() = status != DownloadStatus.FAILURE
            && status != DownloadStatus.SUCCESS
            && status != DownloadStatus.CANCELED

    private fun checkCancel() = status == DownloadStatus.CANCELED

    private fun checkPause() = status == DownloadStatus.PAUSED
}