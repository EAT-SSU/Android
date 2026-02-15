package com.eatssu.android.presentation.util

import com.eatssu.android.test.AppBehaviorSpec
import io.kotest.matchers.shouldBe

class TimeUtilBehaviorSpec : AppBehaviorSpec({

    given("TimeUtil.getTimeIndex") {
        `when`("아침 범위(0~9) 값이면") {
            then("0을 반환한다") {
                TimeUtil.getTimeIndex(0) shouldBe 0
                TimeUtil.getTimeIndex(9) shouldBe 0
            }
        }

        `when`("점심 범위(10~15) 값이면") {
            then("1을 반환한다") {
                TimeUtil.getTimeIndex(10) shouldBe 1
                TimeUtil.getTimeIndex(15) shouldBe 1
            }
        }

        `when`("저녁 범위(16~24) 값이면") {
            then("2를 반환한다") {
                TimeUtil.getTimeIndex(16) shouldBe 2
                TimeUtil.getTimeIndex(24) shouldBe 2
            }
        }

        `when`("그 외 값이면") {
            then("3을 반환한다") {
                TimeUtil.getTimeIndex(-1) shouldBe 3
                TimeUtil.getTimeIndex(25) shouldBe 3
            }
        }
    }
})
