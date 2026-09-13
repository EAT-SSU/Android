package com.eatssu.android.presentation.map

import com.eatssu.android.domain.model.Partnership
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.android.test.samplePartnership
import com.eatssu.common.enums.PeriodType
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class FestivalPartnershipPolicySpec : AppBehaviorSpec({

    given("2026 동연제 제휴 노출 정책") {
        val festivalInfo = Partnership.PartnershipInfo(
            id = 100,
            partnershipType = "DISCOUNT",
            collegeName = "축제",
            departmentName = "",
            likeCount = 0,
            isLiked = false,
            description = "축제 할인",
            startDate = "2026-09-15",
            endDate = "2026-09-16",
            periodType = PeriodType.FESTIVAL,
        )
        val normalInfo = festivalInfo.copy(
            id = 101,
            description = "일반 할인",
            periodType = PeriodType.NORMAL,
        )
        val source = samplePartnership(
            storeName = "축제 매장",
            infos = listOf(normalInfo, festivalInfo),
        )

        `when`("행사 시작 전이거나 종료 후이면") {
            then("축제 제휴를 노출하지 않는다") {
                activeFestivalPartnerships(
                    partnerships = listOf(source),
                    date = LocalDate.of(2026, 9, 14),
                ) shouldBe emptyList()
                activeFestivalPartnerships(
                    partnerships = listOf(source),
                    date = LocalDate.of(2026, 9, 17),
                ) shouldBe emptyList()
            }
        }

        `when`("9월 15일 또는 16일이면") {
            then("FESTIVAL 타입인 제휴만 노출한다") {
                listOf(
                    LocalDate.of(2026, 9, 15),
                    LocalDate.of(2026, 9, 16),
                ).forEach { date ->
                    val result = activeFestivalPartnerships(
                        partnerships = listOf(source),
                        date = date,
                    )

                    result.single().partnershipInfos shouldContainExactly listOf(festivalInfo)
                    result.single().hasFestivalPartnership shouldBe true
                }
            }
        }

        `when`("서버의 축제 날짜 형식이 잘못되면") {
            then("앱을 종료하지 않고 해당 제휴만 제외한다") {
                val invalid = source.copy(
                    partnershipInfos = listOf(festivalInfo.copy(startDate = "invalid")),
                )

                activeFestivalPartnerships(
                    partnerships = listOf(invalid),
                    date = LocalDate.of(2026, 9, 15),
                ) shouldBe emptyList()
            }
        }

        `when`("동일 매장에 학과 제휴와 축제 제휴가 함께 있으면") {
            then("마커는 하나로 유지하고 상세 제휴 목록을 합친다") {
                val normal = source.copy(partnershipInfos = listOf(normalInfo))
                val festival = source.copy(partnershipInfos = listOf(festivalInfo))

                val result = mergePartnerships(
                    userPartnerships = listOf(normal),
                    festivalPartnerships = listOf(festival),
                )

                result.size shouldBe 1
                result.single().partnershipInfos shouldContainExactly listOf(normalInfo, festivalInfo)
            }
        }
    }
})
