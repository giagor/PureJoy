package com.topview.purejoy.musiclibrary.playing.entity

class LrcItem(val time: Long, val content: String, val strTime: String) : Comparable<LrcItem> {

    override fun compareTo(other: LrcItem): Int {
        return (time - other.time).toInt()
    }

    override fun toString(): String {
        return "[LrcItem time = $time, content = $content]"
    }

    companion object {
        fun createItems(standardLrc: String?) : List<LrcItem>? {
            // 检查数据是否是标准lrc歌词
            if (standardLrc == null || standardLrc.isEmpty() ||
                standardLrc.indexOf("[") != 0 || (standardLrc.indexOf("]") != 9
                        && standardLrc.indexOf("]") != 10)) {
                return null
            }
            val lastRight = standardLrc.indexOf("]")
            val content = standardLrc.substring(lastRight + 1)
            val times = standardLrc.substring(0, lastRight + 1).
            replace("]", "-").replace("[", "-")
            val arrTime = times.split("-")
            val list = arrayListOf<LrcItem>()
            for (s in arrTime) {
                if (s.trim().isEmpty()) {
                    continue
                }

                val row = LrcItem(time = translateTime(s), content = content.substring(content.lastIndexOf(']') + 1),
                    strTime = s.replace(".", "-").split("-")[0])
                list.add(row)
            }
            return list

        }

        // 将表示时间的字符串转为Long
        private fun translateTime(timeString: String) : Long {
            val s = timeString.replace(".", ":")
            val arrTime = s.split(":")
            return arrTime[0].toLong()*60*1000 + arrTime[1].toLong()*1000 + arrTime[2].toLong()
        }
    }
}