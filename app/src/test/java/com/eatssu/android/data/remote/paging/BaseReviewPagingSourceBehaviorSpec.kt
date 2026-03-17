package com.eatssu.android.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.eatssu.android.domain.model.Review
import com.eatssu.android.test.AppBehaviorSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

private class TestBaseReviewPagingSource(
    private val execute: suspend (page: Int, size: Int) -> TestResponse,
) : BaseReviewPagingSource<TestResponse>() {
    override suspend fun executeRequest(page: Int, size: Int): TestResponse = execute(page, size)
    override fun TestResponse.toReviewList(): List<Review> = reviews
    override fun TestResponse.hasMorePages(): Boolean = hasNext
}

private data class TestResponse(
    val reviews: List<Review>,
    val hasNext: Boolean,
)

@OptIn(ExperimentalCoroutinesApi::class)
class BaseReviewPagingSourceBehaviorSpec : AppBehaviorSpec({

    given("BaseReviewPagingSource") {
        `when`("첫 페이지 조회가 성공하고 다음 페이지가 있으면") {
            val source = TestBaseReviewPagingSource { _, _ ->
                TestResponse(
                    reviews = listOf(
                        Review(
                            reviewId = 1L,
                            isWriter = true,
                            menuLikeInfoList = emptyList(),
                            writerNickname = "writer",
                            rating = 5,
                            writeDate = "2025-01-01",
                            content = "good",
                            imgUrl = null,
                        )
                    ),
                    hasNext = true,
                )
            }

            then("prevKey는 null, nextKey는 1인 Page를 반환한다") {
                runTest {
                    val result = source.load(
                        PagingSource.LoadParams.Refresh(
                            key = null,
                            loadSize = 20,
                            placeholdersEnabled = false,
                        )
                    ) as PagingSource.LoadResult.Page

                    result.prevKey shouldBe null
                    result.nextKey shouldBe 1
                    result.data.size shouldBe 1
                }
            }
        }

        `when`("중간 페이지 조회가 성공하고 다음 페이지가 없으면") {
            val source = TestBaseReviewPagingSource { _, _ ->
                TestResponse(reviews = emptyList(), hasNext = false)
            }

            then("prevKey는 page-1, nextKey는 null이다") {
                runTest {
                    val result = source.load(
                        PagingSource.LoadParams.Append(
                            key = 2,
                            loadSize = 20,
                            placeholdersEnabled = false,
                        )
                    ) as PagingSource.LoadResult.Page

                    result.prevKey shouldBe 1
                    result.nextKey shouldBe null
                }
            }
        }

        `when`("요청 중 예외가 발생하면") {
            val source = TestBaseReviewPagingSource { _, _ ->
                throw IllegalStateException("boom")
            }

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

        `when`("getRefreshKey를 호출하면") {
            val source = TestBaseReviewPagingSource { _, _ -> TestResponse(emptyList(), hasNext = true) }
            val page = PagingSource.LoadResult.Page(
                data = listOf(
                    Review(
                        reviewId = 1L,
                        isWriter = false,
                        menuLikeInfoList = emptyList(),
                        writerNickname = "writer",
                        rating = 3,
                        writeDate = "2025-01-01",
                        content = "ok",
                        imgUrl = null,
                    )
                ),
                prevKey = 3,
                nextKey = 5,
            )
            val state = PagingState(
                pages = listOf(page),
                anchorPosition = 0,
                config = androidx.paging.PagingConfig(pageSize = 20),
                leadingPlaceholderCount = 0,
            )

            then("anchor 기준으로 적절한 refresh key를 계산한다") {
                source.getRefreshKey(state) shouldBe 4
            }
        }
    }
})
