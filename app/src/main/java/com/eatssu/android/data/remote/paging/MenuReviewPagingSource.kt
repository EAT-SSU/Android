package com.eatssu.android.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.eatssu.android.data.remote.dto.response.toDomain
import com.eatssu.android.data.remote.service.ReviewService
import com.eatssu.android.domain.model.Review
import retrofit2.HttpException
import java.io.IOException

class MenuReviewPagingSource(
    private val reviewService: ReviewService,
    private val menuId: Long?
) : PagingSource<Int, Review>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Review> {
        return try {
            val page = params.key ?: 0
            val pageSize = params.loadSize
            
            val response = reviewService.getMenuReviewList(
                menuId = menuId,
                page = page,
                size = pageSize
            )

            if (response.isSuccess()) {
                val reviews = response.getOrNull()?.toDomain() ?: emptyList()
                val hasNext = response.getOrNull()?.hasNext ?: false
                
                LoadResult.Page(
                    data = reviews,
                    prevKey = if (page == 0) null else page - 1,
                    nextKey = if (hasNext) page + 1 else null
                )
            } else {
                LoadResult.Error(Exception("Failed to load menu reviews"))
            }
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Review>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}