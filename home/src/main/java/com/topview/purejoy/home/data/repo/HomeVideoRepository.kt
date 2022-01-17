package com.topview.purejoy.home.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.topview.purejoy.common.net.ServiceCreator
import com.topview.purejoy.common.net.awaitSync
import com.topview.purejoy.home.data.api.HomeVideoService
import com.topview.purejoy.home.data.source.CategorySource
import com.topview.purejoy.home.entity.ExternVideo
import com.topview.purejoy.home.entity.VideoCategoryTab
import com.topview.purejoy.home.entity.recommendCategoryTab
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class HomeVideoRepository {
    private val service = ServiceCreator.create(HomeVideoService::class.java)

    suspend fun getCategoryArray(): Array<VideoCategoryTab> =
        withContext(Dispatchers.IO) {
            val json = service.getCategoryList().awaitSync()

            json?.data?.let { categoryList ->
                Array(
                    categoryList.size + 1
                ) {
                    if (it == 0) {
                        recommendCategoryTab
                    } else {
                        with(categoryList[it - 1]) {
                            VideoCategoryTab(
                                id = this.id,
                                content = this.name
                            )
                        }
                    }
                }
            } ?: emptyArray()
        }

    /**
     * 根据类别获取ExternVideo数据流
     */
    fun getVideoFlowByCategory(
        categoryId: Long,
        pageSize: Int = 6
    ): Flow<PagingData<ExternVideo>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { CategorySource(categoryId = categoryId, service = service) }
        ).flow
    }
}