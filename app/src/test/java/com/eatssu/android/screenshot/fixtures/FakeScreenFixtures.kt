package com.eatssu.android.screenshot.fixtures

import com.eatssu.android.domain.model.MenuMini
import com.eatssu.android.domain.model.Partnership
import com.eatssu.android.domain.model.PartnershipRestaurant
import com.eatssu.android.domain.model.RestaurantType
import com.eatssu.android.domain.model.Review
import com.eatssu.android.domain.model.ReviewInfo

object FakeScreenFixtures {
    fun menuList(): List<MenuMini> = listOf(
        MenuMini(1, "돈까스"),
        MenuMini(2, "김치찌개"),
        MenuMini(3, "소불고기"),
    )

    fun menuLikeInfoList(): List<Review.MenuLikeInfo> = listOf(
        Review.MenuLikeInfo(1, "돈까스", true),
        Review.MenuLikeInfo(2, "김치찌개", false),
        Review.MenuLikeInfo(3, "소불고기", true),
    )

    fun reviewList(count: Int = 3, writer: Boolean = true): List<Review> {
        return (1..count).map { index ->
            Review(
                isWriter = writer,
                reviewId = index.toLong(),
                menuLikeInfoList = menuLikeInfoList(),
                writerNickname = "유저$index",
                rating = 4,
                writeDate = "2025.01.0$index",
                content = "스크린샷 테스트 리뷰 $index",
                imgUrl = null,
            )
        }
    }

    fun reviewInfo(reviewCount: Int = 3): ReviewInfo {
        return ReviewInfo(
            reviewCnt = reviewCount,
            rating = if (reviewCount == 0) 0.0 else 4.2,
            oneStarCount = 0,
            twoStarCount = 0,
            threeStarCount = 1,
            fourStarCount = 1,
            fiveStarCount = if (reviewCount > 0) reviewCount - 2 else 0,
        )
    }

    fun partnershipList(): List<Partnership> = listOf(
        Partnership(
            storeName = "테스트 카페",
            longitude = 126.9566,
            latitude = 37.4951,
            restaurantType = RestaurantType.CAFE,
            partnershipInfos = listOf(
                Partnership.PartnershipInfo(
                    id = 11,
                    partnershipType = "DISCOUNT",
                    collegeName = "IT대학",
                    departmentName = "컴퓨터학부",
                    likeCount = 3,
                    isLiked = true,
                    description = "아메리카노 10% 할인",
                    startDate = "2025-01-01",
                    endDate = "2025-12-31"
                )
            )
        )
    )

    fun partnershipRestaurant(): PartnershipRestaurant {
        return PartnershipRestaurant(
            id = 11,
            partnershipType = "DISCOUNT",
            storeName = "테스트 카페",
            description = "아메리카노 10% 할인",
            startDate = "2025-01-01",
            endDate = "2025-12-31",
            restaurantType = RestaurantType.CAFE,
            longitude = 126.9566,
            latitude = 37.4951,
            collegeName = "IT대학",
            departmentName = "컴퓨터학부",
            partnershipLikeCount = 3,
            likedByUser = true
        )
    }
}
