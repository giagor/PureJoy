package com.topview.purejoy.musiclibrary.playing.util

import com.topview.purejoy.musiclibrary.playing.entity.LrcItem
import java.io.BufferedReader
import java.io.StringReader

class LrcParser {
    fun parse(resource: String): List<LrcItem>? {
        if(resource.isEmpty()) {
            return null
        }
        val reader = StringReader(resource)
        val bufferReader = BufferedReader(reader)
        val list = mutableListOf<LrcItem>()
        bufferReader.forEachLine {
            val rows = LrcItem.createItems(it)
            if (rows != null) {
                list.addAll(rows)
            }
        }
        reader.close()
        return list
    }
}