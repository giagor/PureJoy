package com.topview.purejoy.home.discover.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.topview.purejoy.home.R
import com.topview.purejoy.home.discover.adapter.RecommendNewSongAdapter

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

        // 调整水平间距
        // 不是最后一列
        if (position < itemCounts - lastColumnCount) {
            outRect.right =
                parent.context.resources
                    .getDimension(R.dimen.home_discover_recommend_new_song_item_horizontal_spacing)
                    .toInt()
        } else {
            // 最后一列
            val discoverPaddingLeft =
                parent.context.resources.getDimension(R.dimen.home_padding_left)
            val discoverPaddingRight =
                parent.context.resources.getDimension(R.dimen.home_padding_right)
            val itemWidthSmallerThanScreen = parent.context.resources
                .getDimension(R.dimen.home_discover_recommend_new_song_item_width_smaller_than_screen)
            outRect.right =
                (itemWidthSmallerThanScreen - discoverPaddingLeft - discoverPaddingRight).toInt()
        }

        // 调整纵向间距
        // 若Item所在位置不是最后一行
        if (position % row != row - 1) {
            val verticalSpacing =
                parent.context.resources.getDimension(R.dimen.home_discover_recommend_new_song_vertical_spacing)
            outRect.bottom = verticalSpacing.toInt()
        }
    }
}