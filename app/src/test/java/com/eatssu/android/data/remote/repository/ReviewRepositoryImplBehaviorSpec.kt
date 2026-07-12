package com.eatssu.android.data.remote.repository

import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.data.remote.dto.request.ModifyReviewRequest
import com.eatssu.android.data.remote.dto.request.WriteMealReviewRequest
import com.eatssu.android.data.remote.dto.request.WriteMenuReviewRequest
import com.eatssu.android.data.remote.dto.response.ImageResponse
import com.eatssu.android.data.remote.dto.response.MealReviewInfoResponse
import com.eatssu.android.data.remote.dto.response.MenuList
import com.eatssu.android.data.remote.dto.response.MenuOfMealResponse
import com.eatssu.android.data.remote.dto.response.MenuReviewInfoResponse
import com.eatssu.android.data.remote.dto.response.MyReviewListResponse
import com.eatssu.android.data.remote.dto.response.ReviewTranslationResponse
import com.eatssu.android.data.remote.service.ReviewService
import com.eatssu.android.domain.model.Review
import com.eatssu.android.test.AppBehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import java.io.File
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class ReviewRepositoryImplBehaviorSpec : AppBehaviorSpec({

    given("ReviewRepositoryImpl") {
        val service = mockk<ReviewService>()
        val repository = ReviewRepositoryImpl(service)

        `when`("writeMealReview를 호출하면") {
            val requestSlot = slot<WriteMealReviewRequest>()
            coEvery { service.writeMealReview(capture(requestSlot)) } returns ApiResult.Success(Unit)

            then("요청 바디를 menuLikes로 매핑하고 성공 true를 반환한다") {
                runTest {
                    val result = repository.writeMealReview(
                        mealId = 10L,
                        rating = 4,
                        content = "맛있어요",
                        imageUrls = listOf("https://img"),
                        likeMenuIdList = listOf(1L, 2L),
                    )

                    result shouldBe true
                    requestSlot.captured.mealId shouldBe 10L
                    requestSlot.captured.rating shouldBe 4
                    requestSlot.captured.content shouldBe "맛있어요"
                    requestSlot.captured.imageUrls shouldBe listOf("https://img")
                    requestSlot.captured.menuLikes shouldBe listOf(
                        WriteMealReviewRequest.MenuLikes(menuId = 1L, isLike = true),
                        WriteMealReviewRequest.MenuLikes(menuId = 2L, isLike = true),
                    )
                }
            }
        }

        `when`("writeMealReview에서 likeMenuIdList가 null이면") {
            val requestSlot = slot<WriteMealReviewRequest>()
            coEvery { service.writeMealReview(capture(requestSlot)) } returns ApiResult.Success(Unit)

            then("menuLikes=null로 전달한다") {
                runTest {
                    repository.writeMealReview(
                        mealId = 1L,
                        rating = 5,
                        content = "content",
                        imageUrls = emptyList(),
                        likeMenuIdList = null,
                    )
                    requestSlot.captured.menuLikes shouldBe null
                }
            }
        }

        `when`("writeMealReview API가 실패하면") {
            coEvery { service.writeMealReview(any()) } returns ApiResult.Failure(400, "bad")

            then("false를 반환한다") {
                runTest {
                    repository.writeMealReview(1L, 5, "x", emptyList(), listOf(1L)) shouldBe false
                }
            }
        }

        `when`("writeMenuReview를 호출하면") {
            val requestSlot = slot<WriteMenuReviewRequest>()
            coEvery { service.writeMenuReview(capture(requestSlot)) } returns ApiResult.Success(Unit)

            then("menuId와 isLike를 menuLike로 전달한다") {
                runTest {
                    repository.writeMenuReview(
                        menuId = 9L,
                        rating = 3,
                        content = "메뉴리뷰",
                        imageUrls = listOf("img"),
                        likeMenuIdList = listOf(9L, 10L),
                    ) shouldBe true

                    requestSlot.captured.rating shouldBe 3
                    requestSlot.captured.menuLike shouldBe WriteMenuReviewRequest.MenuLike(
                        menuId = 9L,
                        isLike = true,
                    )
                    requestSlot.captured.imageUrls shouldBe listOf("img")
                }
            }
        }

        `when`("writeMenuReview에서 likeMenuIdList가 null이면") {
            val requestSlot = slot<WriteMenuReviewRequest>()
            coEvery { service.writeMenuReview(capture(requestSlot)) } returns ApiResult.Success(Unit)

            then("menuLike.isLike=false로 전달한다") {
                runTest {
                    repository.writeMenuReview(
                        menuId = 1L,
                        rating = 2,
                        content = "x",
                        imageUrls = emptyList(),
                        likeMenuIdList = null,
                    )
                    requestSlot.captured.menuLike shouldBe WriteMenuReviewRequest.MenuLike(
                        menuId = 1L,
                        isLike = false,
                    )
                }
            }
        }

        `when`("writeMenuReview에서 likeMenuIdList가 빈 리스트면") {
            val requestSlot = slot<WriteMenuReviewRequest>()
            coEvery { service.writeMenuReview(capture(requestSlot)) } returns ApiResult.Success(Unit)

            then("menuLike.isLike=false로 전달하고 정상 처리한다") {
                runTest {
                    repository.writeMenuReview(
                        menuId = 1L,
                        rating = 1,
                        content = "x",
                        imageUrls = emptyList(),
                        likeMenuIdList = emptyList(),
                    ) shouldBe true

                    requestSlot.captured.menuLike shouldBe WriteMenuReviewRequest.MenuLike(
                        menuId = 1L,
                        isLike = false,
                    )
                }
            }
        }

        `when`("deleteReview API 결과가 성공이면") {
            coEvery { service.deleteReview(100L) } returns ApiResult.Success(Unit)

            then("true를 반환한다") {
                runTest {
                    repository.deleteReview(100L) shouldBe true
                }
            }
        }

        `when`("deleteReview API 결과가 실패면") {
            coEvery { service.deleteReview(100L) } returns ApiResult.UnknownError(IllegalStateException("boom"))

            then("false를 반환한다") {
                runTest {
                    repository.deleteReview(100L) shouldBe false
                }
            }
        }

        `when`("modifyReview를 호출하면") {
            val requestSlot = slot<ModifyReviewRequest>()
            coEvery { service.modifyReview(7L, capture(requestSlot)) } returns ApiResult.Success(Unit)

            then("menuLikeInfoList를 요청 DTO로 매핑한다") {
                runTest {
                    val menuLikeInfo = listOf(
                        Review.MenuLikeInfo(menuId = 1L, name = "A", isLike = true),
                        Review.MenuLikeInfo(menuId = 2L, name = "B", isLike = false),
                    )
                    repository.modifyReview(
                        reviewId = 7L,
                        rating = 5,
                        content = "수정",
                        menuLikeInfoList = menuLikeInfo,
                    ) shouldBe true

                    requestSlot.captured.rating shouldBe 5
                    requestSlot.captured.content shouldBe "수정"
                    requestSlot.captured.menuLikes shouldBe listOf(
                        ModifyReviewRequest.MenuLikes(menuId = 1L, isLike = true),
                        ModifyReviewRequest.MenuLikes(menuId = 2L, isLike = false),
                    )
                }
            }
        }

        `when`("modifyReview API 결과가 실패면") {
            coEvery { service.modifyReview(any(), any()) } returns ApiResult.Failure(500, "error")

            then("false를 반환한다") {
                runTest {
                    repository.modifyReview(
                        reviewId = 1L,
                        rating = 1,
                        content = "x",
                        menuLikeInfoList = emptyList(),
                    ) shouldBe false
                }
            }
        }

        `when`("getMealReviewInfo API가 성공하면") {
            coEvery { service.getMealReviewInfo(3L) } returns ApiResult.Success(
                MealReviewInfoResponse(
                    totalReviewCount = 12,
                    rating = 4.46,
                    reviewRatingCount = MealReviewInfoResponse.ReviewRatingCount(
                        oneStarCount = 1,
                        twoStarCount = 2,
                        threeStarCount = 3,
                        fourStarCount = 4,
                        fiveStarCount = 5,
                    ),
                )
            )

            then("도메인 ReviewInfo로 변환한다") {
                runTest {
                    val result = repository.getMealReviewInfo(3L)
                    result?.reviewCnt shouldBe 12
                    result?.rating shouldBe 4.5
                    result?.oneStarCount shouldBe 1
                    result?.fiveStarCount shouldBe 5
                }
            }
        }

        `when`("getMealReviewInfo API가 실패하면") {
            coEvery { service.getMealReviewInfo(3L) } returns ApiResult.Failure(404, "not found")

            then("null을 반환한다") {
                runTest {
                    repository.getMealReviewInfo(3L) shouldBe null
                }
            }
        }

        `when`("getMenuReviewInfo API가 성공하면") {
            coEvery { service.getMenuReviewInfo(4L) } returns ApiResult.Success(
                MenuReviewInfoResponse(
                    totalReviewCount = 3,
                    rating = 3.24,
                    reviewRatingCount = null,
                )
            )

            then("도메인 ReviewInfo로 변환하며 null 카운트는 0으로 채운다") {
                runTest {
                    val result = repository.getMenuReviewInfo(4L)
                    result?.reviewCnt shouldBe 3
                    result?.rating shouldBe 3.2
                    result?.oneStarCount shouldBe 0
                    result?.fiveStarCount shouldBe 0
                }
            }
        }

        `when`("getMenuReviewInfo API가 실패하면") {
            coEvery { service.getMenuReviewInfo(4L) } returns ApiResult.NetworkError(IOException("offline"))

            then("null을 반환한다") {
                runTest {
                    repository.getMenuReviewInfo(4L) shouldBe null
                }
            }
        }

        `when`("getImageString API가 성공하면") {
            coEvery { service.uploadImage(any()) } returns ApiResult.Success(ImageResponse(url = "https://img"))

            then("업로드된 이미지 URL을 반환한다") {
                runTest {
                    val file = File.createTempFile("review", ".jpg").apply { writeText("x") }
                    try {
                        repository.getImageString(file) shouldBe "https://img"
                    } finally {
                        file.delete()
                    }
                    coVerify(exactly = 1) { service.uploadImage(any()) }
                }
            }
        }

        `when`("getImageString API가 실패하면") {
            coEvery { service.uploadImage(any()) } returns ApiResult.Failure(500, "error")

            then("null을 반환한다") {
                runTest {
                    val file = File.createTempFile("review", ".jpg").apply { writeText("x") }
                    try {
                        repository.getImageString(file) shouldBe null
                    } finally {
                        file.delete()
                    }
                }
            }
        }

        `when`("getValidMenusByMealId API가 성공하면") {
            coEvery { service.getMenuInfoByMealId(8L) } returns ApiResult.Success(
                MenuOfMealResponse(
                    menuList = arrayListOf(
                        MenuList(menuId = 11L, name = "돈까스"),
                        MenuList(menuId = null, name = null),
                    )
                )
            )

            then("MenuMini 리스트로 변환하며 null 필드는 기본값으로 매핑한다") {
                runTest {
                    val result = repository.getValidMenusByMealId(8L)
                    result shouldHaveSize 2
                    result[0].id shouldBe 11L
                    result[0].name shouldBe "돈까스"
                    result[1].id shouldBe -1L
                    result[1].name shouldBe ""
                }
            }
        }

        `when`("getValidMenusByMealId API가 실패하면") {
            coEvery { service.getMenuInfoByMealId(8L) } returns ApiResult.Failure(500, "error")

            then("빈 리스트를 반환한다") {
                runTest {
                    repository.getValidMenusByMealId(8L) shouldBe emptyList()
                }
            }
        }

        `when`("getMyReviews API가 성공하면") {
            coEvery { service.getMyReviews() } returns ApiResult.Success(
                MyReviewListResponse(
                    dataList = arrayListOf(
                        MyReviewListResponse.DataList(
                            reviewId = 15L,
                            rating = 5,
                            writtenAt = "2025-01-01",
                            content = "great",
                            imageUrls = arrayListOf("https://img"),
                            menuList = arrayListOf(
                                MyReviewListResponse.DataList.MenuList(
                                    id = 7L,
                                    name = "제육",
                                    isLike = true,
                                )
                            ),
                        )
                    )
                )
            )

            then("Review 리스트로 매핑한다") {
                runTest {
                    val result = repository.getMyReviews()
                    result shouldHaveSize 1
                    result.first().reviewId shouldBe 15L
                    result.first().menuLikeInfoList.first().menuId shouldBe 7L
                    result.first().imgUrl shouldBe "https://img"
                }
            }
        }

        `when`("getMyReviews API가 실패하면") {
            coEvery { service.getMyReviews() } returns ApiResult.UnknownError(IllegalStateException("boom"))

            then("빈 리스트를 반환한다") {
                runTest {
                    repository.getMyReviews() shouldBe emptyList()
                }
            }
        }
        `when`("getReviewTranslation API succeeds") {
            coEvery { service.getReviewTranslation(1L, "EN") } returns ApiResult.Success(
                ReviewTranslationResponse(
                    reviewId = 1L,
                    sourceLanguage = "KO",
                    targetLanguage = "EN",
                    translatedContent = "It was delicious.",
                    provider = "DEEPL",
                    cached = true,
                )
            )

            then("maps to ReviewTranslation") {
                runTest {
                    val result = repository.getReviewTranslation(1L, "EN")
                    result?.reviewId shouldBe 1L
                    result?.translatedContent shouldBe "It was delicious."
                    result?.cached shouldBe true
                }
            }
        }

        `when`("getReviewTranslation API fails") {
            coEvery { service.getReviewTranslation(2L, "EN") } returns ApiResult.Failure(500, "error")

            then("returns null") {
                runTest {
                    repository.getReviewTranslation(2L, "EN") shouldBe null
                }
            }
        }
    }
})
