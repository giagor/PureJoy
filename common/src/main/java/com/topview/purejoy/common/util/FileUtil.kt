package com.topview.purejoy.common.util

import java.io.File

/**
 * @param dir 要清理的文件或文件夹
 * @param filter 当filter返回true时，不删除[dir]
 * @return 返回被删除的文件的大小
 * 清理文件
 */
fun clear(dir: File, filter: (File) -> Boolean): Long {
    var size = 0L
    if (dir.isDirectory) {

        dir.listFiles()?.forEach {
            size += clear(it, filter)
        }
        if (!filter(dir)) {
            dir.delete()
        }
    } else {

        if (!filter(dir)) {
            size += dir.length()
            dir.delete()
        }
    }
    return size
}