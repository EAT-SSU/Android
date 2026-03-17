package com.eatssu.android.data.remote.paging

import androidx.paging.PagingSource
import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.data.remote.dto.response.MenuReviewListResponse
import com.eatssu.android.data.remote.service.ReviewService
import com.eatssu.android.test.AppBehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class MenuReviewPagingSourceBehaviorSpec : AppBehaviorSpec({

    given("MenuReviewPagingSource") {
        val reviewService = mockk<ReviewService>()

        `when`("API가 성공하면") {
            val response = MenuReviewListResponse(
                numberOfElements = 1,
                hasNext = true,
                dataList = listOf(
                    MenuReviewListResponse.DataList(
                        reviewId = 10L,
                        menu = MenuReviewListResponse.DataList.Menu(
                            id = 100L,
                            name = "돈까스",
                            isLike = true,
                        ),
                        isWriter = true,
                        writerNickname = "writer",
                        rating = 5,
                        writtenAt = "2025-01-01",
                        content = "good",
                        imageUrls = emptyList(),
                    )
                ),
            )
            coEvery { reviewService.getMenuReviewList(1L, 0, 20, any()) } returns ApiResult.Success(response)
            val source = MenuReviewPagingSource(reviewService, menuId = 1L)

            then("도메인 리뷰를 담은 Page를 반환한다") {
                runTest {
                    val result = source.load(
                        PagingSource.LoadParams.Refresh(
                            key = null,
                            loadSize = 20,
                            placeholdersEnabled = false,
                        )
                    ) as PagingSource.LoadResult.Page

                    result.data.first().reviewId shouldBe 10L
                    result.nextKey shouldBe 1
                }
            }
        }

        `when`("API Failure를 받으면") {
            coEvery { reviewService.getMenuReviewList(1L, 0, 20, any()) } returns ApiResult.Failure(400, "bad")
            val source = MenuReviewPagingSource(reviewService, menuId = 1L)

            then("LoadResult.Error를 반환한다") {
                runTest {
                    val result = source.load(
                        PagingSource.LoadParams.Refresh(
                            key = null,
                            loadSize = 20,
                            placeholdersEnabled = false,
                        )
                    )
                    (result is PagingSource.LoadResult.Error) shouldBe true
                }
            }
        }

        `when`("API NetworkError를 받으면") {
            coEvery {
                reviewService.getMenuReviewList(1L, 0, 20, any())
            } returns ApiResult.NetworkError(IOException("offline"))
            val source = MenuReviewPagingSource(reviewService, menuId = 1L)

            then("LoadResult.Error를 반환한다") {
                runTest {
                    val result = source.load(
                        PagingSource.LoadParams.Refresh(
                            key = null,
                            loadSize = 20,
                            placeholdersEnabled = false,
                        )
                    )
                    (result is PagingSource.LoadResult.Error) shouldBe true
                }
            }
        }
    }
})
