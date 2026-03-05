package com.eatssu.android.domain.repository

import com.eatssu.android.domain.model.PublicHoliday
import java.time.LocalDate
import java.time.YearMonth

interface PublicHolidayRepository {

    suspend fun getHolidays(yearMonth: YearMonth): List<PublicHoliday>

    suspend fun getHoliday(date: LocalDate): PublicHoliday?
}
