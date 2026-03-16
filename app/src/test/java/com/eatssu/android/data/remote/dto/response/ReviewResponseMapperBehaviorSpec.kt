package com.eatssu.android.data.remote.dto.response

import com.eatssu.android.test.AppBehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class ReviewResponseMapperBehaviorSpec : AppBehaviorSpec({

    given("MenuReviewInfoResponse.toDomain") {
        `when`("rating/count가 주어지면") {
            then("소수 첫째 자리 반올림 및 null 카운트 기본값을 적용한다") {
                val result = MenuReviewInfoResponse(
                    totalReviewCount = 10,
                    rating = 4.44,
                    reviewRatingCount = MenuReviewInfoResponse.ReviewRatingCount(
                        oneStarCount = 1,
                        twoStarCount = 2,
                        threeStarCount = 3,
                        fourStarCount = 4,
                        fiveStarCount = 5,
                    ),
                ).toDomain()

                result.reviewCnt shouldBe 10
                result.rating shouldBe 4.4
                result.oneStarCount shouldBe 1
                result.fiveStarCount shouldBe 5
            }
        }

        `when`("reviewRatingCount가 null이면") {
            then("별점 카운트는 모두 0으로 채운다") {
                val result = MenuReviewInfoResponse(
                    totalReviewCount = null,
                    rating = null,
                    reviewRatingCount = null,
                ).toDomain()

                result.reviewCnt shouldBe 0
                result.rating shouldBe 0.0
                result.oneStarCount shouldBe 0
                result.fiveStarCount shouldBe 0
            }
        }
    }

    given("MealReviewInfoResponse.toDomain") {
        `when`("rating/count가 주어지면") {
            then("소수 첫째 자리 반올림 및 기본값 매핑을 적용한다") {
                val result = MealReviewInfoResponse(
                    totalReviewCount = 7,
                    rating = 3.66,
                    reviewRatingCount = MealReviewInfoResponse.ReviewRatingCount(
                        oneStarCount = 0,
                        twoStarCount = 1,
                        threeStarCount = 2,
                        fourStarCount = 3,
                        fiveStarCount = 1,
                    ),
                ).toDomain()

                result.reviewCnt shouldBe 7
                result.rating shouldBe 3.7
                result.twoStarCount shouldBe 1
                result.fourStarCount shouldBe 3
            }
        }
    }

    given("MenuReviewListResponse?.toDomain") {
        `when`("응답 자체가 null이면") {
            then("빈 리스트를 반환한다") {
                (null as MenuReviewListResponse?).toDomain() shouldBe emptyList()
            }
        }

        `when`("dataList를 도메인으로 변환하면") {
            val response = MenuReviewListResponse(
                dataList = listOf(
                    MenuReviewListResponse.DataList(
                        reviewId = 1L,
                        menu = MenuReviewListResponse.DataList.Menu(
                            id = 10L,
                            name = "돈까스",
                            isLike = true,
                        ),
                        isWriter = true,
                        writerNickname = "writer",
                        rating = 5,
                        writtenAt = "2025-01-01",
                        content = "great",
                        imageUrls = listOf("https://img1", "https://img2"),
                    ),
                    MenuReviewListResponse.DataList(
                        reviewId = null,
                        menu = null,
                        isWriter = null,
                        writerNickname = null,
                        rating = null,
                        writtenAt = null,
                        content = null,
                        imageUrls = emptyList(),
                    ),
                )
            )

            then("기본값과 첫 번째 이미지 URL 규칙을 적용한다") {
                val result = response.toDomain()
                result shouldHaveSize 2

                result[0].reviewId shouldBe 1L
                result[0].menuLikeInfoList.first().menuId shouldBe 10L
                result[0].menuLikeInfoList.first().isLike shouldBe true
                result[0].imgUrl shouldBe "https://img1"

                result[1].reviewId shouldBe -1L
                result[1].menuLikeInfoList.first().menuId shouldBe -1L
                result[1].menuLikeInfoList.first().name shouldBe ""
                result[1].isWriter shouldBe false
                result[1].imgUrl shouldBe null
            }
        }
    }

    given("MealReviewListResponse?.toDomain") {
        `when`("응답 자체가 null이면") {
            then("빈 리스트를 반환한다") {
                (null as MealReviewListResponse?).toDomain() shouldBe emptyList()
            }
        }

        `when`("dataList를 도메인으로 변환하면") {
            val response = MealReviewListResponse(
                dataList = listOf(
                    MealReviewListResponse.DataList(
                        reviewId = 3L,
                        menuList = listOf(
                            MealReviewListResponse.DataList.MenuList(id = 1L, name = "제육", isLike = true),
                            MealReviewListResponse.DataList.MenuList(id = null, name = null, isLike = null),
                        ),
                        isWriter = false,
                        writerNickname = "other",
                        rating = 4,
                        writtenAt = "2025-01-02",
                        content = "ok",
                        imageUrls = listOf("https://meal"),
                    )
                )
            )

            then("menuList를 포함해 도메인 Review로 매핑한다") {
                val result = response.toDomain()
                result shouldHaveSize 1
                result.first().reviewId shouldBe 3L
                result.first().menuLikeInfoList shouldHaveSize 2
                result.first().menuLikeInfoList[0].name shouldBe "제육"
                result.first().menuLikeInfoList[1].menuId shouldBe -1L
                result.first().menuLikeInfoList[1].isLike shouldBe false
                result.first().imgUrl shouldBe "https://meal"
            }
        }
    }

    given("MyReviewListResponse?.toDomain") {
        `when`("응답 자체가 null이면") {
            then("빈 리스트를 반환한다") {
                (null as MyReviewListResponse?).toDomain() shouldBe emptyList()
            }
        }

        `when`("dataList를 도메인으로 변환하면") {
            val response = MyReviewListResponse(
                dataList = arrayListOf(
                    MyReviewListResponse.DataList(
                        reviewId = 100L,
                        rating = 5,
                        writtenAt = "2025-01-03",
                        content = "best",
                        imageUrls = arrayListOf("https://my"),
                        menuList = arrayListOf(
                            MyReviewListResponse.DataList.MenuList(id = 9L, name = "라면", isLike = true),
                            MyReviewListResponse.DataList.MenuList(id = null, name = null, isLike = null),
                        ),
                    )
                )
            )

            then("isWriter=true로 고정하고 기본값 매핑을 적용한다") {
                val result = response.toDomain()
                result shouldHaveSize 1
                result.first().isWriter shouldBe true
                result.first().reviewId shouldBe 100L
                result.first().menuLikeInfoList shouldHaveSize 2
                result.first().menuLikeInfoList[0].name shouldBe "라면"
                result.first().menuLikeInfoList[1].menuId shouldBe -1L
                result.first().writerNickname shouldBe ""
                result.first().imgUrl shouldBe "https://my"
            }
        }
    }
})
