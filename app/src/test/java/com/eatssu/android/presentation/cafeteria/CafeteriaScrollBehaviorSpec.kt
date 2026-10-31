package com.eatssu.android.presentation.cafeteria

import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.Time
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class CafeteriaScrollBehaviorSpec : AppBehaviorSpec({
    given("알림의 날짜·시간·식당 스크롤 요청") {
        then("식단이 로드될 때까지 목적지를 유지하고 일치한 요청만 소모한다") {
            val vm = CafeteriaScrollViewModel()
            val date = LocalDate.of(2026, 11, 2)
            vm.request(date, Time.DINNER, Restaurant.DORMITORY)
            val first = requireNotNull(vm.target.value)
            first.date shouldBe date
            first.time shouldBe Time.DINNER
            first.restaurant shouldBe Restaurant.DORMITORY
            vm.request(date.plusDays(1), Time.LUNCH, Restaurant.DODAM)
            val next = requireNotNull(vm.target.value)
            vm.consume(first.id)
            vm.target.value shouldBe next
            vm.consume(next.id)
            vm.target.value shouldBe null
        }
        then("일반 알림은 식당 자동 스크롤을 요청하지 않는다") {
            val vm = CafeteriaScrollViewModel()
            vm.request(LocalDate.now(), Time.LUNCH, null)
            vm.target.value shouldBe null
        }
    }
})
