package com.eatssu.android.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.data.remote.dto.response.ReviewListResponse
import com.eatssu.android.data.remote.dto.response.toDomain
import com.eatssu.android.data.remote.service.ReviewService
import com.eatssu.android.domain.model.Review
import retrofit2.HttpException
import java.io.IOException

abstract class BaseReviewPagingSource(
    protected val reviewService: ReviewService,
) : PagingSource<Int, Review>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Review> {
        return try {
            val page = params.key ?: 0
            val pageSize = params.loadSize
            
            val response = executeRequest(page, pageSize)

            when (response) {
                is ApiResult.Success -> {
                    val reviews = response.data.toDomain()
                    val hasNext = response.data.hasNext
                    
                    LoadResult.Page(
                        data = reviews,
                        prevKey = if (page == 0) null else page - 1,
                        nextKey = if (hasNext) page + 1 else null
                    )
                }
                is ApiResult.Failure -> {
                    LoadResult.Error(Exception(response.message ?: "Failed to load reviews"))
                }
                is ApiResult.NetworkError -> {
                    LoadResult.Error(response.exception)
                }
                is ApiResult.UnknownError -> {
                    LoadResult.Error(response.exception)
                }
            }
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

    protected abstract suspend fun executeRequest(page: Int, size: Int): ApiResult<ReviewListResponse>
}