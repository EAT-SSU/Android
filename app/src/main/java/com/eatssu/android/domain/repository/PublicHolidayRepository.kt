package com.eatssu.android.domain.repository

import com.eatssu.android.domain.model.PublicHoliday
import java.time.YearMonth

interface PublicHolidayRepository {

    suspend fun getHolidays(yearMonth: YearMonth): List<PublicHoliday>
}
