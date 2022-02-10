package com.topview.purejoy.common.widget.lyric.parser

import com.topview.purejoy.common.widget.lyric.Sentence
import java.io.BufferedReader
import java.io.StringReader

/**
 * 一个简易的LRC文件解析工具
 */
class LyricParser(
    /**
     * 完整的歌词字符串，以换行符分隔每行内容
     */
    private val lyricString: String
) {
    /**
     * 歌词信息, 读取Sentence之前应当先调用[parse]
     */
    lateinit var sentence: List<Sentence>
        private set

    /**
     * LRC的TAG信息，例如指示作者的标签[by:阿卡林]
     */
    lateinit var tags: List<LyricTag>
        private set

    fun parse(): LyricParser {
        val sentences = mutableListOf<Sentence>()
        val cacheTags = mutableListOf<LyricTag>()
        runCatching {
            val reader = BufferedReader(StringReader(lyricString))
            var temp = reader.readLine()
            while (temp != null) {
                val left = temp.indexOf("[")
                val right = temp.indexOf("]")
                if (left == -1 || right == -1 || right - left < 2) {
                    continue
                } else {
                    val tagContent = temp.substring(left + 1, right)
                    // 先尝试解析成时间信息
                    val time = parseTime(tagContent)
                    if (time >= 0) {
                        val length = sentences.size
                        sentences.add(
                            SimpleSentence(
                                fromTime = time,
                                content = if (right == temp.length - 1) "" else
                                    temp.substring(right + 1, temp.length),
                                index = length
                            )
                        )
                    } else {
                        parseTag(tagContent)?.let {
                            cacheTags.add(it)
                        }
                    }
                }
                temp = reader.readLine()
            }
        }
        sentence = sentences
        tags = cacheTags
        return this
    }

    private fun parseTime(str: String): Long {
        val times = str.split(":", ".", limit = 3)
        if (times.size != 3) {
            return -1L
        }
        return runCatching {
            times[0].toLong() * 60 * 1000 + times[1].toLong() * 1000 + times[2].toLong()
        }.getOrDefault(-1L)
    }

    private fun parseTag(str: String): LyricTag? {
        val tags = str.split(":", limit = 2)
        return if (tags.size < 2) {
            null
        } else {
            LyricTag(
                name = tags[0],
                content = tags[1]
            )
        }
    }

    data class SimpleSentence(
        override val fromTime: Long,
        override val content: String,
        override val index: Int
    ): Sentence
}