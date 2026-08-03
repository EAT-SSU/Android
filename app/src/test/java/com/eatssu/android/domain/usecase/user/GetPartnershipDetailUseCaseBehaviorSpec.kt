package com.eatssu.android.domain.usecase.user

import com.eatssu.android.domain.model.Partnership
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.common.enums.PeriodType
import com.eatssu.common.enums.StoreType
import io.kotest.matchers.shouldBe

class GetPartnershipDetailUseCaseBehaviorSpec : AppBehaviorSpec({

    given("가게 제휴 상세 조회") {
        val useCase = GetPartnershipDetailUseCase()
        val infos = listOf(
            Partnership.PartnershipInfo(
                id = 1,
                partnershipType = "DISCOUNT",
                collegeName = "IT",
                departmentName = "CS",
                likeCount = 2,
                isLiked = true,
                description = "10% 할인",
                startDate = "2025-01-01",
                endDate = "2025-12-31",
                periodType = PeriodType.NORMAL
            ),
            Partnership.PartnershipInfo(
                id = 2,
                partnershipType = "GIFT",
                collegeName = "IT",
                departmentName = "CS",
                likeCount = 1,
                isLiked = false,
                description = "음료 증정",
                startDate = "2025-02-01",
                endDate = "2025-11-30",
                periodType = PeriodType.NORMAL
            ),
        )
        val partnerships = listOf(
            Partnership(
                storeName = "Cafe A",
                longitude = 127.0,
                latitude = 37.0,
                restaurantType = StoreType.CAFE,
                partnershipInfos = infos,
                naverMapUrl = "https://naver.me/test",
                kakaoMapUrl = "https://place.map.kakao.com/test",
            )
        )

        `when`("storeName이 존재하지 않으면") {
            then("null을 반환한다") {
                useCase(partnerships, "Unknown", 1) shouldBe null
            }
        }

        `when`("partnershipId가 주어지고 매칭되면") {
            then("해당 id의 PartnershipRestaurant로 매핑한다") {
                val result = useCase(partnerships, "Cafe A", 2)
                result?.id shouldBe 2
                result?.storeName shouldBe "Cafe A"
                result?.description shouldBe "음료 증정"
                result?.storeType shouldBe StoreType.CAFE
                result?.naverMapUrl shouldBe "https://naver.me/test"
                result?.kakaoMapUrl shouldBe "https://place.map.kakao.com/test"
            }
        }

        `when`("partnershipId가 없으면") {
            then("첫 번째 제휴 정보를 대표로 반환한다") {
                val result = useCase(partnerships, "Cafe A", null)
                result?.id shouldBe 1
                result?.description shouldBe "10% 할인"
            }
        }

        `when`("partnershipId가 있지만 매칭이 없으면") {
            then("첫 번째 제휴 정보로 fallback 한다") {
                val result = useCase(partnerships, "Cafe A", 99)
                result?.id shouldBe 1
                result?.description shouldBe "10% 할인"
            }
        }

        `when`("제휴 정보 리스트가 비어있으면") {
            val emptyInfoPartnership = listOf(
                Partnership(
                    storeName = "Cafe A",
                    longitude = 127.0,
                    latitude = 37.0,
                    restaurantType = StoreType.CAFE,
                    partnershipInfos = emptyList(),
                )
            )

            then("null을 반환한다") {
                useCase(emptyInfoPartnership, "Cafe A", null) shouldBe null
            }
        }
    }
})
