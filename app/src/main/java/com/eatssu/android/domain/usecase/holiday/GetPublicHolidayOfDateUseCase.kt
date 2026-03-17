package com.eatssu.android.domain.usecase.holiday

import com.eatssu.android.domain.model.PublicHoliday
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

/**
 * 특정 날짜가 공휴일이면 해당 공휴일 정보를 반환한다.
 */
class GetPublicHolidayOfDateUseCase @Inject constructor(
    private val getPublicHolidaysOfMonthUseCase: GetPublicHolidaysOfMonthUseCase,
) {
    suspend operator fun invoke(date: LocalDate): PublicHoliday? {
        val holidays = getPublicHolidaysOfMonthUseCase(YearMonth.from(date))
        return holidays.firstOrNull { it.date == date }
    }
}
