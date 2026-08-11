package com.eatssu.android.data.local

import com.eatssu.android.test.AppBehaviorSpec
import io.kotest.matchers.shouldBe

class FavoritePartnershipOrderBehaviorSpec : AppBehaviorSpec({

    given("기기에 기록한 제휴 찜 순서") {
        `when`("서버 목록과 조정하면") {
            then("서버에 남은 로컬 순서를 우선하고 새 서버 항목을 뒤에 붙인다") {
                reconcileFavoriteOrder(
                    current = listOf(3, 1, 9),
                    serverIds = listOf(1, 2, 3),
                ) shouldBe listOf(3, 1, 2)
            }
        }

        `when`("로컬 기록이 없으면") {
            then("서버 응답 순서를 그대로 사용한다") {
                reconcileFavoriteOrder(
                    current = emptyList(),
                    serverIds = listOf(4, 2, 1),
                ) shouldBe listOf(4, 2, 1)
            }
        }

        `when`("기기에 같은 제휴 ID가 중복 저장되어 있으면") {
            then("중복을 제거해 화면에는 한 번만 노출한다") {
                reconcileFavoriteOrder(
                    current = listOf(255, 255, 1),
                    serverIds = listOf(255, 1),
                ) shouldBe listOf(255, 1)
            }
        }
    }
})
