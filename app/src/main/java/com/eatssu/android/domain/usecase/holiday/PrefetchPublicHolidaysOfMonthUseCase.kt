package com.eatssu.android.domain.usecase.holiday

import java.time.YearMonth
import javax.inject.Inject

class PrefetchPublicHolidaysOfMonthUseCase @Inject constructor(
    private val getPublicHolidaysOfMonthUseCase: GetPublicHolidaysOfMonthUseCase,
) {
    suspend operator fun invoke(yearMonth: YearMonth) {
        getPublicHolidaysOfMonthUseCase(yearMonth)
    }
}
