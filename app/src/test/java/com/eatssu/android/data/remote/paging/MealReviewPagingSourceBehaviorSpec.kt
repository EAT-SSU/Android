package com.eatssu.android.data.remote.paging

import androidx.paging.PagingSource
import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.data.remote.dto.response.MealReviewListResponse
import com.eatssu.android.data.remote.service.ReviewService
import com.eatssu.android.test.AppBehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class MealReviewPagingSourceBehaviorSpec : AppBehaviorSpec({

    given("MealReviewPagingSource") {
        val reviewService = mockk<ReviewService>()

        `when`("API가 성공하면") {
            val response = MealReviewListResponse(
                numberOfElements = 1,
                hasNext = false,
                dataList = listOf(
                    MealReviewListResponse.DataList(
                        reviewId = 20L,
                        menuList = listOf(
                            MealReviewListResponse.DataList.MenuList(
                                id = 200L,
                                name = "비빔밥",
                                isLike = false,
                            )
                        ),
                        isWriter = false,
                        writerNickname = "guest",
                        rating = 4,
                        writtenAt = "2025-01-02",
                        content = "nice",
                        imageUrls = emptyList(),
                    )
                ),
            )
            coEvery { reviewService.getMealReviewList(2L, 0, 20, any()) } returns ApiResult.Success(response)
            val source = MealReviewPagingSource(reviewService, mealId = 2L)

            then("도메인 리뷰를 담은 Page를 반환한다") {
                runTest {
                    val result = source.load(
                        PagingSource.LoadParams.Refresh(
                            key = null,
                            loadSize = 20,
                            placeholdersEnabled = false,
                        )
                    ) as PagingSource.LoadResult.Page

                    result.data.first().reviewId shouldBe 20L
                    result.nextKey shouldBe null
                }
            }
        }

        `when`("API Failure를 받으면") {
            coEvery { reviewService.getMealReviewList(2L, 0, 20, any()) } returns ApiResult.Failure(500, "oops")
            val source = MealReviewPagingSource(reviewService, mealId = 2L)

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

        `when`("API UnknownError를 받으면") {
            coEvery {
                reviewService.getMealReviewList(2L, 0, 20, any())
            } returns ApiResult.UnknownError(IOException("boom"))
            val source = MealReviewPagingSource(reviewService, mealId = 2L)

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
