package com.topview.purejoy.home.discover.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.topview.purejoy.home.R
import com.topview.purejoy.home.discover.adapter.RecommendNewSongAdapter
import com.topview.purejoy.home.util.Common.DISCOVER_RECOMMEND_NEW_SONG_ITEM_HORIZONTAL_SPACING
import com.topview.purejoy.home.util.Common.DISCOVER_RECOMMEND_NEW_SONG_ITEM_WIDTH_SMALLER_THAN_SCREEN

class RecommendNewSongDecoration : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        // 获取Adapter
        val adapter: RecommendNewSongAdapter = parent.adapter as RecommendNewSongAdapter
        // 获取LayoutManager
        val layoutManager: GridLayoutManager = parent.layoutManager as GridLayoutManager
        // 获取Item的数量
        val itemCounts = adapter.itemCount
        // 获取行数
        val row = layoutManager.spanCount
        // 计算最后一列的子View的数量
        var lastColumnCount = itemCounts % row
        if (lastColumnCount == 0) {
            lastColumnCount = row
        }

        // 获取子View在Adapter中的位置
        val position = parent.getChildAdapterPosition(view)

        // 不是最后一列
        if (position < itemCounts - lastColumnCount) {
            outRect.right = DISCOVER_RECOMMEND_NEW_SONG_ITEM_HORIZONTAL_SPACING
        } else {
            // 最后一列
            val discoverPaddingLeft =
                parent.context.resources.getDimension(R.dimen.home_discover_padding_left)
            val discoverPaddingRight =
                parent.context.resources.getDimension(R.dimen.home_discover_padding_right)
            outRect.right = (DISCOVER_RECOMMEND_NEW_SONG_ITEM_WIDTH_SMALLER_THAN_SCREEN -
                    discoverPaddingLeft - discoverPaddingRight).toInt()
        }
    }
}