package com.eatssu.android.data.remote.dto.response

import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.common.enums.StoreType
import io.kotest.matchers.shouldBe

class PartnershipResponseMapperBehaviorSpec : AppBehaviorSpec({

    given("PartnershipResponse.toDomain") {
        `when`("restaurantType이 CAFE/RESTAURANT/PUB이면") {
            then("각 enum으로 매핑한다") {
                PartnershipResponse(
                    storeName = "A",
                    longitude = 1.0,
                    latitude = 2.0,
                    restaurantType = StoreType.CAFE,
                    partnershipInfos = emptyList(),
                ).toDomain().restaurantType shouldBe StoreType.CAFE

                PartnershipResponse(
                    storeName = "B",
                    longitude = 1.0,
                    latitude = 2.0,
                    restaurantType = StoreType.RESTAURANT,
                    partnershipInfos = emptyList(),
                ).toDomain().restaurantType shouldBe StoreType.RESTAURANT

                PartnershipResponse(
                    storeName = "C",
                    longitude = 1.0,
                    latitude = 2.0,
                    restaurantType = StoreType.PUB,
                    partnershipInfos = emptyList(),
                ).toDomain().restaurantType shouldBe StoreType.PUB
            }
        }

        `when`("restaurantType이 null이면") {
            then("RESTAURANT로 fallback한다") {
                PartnershipResponse(
                    storeName = "D",
                    longitude = 1.0,
                    latitude = 2.0,
                    restaurantType = null,
                    partnershipInfos = emptyList(),
                ).toDomain().restaurantType shouldBe StoreType.RESTAURANT
            }
        }

        `when`("필드가 null인 응답을 매핑하면") {
            val result = PartnershipResponse(
                storeName = null,
                longitude = null,
                latitude = null,
                restaurantType = null,
                partnershipInfos = listOf(
                    PartnershipResponse.PartnershipInfo(
                        id = null,
                        partnershipType = null,
                        collegeName = null,
                        departmentName = null,
                        likeCount = null,
                        isLiked = null,
                        description = null,
                        startDate = null,
                        endDate = null,
                    )
                ),
            ).toDomain()

            then("기본값으로 채운다") {
                result.storeName shouldBe ""
                result.longitude shouldBe 126.95661313346206
                result.latitude shouldBe 37.49517278813046
                result.partnershipInfos.first().id shouldBe -1
                result.partnershipInfos.first().partnershipType shouldBe ""
                result.partnershipInfos.first().likeCount shouldBe 0
                result.partnershipInfos.first().isLiked shouldBe false
            }
        }
    }

    given("PartnershipRestaurantResponse.toDomain") {
        `when`("restaurantType이 CAFE/RESTAURANT/PUB이면") {
            then("각 enum으로 매핑한다") {
                PartnershipRestaurantResponse(
                    id = 1,
                    partnershipType = "DISCOUNT",
                    storeName = "A",
                    description = "desc",
                    startDate = "2025-01-01",
                    endDate = "2025-12-31",
                    restaurantType = StoreType.CAFE,
                    longitude = 1.0,
                    latitude = 2.0,
                    collegeName = "IT",
                    departmentName = "CS",
                    partnershipLikeCount = 1,
                    likedByUser = true,
                ).toDomain().storeType shouldBe StoreType.CAFE

                PartnershipRestaurantResponse(
                    id = 1,
                    partnershipType = "DISCOUNT",
                    storeName = "A",
                    description = "desc",
                    startDate = "2025-01-01",
                    endDate = "2025-12-31",
                    restaurantType = StoreType.RESTAURANT,
                    longitude = 1.0,
                    latitude = 2.0,
                    collegeName = "IT",
                    departmentName = "CS",
                    partnershipLikeCount = 1,
                    likedByUser = true,
                ).toDomain().storeType shouldBe StoreType.RESTAURANT

                PartnershipRestaurantResponse(
                    id = 1,
                    partnershipType = "DISCOUNT",
                    storeName = "A",
                    description = "desc",
                    startDate = "2025-01-01",
                    endDate = "2025-12-31",
                    restaurantType = StoreType.PUB,
                    longitude = 1.0,
                    latitude = 2.0,
                    collegeName = "IT",
                    departmentName = "CS",
                    partnershipLikeCount = 1,
                    likedByUser = true,
                ).toDomain().storeType shouldBe StoreType.PUB
            }
        }

        `when`("restaurantType이 null이면") {
            then("RESTAURANT로 fallback한다") {
                PartnershipRestaurantResponse(
                    id = 1,
                    partnershipType = "DISCOUNT",
                    storeName = "A",
                    description = "desc",
                    startDate = "2025-01-01",
                    endDate = "2025-12-31",
                    restaurantType = null,
                    longitude = 1.0,
                    latitude = 2.0,
                    collegeName = "IT",
                    departmentName = "CS",
                    partnershipLikeCount = 1,
                    likedByUser = true,
                ).toDomain().storeType shouldBe StoreType.RESTAURANT
            }
        }

        `when`("필드가 null인 응답을 매핑하면") {
            val result = PartnershipRestaurantResponse(
                id = null,
                partnershipType = null,
                storeName = null,
                description = null,
                startDate = null,
                endDate = null,
                restaurantType = null,
                longitude = null,
                latitude = null,
                collegeName = null,
                departmentName = null,
                partnershipLikeCount = null,
                likedByUser = null,
            ).toDomain()

            then("기본값으로 채운다") {
                result.id shouldBe -1
                result.partnershipType shouldBe ""
                result.storeName shouldBe ""
                result.longitude shouldBe 126.95661313346206
                result.latitude shouldBe 37.49517278813046
                result.collegeName shouldBe ""
                result.departmentName shouldBe ""
                result.partnershipLikeCount shouldBe 0
                result.likedByUser shouldBe false
            }
        }
    }
})
