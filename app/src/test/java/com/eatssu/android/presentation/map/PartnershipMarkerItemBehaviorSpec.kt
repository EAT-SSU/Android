package com.eatssu.android.presentation.map

import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.android.test.samplePartnership
import io.kotest.matchers.shouldBe

class PartnershipMarkerItemBehaviorSpec : AppBehaviorSpec({

    given("제휴 지도 마커 목록") {
        val partnership = samplePartnership(storeName = "Cafe Like")

        `when`("찜 상태와 찜 개수만 변경되면") {
            val likedPartnership = partnership.copy(
                partnershipInfos = partnership.partnershipInfos.map { info ->
                    info.copy(
                        isLiked = !info.isLiked,
                        likeCount = info.likeCount + 1,
                    )
                },
            )

            then("지도 마커 입력은 변경되지 않는다") {
                listOf(partnership).toMarkerItems() shouldBe
                    listOf(likedPartnership).toMarkerItems()
            }
        }

        `when`("마커 표시 정보가 변경되면") {
            val movedPartnership = partnership.copy(latitude = partnership.latitude + 0.001)

            then("지도 마커 입력도 변경된다") {
                (listOf(partnership).toMarkerItems() ==
                    listOf(movedPartnership).toMarkerItems()) shouldBe false
            }
        }
    }
})
