package com.topview.purejoy.home.discover.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.topview.purejoy.home.R
import com.topview.purejoy.home.discover.adapter.DailyRecommendPlayListAdapter

class DailyRecommendPlayListDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        // 获取适配器
        val adapter: DailyRecommendPlayListAdapter = parent.adapter as DailyRecommendPlayListAdapter
        // 获取Item的数量
        val itemCounts = adapter.itemCount
        // 获取子View在Adapter中的位置
        val position = parent.getChildAdapterPosition(view)
        // 获取Item间的间距
        val itemSpacing =
            parent.context.resources.getDimension(R.dimen.home_discover_daily_recommend_playlist_horizontal_spacing)
        // 如果不是最后一个Item
        if (position != itemCounts - 1) {
            outRect.right = itemSpacing.toInt()
        }
    }
}