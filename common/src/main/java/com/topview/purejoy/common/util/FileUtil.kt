package com.topview.purejoy.common.util

import java.io.File

/**
 * 获取文件或文件夹的大小，如果[File]是文件夹，那么会递归计算子文件/文件夹的大小
 */
fun File.getSubSize(): Long {
    var size = 0L
    forEachSubFile {
        size += it.length()
    }
    return size
}

/**
 * 删除子文件(夹)，但不删除根[File]
 * @return 删除文件的总大小
 */
fun File.clearSubFile(): Long =
    clearWithFilter {
        it != this
    }


/**
 * 递归遍历子文件/文件夹，根据[filter]确定是否删除子文件(夹)
 * @param filter 如果lambda返回true，则执行删除操作
 * @return 删除文件的总大小
 */
fun File.clearWithFilter(filter: (File) -> Boolean):Long {
    var size = 0L
    forEachSubFile {
        if (filter(it)) {
            size += it.length()
            it.delete()
        }
    }
    return size
}

/**
 * 如果[File]是一个文件夹，这会递归遍历文件夹下的所有子文件。
 * 遍历策略为后序遍历，父文件夹在子文件全部遍历完毕后才会传入[action]
 */
fun File.forEachSubFile(action: (File) -> Unit) {
    if (isDirectory) {
        listFiles()?.forEach {
            it?.forEachSubFile(action)
        }
    }
    action(this)
}