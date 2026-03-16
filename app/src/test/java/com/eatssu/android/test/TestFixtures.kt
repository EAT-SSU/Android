package com.eatssu.android.test

import com.eatssu.android.domain.model.College
import com.eatssu.android.domain.model.Department
import com.eatssu.android.domain.model.Partnership
import com.eatssu.android.domain.model.PartnershipRestaurant
import com.eatssu.android.domain.model.Review
import com.eatssu.android.domain.model.ReviewInfo
import com.eatssu.android.domain.model.Token
import com.eatssu.android.domain.model.UserInfo
import com.eatssu.common.enums.StoreType

fun sampleCollege(
    id: Int = 1,
    name: String = "IT대학",
) = College(id, name)

fun sampleDepartment(
    id: Int = 11,
    name: String = "컴퓨터학부",
) = Department(id, name)

fun sampleUserInfo(
    nickname: String = "eatssu",
    college: College = sampleCollege(),
    department: Department = sampleDepartment(),
) = UserInfo(
    nickname = nickname,
    userDepartment = department,
    userCollege = college,
)

fun sampleToken(
    access: String = "access-token",
    refresh: String = "refresh-token",
) = Token(
    accessToken = access,
    refreshToken = refresh,
)

fun sampleReview(
    id: Long = 1L,
    rating: Int = 5,
    content: String = "good",
    writerNickname: String = "writer",
    isWriter: Boolean = true,
) = Review(
    isWriter = isWriter,
    reviewId = id,
    menuLikeInfoList = listOf(
        Review.MenuLikeInfo(menuId = 101L, name = "A", isLike = true),
    ),
    writerNickname = writerNickname,
    rating = rating,
    writeDate = "2025-01-01",
    content = content,
    imgUrl = null,
)

fun sampleReviewInfo(
    count: Int = 10,
    rating: Double = 4.2,
) = ReviewInfo(
    reviewCnt = count,
    rating = rating,
    oneStarCount = 1,
    twoStarCount = 2,
    threeStarCount = 3,
    fourStarCount = 2,
    fiveStarCount = 2,
)

fun samplePartnership(
    storeName: String = "Cafe A",
    infos: List<Partnership.PartnershipInfo> = listOf(
        Partnership.PartnershipInfo(
            id = 1,
            partnershipType = "DISCOUNT",
            collegeName = "IT",
            departmentName = "CS",
            likeCount = 3,
            isLiked = true,
            description = "desc",
            startDate = "2025-01-01",
            endDate = "2025-12-31",
        ),
    ),
    type: StoreType = StoreType.CAFE,
) = Partnership(
    storeName = storeName,
    longitude = 127.0,
    latitude = 37.0,
    restaurantType = type,
    partnershipInfos = infos,
)

fun samplePartnershipRestaurant(
    id: Int = 1,
    type: StoreType = StoreType.CAFE,
) = PartnershipRestaurant(
    id = id,
    partnershipType = "DISCOUNT",
    storeName = "Cafe A",
    description = "desc",
    startDate = "2025-01-01",
    endDate = "2025-12-31",
    storeType = type,
    longitude = 127.0,
    latitude = 37.0,
    collegeName = "IT",
    departmentName = "CS",
    partnershipLikeCount = 3,
    likedByUser = true,
)
