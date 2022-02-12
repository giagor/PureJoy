package com.topview.purejoy.home.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.topview.purejoy.video.data.bean.MVData
import com.topview.purejoy.video.data.bean.RecommendData
import com.topview.purejoy.video.data.bean.RecommendVideoJson
import org.json.JSONObject
import java.lang.reflect.Type


object JsonUtil {

    private val gson = Gson()
    /**
     * 解析推荐的视频的数据，这些数据的类型可能不一致，因此必须寻找公共的基类
     * 这里并没有采用将所有的data类型都继承一个SuperItem的方案，而是认为它们的共同点为都是JSONObject
     */
    fun parseRecommendVideoJson(jsonString: String): RecommendVideoJson {
        val result = RecommendVideoJson()
        val outerObject = JSONObject(jsonString)
        result.code = outerObject.getInt("code")
        result.message = outerObject.getString("msg")
        if (result.code != 200) {
            result.hasMore = false
            return result
        } else {
            result.hasMore = outerObject.getBoolean("hasmore")

            val dataList = mutableListOf<RecommendVideoJson.OuterData<Any>>()

            val outerDataArray = outerObject.getJSONArray("datas")
            for (i in 0 until outerDataArray.length()) {
                val dataObject = outerDataArray.getJSONObject(i)

                val type: Type? = when (dataObject.getInt("type")) {
                    1 -> {
                        object : TypeToken<RecommendVideoJson.OuterData<RecommendData>>(){}.type
                    }
                    2 -> {
                        object : TypeToken<RecommendVideoJson.OuterData<MVData>>(){}.type
                    }
                    else -> {
                        null
                    }
                }
                type?.let {
                    dataList.add(gson.fromJson(dataObject.toString(), it))
                }
            }
            if (dataList.isNotEmpty()) {
                result.outerList = dataList
            }
        }
        return result
    }
}