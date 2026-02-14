package com.eatssu.android.presentation.util

import com.eatssu.android.test.AppBehaviorSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class CalendarUtilBehaviorSpec : AppBehaviorSpec({

    given("CalendarUtil") {
        `when`("monthYearFromDate를 호출하면") {
            then("yyyy.MM 형식으로 변환한다") {
                CalendarUtil.monthYearFromDate(LocalDate.of(2025, 1, 15)) shouldBe "2025.01"
            }
        }

        `when`("daysInWeekArray를 호출하면") {
            then("해당 주 일요일부터 7일을 반환한다") {
                val days = CalendarUtil.daysInWeekArray(LocalDate.of(2025, 1, 15))
                days.size shouldBe 7
                days.first() shouldBe LocalDate.of(2025, 1, 12)
                days.last() shouldBe LocalDate.of(2025, 1, 18)
            }
        }

        `when`("convertMillisToDateString을 호출하면") {
            then("밀리초를 yyyyMMdd 문자열로 변환한다") {
                val millis = LocalDate.of(2025, 1, 1)
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
                CalendarUtil.convertMillisToDateString(millis) shouldBe "20250101"
            }
        }

        `when`("getNextDayDate를 호출하면") {
            then("내일 날짜의 yyyyMMdd 문자열을 반환한다") {
                val expected = LocalDate.now()
                    .plusDays(1)
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                CalendarUtil.getNextDayDate() shouldBe expected
            }
        }
    }
})
